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
package org.apache.cocoon.portal.pluto.om.common;

import org.apache.pluto.om.common.SecurityRole;
import org.apache.pluto.util.StringUtils;

/**
 * 
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * 
 * @version CVS $Id: SecurityRoleImpl.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class SecurityRoleImpl implements SecurityRole,  java.io.Serializable {

    private String description;
    private String roleName;

    public SecurityRoleImpl() {
        // nothing to do 
    }

    // SecurityRole implementation.

    public String getDescription() {
        return description;
    }

    public String getRoleName() {
        return roleName;
    }

    // additional methods.

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String toString() {
        return toString(0);
    }

    public String toString(int indent) {
        StringBuffer buffer = new StringBuffer(50);
        StringUtils.newLine(buffer,indent);
        buffer.append(getClass().toString());
        buffer.append(": role-name='");
        buffer.append(roleName);
        buffer.append("', description='");
        buffer.append(description);
        buffer.append("'");
        return buffer.toString();
    }

}
