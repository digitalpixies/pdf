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
package org.apache.cocoon.webapps.session.transformation;

/**
 * This is the session transformer which is only for compatibility.
 * It inherits from the ConnectTransformer which inherits from
 * the SessionPostTransformer whicb inherits from the SessionPreTransformer.
 * So those three do all the work
 *
 * @author <a href="mailto:cziegeler@s-und-n.de">Carsten Ziegeler</a>
 * @deprecated This block is deprecated and will be removed in future versions.
 * @version CVS $Id: SessionTransformer.java 433543 2006-08-22 06:22:54Z crossley $
*/
public class SessionTransformer
extends SessionPostTransformer {

}
