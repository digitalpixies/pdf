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
package org.apache.cocoon.serialization;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.CascadingIOException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @cocoon.sitemap.component.documentation
 * The html serializer serializes sax events into an html document.
 * 
 * @cocoon.sitemap.component.name      html
 * @cocoon.sitemap.component.mimetype  text/html
 * @cocoon.sitemap.component.logger sitemap.serializer.html
 * 
 * @cocoon.sitemap.component.pooling.max  32
 * 
 * @cocoon.sitemap.component.configuration
 * <doctype-public>-//W3C//DTD HTML 4.01 Transitional//EN</doctype-public>
 * <doctype-system>http://www.w3.org/TR/html4/loose.dtd</doctype-system>
 *
 * 
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @version CVS $Id: HTMLSerializer.java 433543 2006-08-22 06:22:54Z crossley $
 */

public class HTMLSerializer extends AbstractTextSerializer {

    /**
     * Set the configurations for this serializer.
     */
    public void configure(Configuration conf)
    throws ConfigurationException {
        super.configure(conf);
        this.format.put(OutputKeys.METHOD,"html");
    }

    /**
     * Set the {@link OutputStream} where the requested resource should
     * be serialized.
     */
    public void setOutputStream(OutputStream out) 
    throws IOException {
        super.setOutputStream(out);
        try {
            TransformerHandler handler = this.getTransformerHandler();
            handler.getTransformer().setOutputProperties(this.format);
            handler.setResult(new StreamResult(this.output));
            this.setContentHandler(handler);
            this.setLexicalHandler(handler);
        } catch (Exception e) {
            final String message = "Cannot set HTMLSerializer outputstream"; 
            throw new CascadingIOException(message, e);
        }
    }
}
