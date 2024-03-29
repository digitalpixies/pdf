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
package org.apache.cocoon.forms.formmodel;

import org.apache.cocoon.forms.event.ActionEvent;
import org.apache.cocoon.forms.event.ActionListener;
import org.apache.cocoon.forms.event.WidgetEventMulticaster;

/**
 * The {@link WidgetDefinition} part of an ImageMap widget, see {@link ImageMap}
 * for more information.
 *
 * @version $Id: ImageMapDefinition.java 462520 2006-10-10 19:39:14Z vgritsenko $
 * @since 2.1.8
 */
public class ImageMapDefinition extends AbstractWidgetDefinition {

    private String actionCommand;
    private String imgURI; // URI of widget's image
    private ActionListener listener;

    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public Widget createInstance() {
        return new ImageMap(this);
    }

    public void addActionListener(ActionListener listener) {
        checkMutable();
        this.listener = WidgetEventMulticaster.add(this.listener, listener);
    }

    public void fireActionEvent(ActionEvent event) {
        if (this.listener != null) {
            this.listener.actionPerformed(event);
        }
    }

    public boolean hasActionListeners() {
        return this.listener != null;
    }

    // Set/get image's URI
    public String getImageURI() {
        return imgURI;
    }

    public void setImageURI(String newImgURI) {
        this.imgURI= newImgURI;
    }
}
