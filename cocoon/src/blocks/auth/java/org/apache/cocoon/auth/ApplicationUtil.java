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
package org.apache.cocoon.auth;

import java.util.Map;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;


/**
 * Utility class that can be used from flow script to access the different
 * application functions of Cocoon Authentication.
 * The easiest way to use this class in flow script is to create an instance
 * using cocoon.createObject():
 * var util = cocoon.createObject("org.apache.cocoon.auth.ApplicationUtil");
 * and then you can invoke one of the instance methods like
 * var user = util.getUser();
 *
 * @version $Id: ApplicationUtil.java 502601 2007-02-02 13:15:47Z cziegeler $
*/
public class ApplicationUtil
    implements Contextualizable {

    /** The Avalon context. */
    protected Context context;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(final Context aContext) throws ContextException {
        this.context = aContext;
    }

    /**
     * Return the current user.
     * @param objectModel The object model of the current request.
     * @return The current user or null.
     */
    public static User getUser(final Map objectModel) {
        return (User)objectModel.get(ApplicationManager.USER);
    }

    /**
     * Return the current application.
     * @param objectModel The object model of the current request.
     * @return The current application or null.
     */
    public static Application getApplication(final Map objectModel) {
        return (Application)objectModel.get(ApplicationManager.APPLICATION);
    }

    /**
     * Return the current user data.
     * @param objectModel The object model of the current request.
     * @return The current user data or null.
     */
    public static Object getData(final Map objectModel) {
        return objectModel.get(ApplicationManager.APPLICATION_DATA);
    }

    /**
     * Checks if the user has the given role.
     * First {@link User#isUserInRole(String)} is invoked. If the result is false,
     * the {@link Request#isUserInRole(java.lang.String)} is called.
     *
     * @param user The user to test.
     * @param role The role.
     * @param objectModel The Cocoon object model.
     * @return This returns true, if the user has the role; otherwise false is returned.
     */
    public static boolean isUserInRole(final User user, final String role, final Map objectModel) {
        boolean result = user.isUserInRole(role);
        if ( !result ) {
            final Request req = ObjectModelHelper.getRequest(objectModel);
            result = req.isUserInRole(role);
        }
        return result;
    }

    /**
     * Return the current user.
     * @return The current user or null.
     */
    public User getUser() {
        final Map objectModel = ContextHelper.getObjectModel(this.context);
        return (User)objectModel.get(ApplicationManager.USER);
    }

    /**
     * Return the current application.
     * @return The current application or null.
     */
    public Application getApplication() {
        final Map objectModel = ContextHelper.getObjectModel(this.context);
        return (Application)objectModel.get(ApplicationManager.APPLICATION);
    }

    /**
     * Return the current user data.
     * @return The current user data or null.
     */
    public Object getData() {
        final Map objectModel = ContextHelper.getObjectModel(this.context);
        return objectModel.get(ApplicationManager.APPLICATION_DATA);
    }

    /**
     * Checks if the user has the given role.
     * First {@link User#isUserInRole(String)} is invoked. If the result is false,
     * the {@link Request#isUserInRole(java.lang.String)} is called.
     *
     * @param user The user to test.
     * @param role The role.
     * @return This returns true, if the user has the role; otherwise false is returned.
     */
    public boolean isUserInRole(final User user, final String role) {
        final Map objectModel = ContextHelper.getObjectModel(this.context);
        return isUserInRole(user, role, objectModel);
    }
}
