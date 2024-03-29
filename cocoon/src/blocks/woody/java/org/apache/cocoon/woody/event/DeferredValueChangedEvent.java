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
package org.apache.cocoon.woody.event;

import org.apache.cocoon.woody.formmodel.Widget;

/**
 * A {@link ValueChangedEvent} that defers getting the new value from the widget
 * until it's actually requested.
 * <p>
 * This allows widget validity to be checked only if a listener actually uses the
 * value, thus avoiding unnecessary validation warnings when a user clicks an action.
 * 
 * @author <a href="http://www.apache.org/~sylvain/">Sylvain Wallez</a>
 * @version CVS $Id: DeferredValueChangedEvent.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class DeferredValueChangedEvent extends ValueChangedEvent {
    
    public DeferredValueChangedEvent(Widget source, Object oldValue) {
        super(source, oldValue, null);
    }
    
    public Object getNewValue() {
        return getSourceWidget().getValue();
    }
}
