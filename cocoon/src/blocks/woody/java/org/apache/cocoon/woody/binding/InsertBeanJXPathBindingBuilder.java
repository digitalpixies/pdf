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
package org.apache.cocoon.woody.binding;

import org.apache.cocoon.woody.binding.JXPathBindingManager.Assistant;
import org.apache.cocoon.woody.util.DomHelper;
import org.w3c.dom.Element;

/**
 * InsertBeanJXPathBindingBuilder provides a helper class for the Factory
 * implemented in {@link JXPathBindingManager} that helps construct the
 * actual {@link InsertBeanJXPathBinding} out of the configuration in the
 * provided configElement which looks like:
 * <pre><code>
 * &lt;wb:insert-bean classname="..child-bean-class.." addmethod="..method-to-add.."/&gt;
 * </code></pre>
 *
 * @version CVS $Id: InsertBeanJXPathBindingBuilder.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class InsertBeanJXPathBindingBuilder extends JXPathBindingBuilderBase {

    /**
     * Creates an instance of {@link InsertBeanJXPathBinding} configured
     * with the nested template of the bindingElm.
     */
    public JXPathBindingBase buildBinding(Element bindingElm, Assistant assistant) throws BindingException {

        try {
            CommonAttributes commonAtts = JXPathBindingBuilderBase.getCommonAttributes(bindingElm);

            String className =
                DomHelper.getAttribute(bindingElm, "classname");
            String addMethod =
                DomHelper.getAttribute(bindingElm, "addmethod");

            return new InsertBeanJXPathBinding(commonAtts, className, addMethod);
        } catch (BindingException e) {
            throw e;
        } catch (Exception e) {
            throw new BindingException("Error building a insert-bean binding defined at " + DomHelper.getLocation(bindingElm), e);
        }
    }
}
