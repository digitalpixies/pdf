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
package org.apache.cocoon.portal.factory.impl;

import org.apache.cocoon.portal.aspect.impl.AbstractAspectalizableDescription;
import org.apache.cocoon.portal.factory.ProducibleDescription;

/**
 * This is a description of a {@link org.apache.cocoon.portal.factory.Producible} object.
 * 
 * @author <a href="mailto:cziegeler@s-und-n.de">Carsten Ziegeler</a>
 * 
 * @version CVS $Id: AbstractProducibleDescription.java 433543 2006-08-22 06:22:54Z crossley $
 */
public abstract class AbstractProducibleDescription
    extends AbstractAspectalizableDescription
    implements ProducibleDescription  {

    protected String className;
    
    protected String name;

    protected boolean createId = true;
    
    /**
     * @return The class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return The configured name
     */
    public String getName() {
        return name;
    }

    /**
     * @param string
     */
    public void setClassName(String string) {
        className = string;
    }

    /**
     * @param string
     */
    public void setName(String string) {
        name = string;
    }

    /**
     * Create a unique id for objects of this type
     */
    public boolean createId() {
        return this.createId;
    }

    public void setCreateId(boolean value) {
        this.createId = value;
    }
}
