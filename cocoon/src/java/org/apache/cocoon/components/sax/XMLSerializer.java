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
package org.apache.cocoon.components.sax;

import org.apache.avalon.framework.component.Component;
import org.apache.cocoon.xml.XMLConsumer;

/**
 * This interfaces identifies classes that serialize XML data, receiving
 * notification of SAX events.
 * <br>
 * It's beyond the scope of this interface to specify the format for
 * the serialized data.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Id: XMLSerializer.java 433543 2006-08-22 06:22:54Z crossley $
 */
public interface XMLSerializer extends XMLConsumer, Component {

    String ROLE = XMLSerializer.class.getName();
    /**
     * Get the serialized xml data
     *
     * @return The serialized xml data.
     */
    Object getSAXFragment();
}
