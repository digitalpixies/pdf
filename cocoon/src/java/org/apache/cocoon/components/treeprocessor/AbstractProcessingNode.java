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
package org.apache.cocoon.components.treeprocessor;

import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.location.Location;

/**
 *
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @version CVS $Id: AbstractProcessingNode.java 446922 2006-09-16 19:24:36Z vgritsenko $
 */

public abstract class AbstractProcessingNode extends AbstractLogEnabled implements ProcessingNode {

    protected Location location = Location.UNKNOWN;

    /**
     * Get the <code>SourceResolver</code> in an object model.
     */
    protected static SourceResolver getSourceResolver(Map objectModel) {
        return (SourceResolver) objectModel.get(OBJECT_SOURCE_RESOLVER);
    }

    /**
     * Get the location of this node.
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Set the location of this node.
     */
    public void setLocation(Location location) {
        this.location = location;
    }
}
