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
package org.apache.cocoon.caching;

/**
 * A validation object for time-stamps.
 * This is might be the most used CacheValidity object.
 *
 * @deprecated Use the Avalon Excalibur SourceValidity implementations instead
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Id: TimeStampCacheValidity.java 433543 2006-08-22 06:22:54Z crossley $
 */
public final class TimeStampCacheValidity
implements CacheValidity {

    private long timeStamp;

    public TimeStampCacheValidity(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isValid(CacheValidity validity) {
        if (validity instanceof TimeStampCacheValidity) {
            return this.timeStamp == ((TimeStampCacheValidity)validity).getTimeStamp();
        }
        return false;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public String toString() {
        return "TimeStamp Validity[" + this.timeStamp + ']';
    }
}
