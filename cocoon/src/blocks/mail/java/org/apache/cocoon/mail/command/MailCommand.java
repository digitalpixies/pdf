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
package org.apache.cocoon.mail.command;

import javax.mail.MessagingException;

/**
 *  This interface the basic contract of a MailCommand
 *
 * @author Bernhard Huber
 * @since 23 October 2002
 * @version $Id: MailCommand.java 468424 2006-10-27 15:44:53Z vgritsenko $
 */
public interface MailCommand {

    /**
     *  Execute this MailCommand
     *
     * @exception  MessagingException  Description of the Exception
     */
    void execute() throws MessagingException;
}
