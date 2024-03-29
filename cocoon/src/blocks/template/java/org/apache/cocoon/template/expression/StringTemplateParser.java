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
package org.apache.cocoon.template.expression;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.List;
import java.io.Reader;

/**
 * @version $Id: StringTemplateParser.java 449189 2006-09-23 06:52:29Z crossley $
 */
public interface StringTemplateParser {

    String ROLE = StringTemplateParser.class.getName();

    /**
     * Compile a boolean expression (returns either a Compiled Expression or a
     * Boolean literal)
     */
    JXTExpression compileBoolean(String val, String msg, Locator location) throws SAXException;

    /**
     * Compile an integer expression (returns either a Compiled Expression or an
     * Integer literal)
     */
    JXTExpression compileInt(String val, String msg, Locator location) throws SAXException;

    JXTExpression compileExpr(String inStr, String errorPrefix, Locator location) throws SAXParseException;

    /**
     * Parse a set of expressions spaced with literals
     */
    List parseSubstitutions(Reader in, String errorPrefix, Locator location) throws SAXParseException;

    List parseSubstitutions(Reader in) throws Exception;
}
