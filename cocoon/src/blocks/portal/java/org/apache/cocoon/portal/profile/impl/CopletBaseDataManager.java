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
package org.apache.cocoon.portal.profile.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.cocoon.portal.coplet.CopletBaseData;

/**
 * Holds instances of CopletBaseData.
 *
 * @author <a href="mailto:bluetkemeier@s-und-n.de">Bj&ouml;rn L&uuml;tkemeier</a>
 * 
 * @version CVS $Id: CopletBaseDataManager.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class CopletBaseDataManager {

	/**
	 * The coplet base data instances.
	 */
	private Map copletBaseData = new HashMap();
	
	/**
	 * Gets all coplet base data.
	 */
	public Map getCopletBaseData() {
		return this.copletBaseData;
	}

	/**
	 * Gets the specified coplet base data. 
	 */
	public CopletBaseData getCopletBaseData(String name) {
		return (CopletBaseData)this.copletBaseData.get(name);
	}
	
	/**
	 * Puts the specified coplet base data to the manager.
	 */
	public void putCopletBaseData(CopletBaseData data) {
		this.copletBaseData.put(data.getId(), data);
	}
}
