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
package org.apache.cocoon.transformation.helpers;

import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

/**
 * This action starts the preemptive loader and runs forever.
 * 
 *  @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 *  @version CVS $Id: PreemptiveLoaderAction.java 433543 2006-08-22 06:22:54Z crossley $
 *  @since   2.1
 */
public class PreemptiveLoaderAction 
    extends ServiceableAction
    implements ThreadSafe {

    /**
     * This action starts the preemptive loading
     * It runs forever and is stopped by the {@link DefaultIncludeCacheManager}.
     * @see org.apache.cocoon.acting.Action#act(Redirector, SourceResolver, Map, String, Parameters)
     */
    public Map act(Redirector redirector,
                    SourceResolver resolver,
                    Map objectModel,
                    String source,
                    Parameters parameters)
    throws Exception {
        PreemptiveLoader loader = PreemptiveLoader.getInstance();
        if (!loader.alive) {
            loader.process(this.manager, resolver, this.getLogger());
            return EMPTY_MAP;
        }
        return null;
    }
}
