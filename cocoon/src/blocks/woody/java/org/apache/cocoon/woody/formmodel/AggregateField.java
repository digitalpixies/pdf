/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.woody.formmodel;

import org.apache.cocoon.woody.Constants;
import org.apache.cocoon.woody.FormContext;
import org.apache.cocoon.woody.formmodel.AggregateFieldDefinition.SplitMapping;
import org.apache.cocoon.woody.util.I18nMessage;
import org.apache.cocoon.woody.validation.ValidationError;
import org.apache.cocoon.xml.AttributesImpl;
import org.apache.excalibur.xml.sax.XMLizable;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;

import org.outerj.expression.ExpressionException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An aggregated field allows to represent one value as multiple input fields, or several values
 * as one field. Hence this widget is a field and a container widget simultaneously.
 *
 * <p>Upon submit, it first attempts to read own value from the request, and splits over nested
 * field widgets using a regular expression. If split fails, this will simply give a validation error.
 * If own value was not submitted, it attempts to read values for nested field widgets, and combines
 * theirs values using combine expression.
 *
 * <p>To validate this widget, both the validation rules of the nested widgets are
 * checked, and those of the aggregated field themselves. The validation rules of the aggregated
 * field can perform checks on the string as entered by the user (e.g. check its total length).
 *
 * <p>This field and nested fields can be of any supported type, as long as combine expression
 * gives result of the correct type, and split regular expression can split string representation
 * into parts which can be converted to the values of nested fields.
 *
 * @version CVS $Id: AggregateField.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class AggregateField extends Field {

    /**
     * List of nested fields
     */
    private List fields = new ArrayList();

    /**
     * Map of nested fields
     */
    private Map fieldsById = new HashMap();


    public AggregateField(AggregateFieldDefinition definition) {
        super(definition);
    }

    public final AggregateFieldDefinition getAggregateFieldDefinition() {
        return (AggregateFieldDefinition)super.definition;
    }

    protected void addField(Field field) {
        field.setParent(this);
        fields.add(field);
        fieldsById.put(field.getId(), field);
    }

    public Iterator getChildren() {
        return fields.iterator();
    }

    public void readFromRequest(FormContext formContext) {
        String newEnteredValue = formContext.getRequest().getParameter(getFullyQualifiedId());
        if (newEnteredValue != null) {
            // There is one aggregated entered value. Read it and split it.
            super.readFromRequest(formContext);
            if (needsParse) {
                setFieldsValues(enteredValue);
            }
        } else {
            // Check if there are multiple splitted values. Read them and aggregate them.
            boolean needsParse = false;
            for (Iterator i = fields.iterator(); i.hasNext();) {
                Field field = (Field)i.next();
                field.readFromRequest(formContext);
                needsParse |= field.needsParse;
            }
            if (needsParse) {
                combineFields();
            }
        }
    }

    public void setValue(Object newValue) {
        super.setValue(newValue);
        if (needsValidate) {
            setFieldsValues(enteredValue);
        }
    }

    /**
     * Returns false if all fields have no value.
     */
    private boolean fieldsHaveValues() {
        for (Iterator i = fields.iterator(); i.hasNext();) {
            Field field = (Field)i.next();
            if (field.getValue() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Splits passed value and sets values of all nested fields.
     * If split fails, resets all fields.
     */
    private void setFieldsValues(String value) {
        if (value == null) {
            resetFieldsValues();
        } else {
            PatternMatcher matcher = new Perl5Matcher();
            if (matcher.matches(value, getAggregateFieldDefinition().getSplitPattern())) {
                MatchResult matchResult = matcher.getMatch();
                Iterator iterator = getAggregateFieldDefinition().getSplitMappingsIterator();
                while (iterator.hasNext()) {
                    SplitMapping splitMapping = (SplitMapping)iterator.next();
                    String result = matchResult.group(splitMapping.getGroup());

                    // Fields can have a non-string datatype, going to the readFromRequest
                    Field field = (Field)fieldsById.get(splitMapping.getFieldId());
                    field.readFromRequest(result);
                }
            } else {
                resetFieldsValues();
            }
        }
    }

    public void combineFields() {
        try {
            Object value = getAggregateFieldDefinition().getCombineExpression().evaluate(new ExpressionContextImpl(this, true));
            super.setValue(value);
        } catch (CannotYetResolveWarning e) {
            super.setValue(null);
        } catch (ExpressionException e) {
            super.setValue(null);
        } catch (ClassCastException e) {
            super.setValue(null);
        }
    }

    /**
     * Sets values of all nested fields to null
     */
    private void resetFieldsValues() {
        for (Iterator i = fields.iterator(); i.hasNext();) {
            Field field = (Field)i.next();
            field.setValue(null);
        }
    }

    public boolean validate(FormContext formContext) {
        if ((enteredValue != null) != fieldsHaveValues()) {
            XMLizable failMessage = getAggregateFieldDefinition().getSplitFailMessage();
            if (failMessage != null) {
                validationError = new ValidationError(failMessage);
            } else {
                validationError = new ValidationError(new I18nMessage("aggregatedfield.split-failed",
                                                                      new String[] { getAggregateFieldDefinition().getSplitRegexp() },
                                                                      Constants.I18N_CATALOGUE));
            }
            return false;
        }

        // validate my child fields
        for (Iterator i = fields.iterator(); i.hasNext();) {
            Field field = (Field)i.next();
            if (!field.validate(formContext)) {
                validationError = field.getValidationError();
                return false;
            }
        }

        return super.validate(formContext);
    }


    private static final String AGGREGATEFIELD_EL = "aggregatefield";
    private static final String VALUE_EL = "value";
    private static final String VALIDATION_MSG_EL = "validation-message";

    public void generateSaxFragment(ContentHandler contentHandler, Locale locale) throws SAXException {
        AttributesImpl aggregatedFieldAttrs = new AttributesImpl();
        aggregatedFieldAttrs.addCDATAAttribute("id", getFullyQualifiedId());
        aggregatedFieldAttrs.addCDATAAttribute("required", String.valueOf(getAggregateFieldDefinition().isRequired()));
        contentHandler.startElement(Constants.WI_NS, AGGREGATEFIELD_EL, Constants.WI_PREFIX_COLON + AGGREGATEFIELD_EL, aggregatedFieldAttrs);

        if (enteredValue != null || value != null) {
            contentHandler.startElement(Constants.WI_NS, VALUE_EL, Constants.WI_PREFIX_COLON + VALUE_EL, Constants.EMPTY_ATTRS);
            String stringValue;
            if (value != null) {
                stringValue = getDatatype().convertToString(value, locale);
            } else {
                stringValue = enteredValue;
            }
            contentHandler.characters(stringValue.toCharArray(), 0, stringValue.length());
            contentHandler.endElement(Constants.WI_NS, VALUE_EL, Constants.WI_PREFIX_COLON + VALUE_EL);
        }

        // validation message element: only present if the value is not valid
        if (validationError != null) {
            contentHandler.startElement(Constants.WI_NS, VALIDATION_MSG_EL, Constants.WI_PREFIX_COLON + VALIDATION_MSG_EL, Constants.EMPTY_ATTRS);
            validationError.generateSaxFragment(contentHandler);
            contentHandler.endElement(Constants.WI_NS, VALIDATION_MSG_EL, Constants.WI_PREFIX_COLON + VALIDATION_MSG_EL);
        }

        // generate label, help, hint, etc.
        definition.generateDisplayData(contentHandler);

        // generate selection list, if any
        if (selectionList != null) {
            selectionList.generateSaxFragment(contentHandler, locale);
        } else if (getFieldDefinition().getSelectionList() != null) {
            getFieldDefinition().getSelectionList().generateSaxFragment(contentHandler, locale);
        }
        contentHandler.endElement(Constants.WI_NS, AGGREGATEFIELD_EL, Constants.WI_PREFIX_COLON + AGGREGATEFIELD_EL);
    }

    public void generateLabel(ContentHandler contentHandler) throws SAXException {
        definition.generateLabel(contentHandler);
    }

    public Widget getWidget(String id) {
        return (Widget)fieldsById.get(id);
    }
}
