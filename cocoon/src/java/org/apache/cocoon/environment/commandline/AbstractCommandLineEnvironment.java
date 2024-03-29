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
package org.apache.cocoon.environment.commandline;

import org.apache.avalon.framework.logger.Logger;
import org.apache.cocoon.CascadingIOException;
import org.apache.cocoon.Constants;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.cocoon.environment.AbstractEnvironment;
import org.apache.cocoon.environment.Redirector;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

/**
 * This environment is used to save the requested file to disk.
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @version $Id: AbstractCommandLineEnvironment.java 433543 2006-08-22 06:22:54Z crossley $
 */

public abstract class AbstractCommandLineEnvironment
extends AbstractEnvironment
implements Redirector {

    protected String contentType;
    protected int contentLength;
    protected boolean hasRedirected = false;
    protected int statusCode;

    public AbstractCommandLineEnvironment(String uri,
                                          String view,
                                          File context,
                                          OutputStream stream,
                                          Logger log)
    throws MalformedURLException {
        super(uri, view, context);
        this.enableLogging(log);
        this.outputStream = stream;
        this.statusCode = 0;
    }

    /**
     * Redirect the client to a new URL
     */
    public void redirect(boolean sessionmode, String newURL)
    throws IOException {

        this.hasRedirected = true;

        if (sessionmode) {
            CommandLineSession.getSession(true);
        }

        // fix all urls created with request.getScheme()+... etc.
        if (newURL.startsWith("cli:/")) {
            int pos = newURL.indexOf('/', 6);
            newURL = newURL.substring(pos+1);
        }

        // fix all relative urls to use to cocoon: protocol
        if (newURL.indexOf(":") == -1) {
            newURL = "cocoon:/" + newURL;
        }

        // FIXME: this is a hack for the links view
        if (newURL.startsWith("cocoon:")
            && this.getView() != null
            && this.getView().equals(Constants.LINK_VIEW)) {

            // as the internal cocoon protocol is used the last
            // serializer is removed from it! And therefore
            // the LinkSerializer is not used.
            // so we create one without Avalon...
            org.apache.cocoon.serialization.LinkSerializer ls =
                new org.apache.cocoon.serialization.LinkSerializer();
            ls.setOutputStream(this.outputStream);

            Source redirectSource = null;
            try {
                redirectSource = this.resolveURI(newURL);
                SourceUtil.parse( this.manager, redirectSource, ls);
            } catch (SourceException se) {
                throw new CascadingIOException("SourceException: " + se, se);
            } catch (SAXException se) {
                throw new CascadingIOException("SAXException: " + se, se);
            } catch (ProcessingException pe) {
                throw new CascadingIOException("ProcessingException: " + pe, pe);
            } finally {
                this.release( redirectSource );
            }
        } else {
            Source redirectSource = null;
            try {
                redirectSource = this.resolveURI(newURL);
                InputStream is = redirectSource.getInputStream();
                byte[] buffer = new byte[8192];
                int length = -1;

                while ((length = is.read(buffer)) > -1) {
                    this.outputStream.write(buffer, 0, length);
                }
            } catch (SourceException se) {
                throw new CascadingIOException("SourceException: " + se, se);
            } finally {
                this.release( redirectSource);
            }
        }
    }

    public void sendStatus(int sc) {
        setStatus(sc);
        this.hasRedirected = true;
    }

    public boolean hasRedirected() {
        return this.hasRedirected;
    }

    /**
     * Set the StatusCode
     */
    public void setStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Get the StatusCode
     */
    public int getStatus() {
        return statusCode;
    }

    /**
     * Set the ContentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Set the ContentLength
     */
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * Get the ContentType
     */
    public String getContentType() {
        return this.contentType;
    }
    
    /**
     * Always return <code>true</code>.
     */
    public boolean isExternal() {
        return true;
    }

    /**
     * Return an OutputStream, but allow it to be null for when
     * the pipeline is being streamed to the provided SAX 
     * content handler (using CocoonBean)
     */
    public OutputStream getOutputStream(int bufferSize) throws IOException {
        if (this.outputStream == null) {
            return null;
        } else {
            return super.getOutputStream(bufferSize);
        }
    }
}