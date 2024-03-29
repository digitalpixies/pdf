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

package org.apache.cocoon.components.modules.input;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * RequestParameterModule accesses request parameters. If the
 * parameter name contains an askerisk "*" this is considered a
 * wildcard and all parameters that would match this wildcard are
 * considered to be part of an array of that name for
 * getAttributeValues. Only one "*" is allowed. Wildcard matches take
 * precedence over real arrays. In that case only the first value of
 * such array is returned.
 *
 * @author <a href="mailto:haul@apache.org">Christian Haul</a>
 * @version $Id: RequestParameterModule.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class RequestParameterModule extends AbstractInputModule implements ThreadSafe {

    public Object getAttribute( String name, Configuration modeConf, Map objectModel ) throws ConfigurationException {

        String pname = (String) this.settings.get("parameter", name);
        if ( modeConf != null ) {
            pname = modeConf.getAttribute( "parameter", pname );
            // preferred
            pname = modeConf.getChild("parameter").getValue(pname);
        }
        return ObjectModelHelper.getRequest(objectModel).getParameter( pname );
    }


    public Iterator getAttributeNames( Configuration modeConf, Map objectModel ) throws ConfigurationException {

        return new IteratorHelper(ObjectModelHelper.getRequest(objectModel).getParameterNames());
    }


    public Object[] getAttributeValues( String name, Configuration modeConf, Map objectModel )
        throws ConfigurationException {

        Request request = ObjectModelHelper.getRequest(objectModel);
        String wildcard = (String) this.settings.get("parameter", name);
        if ( modeConf != null ) {
            wildcard = modeConf.getAttribute( "parameter", wildcard );
            // preferred
            wildcard = modeConf.getChild("parameter").getValue(wildcard);
        }
        int wildcardIndex = wildcard.indexOf( "*" );
        if ( wildcardIndex != -1 ) {
            // "*" contained in parameter name => combine all
            // parameters' values that match prefix, suffix

            // split the parameter's name so that the "*" could be
            // determined by looking at the parameters' names that
            // start with the prefix and end with the suffix
            //
            String prefix = wildcard.substring( 0, wildcardIndex );
            String suffix;
            if ( wildcard.length() >= wildcardIndex + 1 ) {
                suffix = wildcard.substring( wildcardIndex + 1 );
            } else {
                suffix = "";
            }
            SortedSet names = new TreeSet();
            Enumeration allNames = request.getParameterNames();

            while (allNames.hasMoreElements()) {
                String pname = (String) allNames.nextElement();
                if ( pname.startsWith( prefix ) && pname.endsWith( suffix ) ) {
                    names.add(pname);
                }
            }

            List values = new LinkedList();
            Iterator j = names.iterator();
            while (j.hasNext()){
                String pname = (String) j.next();
                values.add( request.getParameter( pname ) );
            }

            return values.toArray();

        } else {
            // no "*" in parameter name => just return all values of
            // this one parameter.

            return request.getParameterValues( wildcard );

        }

    }

}
