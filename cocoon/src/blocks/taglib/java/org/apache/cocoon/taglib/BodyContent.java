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
package org.apache.cocoon.taglib;

import org.apache.cocoon.xml.XMLConsumer;
import org.apache.cocoon.xml.SaxBuffer;

/**
 * @version CVS $Id: BodyContent.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class BodyContent {

    private SaxBuffer content;


    public BodyContent(SaxBuffer content, XMLConsumer consumer) {
        this.content = content;
    }

    public void clearBody() {
        this.content.recycle();
    }

    public SaxBuffer getContent() {
        return this.content;
    }

    public String getString() {
        return this.content.toString();
    }
}
