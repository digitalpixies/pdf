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
package org.apache.cocoon.i18n;

import java.util.MissingResourceException;

import org.apache.avalon.framework.component.Component;

/**
 * Resource bundle component interface.
 * Provide the minimal number of methods to be used for i18n.
 *
 * @author <a href="mailto:kpiroumian@apache.org">Konstantin Piroumian</a>
 * @version $Id: Bundle.java 433543 2006-08-22 06:22:54Z crossley $
 */
public interface Bundle extends Component {

    String ROLE = Bundle.class.getName();

    /**
     * Get string value by key.
     *
     * @param key
     * @return Resource as string.
     * @exception MissingResourceException if resource was not found
     */
    String getString(String key) throws MissingResourceException;

    /**
     * Get object value by key.
     *
     * @param key The resource key.
     * @return The resource as object.
     * @exception MissingResourceException if resource was not found
     */
    Object getObject(String key) throws MissingResourceException;

}
