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
package org.apache.cocoon.components.treeprocessor;

/**
 * A named container node that just invokes its children.
 *
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @version CVS $Id: NamedContainerNode.java 433543 2006-08-22 06:22:54Z crossley $
 */

public class NamedContainerNode extends ContainerNode implements NamedProcessingNode {

    private String name;

    public NamedContainerNode(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
