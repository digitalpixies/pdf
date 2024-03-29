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
package org.apache.cocoon.portal.event.aspect.impl;

import org.apache.cocoon.portal.event.Event;
import org.apache.cocoon.portal.event.EventManager;
import org.apache.cocoon.portal.event.impl.ChangeAspectDataEvent;
import org.apache.cocoon.portal.layout.Layout;
import org.apache.cocoon.portal.layout.impl.FrameLayout;

/**
 *
 * @author <a href="mailto:juergen.seitz@basf-it-services.com">J&uuml;rgen Seitz</a>
 * @author <a href="mailto:cziegeler@s-und-n.de">Carsten Ziegeler</a>
 * @author <a href="mailto:volker.schmitt@basf-it-services.com">Volker Schmitt</a>
 * 
 * @version CVS $Id: FrameEventAspect.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class FrameEventAspect extends AbstractContentEventAspect {

    protected String getRequestParameterName() {
        // TODO - make this configurable
        return "frame";
    }

    protected int getRequiredValueCount() {
        return 3;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.portal.event.aspect.impl.AbstractContentEventAspect#publish(org.apache.cocoon.portal.event.Publisher, org.apache.cocoon.portal.layout.Layout, java.lang.String[])
     */
    protected void publish(EventManager publisher,
                             Layout layout,
                             String[] values) {
        if (layout instanceof FrameLayout) {
            final Event e = new ChangeAspectDataEvent(layout, "frame", values[2]);
            publisher.send(e);
        } else {
            this.getLogger().warn(
                "the configured layout: "
                    + layout.getName()
                    + " is not a FrameLayout.");
        }
    }

}
