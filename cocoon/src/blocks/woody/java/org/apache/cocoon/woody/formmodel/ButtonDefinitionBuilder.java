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

import org.w3c.dom.Element;
import org.apache.cocoon.woody.util.DomHelper;

/**
 * The ButtonDefinitionBuilder has been replaced by {@link ActionDefinitionBuilder}. This implementation
 * is only left here to give a warning to users.
 * 
 * @version $Id: ButtonDefinitionBuilder.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class ButtonDefinitionBuilder implements WidgetDefinitionBuilder {
    public WidgetDefinition buildWidgetDefinition(Element widgetElement) throws Exception {
        throw new Exception("The button widget has been renamed to action. Please update your form definition files. Found at " + DomHelper.getLocation(widgetElement));
    }
}
