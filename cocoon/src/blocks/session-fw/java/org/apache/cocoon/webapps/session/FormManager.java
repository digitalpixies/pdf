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
package org.apache.cocoon.webapps.session;

import org.apache.cocoon.ProcessingException;
import org.w3c.dom.DocumentFragment;

/**
 * Form manager
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @deprecated This block is deprecated and will be removed in future versions.
 * @version CVS $Id: FormManager.java 433543 2006-08-22 06:22:54Z crossley $
*/
public interface FormManager {

    /** Avalon role */
    String ROLE = FormManager.class.getName();;

    /**
     * Register input field and return the current value of the field.
     */
    DocumentFragment registerInputField(String contextName,
                                        String path,
                                        String name,
                                        String formName)
    throws ProcessingException;

    /**
     * Process the request.
     * The incoming parameters are evaluated, if they contain information
     * for a previously registered input field.
     */
    void processInputFields();
}
