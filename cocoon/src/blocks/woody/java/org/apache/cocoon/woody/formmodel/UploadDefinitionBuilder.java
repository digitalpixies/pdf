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

import org.apache.cocoon.woody.util.DomHelper;
import org.w3c.dom.Element;

/**
 * Builds {@link org.apache.cocoon.woody.formmodel.UploadDefinition}s.
 * 
 * @author <a href="mailto:uv@upaya.co.uk">Upayavira</a>
 * @author <a href="http://www.apache.org/~sylvain/">Sylvain Wallez</a>
 * @version CVS $Id: UploadDefinitionBuilder.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class UploadDefinitionBuilder extends AbstractWidgetDefinitionBuilder {

    public WidgetDefinition buildWidgetDefinition(Element widgetElement) throws Exception {
        String mimeTypes = DomHelper.getAttribute(widgetElement, "mime-types", null);
        boolean required = DomHelper.getAttributeAsBoolean(widgetElement, "required", false);
        
        UploadDefinition uploadDefinition = new UploadDefinition(required, mimeTypes);
        setLocation(widgetElement, uploadDefinition);
        setId(widgetElement, uploadDefinition);

        setDisplayData(widgetElement, uploadDefinition);
        setValidators(widgetElement, uploadDefinition);

        return uploadDefinition;
    }
}
