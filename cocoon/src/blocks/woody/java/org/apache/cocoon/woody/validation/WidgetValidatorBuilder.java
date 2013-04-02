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
package org.apache.cocoon.woody.validation;

import org.apache.cocoon.woody.formmodel.WidgetDefinition;
import org.w3c.dom.Element;

/**
 * 
 * @author <a href="http://www.apache.org/~sylvain/">Sylvain Wallez</a>
 * @version CVS $Id: WidgetValidatorBuilder.java 433543 2006-08-22 06:22:54Z crossley $
 */
public interface WidgetValidatorBuilder {
    
    static final String ROLE = WidgetValidatorBuilder.class.getName();
    
    /**
     * Builds a {@link WidgetValidator} for a particular widget definition.
     * 
     * @param validationRuleElement the DOM element defining the validator
     * @param definition the widget definition the validator will have to validate.
     *        This may be used to check applicability of the validator to the widget.
     * @return A {@link WidgetValidator}
     * @throws Exception if some problem occurs
     */
    WidgetValidator build(Element validationRuleElement, WidgetDefinition definition) throws Exception;
}
