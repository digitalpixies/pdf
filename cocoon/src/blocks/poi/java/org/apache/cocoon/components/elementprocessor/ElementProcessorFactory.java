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
package org.apache.cocoon.components.elementprocessor;

import org.apache.avalon.framework.component.Component;

/**
 * Create instances of specific ElementProcessor implementations to
 * handle specific XML elements and their content.
 *
 * @author Marc Johnson (marc_johnson27591@hotmail.com)
 * @version CVS $Id: ElementProcessorFactory.java 433543 2006-08-22 06:22:54Z crossley $
 */
public interface ElementProcessorFactory extends Component
{
    String ROLE = ElementProcessorFactory.class.getName();

    /**
     * Given an XML element name, create and return an appropriate
     * ElementProcessor.
     *
     * @param name element name
     *
     * @return the specified ElementProcessor
     *
     * @exception CannotCreateElementProcessorException if there is no
     *            ElementProcessor available for the specified name
     */

    public ElementProcessor createElementProcessor(final String name)
        throws CannotCreateElementProcessorException;

}       // end public interface ElementProcessorFactory
