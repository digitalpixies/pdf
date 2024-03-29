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
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * A Convertor for {@link Long}s backed by the
 * {@link java.text.DecimalFormat DecimalFormat} class.
 *
 * <p>This class is mostly the same as the {@link FormattingDecimalConvertor},
 * so see there for more information.
 *
 * @version CVS $Id: FormattingLongConvertor.java 503643 2007-02-05 11:27:11Z cziegeler $
 */
public class FormattingLongConvertor extends FormattingDecimalConvertor {

    public FormattingLongConvertor() {
        super();
    }

    public Object convertFromString(String value, Locale locale, Convertor.FormatCache formatCache) {
        DecimalFormat decimalFormat = getDecimalFormat(locale, formatCache);
        try {
            Number decimalValue = decimalFormat.parse(value);
            if (decimalValue instanceof Long)
                return decimalValue;
            else
                return new Long(decimalValue.longValue());
        } catch (ParseException e) {
            return null;
        }
    }

    protected int getDefaultVariant() {
        return INTEGER;
    }

    public Class getTypeClass() {
        return Long.class;
    }
}
