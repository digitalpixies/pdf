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
package org.apache.cocoon.faces.context;

import org.apache.cocoon.environment.Context;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Application context attributes map
 *
 * @author <a href="mailto:vgritsenko@apache.org">Vadim Gritsenko</a>
 * @version CVS $Id: ApplicationMap.java 433543 2006-08-22 06:22:54Z crossley $
 */
class ApplicationMap extends BaseMap {

    private Context context;


    ApplicationMap(Context context) {
        this.context = context;
    }

    public Object get(Object key) {
        return context.getAttribute(key.toString());
    }

    public Object put(Object key, Object value) {
        String sKey = key.toString();
        Object old = context.getAttribute(sKey);
        context.setAttribute(sKey, value);
        return old;
    }

    public Object remove(Object key) {
        String sKey = key.toString();
        Object old = context.getAttribute(sKey);
        context.removeAttribute(sKey);
        return old;
    }

    public Set entrySet() {
        Set entries = new HashSet();
        for (Enumeration e = context.getAttributeNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            entries.add(new BaseMap.Entry(name, context.getAttribute(name)));
        }

        return entries;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ApplicationMap)) {
            return false;
        }

        return super.equals(obj);
    }
}
