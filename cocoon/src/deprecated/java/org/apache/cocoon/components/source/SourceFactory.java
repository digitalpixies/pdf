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
package org.apache.cocoon.components.source;

import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.Environment;
import org.apache.cocoon.environment.Source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @deprecated by the Avalon Exalibur Source Resolving
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Id: SourceFactory.java 433543 2006-08-22 06:22:54Z crossley $
 */
public interface SourceFactory extends ThreadSafe {

    /**
     * Get a <code>Source</code> object.
     * @param environment This is optional.
     */
    Source getSource(Environment environment, String location)
    throws ProcessingException, MalformedURLException, IOException;

    /**
     * Get a <code>Source</code> object.
     * @param environment This is optional.
     */
    Source getSource(Environment environment, URL base, String location)
    throws ProcessingException, MalformedURLException, IOException;
}
