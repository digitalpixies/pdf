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
package org.apache.cocoon.woody.datatype.convertor;

import java.util.Locale;

import org.apache.cocoon.woody.datatype.Sex;

import junit.framework.TestCase;

/**
 * Test case for the {@link EnumConvertor} class.
 * 
 * @version CVS $Id: EnumConvertorTestCase.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class EnumConvertorTestCase extends TestCase {

    public EnumConvertorTestCase(String name) {
        super(name);
    }

    /**
     * Test the {@link EnumConvertor#convertFromString(java.lang.String, java.util.Locale, org.apache.cocoon.woody.datatype.convertor.Convertor.FormatCache)
     * method.
     */
    public void testConvertFromString() {
        EnumConvertor convertor = new EnumConvertor("org.apache.cocoon.woody.datatype.Sex");
        Object sex = convertor.convertFromString
            (Sex.class.getName() + ".FEMALE", Locale.getDefault(), null);
        assertSame("Returned sex must be FEMALE", Sex.FEMALE, sex);
    }
    
    /**
     * Test the {@link EnumConvertor##convertToString(java.lang.Object, java.util.Locale, org.apache.cocoon.woody.datatype.convertor.Convertor.FormatCache)
     * method.
     */
    public void testConvertToString() {
        EnumConvertor convertor = new EnumConvertor("org.apache.cocoon.woody.datatype.Sex");
        assertEquals("Converted value must match string",
                Sex.class.getName() + ".MALE",
                convertor.convertToString
                    (Sex.MALE, Locale.getDefault(), null));
    }
}
