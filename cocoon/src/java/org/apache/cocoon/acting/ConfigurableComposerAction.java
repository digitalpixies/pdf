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
package org.apache.cocoon.acting;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;

/**
 * The <code>ComposerAction</code> will allow any <code>Action</code>
 * that extends this to access SitemapComponents.
 *
 * Basically a copy of {@link ComposerAction} that inherits from
 * {@link AbstractConfigurableAction}.
 *
 * @deprecated Use the ConfigurableServiceableAction instead
 * @author <a href="mailto:Giacomo.Pati@pwr.ch">Giacomo Pati</a>
 * @author <a href="mailto:haul@apache.org">Christian Haul</a>
 * @version CVS $Id: ConfigurableComposerAction.java 433543 2006-08-22 06:22:54Z crossley $
 */
public abstract class ConfigurableComposerAction extends AbstractConfigurableAction implements Composable {

    /** The component manager instance */
    protected ComponentManager manager;

    /**
     * Set the current <code>ComponentManager</code> instance used by this
     * <code>Composable</code>.
     */
    public void compose(ComponentManager manager) throws ComponentException {
        this.manager=manager;
    }
}
