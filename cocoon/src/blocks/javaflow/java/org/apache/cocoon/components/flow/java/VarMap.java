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
package org.apache.cocoon.components.flow.java;

import java.util.HashMap;
import java.util.Map;

/**
 * Simplified version of a map.
 *
 * @author <a href="mailto:stephan@apache.org">Stephan Michels</a>
 * @version CVS $Id: VarMap.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class VarMap {
    private HashMap map = new HashMap();

    public VarMap() {
    }

    public VarMap(String name, Object value) {
        add(name, value);
    }

    public VarMap(String name, int value) {
        add(name, value);
    }

    public VarMap(String name, long value) {
        add(name, value);
    }

    public VarMap(String name, float value) {
        add(name, value);
    }

    public VarMap(String name, double value) {
        add(name, value);
    }

    public VarMap add(String name, Object value) {
        map.put(name, value);
        return this;
    }

    public VarMap add(String name, int value) {
        add(name, new Integer(value));
        return this;
    }

    public VarMap add(String name, long value) {
        add(name, new Long(value));
        return this;
    }

    public VarMap add(String name, float value) {
        add(name, new Float(value));
        return this;
    }

    public VarMap add(String name, double value) {
        add(name, new Double(value));
        return this;
    }

    public Map getMap() {
        return map;
    }
}
