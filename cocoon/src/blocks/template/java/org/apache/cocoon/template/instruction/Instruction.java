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
package org.apache.cocoon.template.instruction;

import org.apache.cocoon.template.script.event.EndInstruction;
import org.apache.cocoon.template.script.event.Event;
import org.apache.cocoon.template.script.event.StartElement;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * @version $Id: Instruction.java 449189 2006-09-23 06:52:29Z crossley $
 */
public abstract class Instruction extends Event {

    protected final StartElement startElement;
    private EndInstruction endInstruction;

    public Instruction(Locator locator) {
        super(locator);
        startElement = null;
    }

    public Instruction(StartElement startElement) {
        super(startElement.getLocation());
        this.startElement = startElement;
    }

    public EndInstruction getEndInstruction() {
        return endInstruction;
    }

    public void setEndInstruction(EndInstruction endInstruction) {
        this.endInstruction = endInstruction;
    }

    public void endNotify() throws SAXException {
        return;
    }

    public StartElement getStartElement() {
        return startElement;
    }
}
