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

/**
 * This is the media manager.
 * It provides simple support for developing multi-channel applications
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @deprecated This block is deprecated and will be removed in future versions.
 * @version CVS $Id: MediaManager.java 433543 2006-08-22 06:22:54Z crossley $
*/
public interface MediaManager {

    /** The Avalon Role */
    String ROLE = MediaManager.class.getName();

    /**
     * Test if the media of the current request is the given value
     */
    boolean testMedia(String value);

    /**
     * Get all media type names
     */
    String[] getMediaTypes();

    /**
     * Return the current media type
     */
    String getMediaType();
}
