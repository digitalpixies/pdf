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
package org.apache.cocoon.portal.layout.renderer.aspect.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.portal.PortalService;
import org.apache.cocoon.portal.acting.helpers.CopletEventDescription;
import org.apache.cocoon.portal.acting.helpers.LayoutEventDescription;
import org.apache.cocoon.portal.coplet.CopletInstanceData;
import org.apache.cocoon.portal.layout.Layout;
import org.apache.cocoon.portal.layout.impl.CopletLayout;
import org.apache.cocoon.portal.layout.renderer.aspect.RendererAspectContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/**
 * Save the current state of the layout into the session. Takes into account
 * state already present as request attribute. Includes aspect data and parameters
 * as well as aspect data and attributes of a coplet instance if layout is a 
 * coplet layout. This aspect does not add to the XML created by the renderer chain.
 * 
 * <h2>Example XML</h2>
 * <pre>
 *   &lt;!-- output from following renderers --&gt; 
 * </pre>
 * 
 * <h2>Applicable to:</h2>
 * <ul>
 *  <li>{@link org.apache.cocoon.portal.layout.Layout}</li>
 * </ul>
 * 
 * @author <a href="mailto:cziegeler@s-und-n.de">Carsten Ziegeler</a>
 * 
 * @version CVS $Id: HistoryAspect.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class HistoryAspect 
    extends AbstractAspect {

    /**
     * Add the values to the state
     * @param id
     * @param state
     * @param values
     * @param isCopletEvent tells if the event has to be a coplet event 
     */
    protected void addValues(String id, List state, Map values, String prefix,boolean isCopletEvent) {
        final Iterator iter = values.entrySet().iterator();
        while ( iter.hasNext() ) {
            final Map.Entry entry = (Map.Entry)iter.next();
            final String path = prefix + entry.getKey();
            if(!isCopletEvent){
            	LayoutEventDescription led = new LayoutEventDescription();
            	led.path = path;
            	led.layoutId = id;
            	led.data = entry.getValue();
            	state.add(led);
            }else{
            	CopletEventDescription ced = new CopletEventDescription();
            	ced.path = path;
            	ced.copletId = id;
            	ced.data = entry.getValue();
            	state.add(ced);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.portal.layout.renderer.aspect.RendererAspect#toSAX(org.apache.cocoon.portal.layout.renderer.aspect.RendererAspectContext, org.apache.cocoon.portal.layout.Layout, org.apache.cocoon.portal.PortalService, org.xml.sax.ContentHandler)
     */
    public void toSAX(RendererAspectContext context,
                    Layout layout,
                    PortalService service,
                    ContentHandler handler)
    throws SAXException {
        if ( layout.getId() != null ) {
            final Request request = ObjectModelHelper.getRequest(context.getObjectModel());
            final Session session = request.getSession(false);
            if ( session != null ) {
                List history = (List)session.getAttribute("portal-history");
                if ( history == null ) {
       
                    history = new ArrayList();
                }
                List state = (List)request.getAttribute("portal-history");
                if ( state == null ) {
                    state = new ArrayList();
                    request.setAttribute("portal-history", state);
                    history.add(state);
                }
                
                this.addValues(layout.getId(), state, layout.getAspectDatas(), "aspectDatas/",false);
                this.addValues(layout.getId(), state, layout.getParameters(), "parameters/",false);
                
                // are we a coplet layout
                if ( layout instanceof CopletLayout ) {
                    CopletInstanceData cid = ((CopletLayout)layout).getCopletInstanceData();
                    this.addValues(cid.getId(), state, cid.getAspectDatas(), "aspectDatas/",true);
                    this.addValues(cid.getId(), state, cid.getAttributes(), "attributes/",true);
                }
                session.setAttribute("portal-history", history);
            }
        }
        context.invokeNext(layout, service, handler);
    }

}
