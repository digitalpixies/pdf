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
package org.apache.cocoon.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimePart;

/**
 * @author Bernhard Huber
 * @since 26 October 2002
 * @version $Id: IndexerCtPref.java 468424 2006-10-27 15:44:53Z vgritsenko $
 */
public class IndexerCtPref implements ContentTypePreference {

    public int preference(MimePart part) {
        try {
            if (part.isMimeType("text/html")) {
                return 5;
            }
            if (part.isMimeType("text/*")) {
                return 10;
            }
            if (part.isMimeType("text")) {
                return 9;
            }
            return 0;
        } catch (MessagingException messagingexception) {
            return 0;
        }
    }
}
