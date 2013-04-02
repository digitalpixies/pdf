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
package org.apache.cocoon.profiling.statistics;

/**
 * A simple statistics object.
 * A statistics object stores the duration for an invocation
 * of a defined category.
 *
 * @version $Id: Statistics.java 485636 2006-12-11 12:20:15Z cziegeler $
 * @since 2.1.10
 */
public interface Statistics {

    /**
     * The duration of the invocation in milliseconds.
     */
    long getDuration();

    /**
     * The category for the invocation.
     */
    String getCategory();
}
