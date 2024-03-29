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
package org.apache.cocoon.woody.datatype;

import org.apache.excalibur.xml.sax.XMLizable;

/**
 * @deprecated Validations error are now a general feature of widgets, not limited
 *             widgets having a datatype.
 * @see org.apache.cocoon.woody.validation.ValidationError
 * @version $Id: ValidationError.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class ValidationError extends org.apache.cocoon.woody.validation.ValidationError {
    

    public ValidationError(String errorMessage, boolean i18n) {
        super(errorMessage, i18n);
    }

    public ValidationError(String errorMessageKey) {
        super(errorMessageKey);
    }

    public ValidationError(String errorMessageKey, String[] parameters) {
        super(errorMessageKey, parameters);
    }

    public ValidationError(String errorMessageKey, String[] parameters, boolean[] keys) {
        super(errorMessageKey, parameters, keys);
    }

    public ValidationError(XMLizable errorMessage) {
        super(errorMessage);
    }

}
