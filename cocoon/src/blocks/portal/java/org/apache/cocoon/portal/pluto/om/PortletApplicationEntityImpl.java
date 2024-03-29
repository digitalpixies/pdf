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
package org.apache.cocoon.portal.pluto.om;

import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletEntityList;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;

/**
 * 
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * 
 * @version CVS $Id: PortletApplicationEntityImpl.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class PortletApplicationEntityImpl 
implements PortletApplicationEntity {

    protected PortletEntityList portlets = new PortletEntityListImpl();

    protected ObjectID objectId;    

    protected PortletApplicationDefinition pd;
    
    /**
     * Constructor
     */
    public PortletApplicationEntityImpl(String id, PortletApplicationDefinition pd) {
        this.objectId = org.apache.cocoon.portal.pluto.om.common.ObjectIDImpl.createFromString(id);
        this.pd = pd;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.om.entity.PortletApplicationEntity#getId()
     */
    public ObjectID getId() {
        return this.objectId;
    }    

    /* (non-Javadoc)
     * @see org.apache.pluto.om.entity.PortletApplicationEntity#getPortletEntityList()
     */
    public PortletEntityList getPortletEntityList() {
        return this.portlets;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.om.entity.PortletApplicationEntity#getPortletApplicationDefinition()
     */
    public PortletApplicationDefinition getPortletApplicationDefinition() {
        return this.pd;
    }

}
