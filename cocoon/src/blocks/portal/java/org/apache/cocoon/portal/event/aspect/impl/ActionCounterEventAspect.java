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

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.portal.PortalService;
import org.apache.cocoon.portal.event.aspect.EventAspect;
import org.apache.cocoon.portal.event.aspect.EventAspectContext;

/**
 * This aspect "disables" the back button of the browser and tries to avoid
 * problems with the user browsing in multiple windows.
 * This event attaches a unique number to each request. For each user only the
 * current number is "active". Every request comming in containing an older
 * number is disregarded and therefore ignored.
 * WARNING: This aspect solves some problems while introducing new ones. Some
 *          features of the portal do NOT work when this aspect is used.
 *
 * @author <a href="mailto:cziegeler@s-und-n.de">Carsten Ziegeler</a>
 * @author <a href="mailto:volker.schmitt@basf-it-services.com">Volker Schmitt</a>
 *
 * @version CVS $Id: ActionCounterEventAspect.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class ActionCounterEventAspect
	extends AbstractLogEnabled
	implements EventAspect,
               ThreadSafe,
               Parameterizable {

    protected final static String ATTRIBUTE_NAME = ActionCounterEventAspect.class.getName();

    /** The name of the parameter to check */
    protected String parameterName;

	/* (non-Javadoc)
	 * @see org.apache.cocoon.portal.event.aspect.EventAspect#process(org.apache.cocoon.portal.event.aspect.EventAspectContext, org.apache.cocoon.portal.PortalService)
	 */
	public void process(EventAspectContext context, PortalService service) {
        final String requestParameterName = context.getAspectParameters().getParameter("parameter-name", this.parameterName);

        int actionCount;

        Integer actionValue = (Integer) service.getAttribute(ATTRIBUTE_NAME);
        if (null == actionValue) {
            actionValue = new Integer(0);
            service.setAttribute(ATTRIBUTE_NAME, actionValue);
            actionCount = 0;
        } else {
            actionCount = actionValue.intValue() + 1;
            service.setAttribute(ATTRIBUTE_NAME, new Integer(actionCount));
        }

        final Request request = ObjectModelHelper.getRequest( context.getObjectModel() );
        String value = request.getParameter( requestParameterName );
        if ( value != null && actionCount > 0) {
            // get number
            int number = 0;
            try {
                number = Integer.parseInt( value );
            } catch (Exception ignore) {
                number = -1;
            }

            if ( number == actionCount - 1) {
                // and invoke next one
                context.invokeNext( service );
            }
        }
        service.getComponentManager().getLinkService().addUniqueParameterToLink( requestParameterName, String.valueOf(actionCount));
        
        final Response response = ObjectModelHelper.getResponse( context.getObjectModel() );
        response.setHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Thu, 01 Jan 2000 00:00:00 GMT");
	}

    /* (non-Javadoc)
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters parameters) 
    throws ParameterException {
        this.parameterName = parameters.getParameter("parameter-name", "cocoon-portal-action");
    }
}
