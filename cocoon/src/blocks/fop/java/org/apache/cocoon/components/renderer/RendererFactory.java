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
package org.apache.cocoon.components.renderer;

import org.apache.fop.render.Renderer;

/**
 * Apache FOP Renderer factory.
 * When given a MIME type, find a Renderer which supports that MIME type.
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version CVS $Id: RendererFactory.java 433543 2006-08-22 06:22:54Z crossley $
 */
public interface RendererFactory {

  /**
   * Create a transcoder for a specified MIME type.
   * @param mimeType The MIME type of the destination format
   * @return A suitable renderer, or <code>null> if one cannot be found
   */
  Renderer createRenderer(String mimeType);
}
