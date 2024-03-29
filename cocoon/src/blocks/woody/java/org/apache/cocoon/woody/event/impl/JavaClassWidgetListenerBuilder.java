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
package org.apache.cocoon.woody.event.impl;

import org.apache.cocoon.util.ClassUtils;
import org.apache.cocoon.woody.event.WidgetListener;
import org.apache.cocoon.woody.event.WidgetListenerBuilder;
import org.apache.cocoon.woody.util.DomHelper;
import org.w3c.dom.Element;

/**
 * A {@link WidgetListenerBuilder} that creates java classes.
 * <p>
 * The syntax for this listener is as follows :<br/>
 * <pre>
 *   &lt;java class="com.my.SuperListener"/&gt;
 * </pre>
 *
 * @author <a href="http://www.apache.org/~sylvain/">Sylvain Wallez</a>
 * @version CVS $Id: JavaClassWidgetListenerBuilder.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class JavaClassWidgetListenerBuilder implements WidgetListenerBuilder {

    public static final JavaClassWidgetListenerBuilder INSTANCE = new JavaClassWidgetListenerBuilder();

    public WidgetListener buildListener(Element element, Class listenerClass) throws Exception {

        String name = DomHelper.getAttribute(element, "class");

        Object listener = ClassUtils.newInstance(name);
        if (listenerClass.isAssignableFrom(listener.getClass())) {
            // FIXME : apply filecyclehelper
            return (WidgetListener)listener;
        } else {
            throw new Exception("Class " + listener.getClass() + " is not a " + listenerClass);
        }
    }
}