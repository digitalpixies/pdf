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
package org.apache.cocoon.generation;

import org.apache.avalon.framework.component.Recomposable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentException;

import org.apache.cocoon.caching.CacheValidity;
import org.apache.cocoon.caching.Cacheable;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.components.language.generator.CompiledComponent;
import org.apache.cocoon.environment.Request;

import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.File;
import java.io.Serializable;

/**
 * Base implementation of <code>ServerPagesGenerator</code>. This class
 * declares variables that must be explicitly initialized by code generators.
 *
 * @author <a href="mailto:ricardo@apache.org">Ricardo Rocha</a>
 * @version CVS $Id: AbstractServerPage.java 433543 2006-08-22 06:22:54Z crossley $
 */
public abstract class AbstractServerPage
  extends ServletGenerator 
  implements CompiledComponent, CacheableProcessingComponent, Cacheable, Recomposable {
    /**
     * Code generators should produce a constructor
     * block that initializes the generator's
     * creation date and file dependency list.
     * Example:
     *
     *  {
     *    this.dateCreated = 958058788948L;
     *    this.dependencies = new File[] {
     *      new File("source.xml"),
     *    };
     *  }
     *
     */

    /** The creation date */
    protected long dateCreated = -1L;
    /** The dependency file list */
    protected File[] dependencies = null;

    /**
     * Recompose with the actual <code>ComponentManager</code> that should
     * be used.
     */
    public void recompose(ComponentManager manager) throws ComponentException {
        this.manager = manager;
    }

    /**
     * Determines whether this generator's source files have changed
     *
     * @return Whether any of the files this generator depends on has changed
     * since it was created
     */
    public boolean modifiedSince(long date) {
        if (date == 0 || dateCreated < date) {
            return true;
        }

        for (int i = 0; i < dependencies.length; i++) {
            if (dateCreated < dependencies[i].lastModified()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether generated content has changed since
     * last invocation. Users may override this method to take
     * advantage of SAX event cacheing
     *
     * @param request The request whose data must be inspected to assert whether
     * dynamically generated content has changed
     * @return Whether content has changes for this request's data
     */
    public boolean hasContentChanged(Request request) {
      return true;
    }

    /**
     * Generate the unique key.
     * This key must be unique inside the space of this component.
     * This method must be invoked before the generateValidity() method.
     *
     * @return The generated key or <code>null</code> if the component
     *         is currently not cacheable.
     */
    public Serializable getKey() {
        return null;
    }

    /**
     * Generate the validity object.
     * Before this method can be invoked the generateKey() method
     * must be invoked.
     *
     * @return The generated validity object, <code>NOPCacheValidity</code>
     *         is the default if hasContentChange() gives false otherwise
     *         <code>null</code> will be returned.
     */
    public SourceValidity getValidity() {
        if (hasContentChanged(request))
            return null;
        else
            return NOPValidity.SHARED_INSTANCE;
    }

    // FIXME: Add more methods!
    /* SAX Utility Methods */
    /**
     * Add an attribute
     *
     * @param attr The attribute list to add to
     * @param name The attribute name
     * @param value The attribute value
     */
    protected void attribute(AttributesImpl attr, String name, String value) {
        attr.addAttribute("", name, name, "CDATA", value);
    }

    /**
     * Start an element
     *
     * @param name The element name
     * @param attr The element attributes
     */
    protected void start(String name, AttributesImpl attr) throws SAXException {
        this.contentHandler.startElement("", name, name, attr);
        attr.clear();
    }

    /**
     * End an element
     *
     * @param name The element name
     */
    protected void end(String name) throws SAXException {
        this.contentHandler.endElement("", name, name);
    }

    /**
     * Add character data
     *
     * @param data The character data
     */
    protected void characters(String data) throws SAXException {
        this.contentHandler.characters(data.toCharArray(), 0, data.length());
    }

    /**
     * Add a comment
     *
     * @param data The comment data
     */
    protected void comment(String data) throws SAXException {
        this.lexicalHandler.comment(data.toCharArray(), 0, data.length());
    }

    /**
     * Generates the unique key.
     * This key must be unique inside the space of this component.
     * Users may override this method to take
     * advantage of SAX event cacheing
     *
     * @return A long representing the cache key (defaults to not cachable)
     */
    public long generateKey() {
        return 0;
    }

    /**
     * Generate the validity object.
     *
     * @return The generated validity object, <code>NOPCacheValidity</code>
     *         is the default if hasContentChange() gives false otherwise
     *         <code>null</code> will be returned.
     */
    public CacheValidity generateValidity() {
        if (hasContentChanged(request))
            return null;
        else
            return NOPCacheValidity.CACHE_VALIDITY;
    }

}

/** 
 * This is here to avaid references to the deprecated package.
 * It is required to support the deprecated caching algorithm
 */
final class NOPCacheValidity
implements CacheValidity {

    public static final CacheValidity CACHE_VALIDITY = new NOPCacheValidity();

    public boolean isValid(CacheValidity validity) {
        return validity instanceof NOPCacheValidity;
    }

    public String toString() {
        return "NOP Validity";
    }
}