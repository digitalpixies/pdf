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
import org.apache.cocoon.woody.util.I18nMessage;
import org.apache.cocoon.woody.validation.ValidationError;
import org.apache.cocoon.woody.validation.ValidationErrorAware;
import org.apache.cocoon.woody.datatype.SelectionList;
import org.apache.cocoon.woody.datatype.Datatype;
import org.apache.cocoon.woody.event.DeferredValueChangedEvent;
import org.apache.cocoon.woody.event.WidgetEvent;
import org.apache.cocoon.woody.event.ValueChangedEvent;
import org.apache.cocoon.xml.AttributesImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.Locale;

/**
 * A general-purpose Widget that can hold one value. A Field widget can be associated
 * with a {@link org.apache.cocoon.woody.datatype.Datatype Datatype}, and thus
 * a Field widget can be used to edit different kinds of data, such as strings,
 * numbers and dates. A Datatype can also have an associated SelectionList, so
 * that the value for the Field can be selected from a list, rather than being
 * entered in a textbox. The validation of the field is delegated to its associated
 * Datatype.
 *
 * @author Bruno Dumon
 * @author <a href="http://www.apache.org/~sylvain/">Sylvain Wallez</a>
 * @version CVS $Id: Field.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class Field extends AbstractWidget implements ValidationErrorAware, DataWidget, SelectableWidget {
    protected SelectionList selectionList;

    protected String enteredValue;
    protected Object value;

    // At startup, we don't need to parse (both enteredValue and value are null),
    // but need to validate (error if field is required)
    protected boolean needsParse = true;
    protected boolean needsValidate = true;
    private boolean isValidating;
    protected ValidationError validationError;


    public Field(FieldDefinition fieldDefinition) {
        setDefinition(fieldDefinition);
        setLocation(fieldDefinition.getLocation());
    }

    public final FieldDefinition getFieldDefinition() {
        return (FieldDefinition)super.definition;
    }

    public String getId() {
        return definition.getId();
    }

    public Object getValue() {
        // Parse the value
        if (this.needsParse) {
            // Clear value, it will be recomputed
            this.value = null;
            if (this.enteredValue != null) {
                // Parse the value
                this.value = getDatatype().convertFromString(this.enteredValue, getForm().getLocale());
                if (this.value != null) {       // Conversion successfull
                    this.needsParse = false;
                    this.needsValidate = true;
                } else {        // Conversion failed
                    this.validationError = new ValidationError(new I18nMessage(
                        "datatype.conversion-failed",
                        new String[] {"datatype." + getDatatype().getDescriptiveName()},
                        new boolean[] { true },
                        Constants.I18N_CATALOGUE
                    ));
                    // No need for further validation (and need to keep the above error)
                    this.needsValidate = false;
                }
            } else {
                this.needsParse = false;
                this.needsValidate = true;
            }
        }

        // if getValue() is called on this field while we're validating, then it's because a validation
        // rule called getValue(), so then we just return the parsed (but not validated) value to avoid an endless loop
        if (isValidating) {
            return value;
        }

        // Validate the value
        if (this.needsValidate) {
            isValidating = true;
            try {
                if (super.validate(null)) {
                    // New-style validators were successful. Check the old-style ones.
                    if (this.value != null) {
                        this.validationError = getDatatype().validate(value, new ExpressionContextImpl(this));
                    } else {        // No value : is it required ?
                        if (getFieldDefinition().isRequired()) {
                            this.validationError = new ValidationError(new I18nMessage("general.field-required", Constants.I18N_CATALOGUE));
                        }
                    }
                }
                this.needsValidate = false;
            } finally {
                isValidating = false;
            }
        }
        return this.validationError == null ? this.value : null;
    }

    public void setValue(Object newValue) {
        if (newValue != null && !getDatatype().getTypeClass().isAssignableFrom(newValue.getClass())) {
            throw new RuntimeException("Incorrect value type for \"" + getFullyQualifiedId() +
                           "\" (expected " + getDatatype().getTypeClass() +
                           ", got " + newValue.getClass() + ".");
        }
        Object oldValue = this.value;
        boolean changed = ! (oldValue == null ? "" : oldValue).equals(newValue == null ? "" : newValue);
        // Do something only if value is different or null
        // (null allows to reset validation error)
        if (changed || newValue == null) {
            this.value = newValue;
            this.needsParse = false;
            this.validationError = null;
            // Force validation, even if set by the application
            this.needsValidate = true;
            if (newValue != null) {
                this.enteredValue = getDatatype().convertToString(newValue, getForm().getLocale());
            } else {
                this.enteredValue = null;
            }
            if (changed) {
                getForm().addWidgetEvent(new ValueChangedEvent(this, oldValue, newValue));
            }
        }
    }

    public void readFromRequest(FormContext formContext) {
        String newEnteredValue = formContext.getRequest().getParameter(getFullyQualifiedId());
        readFromRequest(newEnteredValue);
    }

    protected void readFromRequest(String newEnteredValue) {
        // whitespace & empty field handling
        if (newEnteredValue != null) {
            // TODO make whitespace behaviour configurable !!
            newEnteredValue = newEnteredValue.trim();
            if (newEnteredValue.length() == 0) {
                newEnteredValue = null;
            }
        }

        // Only convert if the text value actually changed. Otherwise, keep the old value
        // and/or the old validation error (allows to keep errors when clicking on actions)
        if (!(newEnteredValue == null ? "" : newEnteredValue).equals((enteredValue == null ? "" : enteredValue))) {
            getForm().addWidgetEvent(new DeferredValueChangedEvent(this, value));
            enteredValue = newEnteredValue;
            validationError = null;
            value = null;
            needsParse = true;
        }

        // Always revalidate, as validation may depend on the value of other fields
        this.needsValidate = true;
    }

    public boolean validate(FormContext formContext) {
        // If needed, getValue() will do the validation
        getValue();
        return this.validationError == null;
    }

    /**
     * Returns the validation error, if any. There will always be a validation error in case the
     * {@link #validate(FormContext)} method returned false.
     */
    public ValidationError getValidationError() {
        return validationError;
    }

    /**
     * Set a validation error on this field. This allows fields to be externally marked as invalid by
     * application logic.
     *
     * @param error the validation error
     */
    public void setValidationError(ValidationError error) {
        this.validationError = error;
    }

    public boolean isRequired() {
        return getFieldDefinition().isRequired();
    }


    private static final String FIELD_EL = "field";
    private static final String VALUE_EL = "value";
    private static final String VALIDATION_MSG_EL = "validation-message";

    public void generateSaxFragment(ContentHandler contentHandler, Locale locale) throws SAXException {
        AttributesImpl fieldAttrs = new AttributesImpl();
        fieldAttrs.addCDATAAttribute("id", getFullyQualifiedId());
        fieldAttrs.addCDATAAttribute("required", String.valueOf(isRequired()));
        contentHandler.startElement(Constants.WI_NS, FIELD_EL, Constants.WI_PREFIX_COLON + FIELD_EL, fieldAttrs);

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
        contentHandler.endElement(Constants.WI_NS, FIELD_EL, Constants.WI_PREFIX_COLON + FIELD_EL);
    }

    public void generateLabel(ContentHandler contentHandler) throws SAXException {
        definition.generateLabel(contentHandler);
    }

    /**
     * Set this field's selection list.
     * @param selectionList The new selection list.
     */
    public void setSelectionList(SelectionList selectionList) {
        if (selectionList != null &&
            selectionList.getDatatype() != null &&
            selectionList.getDatatype() != getDatatype()) {
            throw new RuntimeException("Tried to assign a SelectionList that is not associated with this widget's datatype.");
        }
        this.selectionList = selectionList;
    }

    /**
     * Read this field's selection list from an external source.
     * All Cocoon-supported protocols can be used.
     * The format of the XML produced by the source should be the
     * same as in case of inline specification of the selection list,
     * thus the root element should be a <code>wd:selection-list</code>
     * element.
     * @param uri The URI of the source.
     */
    public void setSelectionList(String uri) {
        setSelectionList(getFieldDefinition().buildSelectionList(uri));
    }

    /**
     * Set this field's selection list using values from an in-memory
     * object. The <code>object</code> parameter should point to a collection
     * (Java collection or array, or Javascript array) of objects. Each object
     * belonging to the collection should have a <em>value</em> property and a
     * <em>label</em> property, whose values are used to specify the <code>value</code>
     * attribute and the contents of the <code>wd:label</code> child element
     * of every <code>wd:item</code> in the list.
     * <p>Access to the values of the above mentioned properties is done
     * via <a href="http://jakarta.apache.org/commons/jxpath/users-guide.html">XPath</a> expressions.
     * @param model The collection used as a model for the selection list.
     * @param valuePath An XPath expression referring to the attribute used
     * to populate the values of the list's items.
     * @param labelPath An XPath expression referring to the attribute used
     * to populate the labels of the list's items.
     */
    public void setSelectionList(Object model, String valuePath, String labelPath) {
        setSelectionList(getFieldDefinition().buildSelectionListFromModel(model, valuePath, labelPath));
    }

    public Datatype getDatatype() {
        return getFieldDefinition().getDatatype();
    }

    public void broadcastEvent(WidgetEvent event) {
        getFieldDefinition().fireValueChangedEvent((ValueChangedEvent)event);
    }
}
