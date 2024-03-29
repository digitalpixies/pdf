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
package org.apache.cocoon.environment.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.Environment;
import org.apache.cocoon.environment.Source;
import org.apache.excalibur.source.SourceResolver;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/* @version $Id: MockEnvironment.java 433543 2006-08-22 06:22:54Z crossley $ */
public class MockEnvironment implements Environment {

    private SourceResolver resolver;

    private String uri;
    private String uriprefix;
    private String rootcontext;
    private String context;
    private String view;
    private String action;
    private String contenttype;
    private int contentlength;
    private int status;
    private ByteArrayOutputStream outputstream;
    private HashMap objectmodel;
    private Hashtable attributes = new Hashtable();

    public MockEnvironment(SourceResolver resolver) {
        this.resolver = resolver;
    }

    public String getURI() {
        return uri;
    }

    public String getURIPrefix() {
        return uriprefix;
    }

    public String getRootContext() {
        return rootcontext;
    }

    public String getContext() {
        return context;
    }

    public String getView() {
        return view;
    }

    public String getAction() {
        return action;
    }

    public void setContext(String prefix, String uri, String context) {
        throw new AssertionFailedError("Not implemented");
    }

    public void changeContext(String uriprefix, String context) throws Exception {
        throw new AssertionFailedError("Not implemented");
    }

    public void redirect(boolean sessionmode, String url) throws IOException {
        throw new AssertionFailedError("Use Redirector.redirect instead!");
    }

    public void setContentType(String contenttype) {
        this.contenttype = contenttype;
    }

    public String getContentType() {
        return contenttype;
    }

    public void setContentLength(int length) {
        this.contentlength = length;
    }

    public int getContentLength() {
        return contentlength;
    }

    public void setStatus(int statusCode) {
        this.status = statusCode;
    }

    public int getStatus() {
        return status;
    }

    /**
     * Get the output stream where to write the generated resource.
     * @deprecated Use {@link #getOutputStream(int)} instead.
     */
    public OutputStream getOutputStream() throws IOException {
        return getOutputStream(-1);
    }

    public OutputStream getOutputStream(int bufferSize) throws IOException {
        outputstream = new ByteArrayOutputStream();
        return outputstream;
    }

    public byte[] getOutput() {
        return outputstream.toByteArray();
    }

    public Map getObjectModel() {
        return objectmodel;
    }

    public boolean isResponseModified(long lastModified) {
        throw new AssertionFailedError("Not implemented");
    }

    public void setResponseIsNotModified() {
        throw new AssertionFailedError("Not implemented");
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public Enumeration getAttributeNames() {
        return attributes.keys();
    }

    public boolean tryResetResponse() throws IOException {
        throw new AssertionFailedError("Not implemented");
    }

    public void commitResponse() throws IOException {
        throw new AssertionFailedError("Not implemented");
    }
    
    public void startingProcessing() {
        throw new AssertionFailedError("Not implemented");
    }
    
    public void finishingProcessing() {
        throw new AssertionFailedError("Not implemented");
    }


    public Source resolve(String systemID)
      throws ProcessingException, SAXException, IOException {
  
        throw new AssertionFailedError("Not not use deprecated methods!");
    }

    public void toSAX(org.apache.excalibur.source.Source source,
                ContentHandler handler)
      throws SAXException, IOException, ProcessingException {

        throw new AssertionFailedError("Not not use deprecated methods!");
    }

    public void toSAX(org.apache.excalibur.source.Source source,
               String         mimeTypeHint,
               ContentHandler handler)
      throws SAXException, IOException, ProcessingException {

        throw new AssertionFailedError("Not not use deprecated methods!");
    }

    public org.apache.excalibur.source.Source resolveURI(String location)
        throws MalformedURLException, IOException, org.apache.excalibur.source.SourceException {

        return resolver.resolveURI(location);
    }

    public org.apache.excalibur.source.Source resolveURI(String location,
                                                         String base,
                                                         Map parameters)
        throws MalformedURLException, IOException, org.apache.excalibur.source.SourceException {

        return resolver.resolveURI(location, base, parameters);
    }

    /**
     * Releases a resolved resource
     */
    public void release(org.apache.excalibur.source.Source source) {
        resolver.release(source);
    }
    
    /**
     * Always return <code>true</code>.
     */
    public boolean isExternal() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.environment.Environment#isInternRedirect()
     */
    public boolean isInternalRedirect() {
        return false;
    }
}
