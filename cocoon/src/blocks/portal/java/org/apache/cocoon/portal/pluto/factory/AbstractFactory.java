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

import java.util.Map;
import javax.servlet.ServletConfig;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.pluto.factory.Factory;


/**
 * Abstract implementation for all factories
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * 
 * @version CVS $Id: AbstractFactory.java 433543 2006-08-22 06:22:54Z crossley $
 */
public abstract class AbstractFactory 
extends AbstractLogEnabled
implements Factory {

    /** The servlet config */
    protected ServletConfig servletConfig;

    /** The properties */
    protected Map properties;
    
    /* (non-Javadoc)
     * @see org.apache.pluto.factory.Factory#init(javax.servlet.ServletConfig, java.util.Map)
     */
    public void init(ServletConfig config, Map properties) 
    throws Exception {
        this.servletConfig = config;
        this.properties = properties;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.factory.Factory#destroy()
     */
    public void destroy() throws Exception {
        // nothing to do
    }
}
