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
package org.apache.cocoon.matching;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.sitemap.PatternException;
import org.apache.cocoon.sitemap.SitemapParameters;
import org.apache.cocoon.util.WildcardMatcherHelper;

import java.util.Map;

/**
 * Base class for wildcard matchers
 *
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @version $Id: AbstractWildcardMatcher.java 433543 2006-08-22 06:22:54Z crossley $
 */

public abstract class AbstractWildcardMatcher
    extends AbstractLogEnabled
    implements Matcher, ThreadSafe {

    /**
     * Match the prepared pattern against the result of {@link #getMatchString(Map, Parameters)}.
     * @see org.apache.cocoon.matching.AbstractPreparableMatcher#match(java.lang.String, java.util.Map, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map match(String pattern, Map objectModel, Parameters parameters) throws PatternException {
        if (pattern == null) {
            throw new PatternException("A pattern is needed at " +
                    SitemapParameters.getLocation(parameters));
        }

        final String match = this.getMatchString(objectModel, parameters);

        if (match == null) {
            return null;
        }

        return WildcardMatcherHelper.match(pattern, match);
    }

    /**
     * Get the string to test against the wildcard expression. To be defined
     * by concrete subclasses.
     */
    protected abstract String getMatchString(Map objectModel, Parameters parameters);
}
