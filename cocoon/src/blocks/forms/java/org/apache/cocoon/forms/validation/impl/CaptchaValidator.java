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
package org.apache.cocoon.forms.validation.impl;

import org.apache.cocoon.forms.formmodel.Widget;
import org.apache.cocoon.forms.validation.ValidationError;
import org.apache.cocoon.forms.validation.ValidationErrorAware;
import org.apache.cocoon.forms.validation.WidgetValidator;


/**
 * A {@link org.apache.cocoon.forms.validation.WidgetValidator} that relies on a CAPTCHA
 * test.
 * 
 * @see <a href="http://www.captcha.net">www.captcha.net</a>
 * @version $Id: CaptchaValidator.java 449149 2006-09-23 03:58:05Z crossley $
 */
public class CaptchaValidator implements WidgetValidator {

    private static final String VALIDATION_MESSAGE_KEY = "validation.captcha.mismatch"; 

    public boolean validate(Widget widget) {
        if (! (widget instanceof ValidationErrorAware)) {
            // Invalid widget type
            throw new IllegalArgumentException("Widget '" + widget.getRequestParameterName() + "' is not ValidationErrorAware");
        }
        boolean result = widget.getValue() != null && widget.getValue().equals(widget.getAttribute("secret"));
        if (! result) {
            ((ValidationErrorAware) widget).setValidationError(new ValidationError(VALIDATION_MESSAGE_KEY));
        }
        return result;
    }
}
