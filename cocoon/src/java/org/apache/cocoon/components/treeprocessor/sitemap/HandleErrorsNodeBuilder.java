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
package org.apache.cocoon.components.treeprocessor.sitemap;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.cocoon.components.treeprocessor.AbstractParentProcessingNodeBuilder;
import org.apache.cocoon.components.treeprocessor.ProcessingNode;
import org.apache.cocoon.util.Deprecation;

/**
 * Builds a &lt;map:handle-errors&gt;
 *
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @version $Id: HandleErrorsNodeBuilder.java 606905 2007-12-26 14:52:33Z vgritsenko $
 */
public class HandleErrorsNodeBuilder extends AbstractParentProcessingNodeBuilder
                                     implements ThreadSafe {

    /** This builder has no parameters -- return <code>false</code> */
    protected boolean hasParameters() {
        return false;
    }

    public ProcessingNode buildNode(Configuration config) throws Exception {

        int type = config.getAttributeAsInteger("type", -1);
        if (type != -1) {
            Deprecation.logger.warn("Attribute 'type' on <map:handle-errors> is deprecated. " +
                                    "Omit type attribute and use <map:select type='exception'> instead, at " + config.getLocation());
        }

        HandleErrorsNode node = new HandleErrorsNode(type,
                                                     config.getAttribute("when", "external"));
        this.treeBuilder.setupNode(node, config);

        // Set a flag that will prevent redirects
        ((SitemapLanguage) this.treeBuilder).setBuildingErrorHandler(true);
        try {
            // Get all children
            node.setChildren(buildChildNodes(config));
        } finally {
            // And clear the flag
            ((SitemapLanguage) this.treeBuilder).setBuildingErrorHandler(false);
        }

        return node;
    }
}
