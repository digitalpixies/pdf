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
package org.apache.cocoon.forms.binding;

import org.apache.cocoon.forms.util.DomHelper;

import org.w3c.dom.Element;

/**
 * ClassJXPathBindingBuilder provides a helper class for the Factory
 * implemented in {@link JXPathBindingManager} that helps construct the
 * actual {@link ClassJXPathBinding} out of the configuration in the
 * provided configElement which looks like:
 * <pre><code>
 * &lt;fb:class id="<i>widget-id</i>"&gt;
 *   &lt;fb:field id="<i>sub-widget-id</i>" path="<i>relative-xpath</i>"
 *       direction="<i>load|save</i>" lenient="<i>true|false</i>"/&gt;
 * &lt;/fb:class&gt;
 * </code></pre>
 *
 * @version $Id: ClassJXPathBindingBuilder.java 517733 2007-03-13 15:37:22Z vgritsenko $
 */
public class ClassJXPathBindingBuilder extends JXPathBindingBuilderBase {

    public JXPathBindingBase buildBinding(Element bindingElm, JXPathBindingManager.Assistant assistant)
    throws BindingException {
        try {
            String widgetId = DomHelper.getAttribute(bindingElm, "id", null);
            CommonAttributes commonAtts = JXPathBindingBuilderBase.getCommonAttributes(bindingElm);

            JXPathBindingBase[] childBindings = new JXPathBindingBase[0];

            // do inheritance
            ClassJXPathBinding otherBinding = (ClassJXPathBinding)assistant.getContext().getSuperBinding();
            if (otherBinding != null) {
                childBindings = otherBinding.getChildBindings();
                commonAtts = JXPathBindingBuilderBase.mergeCommonAttributes(otherBinding.getCommonAtts(), commonAtts);

                if (widgetId == null) {
                    widgetId = otherBinding.getId();
                }
            }

            childBindings = assistant.makeChildBindings(bindingElm,childBindings);

            return new ClassJXPathBinding(commonAtts, widgetId, childBindings);
        } catch (BindingException e) {
            throw e;
        } catch (Exception e) {
            throw new BindingException("Error building class binding", e,
                                       DomHelper.getLocationObject(bindingElm));
        }
    }
}
