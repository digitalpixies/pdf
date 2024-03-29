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

import java.util.Iterator;

import org.apache.cocoon.woody.event.ActionListener;
import org.apache.cocoon.woody.util.DomHelper;
import org.w3c.dom.Element;

/**
 * 
 * @author <a href="http://www.apache.org/~sylvain/">Sylvain Wallez</a>
 * @version CVS $Id: RowActionDefinitionBuilder.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class RowActionDefinitionBuilder extends AbstractWidgetDefinitionBuilder {
    
    
    public WidgetDefinition buildWidgetDefinition(Element widgetElement) throws Exception {
        String actionCommand = DomHelper.getAttribute(widgetElement, "action-command");
        RowActionDefinition definition = createDefinition(widgetElement, actionCommand);
        setLocation(widgetElement, definition);
        setId(widgetElement, definition);
        setDisplayData(widgetElement, definition);

        definition.setActionCommand(actionCommand);

        Iterator iter = buildEventListeners(widgetElement, "on-activate", ActionListener.class).iterator();
        while (iter.hasNext()) {
            definition.addActionListener((ActionListener)iter.next());
        }

        return definition;
    }
    
    protected RowActionDefinition createDefinition(Element element, String actionCommand) throws Exception {

        if ("delete".equals(actionCommand)) {
            return new RowActionDefinition.DeleteRowDefinition();

        } else if ("add-after".equals(actionCommand)) {
            return new RowActionDefinition.AddAfterDefinition();

        } else if ("move-up".equals(actionCommand)) {
            return new RowActionDefinition.MoveUpDefinition();

        } else if ("move-down".equals(actionCommand)) {
            return new RowActionDefinition.MoveDownDefinition();

        } else {
            throw new Exception("Unknown repeater row action '" + actionCommand + "' at " + DomHelper.getLineLocation(element));
        }
    }
}

