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
package org.apache.cocoon.components.web3;

import com.sap.mw.jco.JCO;

import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

import org.apache.avalon.framework.component.Component;

/**
 * The standard interface for Web3Producer.
 *
 * @author <a href="mailto:michael.gerzabek@at.efp.cc">Michael Gerzabek</a>
 * @since 2.1
 * @version CVS $Id: Web3Streamer.java 524407 2007-03-31 10:59:28Z joerg $
 */
public interface Web3Streamer extends Component {

    String ROLE = Web3Streamer.class.getName();

    void stream(JCO.Function function, 
        ContentHandler contentHandler) throws SAXException;
}
