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
package org.apache.cocoon.woody.transformation;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.components.flow.FlowHelper;
import org.apache.cocoon.components.flow.WebContinuation;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.i18n.I18nUtils;
import org.apache.cocoon.woody.formmodel.Form;
import org.apache.cocoon.xml.AttributesImpl;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.Variables;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * WoodyPipeLineConfig
 * 
 * @version CVS $Id: WoodyPipelineConfig.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class WoodyPipelineConfig {

    /**
     * Default key under which the woody form is stored in the JXPath context.
     */
    public static final String WOODY_FORM = "woody-form";

    /** 
     * Name of the request attribute under which the Woody form is stored (optional). */
    private final String attributeName;

    /**
     * Pointer to the current request object.     */
    private final Request request;

    /**
     * Initialized jxpathcontext to evaluate passed expressions with.     */
    private final JXPathContext jxpathContext;

    /** 
     * Containts locale specified as a parameter to the transformer, if any. */
    private final Locale localeParameter;

    /** 
     * The locale currently used by the transformer. */
    private Locale locale;

    /**
     * Value for the action attribute of the form.
     */
    private String formAction;

    /**
     * Value for the method attribute of the form.
     */
    private String formMethod;

    private WoodyPipelineConfig(JXPathContext jxpc, Request req, Locale localeParam, 
            String attName, String actionExpression, String method) {
        this.attributeName = attName;
        this.request = req;
        this.jxpathContext =jxpc;
        this.localeParameter = localeParam;
        this.formAction = translateText(actionExpression);
        this.formMethod = method;
    }

    /**
     * Creates and initializes a WoodyPipelineConfig object based on the passed
     * arguments of the setup() of the specific Pipeline-component.
     * 
     * @param objectModel the objectmodel as passed in the setup()
     * @param parameters the parameters as passed in the setup()
     * @return an instance of WoodyPipelineConfig initialized according to the 
     * settings in the sitemap.
     */
    public static WoodyPipelineConfig createConfig(Map objectModel, Parameters parameters) {
        // create and set the jxpathContext...
        Object flowContext = FlowHelper.getContextObject(objectModel);
        WebContinuation wk = FlowHelper.getWebContinuation(objectModel);
        JXPathContext jxpc = JXPathContext.newContext(flowContext);
        Variables vars = jxpc.getVariables();
        vars.declareVariable("continuation", wk);
        Request request = ObjectModelHelper.getRequest(objectModel);
        vars.declareVariable("request", request);
        Session session = request.getSession(false);
        vars.declareVariable("session", session);
        vars.declareVariable("parameters", parameters);
        
        Locale localeParameter = null;
        String localeStr = parameters.getParameter("locale", null);
        if (localeStr != null) {
            localeParameter = I18nUtils.parseLocale(localeStr);
        }

        String attributeName = parameters.getParameter("attribute-name", null);
        String actionExpression = parameters.getParameter("form-action", null);
        String formMethod = parameters.getParameter("form-method", "POST");
        //TODO (20031223 mpo)think about adding form-encoding for the Generator.
        // Note generator will also need some text to go on the submit-button? 
        // Alternative to adding more here is to apply xinclude ?

        return new WoodyPipelineConfig(jxpc, request, localeParameter, 
                attributeName, actionExpression, formMethod);
    }

    /**
     * Overloads {@link #findForm(String)} by setting the jxpath-expression to null
     */
    public Form findForm() throws SAXException {
        return this.findForm(null);
    }

    /**
     * Finds the form from the current request-context based on the settings of 
     * this configuration object.  The fall-back search-procedure is as follows:
     * <ol><li>Use the provided jxpathExpression (if not null)</li>
     * <li>Use the setting of the 'attribute-name' parameter on the request</li>
     * <li>Obtain the form from it's default location in the flow context</li>
     * </ol> 
     * 
     * @param jxpathExpression that should be pointing to the form
     * @return the found form if found
     * @throws SAXException in any of the following cases:
     * <ul><li>The provided jxpathExpression (if not null) not point to a
     * {@link Form} instance.</li>
     * <li>The request is not holding a {@link Form} instance under the key 
     * specified by 'attribute-name' (if specified)</li>
     * <li>Both jxpathExpression and 'attribute-name' were not specified AND
     * also the default location was not holding a valid {@link Form} instance.</li>
     * </ol> 
     */
    public Form findForm(String jxpathExpression) throws SAXException {
        Object form = null;
        if (jxpathExpression != null) {
            form = this.jxpathContext.getValue(jxpathExpression);
            if (form == null) {
                throw new SAXException("No form found at location \"" + jxpathExpression + "\".");
            } else if (!(form instanceof Form)) {
                throw new SAXException("Object returned by expression \"" + jxpathExpression + "\" is not a Woody Form.");
            }
        } else if (this.attributeName != null) { // then see if an attribute-name was specified
            form = this.request.getAttribute(this.attributeName);
            if (form == null) {
                throw new SAXException("No form found in request attribute with name \"" + this.attributeName + "\"");
            } else if (!(form instanceof Form)) {
                throw new SAXException("Object found in request (attribute = '" + this.attributeName + "') is not a Woody Form.");
            }
        } else { // and then see if we got a form from the flow
            jxpathExpression = "/" + WoodyPipelineConfig.WOODY_FORM;
            try {
                form = this.jxpathContext.getValue(jxpathExpression);
            } catch (JXPathException e) { /* do nothing */ }
            if (form == null) {
                throw new SAXException("No Woody form found.");
            }
        }
        return (Form)form;
    }

    /**
     * Replaces JXPath expressions embedded inside #{ and } by their value.
     * This will parse the passed String looking for #{} occurences and then
     * uses the {@link #evaluateExpression(String)} to evaluate the found expression.
     * 
     * @return the original String with it's #{}-parts replaced by the evaulated results.
     */
    public String translateText(String original) {
        if (original==null) {
            return null;
        }

        StringBuffer expression;
        StringBuffer translated = new StringBuffer();
        StringReader in = new StringReader(original);
        int chr;
        try {
            while ((chr = in.read()) != -1) {
                char c = (char) chr;
                if (c == '#') {
                    chr = in.read();
                    if (chr != -1) {
                        c = (char) chr;
                        if (c == '{') {
                            expression = new StringBuffer();
                            boolean more = true;
                            while ( more ) {
                                more = false;
                                if ((chr = in.read()) != -1) {
                                    c = (char)chr;
                                    if (c != '}') {
                                        expression.append(c);
                                        more = true;
                                    } else {
                                        translated.append(evaluateExpression(expression.toString()).toString());
                                    }
                                } else {
                                    translated.append('#').append('{').append(expression);
                                }
                            }
                        }
                    } else {
                        translated.append((char) chr);
                    }
                } else {
                    translated.append(c);
                }
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        return translated.toString();
    }

    /**
     * Evaluates the passed xpath expression using the internal jxpath context 
     * holding the declared variables:
     * <ol><li>continuation: as made available by flowscript</li>
     * <li>request: as present in the cocoon processing environment</li>
     * <li>session: as present in the cocoon processing environment</li>
     * <li>parameters: as present in the cocoon sitemap node of the pipeline component</li></ol>
     * 
     * @param expression
     * @return the object-value resulting the expression evaluation.
     */
    public Object evaluateExpression(String expression) {
        return this.jxpathContext.getValue(expression);
    }    

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocaleParameter() {
        return localeParameter;
    }

    /**
     * The value for the wi:form-generated/@action. 
     * Note: wi:form-template copies this from its wt:form-template counterpart.
     *  
     * @return the {@link #translateText(String)} result of the 'form-action' sitemap 
     * parameter to the pipeline component, or null if that parameter was not set.
     */
    public String getFormAction() {
        return formAction;
    }

    /**
     * The value for the wi:form-generated/@method.
     * Note: wi:form-template copies this from its wt:form-template counterpart.
     * 
     * @return the value of the 'form-method' sitemap parameter to the pipeline 
     * component. Defaults to 'POST' if it was not set.
     */
    public String getFormMethod() {
        return formMethod;
    }

    /**
     * The grouped attributes to set on the wi:form-generated element.
     * Note: wi:form-template copies this from its wt:form-template counterpart.
     * 
     * @see #getFormAction()
     * @see #getFormMethod()
     */
    public Attributes getFormAttributes() {
        AttributesImpl formAtts = new AttributesImpl();
        if (getFormAction() != null) {
            formAtts.addCDATAAttribute("action", getFormAction());
        }
        formAtts.addCDATAAttribute("method", getFormMethod());
        return formAtts;
    }
}
