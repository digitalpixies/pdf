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
package org.apache.cocoon.components.parser;

import org.apache.avalon.framework.component.Component;
import org.apache.cocoon.xml.XMLProducer;
import org.apache.cocoon.xml.dom.DOMFactory;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import java.io.IOException;

/**
 * @deprecated The Avalon XML Parser is now used inside Cocoon. This role
 *             will be removed in future releases.
 *
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 *         (Apache Software Foundation)
 * @version CVS $Id: Parser.java 433543 2006-08-22 06:22:54Z crossley $
 */
public interface Parser extends Component, XMLProducer, DOMFactory {

    String ROLE = Parser.class.getName();

    void setContentHandler(ContentHandler contentHandler);

    void setLexicalHandler(LexicalHandler lexicalHandler);

    void parse(InputSource in) throws SAXException, IOException;

    Document parseDocument(InputSource in) throws SAXException, IOException;
}
