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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Session;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This is the action used to validate Session parameters (attributes).
 * The parameters are described via the external xml
 * file.
 * 
 * @see org.apache.cocoon.acting.AbstractValidatorAction
 *
 * @author <a href="mailto:Martin.Man@seznam.cz">Martin Man</a>
 * @author <a href="mailto:haul@apache.org">Christian Haul</a>
 * @version CVS $Id: SessionValidatorAction.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class SessionValidatorAction extends AbstractValidatorAction implements ThreadSafe {

    /* (non-Javadoc)
     * @see org.apache.cocoon.acting.AbstractValidatorAction#createMapOfParameters(java.util.Map, java.util.Collection)
     */
    protected HashMap createMapOfParameters(Map objectModel, Collection set) {
        String name;
        HashMap params = new HashMap(set.size());
        // put required params into hash
        Session session = ObjectModelHelper.getRequest(objectModel).getSession();
        for (Iterator i = set.iterator(); i.hasNext();) {
            name = ((Configuration) i.next()).getAttribute("name", "").trim();
            Object value = session.getAttribute(name);
            params.put(name, value);
        }
        return params;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.cocoon.acting.AbstractValidatorAction#setResult(java.util.Map, java.util.Map, java.util.Map, boolean)
     */
    protected Map setResult(Map objectModel, Map actionMap, Map resultMap, boolean allOK) {
        if (allOK){
            Session session = ObjectModelHelper.getRequest(objectModel).getSession();
            for (Iterator i = actionMap.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry me = (Map.Entry)i.next();
                session.setAttribute((String)me.getKey(), me.getValue());
            }
        }
        return super.setResult(objectModel, actionMap, resultMap, allOK);
    }


    /* (non-Javadoc)
     * @see org.apache.cocoon.acting.AbstractValidatorAction#isStringEncoded()
     */
    boolean isStringEncoded() {
        return false;
    }

}
