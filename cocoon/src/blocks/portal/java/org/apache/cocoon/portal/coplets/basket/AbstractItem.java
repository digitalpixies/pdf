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
package org.apache.cocoon.portal.coplets.basket;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * This is a possible base class for item implementations.
 * 
 * It just adds attributes (or meta-data) functionality
 *
 * @version CVS $Id: AbstractItem.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class AbstractItem implements Serializable {
    
    protected static long currentId = System.currentTimeMillis();
    
    /** The attributes */
    protected Map attributes = new HashMap();
    
    /** Unique id */
    protected long id;
    
    public AbstractItem() {
        synchronized ( this.getClass() ) {
            currentId++;
            this.id = currentId;
        }
    }

    /** Return an attribute or null */
    public Object getAttribute(String name) { 
        return this.attributes.get(name); 
    }
    
    /** Set an attribute */
    public void setAttribute(String name, Object value) { 
        this.attributes.put(name, value); 
    }
    
    /** Get all attribute names */
    public Iterator getAttributeNames() { 
        return this.attributes.keySet().iterator(); 
    }
    
    /** Remove one attribute */
    public void removeAttribute(String name) { 
        this.attributes.remove(name);
    }
    
    /** Check if an attribute is available */
    public boolean hasAttribute(String name) { 
        return this.attributes.containsKey(name); 
    }
    
    public long getId() {
        return this.id;
    }
}
