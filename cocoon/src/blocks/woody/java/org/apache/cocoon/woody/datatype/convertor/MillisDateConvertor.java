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
import java.util.Date;

/**
 * A Convertor for Date objects which uses the number of milliseconds since
 * January 1, 1970, 00:00:00 GMT as string representation.
 *
 * @version CVS $Id: MillisDateConvertor.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class MillisDateConvertor implements Convertor {
    public Object convertFromString(String value, Locale locale, Convertor.FormatCache formatCache) {
        try {
            long date = Long.parseLong(value);
            return new Date(date);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String convertToString(Object value, Locale locale, Convertor.FormatCache formatCache) {
        Date date = (Date)value;
        return String.valueOf(date.getTime());
    }

    public Class getTypeClass() {
        return Date.class;
    }
}
