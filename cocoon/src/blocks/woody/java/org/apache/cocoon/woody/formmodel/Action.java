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

import org.apache.cocoon.environment.Request;
import org.apache.cocoon.woody.FormContext;
import org.apache.cocoon.woody.Constants;
import org.apache.cocoon.woody.event.ActionEvent;
import org.apache.cocoon.woody.event.WidgetEvent;
import org.apache.cocoon.xml.AttributesImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.Locale;

/**
 * An Action widget. An Action widget can cause an {@link ActionEvent} to be triggered
 * on the server side, which will be handled by either the event handlers defined in the
 * form definition, and/or by the {@link org.apache.cocoon.woody.event.FormHandler FormHandler}
 * registered with the form, if any. An Action widget can e.g. be rendered as a button,
 * or as a hidden field which gets its value set by javascript. The Action widget will generate its associated
 * ActionEvent when a requestparameter is present with as name the id of this Action widget, and as
 * value a non-empty value.
 * 
 * @version $Id: Action.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class Action extends AbstractWidget {
    protected ActionDefinition definition;

    public Action(ActionDefinition definition) {
        this.definition = definition;
        setLocation(definition.getLocation());
    }

    public String getId() {
        return definition.getId();
    }

    public void readFromRequest(final FormContext formContext) {
        Form form = getForm();
        
        // Set the submit widget if we can determine it from the request
        String fullId = getFullyQualifiedId();
        Request request = formContext.getRequest();
        
        String value = request.getParameter(fullId);
        if (value != null && value.length() > 0) {
            form.setSubmitWidget(this);
            
        } else {
            // Special workaround an IE bug for <input type="image" name="foo"> :
            // in that case, IE only sends "foo.x" and "foo.y" and not "foo" whereas
            // standards-compliant browsers such as Mozilla do send the "foo" parameter.
            //
            // Note that since actions are terminal widgets, there's no chance of conflict
            // with a child "x" or "y" widget.
            value = request.getParameter(fullId + ".x");
            if ((value != null) && value.length() > 0) {
                form.setSubmitWidget(this);
            }
        }
        
        if (form.getSubmitWidget() == this) {
            form.addWidgetEvent(new ActionEvent(this, definition.getActionCommand()));
            
            handleActivate();
        }
    }
    
    /**
     * Handle the fact that this action was activated. The default here is to end the
     * current form processing and redisplay the form, which means that actual behaviour
     * should be implemented in event listeners.
     */
    protected void handleActivate() {
        getForm().endProcessing(true);
    }

    /**
     * Always return <code>true</code> (an action has no validation)
     * 
     * @todo is there a use case for actions having validators?
     */
    public boolean validate(FormContext formContext) {
        return true;
    }

    private static final String ACTION_EL = "action";

    public void generateSaxFragment(ContentHandler contentHandler, Locale locale) throws SAXException {
        AttributesImpl buttonAttrs = new AttributesImpl();
        buttonAttrs.addCDATAAttribute("id", getFullyQualifiedId());
        contentHandler.startElement(Constants.WI_NS, ACTION_EL, Constants.WI_PREFIX_COLON + ACTION_EL, buttonAttrs);
        // generate label, help, hint, etc.
        definition.generateDisplayData(contentHandler);
        contentHandler.endElement(Constants.WI_NS, ACTION_EL, Constants.WI_PREFIX_COLON + ACTION_EL);
    }

    public void generateLabel(ContentHandler contentHandler) throws SAXException {
        definition.generateLabel(contentHandler);
    }
    
    public void broadcastEvent(WidgetEvent event) {
        this.definition.fireActionEvent((ActionEvent)event);
    }
}
