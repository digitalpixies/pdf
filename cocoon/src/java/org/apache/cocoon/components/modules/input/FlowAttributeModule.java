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
package org.apache.cocoon.components.modules.input;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.flow.FlowHelper;

import java.util.Map;

/**
 * FlowAttributeModule provides access to the flow business object properties.
 * To get access to the properties use XPath syntax. If requested
 * object is not found then an exception will be thrown.
 *
 * @author <a href="mailto:danielf@nada.kth.se">Daniel Fagerstrom</a>
 * @version $Id: FlowAttributeModule.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class FlowAttributeModule extends AbstractJXPathModule
    implements ThreadSafe {

    protected Object getContextObject(Configuration modeConf,
                                      Map objectModel) {

        return FlowHelper.getContextObject(objectModel);
    }
}
