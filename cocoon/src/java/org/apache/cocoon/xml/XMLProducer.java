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
package org.apache.cocoon.xml;



/**
 * This interfaces identifies classes that produce XML data, sending SAX
 * events to the configured <code>XMLConsumer</code>.
 * <br>
 * It's beyond the scope of this interface to specify a way in which the XML
 * data production is started.
 *
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 *         (Apache Software Foundation)
 * @version CVS $Id: XMLProducer.java 433543 2006-08-22 06:22:54Z crossley $
 */
public interface XMLProducer {

    /**
     * Set the <code>XMLConsumer</code> that will receive XML data.
     */
    void setConsumer(XMLConsumer consumer);


}
