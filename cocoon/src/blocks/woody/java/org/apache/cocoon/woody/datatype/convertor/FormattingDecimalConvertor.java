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
import java.text.NumberFormat;
import java.text.ParseException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A Convertor for {@link BigDecimal}s backed by the
 * {@link java.text.DecimalFormat DecimalFormat} class.
 *
 * <p>It can be configured to use one of these variants: integer,
 * number, currency or percent.
 *
 * <p>Alternatively, a <strong>formatting pattern</strong> can be used. This can either be a locale-dependent
 * or locale-independent formatting pattern. When looking up a formatting pattern, a mechansim
 * similar to resource bundle lookup is used. Suppose the locale is nl-BE, then first a formatting
 * pattern for nl-BE will be sought, then one for nl, and if that is not
 * found, finally the locale-independent formatting pattern will be used.
 *
 * <p>Note: the earlier statement about the fact that this class uses java.text.DecimalFormat
 * is not entirely correct. In fact, it uses a small wrapper class that will either delegate to
 * java.text.DecimalFormat or com.ibm.icu.text.DecimalFormat. The com.ibm version will automatically
 * be used if it is present on the classpath, otherwise the java.text version will be used.
 *
 * @version CVS $Id: FormattingDecimalConvertor.java 503643 2007-02-05 11:27:11Z cziegeler $
 */
public class FormattingDecimalConvertor implements Convertor {
    private int variant;
    /** Locale-specific formatting patterns. */
    private LocaleMap localizedPatterns;
    /** Non-locale specific formatting pattern. */
    private String nonLocalizedPattern;

    public static final int INTEGER = 0;
    public static final int NUMBER = 1;
    public static final int CURRENCY = 2;
    public static final int PERCENT = 3;

    public FormattingDecimalConvertor() {
        this.variant = getDefaultVariant();
        this.localizedPatterns = new LocaleMap();
    }

    protected int getDefaultVariant() {
        return NUMBER;
    }

    public Object convertFromString(String value, Locale locale, Convertor.FormatCache formatCache) {
        DecimalFormat decimalFormat = getDecimalFormat(locale, formatCache);
        Number decimalValue;
        try {
            decimalValue = decimalFormat.parse(value);
        } catch (ParseException e) {
            return null;
        }

        if (decimalValue instanceof BigDecimal) {
            // no need for conversion
        } else if (decimalValue instanceof Integer) {
				decimalValue = new BigDecimal(decimalValue .intValue());
        } else if (decimalValue instanceof Long) {
                decimalValue = new BigDecimal(decimalValue.longValue());
        } else if (decimalValue instanceof Double) {
                decimalValue = new BigDecimal(decimalValue.doubleValue());
        } else if (decimalValue instanceof BigInteger) {
                decimalValue = new BigDecimal((BigInteger)decimalValue);
        } else {
                return null;
        }

            return decimalValue;
    }

    public String convertToString(Object value, Locale locale, Convertor.FormatCache formatCache) {
        DecimalFormat decimalFormat = getDecimalFormat(locale, formatCache);
        return decimalFormat.format(value);
    }

    protected final DecimalFormat getDecimalFormat(Locale locale, Convertor.FormatCache formatCache) {
        DecimalFormat decimalFormat = null;
        if (formatCache != null)
            decimalFormat = (DecimalFormat)formatCache.get();
        if (decimalFormat == null) {
            decimalFormat = getDecimalFormat(locale);
            if (formatCache != null)
                formatCache.store(decimalFormat);
        }
        return decimalFormat;
    }

    private DecimalFormat getDecimalFormat(Locale locale) {
        DecimalFormat decimalFormat = null;

        switch (variant) {
            case INTEGER:
                decimalFormat = (DecimalFormat)NumberFormat.getNumberInstance(locale);
                decimalFormat.setMaximumFractionDigits(0);
                decimalFormat.setDecimalSeparatorAlwaysShown(false);
                decimalFormat.setParseIntegerOnly(true);
                break;
            case NUMBER:
                decimalFormat = (DecimalFormat)NumberFormat.getNumberInstance(locale);
                break;
            case CURRENCY:
                decimalFormat = (DecimalFormat)NumberFormat.getCurrencyInstance(locale);
                break;
            case PERCENT:
                decimalFormat = (DecimalFormat)NumberFormat.getPercentInstance(locale);
                break;
        }

        String pattern = (String)localizedPatterns.get(locale);

        if (pattern != null) {
            decimalFormat.applyPattern(pattern);
        } else if (nonLocalizedPattern != null) {
            decimalFormat.applyPattern(nonLocalizedPattern);
        }
        return decimalFormat;
    }

    public void setVariant(int variant) {
        if (variant != INTEGER && variant != NUMBER && variant != CURRENCY && variant != PERCENT)
            throw new IllegalArgumentException("Invalid value for variant parameter.");
        this.variant = variant;
    }

    public void addFormattingPattern(Locale locale, String pattern) {
        localizedPatterns.put(locale, pattern);
    }

    public void setNonLocalizedPattern(String pattern) {
        this.nonLocalizedPattern = pattern;
    }

    public Class getTypeClass() {
        return java.math.BigDecimal.class;
    }
}
