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
package org.apache.cocoon.portal.coplet.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.cocoon.portal.aspect.AspectDescription;
import org.apache.cocoon.portal.factory.impl.AbstractProducibleDescription;


/**
 * A description of a coplet data or a coplet instance data
 * 
 * @author <a href="mailto:cziegeler@s-und-n.de">Carsten Ziegeler</a>
 * 
 * @version CVS $Id: DefaultCopletDescription.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class DefaultCopletDescription
    extends AbstractProducibleDescription  {

    protected List instanceAspects = new ArrayList();

    public List getInstanceAspectDescriptions() {
        return this.instanceAspects;
    }

    public void addInstanceAspectDescription(AspectDescription aspect) {
        this.instanceAspects.add(aspect);
    }

    /**
     * Return the description for an aspect
     */
    public AspectDescription getInstanceAspectDescription(String name) {
        AspectDescription desc = null;
        Iterator i = this.instanceAspects.iterator();
        while (desc == null && i.hasNext() ) {
            AspectDescription current = (AspectDescription)i.next();
            if ( name.equals(current.getName())) {
                desc = current;
            }
        }
        return desc;
    }

}
