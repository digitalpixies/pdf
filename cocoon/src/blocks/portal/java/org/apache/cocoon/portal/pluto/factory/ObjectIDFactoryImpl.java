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

import org.apache.pluto.factory.ObjectIDFactory;
import org.apache.pluto.om.common.ObjectID;
    
/**
 * 
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * 
 * @version CVS $Id: ObjectIDFactoryImpl.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class ObjectIDFactoryImpl
extends AbstractFactory
implements ObjectIDFactory {

    /* (non-Javadoc)
     * @see org.apache.pluto.factory.ObjectIDFactory#createObjectID(java.lang.String)
     */
    public ObjectID createObjectID(String portletGUID) {
        return org.apache.cocoon.portal.pluto.om.common.ObjectIDImpl.createFromString(portletGUID);
    }

}
