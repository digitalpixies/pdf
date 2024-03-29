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
package org.apache.cocoon.servlet.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * This class represents a file part parsed from a http post stream.
 *
 * @author <a href="mailto:j.tervoorde@home.nl">Jeroen ter Voorde</a>
 * @version CVS $Id: PartInMemory.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class PartInMemory extends Part {

    private InputStream in;

    private int size;

    /**
     * Constructor PartInMemory
     *
     * @param headers
     * @param in
     * @param size
     */
    public PartInMemory(Map headers, InputStream in, int size) {
        super(headers);
        this.in = in;
        this.size = size;
    }

    /**
     * Returns the filename
     */
    public String getFileName() {
        return (String) headers.get("filename");
    }

    /**
     * Returns the filesize in bytes
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Returns a (ByteArray)InputStream containing the file data
     *
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException {
        if (this.in != null) {
            return this.in;
        } else {
            throw new IllegalStateException("This part has already been disposed.");
        }
    }
    
    /**
     * Clean the byte array content buffer holding part data
     */
    public void dispose() {
        this.in = null;
    }
}
