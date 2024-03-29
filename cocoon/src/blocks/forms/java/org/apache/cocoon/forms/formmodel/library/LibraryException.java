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
package org.apache.cocoon.forms.formmodel.library;

import org.apache.cocoon.forms.FormsException;
import org.apache.cocoon.util.location.Location;

/**
 * This exception is thrown when something specific to the library system goes wrong.
 *
 * @version $Id: LibraryException.java 449149 2006-09-23 03:58:05Z crossley $
 */
public class LibraryException extends FormsException {

    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }

    public LibraryException(String message, Location location) {
        super(message, location);
    }

    public LibraryException(String message, Throwable cause, Location location) {
        super(message, cause, location);
    }
}
