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

import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.forms.formmodel.WidgetDefinition;
import org.apache.cocoon.forms.validation.WidgetValidator;
import org.apache.cocoon.forms.validation.WidgetValidatorBuilder;
import org.w3c.dom.Element;


/**
 * Builds a CAPTCHA validator.
 * 
 * @see org.apache.cocoon.forms.validation.impl.CaptchaValidator
 * @version $Id: CaptchaValidatorBuilder.java 449149 2006-09-23 03:58:05Z crossley $
 */
public class CaptchaValidatorBuilder implements WidgetValidatorBuilder, ThreadSafe {

    public WidgetValidator build(Element validationRuleElement,
            WidgetDefinition definition) throws Exception {
        return new CaptchaValidator();
    }

}
