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
package org.apache.cocoon.forms.datatype.typeimpl;

import org.apache.cocoon.forms.datatype.Datatype;
import org.apache.cocoon.forms.datatype.DatatypeManager;
import org.w3c.dom.Element;

/**
 * Builds {@link EnumType}s.
 * 
 * @version $Id: EnumTypeBuilder.java 449149 2006-09-23 03:58:05Z crossley $
 */
public class EnumTypeBuilder extends AbstractDatatypeBuilder {

    /* (non-Javadoc)
     * @see org.apache.cocoon.forms.datatype.DatatypeBuilder#build(org.w3c.dom.Element, boolean, org.apache.cocoon.forms.datatype.DatatypeManager)
     */
    public Datatype build(Element datatypeElement,
						  boolean arrayType,
						  DatatypeManager datatypeManager) throws Exception {
        EnumType type = new EnumType();
        type.setArrayType(arrayType);
        type.setBuilder(this);

        buildValidationRules(datatypeElement, type, datatypeManager);
        buildConvertor(datatypeElement, type);

        return type;
    }
}
