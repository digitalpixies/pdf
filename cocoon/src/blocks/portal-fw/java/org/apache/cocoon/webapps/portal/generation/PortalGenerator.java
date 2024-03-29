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
package org.apache.cocoon.webapps.portal.generation;

import java.io.IOException;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.cocoon.webapps.portal.components.PortalManager;
import org.apache.cocoon.webapps.session.FormManager;
import org.xml.sax.SAXException;

/**
 * This generator generates the portal for the current user.
 *
 * @author <a href="mailto:cziegeler@s-und-n.de">Carsten Ziegeler</a>
 * @version CVS $Id: PortalGenerator.java 433543 2006-08-22 06:22:54Z crossley $
*/
public final class PortalGenerator
extends ServiceableGenerator {

    public void generate()
    throws IOException, SAXException, ProcessingException {

        PortalManager portal = null;
        FormManager formManager = null;
        try {
            formManager = (FormManager)this.manager.lookup(FormManager.ROLE);
            formManager.processInputFields();

            portal = (PortalManager) this.manager.lookup(PortalManager.ROLE);
            this.xmlConsumer.startDocument();

            final Request request = ObjectModelHelper.getRequest(this.objectModel);
            if (request.getSession(false) != null) {

                portal.showPortal(this.xmlConsumer, false, false);

            }
            this.xmlConsumer.endDocument();
        } catch (ServiceException ce) {
            throw new ProcessingException("Lookup of PortalManager failed.", ce);
        } finally {
            this.manager.release( formManager);
            this.manager.release(portal);
        }
    }

}
