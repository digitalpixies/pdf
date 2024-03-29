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

package org.apache.cocoon.components.elementprocessor.impl.poi.hssf.elements;

/**
 * Underline codes
 *
 * @author Marc Johnson (marc_johnson27591@hotmail.com)
 * @version CVS $Id: UnderlineType.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class UnderlineType {
    public static final int UNDERLINE_NONE = 0;
    public static final int UNDERLINE_SINGLE = 1;
    public static final int UNDERLINE_DOUBLE = 2;

    private UnderlineType() {}

    /**
     * Is this a valid underline shading?
     * @param val value to be checked
     * @return true if valid, false otherwise
     */
    public static boolean isValid(int val) {
        return (val >= UNDERLINE_NONE && val <= UNDERLINE_DOUBLE);
    }
} // end public class UnderlineType
