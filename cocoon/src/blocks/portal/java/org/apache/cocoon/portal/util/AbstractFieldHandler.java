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
package org.apache.cocoon.portal.util;

/**
 * Base field handler implementation
 *
 * @author <a href="mailto:cziegeler@s-und-n.de">Carsten Ziegeler</a>
 * @version CVS $Id: AbstractFieldHandler.java 433543 2006-08-22 06:22:54Z crossley $
 */
public abstract class AbstractFieldHandler 
    extends org.exolab.castor.mapping.AbstractFieldHandler {

    /* (non-Javadoc)
     * @see org.exolab.castor.mapping.ExtendedFieldHandler#newInstance(java.lang.Object, java.lang.Object[])
     */
    public Object newInstance(Object arg0, Object[] arg1) {
        if ( arg1 == null ) {
            return this.newInstance(arg0);
        }
        throw new IllegalStateException("Constructor is not supported.");
    }
}
