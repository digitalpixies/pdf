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
package org.apache.cocoon.components.sax;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.CascadingRuntimeException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.HashMap;

/**
 * This a simple xml compiler which outputs a byte array.
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @author <a href="mailto:tcurdt@apache.org">Torsten Curdt</a>
 * @version CVS $Id: XMLByteStreamCompiler.java 433543 2006-08-22 06:22:54Z crossley $
 */

public final class XMLByteStreamCompiler implements XMLSerializer, Recyclable {

    private HashMap map;
    private int mapCount;

    /** The buffer for the compile xml byte stream. */
    private byte buf[];

    /** The number of valid bytes in the buffer. */
    private int bufCount;

    private int bufCountAverage;


    public XMLByteStreamCompiler() {
        this.map = new HashMap();
        this.bufCountAverage = 2000;
        this.initOutput();
    }

    private void initOutput() {
        this.mapCount = 0;
        this.map.clear();
        this.buf = new byte[bufCountAverage];
        this.buf[0] = (byte)'C';
        this.buf[1] = (byte)'X';
        this.buf[2] = (byte)'M';
        this.buf[3] = (byte)'L';
        this.buf[4] = (byte)1;
        this.buf[5] = (byte)0;
        this.bufCount = 6;
    }

    public void recycle() {
        bufCountAverage = (bufCountAverage + bufCount) / 2;
        this.initOutput();
    }

    private static final int START_DOCUMENT         = 0;
    private static final int END_DOCUMENT           = 1;
    private static final int START_PREFIX_MAPPING   = 2;
    private static final int END_PREFIX_MAPPING     = 3;
    private static final int START_ELEMENT          = 4;
    private static final int END_ELEMENT            = 5;
    private static final int CHARACTERS             = 6;
    private static final int IGNORABLE_WHITESPACE   = 7;
    private static final int PROCESSING_INSTRUCTION = 8;
    private static final int COMMENT                = 9;
    private static final int LOCATOR                = 10;
    private static final int START_DTD              = 11;
    private static final int END_DTD                = 12;
    private static final int START_CDATA            = 13;
    private static final int END_CDATA              = 14;
    private static final int SKIPPED_ENTITY         = 15;
    private static final int START_ENTITY           = 16;
    private static final int END_ENTITY             = 17;


    public Object getSAXFragment() {
        if (this.bufCount == 6) { // no event arrived yet
            return null;
        }
        byte newbuf[] = new byte[this.bufCount];
        System.arraycopy(this.buf, 0, newbuf, 0, this.bufCount);
        return newbuf;
    }

    public void startDocument() throws SAXException {
        this.writeEvent(START_DOCUMENT);
    }

    public void endDocument() throws SAXException {
        this.writeEvent(END_DOCUMENT);
    }

    public void startPrefixMapping(java.lang.String prefix, java.lang.String uri) throws SAXException {
        this.writeEvent(START_PREFIX_MAPPING);
        this.writeString(prefix);
        this.writeString(uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
       this.writeEvent(END_PREFIX_MAPPING);
       this.writeString(prefix);
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        int length = atts.getLength();
        this.writeEvent(START_ELEMENT);
        this.writeAttributes(length);
        for (int i = 0; i < length; i++) {
            this.writeString(atts.getURI(i));
            this.writeString(atts.getLocalName(i));
            this.writeString(atts.getQName(i));
            this.writeString(atts.getType(i));
            this.writeString(atts.getValue(i));
         }
         this.writeString((namespaceURI == null ? "" : namespaceURI));
         this.writeString(localName);
         this.writeString(qName);
     }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        this.writeEvent(END_ELEMENT);
        this.writeString((namespaceURI == null ? "" : namespaceURI));
        this.writeString(localName);
        this.writeString(qName);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        this.writeEvent(CHARACTERS);
        this.writeChars(ch, start, length);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.writeEvent(IGNORABLE_WHITESPACE);
        this.writeChars(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        this.writeEvent(PROCESSING_INSTRUCTION);
        this.writeString(target);
        this.writeString(data);
    }

    public void setDocumentLocator(Locator locator) {
        try {
            this.writeEvent(LOCATOR);
            String publicId = locator.getPublicId();
            String systemId = locator.getSystemId();
            this.writeString(publicId!=null?publicId:"");
            this.writeString(systemId!=null?systemId:"");
            this.write(locator.getLineNumber());
            this.write(locator.getColumnNumber());
        } catch (Exception e) {
             throw new CascadingRuntimeException("Error while handling locator", e);
        }
    }

    public void skippedEntity(java.lang.String name) throws SAXException {
        this.writeEvent(SKIPPED_ENTITY);
        this.writeString(name);
    }

    /**
     * SAX Event Handling: LexicalHandler
     */
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        this.writeEvent(START_DTD);
        this.writeString(name);
        this.writeString(publicId!=null?publicId:"");
        this.writeString(systemId!=null?systemId:"");
    }

    /**
     * SAX Event Handling: LexicalHandler
     */
    public void endDTD() throws SAXException {
        this.writeEvent(END_DTD);
    }

    /**
     * SAX Event Handling: LexicalHandler
     */
    public void startEntity(String name) throws SAXException {
        this.writeEvent(START_ENTITY);
        this.writeString(name);
    }

    /**
     * SAX Event Handling: LexicalHandler
     */
    public void endEntity(String name) throws SAXException {
        this.writeEvent(END_ENTITY);
        this.writeString(name);
    }

    /**
     * SAX Event Handling: LexicalHandler
     */
    public void startCDATA() throws SAXException {
        this.writeEvent(START_CDATA);
    }

    /**
     * SAX Event Handling: LexicalHandler
     */
    public void endCDATA() throws SAXException {
        this.writeEvent(END_CDATA);
    }


    /**
     * SAX Event Handling: LexicalHandler
     */
    public void comment(char ary[], int start, int length) throws SAXException {
        try {
            this.writeEvent(COMMENT);
            this.writeChars(ary, start, length);
        } catch (Exception e) {
            throw new SAXException(e);
        }
    }

    public final void writeEvent( final int event) {
        this.write(event);
    }

    public final void writeAttributes( final int attributes) throws SAXException {
        if (attributes > 0xFFFF) throw new SAXException("Too many attributes");
        this.write((attributes >>> 8) & 0xFF);
        this.write((attributes >>> 0) & 0xFF);
    }

    public final void writeString( final String str) throws SAXException {
        Integer index = (Integer) map.get(str);
        if (index == null) {
            map.put(str, new Integer(mapCount++));
            int length = str.length();
            this.writeChars(str.toCharArray(), 0, length);
        }
        else {
            int i = index.intValue();

            if (i > 0xFFFF) throw new SAXException("Index too large");

            this.write(((i >>> 8) & 0xFF) | 0x80);
            this.write((i >>> 0) & 0xFF);
        }
    }

    public final void writeChars( final char[] ch, final int start, final int length) {
        int utflen = 0;
        int c;

        for (int i = 0; i < length; i++) {
            c = ch[i + start];
            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            }
            else if (c > 0x07FF) {
                utflen += 3;
            }
            else {
                utflen += 2;
            }
        }

        if (utflen >= 0x00007FFF) {
            assure(bufCount + utflen + 6);

            buf[bufCount++] = (byte)0x7F;
            buf[bufCount++] = (byte)0xFF;

            buf[bufCount++] = (byte) ((utflen >>> 24) & 0xFF);
            buf[bufCount++] = (byte) ((utflen >>> 16) & 0xFF);
            buf[bufCount++] = (byte) ((utflen >>>  8) & 0xFF);
            buf[bufCount++] = (byte) ((utflen >>>  0) & 0xFF);
        }
        else {
            assure(bufCount + utflen + 2);

            buf[bufCount++] = (byte) ((utflen >>> 8) & 0xFF);
            buf[bufCount++] = (byte) ((utflen >>> 0) & 0xFF);
        }

        for (int i = 0; i < length; i++) {
            c = ch[i + start];
            if ((c >= 0x0001) && (c <= 0x007F)) {
                buf[bufCount++] = (byte) c;
            }
            else if (c > 0x07FF) {
                buf[bufCount++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                buf[bufCount++] = (byte) (0x80 | ((c >>  6) & 0x3F));
                buf[bufCount++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            }
            else {
                buf[bufCount++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
                buf[bufCount++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            }
        }


/*
        if (length == 0) return;

        assure( (int) (buf.length + length * utfRatioAverage) );

        int utflentotal = 0;

        bufCount += 2;
        int bufStart = bufCount;

        for (int i = 0; i < length; i++) {
            int c = ch[i + start];
            int l = bufCount-bufStart;

            if (l+3 >= 0x7FFF) {
                buf[bufStart-2] = (byte) ((l >>> 8) & 0xFF);
                buf[bufStart-1] = (byte) ((l >>> 0) & 0xFF);
                utflentotal += l;
                bufCount += 2;
                bufStart = bufCount;
            }

            if ((c >= 0x0001) && (c <= 0x007F)) {
                assure(bufCount+1);
                buf[bufCount++] = (byte)c;
            }
            else if (c > 0x07FF) {
                assure(bufCount+3);
                buf[bufCount++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                buf[bufCount++] = (byte) (0x80 | ((c >>  6) & 0x3F));
                buf[bufCount++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            }
            else {
                assure(bufCount+2);
                buf[bufCount++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
                buf[bufCount++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            }
        }

        int l = bufCount-bufStart;
        buf[bufStart-2] = (byte) ((l >>> 8) & 0xFF);
        buf[bufStart-1] = (byte) ((l >>> 0) & 0xFF);
        utflentotal += l;

        utfRatioAverage = (utfRatioAverage + (utflentotal / length) / 2);
*/
    }

/*  JH (2003-11-20): seems to be never used

    private void write( final byte[] b ) {
        int newcount = this.bufCount + b.length;
        assure(newcount);
        System.arraycopy(b, 0, this.buf, this.bufCount, b.length);
        this.bufCount = newcount;
    }
*/

    private void write( final int b ) {
        int newcount = this.bufCount + 1;
        assure(newcount);
        this.buf[this.bufCount] = (byte)b;
        this.bufCount = newcount;
    }

    private void assure( final int size ) {
        if (size > this.buf.length) {
            byte newbuf[] = new byte[Math.max(this.buf.length << 1, size)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.bufCount);
            this.buf = newbuf;
        }
    }
}

