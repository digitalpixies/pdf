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
package org.apache.cocoon.woody.samples;

import org.apache.cocoon.woody.acting.AbstractWoodyAction;
import org.apache.cocoon.woody.formmodel.Form;
import org.apache.cocoon.woody.formmodel.Repeater;
import org.apache.cocoon.woody.formmodel.Field;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.avalon.framework.parameters.Parameters;

import java.util.Map;
import java.util.Date;

/**
 * An action that creates an instance of a specific example form included with Woody,
 * and adds some rows to its repeater widget. This example is meant to illustrate
 * how you can prepopulate a Form instance before its initial display.
 * 
 * @version $Id: InitForm1Action.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class InitForm1Action extends AbstractWoodyAction {
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source, Parameters parameters)
            throws Exception {
        String formSource = parameters.getParameter("form-definition");
        String formAttribute = parameters.getParameter("attribute-name");

        Form form = formManager.createForm(resolver.resolveURI(formSource));

        Field birthDate = (Field)form.getWidget("birthdate");
        birthDate.setValue(new Date());

        Repeater repeater = (Repeater)form.getWidget("contacts");
        repeater.addRow();
        Field field = (Field)repeater.getWidget(0, "firstname");
        field.setValue("Jules");

        repeater.addRow();
        field = (Field)repeater.getWidget(1, "firstname");
        field.setValue("Lucien");

        Request request = ObjectModelHelper.getRequest(objectModel);
        request.setAttribute(formAttribute, form);

        return null;
    }
}
