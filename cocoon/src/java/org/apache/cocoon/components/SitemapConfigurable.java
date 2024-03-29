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
package org.apache.cocoon.components;

import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Objects implementing this marker interface can get a configuration
 * from the map:pipelines section of the sitemap when they are created.
 * Due to a problem in the component handling the
 * {@link #configure(SitemapConfigurationHolder)} method is actually called
 * each time the component is looked up!
 *
 * @since 2.1
 * @deprecated This functionality will be replaced in 2.2 with a more flexible
 *             configuration mechanism.
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Id: SitemapConfigurable.java 433543 2006-08-22 06:22:54Z crossley $
 */
public interface SitemapConfigurable {

    /**
     * Set the <code>Configuration</code>.
     */
    void configure(SitemapConfigurationHolder holder)
    throws ConfigurationException;
}
