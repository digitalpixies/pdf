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
package org.apache.cocoon.selection;

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.ObjectModelHelper;

/**
 * <p>The {@link RegexpHeaderSelector} class defines a selector matching
 * specific headers to configured regular-expression patterns.</p> 
 *
 * <p>The configuration of an {@link RegexpHeaderSelector} follows exactly
 * what has been outlined in {@link AbstractRegexpSelector} regarding regular
 * expression patterns, and additionally it requires an extra configuration element
 * specifying the header whose value needs to be matched:</p>
 * 
 * <pre>
 * &lt;map:components&gt;
 *   ...
 *   &lt;map:selectors default="..."&gt;
 *     &lt;map:selector name="..." src="org.apache.cocoon.selection...."&gt;
 *       &lt;pattern name="empty"&gt;^$&lt;/pattern&gt;
 *       &lt;pattern name="number"&gt;^[0-9]+$&lt;/pattern&gt;
 *       &lt;pattern name="string"&gt;^.+$&lt;/pattern&gt;
 *       &lt;header-name&gt;...&lt;/header-name&gt;
 *     &lt;/map:selector&gt;
 *  &lt;/map:selectors&gt;
 * &lt;/map:components&gt;
 * </pre>
 * 
 * <p>If not configured, or if it needs to be overriddent, the header name can
 * also be specified as a <code>&lt;map:parameter&nbsp;.../&gt;</code> inside the
 * pipeline itself.</p>
 * 
 * @version CVS $Id: RegexpHeaderSelector.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class RegexpHeaderSelector extends AbstractRegexpSelector {

    /** <p>The name of the header to work on.</p> */
    protected String headerName;

    /**
     * <p>Create a new {@link RegexpHeaderSelector} instance.</p>
     */
    public RegexpHeaderSelector() {
        super();
    }

    /**
     * <p>Configure this instance parsing all regular expression patterns and
     * storing the header name upon which selection occurs.</p>
     * 
     * @param configuration the {@link Configuration} instance where configured
     *                      patterns are defined.
     * @throws ConfigurationException if one of the regular-expression to configure
     *                                could not be compiled.
     */
    public void configure(Configuration configuration)
    throws ConfigurationException {
        super.configure(configuration);
        this.headerName = configuration.getChild("header-name").getValue(null);
    }

    /**
     * <p>Return the value of the header identified by the configured header
     * name, if any.</p>
     * 
     * @param objectModel the Cocoon object model.
     * @param parameters the {@link Parameters} associated with the pipeline.
     * @return the value of the configured request parameter or <b>null</b>.
     */
    public Object getSelectorContext(Map objectModel, Parameters parameters) {
        String name = parameters.getParameter("header-name", this.headerName);
        if (name == null) {
            this.getLogger().warn("No header name given -- failing.");
            return null;
        }
        return ObjectModelHelper.getRequest(objectModel).getHeader(name);
    }

    /**
     * Selectors test pattern against some objects in a <code>Map</code>
     * model and signals success with the returned boolean value
     * @param expr        The expression to test.
     * @return Signals successful test.
     */
    public boolean select(String expr, Map objectModel, Parameters params) {
	// Inform proxies that response varies with the selector header
	String name = params.getParameter("header-name", this.headerName);
	if (name != null)
	    ObjectModelHelper.getResponse(objectModel).addHeader("Vary", name);
        return select(expr, getSelectorContext(objectModel, params));
    }

}
