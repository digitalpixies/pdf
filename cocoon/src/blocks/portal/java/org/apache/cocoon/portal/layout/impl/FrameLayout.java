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
package org.apache.cocoon.portal.layout.impl;

import org.apache.cocoon.portal.layout.AbstractLayout;

/**
 * A frame layout holds a source URI. The URI can be changed dynamically through
 * events. The URI may contain any URI that can be resolved by the Cocoon 
 * {@link org.apache.cocoon.environment.SourceResolver}.
 *
 * @author <a href="mailto:cziegeler@s-und-n.de">Carsten Ziegeler</a>
 * @author <a href="mailto:volker.schmitt@basf-it-services.com">Volker Schmitt</a>
 * 
 * @version CVS $Id: FrameLayout.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class FrameLayout extends AbstractLayout {
    
    private String source;
    
    /**
     * @return String
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source.
     * @param source The source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    protected Object clone() throws CloneNotSupportedException {
        FrameLayout clone = (FrameLayout)super.clone();
        
        clone.source = this.source;
        
        return clone;
    }
    
}
