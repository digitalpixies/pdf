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
import java.io.PushbackInputStream;

/**
 * Utility class for MultipartParser. Divides the inputstream into parts
 * separated by a given boundary.
 *
 * A newline is espected after each boundary and is parsed away.
 * @author <a href="mailto:j.tervoorde@home.nl">Jeroen ter Voorde</a>
 * @version CVS $Id: TokenStream.java 433543 2006-08-22 06:22:54Z crossley $
 */
class TokenStream extends PushbackInputStream {

    /**
     * Initial state, no boundary has been set.
     */
    public static final int STATE_NOBOUNDARY = -1;

    /**
     * Fully read a part, now at the beginning of a new part
     */
    public static final int STATE_NEXTPART = -2;

    /**
     * Read last boundary, end of multipart block
     */
    public static final int STATE_ENDMULTIPART = -3;

    /**
     * End of stream, this should not happen
     */
    public static final int STATE_ENDOFSTREAM = -4;

    /**
     * Currently reading a part
     */
    public static final int STATE_READING = -5;

    /** Field in           */
    private PushbackInputStream in = null;

    /** Field boundary           */
    private byte[] boundary = null;

    /** Field state           */
    private int state = STATE_NOBOUNDARY;

    /**
     * Creates a new pushback token stream from in.
     *
     * @param in The input stream
     */
    public TokenStream(PushbackInputStream in) {
        this(in,1);
    }
    
    /**
     * Creates a new pushback token stream from in.
     *
     * @param in The input stream
     * @param size Size (in bytes) of the pushback buffer
     */
    public TokenStream(PushbackInputStream in, int size) {
        super(in,size);
        this.in = in;
    }

    /**
     * Sets the boundary to scan for
     *
     * @param boundary A byte array containg the boundary
     *
     * @throws MultipartException
     */
    public void setBoundary(byte[] boundary) throws MultipartException {
        this.boundary = boundary;
        if (state == STATE_NOBOUNDARY) {
            state = STATE_READING;
        }
    }

    /**
     * Start reading the next part in the stream. This method may only be called
     * if state is STATE_NEXTPART. It will throw a MultipartException if not.
     *
     * @throws MultipartException
     */
    public void nextPart() throws MultipartException {
        if (state != STATE_NEXTPART) {
            throw new MultipartException("Illegal state");
        }
        state = STATE_READING;
    }

    /**
     * Return the stream state
     */
    public int getState() {
        return state;
    }

    /**
     * Fill the ouput buffer until either it's full, the boundary has been reached or
     * the end of the inputstream has been reached.
     * When a boundary is reached it is entirely read away including trailing \r's and \n's.
     * It will not be written to the output buffer.
     * The stream state is updated after each call.
     *
     * @param out The output buffer
     *
     * @throws IOException
     */
    private int readToBoundary(byte[] out) throws IOException {
        if (state != STATE_READING) {
            return 0;
        }
        int boundaryIndex = 0;
        int written = 0;
        int b = in.read();

        while (true) {
            while ((byte) b != boundary[0]) {
                if (b == -1) {
                    state = STATE_ENDOFSTREAM;
                    return written;
                }
                out[written++] = (byte) b;

                if (written == out.length) {
                    return written;
                }
                b = in.read();
            }
            boundaryIndex = 0;                         // we know the first byte matched
            // check for boundary
            while ((boundaryIndex < boundary.length)
                    && ((byte) b == boundary[boundaryIndex])) {
                b = in.read();
                boundaryIndex++;
            }

            if (boundaryIndex == boundary.length) {    // matched boundary
                if (b != -1) {
                    if (b == '\r') {                   // newline, another part follows
                        state = STATE_NEXTPART;
                        in.read();
                    } else if (b == '-') {             // hyphen, end of multipart
                        state = STATE_ENDMULTIPART;
                        in.read();                     // read next hyphen
                        in.read();                     // read \r
                        in.read();                     // read \n
                    } else {                           // something else, error
                        throw new IOException(
                                "Unexpected character after boundary");
                    }
                } else {    // nothing after boundary, this shouldn't happen either
                    state = STATE_ENDOFSTREAM;
                }
                return written;
            } else {                                   // did not match boundary
                // bytes skipped, write first skipped byte, push back the rest
                if (b != -1) {                         // b may be -1
                    in.unread(b);                      // the non-matching byte
                }
                in.unread(boundary, 1,
                        boundaryIndex - 1);          // unread skipped boundary data
                out[written++] = boundary[0];
                if (written == out.length) {
                    return written;
                }
            }
            b = in.read();
        }
    }

    /**
     * @see java.io.InputStream#read(byte[])
     *
     * @param out
     *
     * @throws IOException
     */
    public int read(byte[] out) throws IOException {
        if (state != STATE_READING) {
            return 0;
        }
        return readToBoundary(out);
    }

    /**
     * @see java.io.InputStream#read(byte[],int,int)
     *
     * @param out
     * @param off
     * @param len
     *
     * @throws IOException
     */
    public int read(byte[] out, int off, int len) throws IOException {
        if ((off < 0) || (off >= out.length)) {
            throw new IOException("Buffer offset outside buffer");
        }
        if (off + len >= out.length) {
            throw new IOException("Buffer end outside buffer");
        }
        if (len < 0) {
            throw new IOException("Length must be a positive integer");
        }
        byte[] buf = new byte[len];
        int read = read(buf);
        if (read > 0) {
            System.arraycopy(buf, 0, out, off, read);
        }
        return read;
    }

    /**
     * @see java.io.InputStream#read()
     *
     * @throws IOException
     */
    public int read() throws IOException {
        byte[] buf = new byte[1];
        int read = read(buf);

        if (read == 0) {
            return -1;
        } else {
            return buf[0];
        }
    }
}
