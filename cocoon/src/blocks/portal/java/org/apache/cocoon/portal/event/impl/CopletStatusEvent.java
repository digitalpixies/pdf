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
package org.apache.cocoon.portal.event.impl;

import org.apache.cocoon.portal.coplet.CopletInstanceData;
import org.apache.cocoon.portal.event.ComparableEvent;
import org.apache.cocoon.portal.event.CopletInstanceEvent;
import org.apache.cocoon.portal.event.Event;

/**
 * EventSource: copletID
 *
 * @author <a href="mailto:cziegeler@s-und-n.de">Carsten Ziegeler</a>
 * @author <a href="mailto:volker.schmitt@basf-it-services.com">Volker Schmitt</a>
 * 
 * @version CVS $Id: CopletStatusEvent.java 433543 2006-08-22 06:22:54Z crossley $
 */
public abstract class CopletStatusEvent
    implements Event, ComparableEvent, CopletInstanceEvent {

    protected CopletInstanceData coplet;

    public CopletInstanceData getCopletInstanceData() {
        return this.coplet;
    }

    public void setCopletInstanceData(CopletInstanceData data) {
        this.coplet = data;
    }

    public boolean equalsEvent(ComparableEvent event) {
        if (event instanceof CopletStatusEvent) {
            return ((CopletStatusEvent)event).getCopletInstanceData().getId().equals( this.coplet.getId() );
        }
        return false;
    }

    /**
     * @see org.apache.cocoon.portal.event.ActionEvent#getTarget()
     */
    public Object getTarget() {
        return this.coplet;
    }

}
