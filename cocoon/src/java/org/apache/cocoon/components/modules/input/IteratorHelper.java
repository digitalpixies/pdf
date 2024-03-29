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
package org.apache.cocoon.components.modules.input;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Wraps an Enumeration and provides Iterator interface.
 *
 * @version $Id: IteratorHelper.java 433543 2006-08-22 06:22:54Z crossley $
 */
class IteratorHelper implements Iterator {

    protected Enumeration enumeration;

    public IteratorHelper( Enumeration e ) { this.enumeration = e; }
    public boolean hasNext() { return this.enumeration.hasMoreElements(); }
    public Object next() { return this.enumeration.nextElement(); }
    /** ignored */
    public void remove() {}
}
