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
package org.apache.cocoon.portal.pluto.factory;

import org.apache.cocoon.portal.pluto.om.common.UnmodifiableSet;
import org.apache.pluto.om.Controller;
import org.apache.pluto.om.ControllerFactory;
import org.apache.pluto.om.Model;

/**
 * The implementation of the controller factory
 * We simply assume that each model is it's controller
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * 
 * @version CVS $Id: ControllerFactoryImpl.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class ControllerFactoryImpl 
extends AbstractFactory
implements ControllerFactory {

    /* (non-Javadoc)
     * @see org.apache.pluto.om.ControllerFactory#get(org.apache.pluto.om.Model)
     */
    public Controller get(Model model) {
        if (model instanceof UnmodifiableSet) {
            model = (Model)((UnmodifiableSet)model).getModifiableSet();
        }
        return (Controller)model;
    }

}
