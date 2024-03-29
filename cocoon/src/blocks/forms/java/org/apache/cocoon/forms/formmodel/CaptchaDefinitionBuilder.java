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
package org.apache.cocoon.forms.formmodel;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.forms.util.DomHelper;
import org.w3c.dom.Element;

/**
 * Builds {@link CaptchaFieldDefinition}s.
 *
 * @see <a href="http://www.captcha.net">www.captcha.net</a>
 * @version $Id: CaptchaDefinitionBuilder.java 449149 2006-09-23 03:58:05Z crossley $
 */
public class CaptchaDefinitionBuilder extends AbstractDatatypeWidgetDefinitionBuilder implements Contextualizable, ThreadSafe {
    
    private Context avalonContext;
    
    public void contextualize(Context context) throws ContextException {
        this.avalonContext = context;
    }

    public WidgetDefinition buildWidgetDefinition(Element widgetElement) throws Exception {
        FieldDefinition definition = new CaptchaFieldDefinition(avalonContext);
        setupDefinition(widgetElement, definition);
        definition.makeImmutable();
        return definition;
    }

    protected void setupDefinition(Element widgetElement, FieldDefinition definition) throws Exception {
        super.setupDefinition(widgetElement, definition);
        
        // parse "@required"
        boolean required = DomHelper.getAttributeAsBoolean(widgetElement, "required", false);
        definition.setRequired(required);
        
        // parse "@length"
        int length = DomHelper.getAttributeAsInteger(widgetElement, "length", 7);
        ((CaptchaFieldDefinition) definition).setLength(length);
    }
}
