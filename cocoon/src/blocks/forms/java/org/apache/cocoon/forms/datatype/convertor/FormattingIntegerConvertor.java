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
package org.apache.cocoon.forms.datatype.convertor;

import java.util.Locale;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * A Convertor for {@link Integer}s backed by the
 * {@link java.text.DecimalFormat DecimalFormat} class.
 *
 * <p>This class is mostly the same as the {@link FormattingDecimalConvertor},
 * so see there for more information.
 * 
 * @version $Id: FormattingIntegerConvertor.java 449149 2006-09-23 03:58:05Z crossley $
*/
public class FormattingIntegerConvertor extends FormattingDecimalConvertor {

    public FormattingIntegerConvertor() {
        super();
    }

    public ConversionResult convertFromString(String value, Locale locale, Convertor.FormatCache formatCache) {
        // Some locales (e.g. "fr") produce non-breaking spaces sent back as space by the browser
        value = value.replace(' ', (char)160);
        DecimalFormat decimalFormat = getDecimalFormat(locale, formatCache);
        try {
            Number decimalValue = decimalFormat.parse(value);
            if (decimalValue instanceof Integer)
                return new ConversionResult(decimalValue);
            else
                return new ConversionResult(new Integer(decimalValue.intValue()));
        } catch (ParseException e) {
            return ConversionResult.create("integer");
        }
    }

    protected int getDefaultVariant() {
        return INTEGER;
    }

    public Class getTypeClass() {
        return Integer.class;
    }
}
