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

import java.util.Iterator;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.forms.event.ProcessingPhaseListener;
import org.apache.cocoon.forms.formmodel.library.LibraryManager;
import org.apache.cocoon.util.location.LocationAttributes;
import org.w3c.dom.Element;

/**
 * Builds {@link FormDefinition}s.
 *
 * @version $Id: FormDefinitionBuilder.java 449149 2006-09-23 03:58:05Z crossley $
 */
public final class FormDefinitionBuilder extends AbstractContainerDefinitionBuilder {

    protected LibraryManager libraryManager;

    public void service(ServiceManager manager) throws ServiceException {
        super.service(manager);
        libraryManager = (LibraryManager) serviceManager.lookup(LibraryManager.ROLE);
    }

    public WidgetDefinition buildWidgetDefinition(Element widgetElement, WidgetDefinitionBuilderContext context)
    throws Exception {
        throw new UnsupportedOperationException("Please use the other signature without WidgetDefinitionBuilderContext!");
    }

    public WidgetDefinition buildWidgetDefinition(Element formElement) throws Exception {
        FormDefinition formDefinition = new FormDefinition(libraryManager);
        this.context = new WidgetDefinitionBuilderContext();
        this.context.setLocalLibrary(formDefinition.getLocalLibrary());

        // set local URI
        formDefinition.getLocalLibrary().setSourceURI(LocationAttributes.getURI(formElement));

        Iterator i = buildEventListeners(formElement, "on-processing-phase", ProcessingPhaseListener.class).iterator();
        while (i.hasNext()) {
            formDefinition.addProcessingPhaseListener((ProcessingPhaseListener) i.next());
        }

        super.setupDefinition(formElement, formDefinition);
        setDisplayData(formElement, formDefinition);

        setupContainer(formElement,"widgets",formDefinition);

        formDefinition.resolve();

        formDefinition.makeImmutable();

        this.context = null;
        return formDefinition;
    }
}
