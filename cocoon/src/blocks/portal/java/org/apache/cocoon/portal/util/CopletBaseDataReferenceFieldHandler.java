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

import org.apache.cocoon.portal.coplet.CopletBaseData;
import org.apache.cocoon.portal.coplet.CopletData;

/**
 * Field handler for external CopletBaseData references.
 *
 * @author <a href="mailto:bluetkemeier@s-und-n.de">Bj&ouml;rn L&uuml;tkemeier</a>
 * 
 * @version CVS $Id: CopletBaseDataReferenceFieldHandler.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class CopletBaseDataReferenceFieldHandler
    extends ReferenceFieldHandler {

    public Object getValue(Object object) {
        CopletBaseData copletBaseData = ((CopletData) object).getCopletBaseData();
        if (copletBaseData != null) {
            return copletBaseData.getId();
        }
        return null;
    }

    public Object newInstance(Object parent) {
        return new CopletBaseData();
    }

    public void resetValue(Object object) {
        ((CopletData) object).setCopletBaseData(null);
    }

    public void setValue(Object object, Object value) {
        CopletBaseData copletBaseData = (CopletBaseData) getObjectMap().get(value);
        if (copletBaseData == null) {
            throw new ProfileException(
                "Referenced Coplet Base Data " + value + " does not exist.");
        }
        ((CopletData) object).setCopletBaseData(copletBaseData);
    }
}
