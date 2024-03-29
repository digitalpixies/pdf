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
package org.apache.cocoon.auth.impl;

import java.util.Map;

import org.apache.cocoon.auth.AbstractSecurityHandler;
import org.apache.cocoon.auth.StandardUser;
import org.apache.cocoon.auth.User;

/**
 * This security handlers doesn't check any credentials of the user.
 * It just creates a new user object.
 *
 * @version $Id: AnonymousSecurityHandler.java 433543 2006-08-22 06:22:54Z crossley $
*/
public class AnonymousSecurityHandler
    extends AbstractSecurityHandler {

    /** Counter to generate the anonymous user object. */
    protected long number = 1;

    /**
     * @see org.apache.cocoon.auth.SecurityHandler#login(java.util.Map)
     */
    public synchronized User login(final Map loginContext) throws Exception {
        final User user = new StandardUser("anonymous"+this.number);
        this.number++;
        return user;
    }

    /**
     * @see org.apache.cocoon.auth.SecurityHandler#logout(java.util.Map, org.apache.cocoon.auth.User)
     */
    public void logout(final Map context, final User user) {
        // nothing to do
    }
}
