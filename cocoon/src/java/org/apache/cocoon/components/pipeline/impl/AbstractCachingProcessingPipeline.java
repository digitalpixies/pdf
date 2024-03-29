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
package org.apache.cocoon.components.pipeline.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheValidity;
import org.apache.cocoon.caching.CacheValidityToSourceValidity;
import org.apache.cocoon.caching.Cacheable;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.caching.CachedResponse;
import org.apache.cocoon.caching.CachingOutputStream;
import org.apache.cocoon.caching.ComponentCacheKey;
import org.apache.cocoon.caching.PipelineCacheKey;
import org.apache.cocoon.environment.Environment;
import org.apache.cocoon.transformation.Transformer;
import org.apache.cocoon.util.HashUtil;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.AggregatedValidity;
import org.apache.excalibur.source.impl.validity.DeferredValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;
import org.apache.excalibur.store.Store;

/**
 * This is the base class for all caching pipeline implementations
 * that check different pipeline components.
 *
 * @since 2.1
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @author <a href="mailto:Michael.Melhem@managesoft.com">Michael Melhem</a>
 * @version $Id: AbstractCachingProcessingPipeline.java 498470 2007-01-21 22:34:30Z anathaniel $
 */
public abstract class AbstractCachingProcessingPipeline extends BaseCachingProcessingPipeline {

    public static final String PIPELOCK_PREFIX = "PIPELOCK:";

    /** The role name of the generator */
    protected String generatorRole;

    /** The role names of the transfomrers */
    protected ArrayList transformerRoles = new ArrayList();

    /** The role name of the serializer */
    protected String serializerRole;

    /** The role name of the reader */
    protected String readerRole;

    /** The cached response */
    protected CachedResponse cachedResponse;

    /** The index indicating the first transformer getting input from the cache */
    protected int firstProcessedTransformerIndex;

    /** Complete response is cached */
    protected boolean completeResponseIsCached;


    /** This key indicates the response that is fetched from the cache */
    protected PipelineCacheKey fromCacheKey;

    /** This key indicates the response that will get into the cache */
    protected PipelineCacheKey toCacheKey;

    /** The source validities used for caching */
    protected SourceValidity[] toCacheSourceValidities;

    /** The index indicating to the first transformer which is not cacheable */
    protected int firstNotCacheableTransformerIndex;

    /** Cache complete response */
    protected boolean cacheCompleteResponse;

    protected boolean   generatorIsCacheableProcessingComponent;
    protected boolean   serializerIsCacheableProcessingComponent;
    protected boolean[] transformerIsCacheableProcessingComponent;

    protected Store transientStore = null;

    /** Abstract method defined in subclasses */
    protected abstract void cacheResults(Environment environment,
            OutputStream os)
        throws Exception;

    /** Abstract method defined in subclasses */
    protected abstract ComponentCacheKey newComponentCacheKey(int type,
            String role,
            Serializable key);

    /** Abstract method defined in subclasses */
    protected abstract void connectCachingPipeline(Environment environment)
        throws ProcessingException;

    /**
     * Parameterizable Interface - Configuration
     */
    public void parameterize(Parameters params)
        throws ParameterException {
        super.parameterize(params);

        String storeRole = params.getParameter("store-role",Store.TRANSIENT_STORE); 

        try {
            transientStore = (Store) manager.lookup(storeRole);
        } catch (ComponentException e) {
            if(getLogger().isDebugEnabled()) {
                getLogger().debug("Could not look up transient store, synchronizing requests will not work!",e);
            }
        }
    }

    /**
     * Set the generator.
     */
    public void setGenerator (String role, String source, Parameters param,
            Parameters hintParam)
        throws ProcessingException {
        super.setGenerator(role, source, param, hintParam);
        this.generatorRole = role;
    }

    /**
     * Add a transformer.
     */
    public void addTransformer (String role, String source, Parameters param,
            Parameters hintParam) throws ProcessingException {
        super.addTransformer(role, source, param, hintParam);
        this.transformerRoles.add(role);
    }

    /**
     * Set the serializer.
     */
    public void setSerializer (String role, String source, Parameters param,
            Parameters hintParam, String mimeType) throws ProcessingException {
        super.setSerializer(role, source, param, hintParam, mimeType);
        this.serializerRole = role;
    }

    /**
     * Set the Reader.
     */
    public void setReader (String role, String source, Parameters param,
            String mimeType)
        throws ProcessingException {
        super.setReader(role, source, param, mimeType);
        this.readerRole = role;
    }

    protected boolean waitForLock(Object key) {
        if(transientStore != null) {
            Object lock = null;
            synchronized(transientStore) {
                String lockKey = PIPELOCK_PREFIX+key;
                if(transientStore.containsKey(lockKey)) {
                    // cache content is currently being generated, wait for other thread
                    lock = transientStore.get(lockKey);
                }
            }
            // Avoid deadlock with self (see JIRA COCOON-1985).
            if(lock != null && lock != Thread.currentThread()) {
                try {
                    // become owner of monitor
                    synchronized(lock) {
                        lock.wait();
                    }
                } catch (InterruptedException e) {
                    if(getLogger().isDebugEnabled()) {
                        getLogger().debug("Got interrupted waiting for other pipeline to finish processing, retrying...",e);
                    }
                    return false;
                }
                if(getLogger().isDebugEnabled()) {
                    getLogger().debug("Other pipeline finished processing, retrying to get cached response.");
                }
                return false;
            }
        }
        return true;
    }

    /**
     * makes the lock (instantiates a new object and puts it into the store)
     */
    protected boolean generateLock(Object key) {
        boolean succeeded = true;

        if( transientStore != null && key != null ) {
            String lockKey = PIPELOCK_PREFIX+key;
            synchronized(transientStore) {
                if(transientStore.containsKey(lockKey)) {
                    succeeded = false;
                    if(getLogger().isDebugEnabled()) {
                        getLogger().debug("Lock already present in the store!");
                    }
                } else {
                    Object lock = Thread.currentThread();
                    try {
                        transientStore.store(lockKey, lock);
                    } catch (IOException e) {
                        if(getLogger().isDebugEnabled()) {
                            getLogger().debug("Could not put lock in the store!",e);
                        }
                        succeeded = false;
                    }
                }	
            }
        }

        return succeeded;
    }

    /**
     * releases the lock (notifies it and removes it from the store)
     */
    protected boolean releaseLock(Object key) {
        boolean succeeded = true;

        if( transientStore != null && key != null ) {
            String lockKey = PIPELOCK_PREFIX+key;
            Object lock = null;
            synchronized(transientStore) {
                if(!transientStore.containsKey(lockKey)) {
                    succeeded = false;
                    if(getLogger().isDebugEnabled()) {
                        getLogger().debug("Lock not present in the store!");
                    }
                } else {
                    try {
                        lock = transientStore.get(lockKey);
                        transientStore.remove(lockKey);
                    } catch (Exception e) {
                        if(getLogger().isDebugEnabled()) {
                            getLogger().debug("Could not get lock from the store!",e);
                        }
                        succeeded = false;
                    }
                }
            }
            if(succeeded && lock != null) {
                // become monitor owner
                synchronized(lock) {
                    lock.notifyAll();
                }
            }
        }

        return succeeded;
    }

    /**
     * Process the given <code>Environment</code>, producing the output.
     */
    protected boolean processXMLPipeline(Environment environment)
        throws ProcessingException {
        if (this.toCacheKey == null && this.cachedResponse == null) {
            return super.processXMLPipeline(environment);
        }

        if (this.cachedResponse != null && this.completeResponseIsCached) {

            // Allow for 304 (not modified) responses in dynamic content
            if (checkIfModified(environment, this.cachedResponse.getLastModified())) {
                return true;
            }

            // Set mime-type
            if (this.cachedResponse.getContentType() != null) {
                environment.setContentType(this.cachedResponse.getContentType());
            } else {
                setMimeTypeForSerializer(environment);
            }

            // Write response out
            try {
                final OutputStream outputStream = environment.getOutputStream(0);
                final byte[] content = this.cachedResponse.getResponse();
                if (content.length > 0) {
                    environment.setContentLength(content.length);
                    outputStream.write(content);
                }
            } catch (Exception e) {
                handleException(e);
            }
        } else {
            setMimeTypeForSerializer(environment);
            if (getLogger().isDebugEnabled() && this.toCacheKey != null) {
                getLogger().debug("processXMLPipeline: caching content for further" +
                        " requests of '" + environment.getURI() +
                        "' using key " + this.toCacheKey);
            }

            generateLock(this.toCacheKey);

            try {
                OutputStream os = null;

                if (this.cacheCompleteResponse && this.toCacheKey != null) {
                    os = new CachingOutputStream(environment.getOutputStream(this.outputBufferSize));
                }

                if (super.serializer != super.lastConsumer) {
                    if (os == null) {
                        os = environment.getOutputStream(this.outputBufferSize);
                    }

                    // internal processing
                    if (this.xmlDeserializer != null) {
                        this.xmlDeserializer.deserialize(this.cachedResponse.getResponse());
                    } else {
                        this.generator.generate();
                    }

                } else {
                    if (this.serializer.shouldSetContentLength()) {
                        if (os == null) {
                            os = environment.getOutputStream(0);
                        }

                        // Set the output stream
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        this.serializer.setOutputStream(baos);

                        // Execute the pipeline
                        if (this.xmlDeserializer != null) {
                            this.xmlDeserializer.deserialize(this.cachedResponse.getResponse());
                        } else {
                            this.generator.generate();
                        }

                        environment.setContentLength(baos.size());
                        baos.writeTo(os);
                    } else {
                        if (os == null) {
                            os = environment.getOutputStream(this.outputBufferSize);
                        }

                        // Set the output stream
                        this.serializer.setOutputStream(os);

                        // Execute the pipeline
                        if (this.xmlDeserializer != null) {
                            this.xmlDeserializer.deserialize(this.cachedResponse.getResponse());
                        } else {
                            this.generator.generate();
                        }
                    }
                }

                //
                // Now that we have processed the pipeline,
                // we do the actual caching
                //
                cacheResults(environment,os);

            } catch (Exception e) {
                handleException(e);
            } finally {
                releaseLock(this.toCacheKey);
            }

            return true;
        }

        return true;
    }

    /**
     * The components of the pipeline are checked if they are Cacheable.
     */
    protected void generateCachingKey(Environment environment)
        throws ProcessingException {

        this.toCacheKey = null;

        this.generatorIsCacheableProcessingComponent = false;
        this.serializerIsCacheableProcessingComponent = false;
        this.transformerIsCacheableProcessingComponent =
            new boolean[this.transformers.size()];

        this.firstNotCacheableTransformerIndex = 0;
        this.cacheCompleteResponse = false;

        // first step is to generate the key:
        // All pipeline components starting with the generator
        // are tested if they are either a CacheableProcessingComponent
        // or Cacheable (deprecated). The returned keys are chained together
        // to build a unique key of the request

        // is the generator cacheable?
        Serializable key = getGeneratorKey();
        if (key != null) {
            this.toCacheKey = new PipelineCacheKey();
            this.toCacheKey.addKey(
                    this.newComponentCacheKey(
                        ComponentCacheKey.ComponentType_Generator,
                        this.generatorRole, key));

            // now testing transformers
            final int transformerSize = super.transformers.size();
            boolean continueTest = true;

            while (this.firstNotCacheableTransformerIndex < transformerSize && continueTest) {
                final Transformer trans =
                    (Transformer)super.transformers.get(this.firstNotCacheableTransformerIndex);
                key = getTransformerKey(trans);
                if (key != null) {
                    this.toCacheKey.addKey(
                            this.newComponentCacheKey(
                                ComponentCacheKey.ComponentType_Transformer,
                                (String)this.transformerRoles.get(
                                                                  this.firstNotCacheableTransformerIndex),
                                key));

                    this.firstNotCacheableTransformerIndex++;
                } else {
                    continueTest = false;
                }
            }
            // all transformers are cacheable => pipeline is cacheable
            // test serializer if this is not an internal request
            if (this.firstNotCacheableTransformerIndex == transformerSize
                    && super.serializer == this.lastConsumer) {

                key = getSerializerKey();
                if (key != null) {
                    this.toCacheKey.addKey(
                            this.newComponentCacheKey(
                                ComponentCacheKey.ComponentType_Serializer,
                                this.serializerRole,
                                key));
                    this.cacheCompleteResponse = true;
                }
                    }
        }
    }

    /**
     * Generate validity objects for the new response
     */
    protected void setupValidities() throws ProcessingException {

        if (this.toCacheKey != null) {
            // only update validity objects if we cannot use
            // a cached response or when the cached response does
            // cache less than now is cacheable
            if (this.fromCacheKey == null
                    || this.fromCacheKey.size() < this.toCacheKey.size()) {

                this.toCacheSourceValidities =
                    new SourceValidity[this.toCacheKey.size()];

                int len = this.toCacheSourceValidities.length;
                int i = 0;
                while (i < len) {
                    final SourceValidity validity = getValidityForInternalPipeline(i);

                    if (validity == null) {
                        if (i > 0
                                && (this.fromCacheKey == null
                                    || i > this.fromCacheKey.size())) {
                            // shorten key
                            for (int m=i; m < this.toCacheSourceValidities.length; m++) {
                                this.toCacheKey.removeLastKey();
                                if (!this.cacheCompleteResponse) {
                                    this.firstNotCacheableTransformerIndex--;
                                }
                                this.cacheCompleteResponse = false;
                            }
                            SourceValidity[] copy = new SourceValidity[i];
                            System.arraycopy(this.toCacheSourceValidities, 0, copy, 0, copy.length);
                            this.toCacheSourceValidities = copy;
                            len = this.toCacheSourceValidities.length;
                        } else {
                            // caching is not possible!
                            this.toCacheKey = null;
                            this.toCacheSourceValidities = null;
                            this.cacheCompleteResponse = false;
                            len = 0;
                        }
                    } else {
                        this.toCacheSourceValidities[i] = validity;
                    }
                    i++;
                }
            } else {
                // we don't have to cache
                this.toCacheKey = null;
                this.cacheCompleteResponse = false;
            }
        }
    }

    /**
     * Calculate the key that can be used to get something from the cache, and
     * handle expires properly.
     */
    protected void validatePipeline(Environment environment)
        throws ProcessingException {
        this.completeResponseIsCached = this.cacheCompleteResponse;
        this.fromCacheKey = this.toCacheKey.copy();
        this.firstProcessedTransformerIndex = this.firstNotCacheableTransformerIndex;

        boolean finished = false;
        while (this.fromCacheKey != null && !finished) {
            finished = true;

            final CachedResponse response = this.cache.get(this.fromCacheKey);

            // now test validity
            if (response != null) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Found cached response for '" + environment.getURI() +
                            "' using key: " + this.fromCacheKey);
                }

                boolean responseIsValid = true;
                boolean responseIsUsable = true;

                // See if we have an explicit "expires" setting. If so,
                // and if it's still fresh, we're done.
                Long responseExpires = response.getExpires();

                if (responseExpires != null) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Expires time found for " + environment.getURI());
                    }

                    if (responseExpires.longValue() > System.currentTimeMillis()) {
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("Expires time still fresh for " + environment.getURI() +
                                    ", ignoring all other cache settings. This entry expires on "+
                                    new Date(responseExpires.longValue()));
                        }
                        this.cachedResponse = response;
                        return;
                    } else {
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("Expires time has expired for " + environment.getURI() +
                                    ", regenerating content.");
                        }

                        // If an expires parameter was provided, use it. If this parameter is not available
                        // it means that the sitemap was modified, and the old expires value is not valid
                        // anymore.
                        if (expires != 0) {
                            if (this.getLogger().isDebugEnabled())
                                this.getLogger().debug("Refreshing expires informations");
                            response.setExpires(new Long(expires + System.currentTimeMillis()));
                        } else {
                            if (this.getLogger().isDebugEnabled())
                                this.getLogger().debug("No expires defined anymore for this object, setting it to no expires");
                            response.setExpires(null);
                        }
                    }
                } else {
                    // The response had no expires informations. See if it needs to be set (i.e. because the configuration has changed)
                    if (expires != 0) {
                        if (this.getLogger().isDebugEnabled())
                            this.getLogger().debug("Setting a new expires object for this resource");
                        response.setExpires(new Long(expires + System.currentTimeMillis()));
                    }
                }

                SourceValidity[] fromCacheValidityObjects = response.getValidityObjects();

                int i = 0;
                while (responseIsValid && i < fromCacheValidityObjects.length) {
                    boolean isValid = false;

                    // BH Check if validities[i] is null, may happen
                    //    if exception was thrown due to malformed content
                    SourceValidity validity = fromCacheValidityObjects[i];
                    int valid = validity == null ? SourceValidity.INVALID : validity.isValid();
                    if (valid == SourceValidity.UNKNOWN) {
                        // Don't know if valid, make second test
                        validity = getValidityForInternalPipeline(i);
                        if (validity != null) {
                            valid = fromCacheValidityObjects[i].isValid(validity);
                            if (valid == SourceValidity.UNKNOWN) {
                                validity = null;
                            } else {
                                isValid = (valid == SourceValidity.VALID);
                            }
                        }
                    } else {
                        isValid = (valid == SourceValidity.VALID);
                    }

                    if (!isValid) {
                        responseIsValid = false;
                        // update validity
                        if (validity == null) {
                            responseIsUsable = false;
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug("validatePipeline: responseIsUsable is false, valid=" +
                                        valid + " at index " + i);
                            }
                        } else {
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug("validatePipeline: responseIsValid is false due to " +
                                        validity);
                            }
                        }
                    } else {
                        i++;
                    }
                }

                if (responseIsValid) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("validatePipeline: using valid cached content for '" +
                                environment.getURI() + "'.");
                    }

                    // we are valid, ok that's it
                    this.cachedResponse = response;
                    this.toCacheSourceValidities = fromCacheValidityObjects;
                } else {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("validatePipeline: cached content is invalid for '" +
                                environment.getURI() + "'.");
                    }
                    // we are not valid!

                    if (!responseIsUsable) {
                        // we could not compare, because we got no
                        // validity object, so shorten pipeline key
                        if (i > 0) {
                            int deleteCount = fromCacheValidityObjects.length - i;
                            if (i > 0 && i <= firstNotCacheableTransformerIndex + 1) {
                                this.firstNotCacheableTransformerIndex = i-1;
                            }
                            for(int x=0; x < deleteCount; x++) {
                                this.toCacheKey.removeLastKey();
                            }
                            finished = false;
                        } else {
                            this.toCacheKey = null;
                        }
                        this.cacheCompleteResponse = false;
                    } else {
                        // the entry is invalid, remove it
                        this.cache.remove(this.fromCacheKey);
                    }

                    // try a shorter key
                    if (i > 0) {
                        this.fromCacheKey.removeLastKey();
                        if (!this.completeResponseIsCached) {
                            this.firstProcessedTransformerIndex--;
                        }
                    } else {
                        this.fromCacheKey = null;
                    }
                    finished = false;
                    this.completeResponseIsCached = false;
                }
            } else {

                // check if there might be one being generated
                if(!waitForLock(this.fromCacheKey)) {
                    finished = false;
                    continue;
                }

                // no cached response found
                if (this.getLogger().isDebugEnabled()) {
                    this.getLogger().debug(
                            "Cached response not found for '" + environment.getURI() +
                            "' using key: " +  this.fromCacheKey
                            );
                }

                finished = setupFromCacheKey();
                this.completeResponseIsCached = false;
            }
        }

    }

    boolean setupFromCacheKey() {
        // stop on longest key for smart caching
        this.fromCacheKey = null;
        return true;
    }

    /**
     * Setup the evenet pipeline.
     * The components of the pipeline are checked if they are
     * Cacheable.
     */
    protected void setupPipeline(Environment environment)
        throws ProcessingException {
        super.setupPipeline(environment);

        // Generate the key to fill the cache
        generateCachingKey(environment);

        // Test the cache for a valid response
        if (this.toCacheKey != null) {
            validatePipeline(environment);
        }

        setupValidities();
    }

    /**
     * Connect the pipeline.
     */
    protected void connectPipeline(Environment   environment)
        throws ProcessingException {
        if (this.toCacheKey == null && this.cachedResponse == null) {
            super.connectPipeline(environment);
            return;
        } else if (this.completeResponseIsCached) {
            // do nothing
            return;
        } else {
            this.connectCachingPipeline(environment);
        }
    }

    /** Process the pipeline using a reader.
     * @throws ProcessingException if an error occurs
     */
    protected boolean processReader(Environment  environment)
        throws ProcessingException {
        try {
            boolean usedCache = false;
            OutputStream outputStream = null;
            SourceValidity readerValidity = null;
            PipelineCacheKey pcKey = null;

            // test if reader is cacheable
            Serializable readerKey = null;
            boolean isCacheableProcessingComponent = false;
            if (super.reader instanceof CacheableProcessingComponent) {
                readerKey = ((CacheableProcessingComponent)super.reader).getKey();
                isCacheableProcessingComponent = true;
            } else if (super.reader instanceof Cacheable) {
                readerKey = new Long(((Cacheable)super.reader).generateKey());
            }

            boolean finished = false;

            if (readerKey != null) {
                // response is cacheable, build the key
                pcKey = new PipelineCacheKey();
                pcKey.addKey(new ComponentCacheKey(ComponentCacheKey.ComponentType_Reader,
                            this.readerRole,
                            readerKey)
                        );

                while(!finished) {
                    finished = true;
                    // now we have the key to get the cached object
                    CachedResponse cachedObject = this.cache.get(pcKey);
                    if (cachedObject != null) {
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("Found cached response for '" +
                                    environment.getURI() + "' using key: " + pcKey);
                        }

                        SourceValidity[] validities = cachedObject.getValidityObjects();
                        if (validities == null || validities.length != 1) {
                            // to avoid getting here again and again, we delete it
                            this.cache.remove(pcKey);
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug("Cached response for '" + environment.getURI() +
                                        "' using key: " + pcKey + " is invalid.");
                            }
                            this.cachedResponse = null;
                        } else {
                            SourceValidity cachedValidity = validities[0];
                            boolean isValid = false;
                            int valid = cachedValidity.isValid();
                            if (valid == SourceValidity.UNKNOWN) {
                                // get reader validity and compare
                                if (isCacheableProcessingComponent) {
                                    readerValidity = ((CacheableProcessingComponent) super.reader).getValidity();
                                } else {
                                    CacheValidity cv = ((Cacheable) super.reader).generateValidity();
                                    if (cv != null) {
                                        readerValidity = CacheValidityToSourceValidity.createValidity(cv);
                                    }
                                }
                                if (readerValidity != null) {
                                    valid = cachedValidity.isValid(readerValidity);
                                    if (valid == SourceValidity.UNKNOWN) {
                                        readerValidity = null;
                                    } else {
                                        isValid = (valid == SourceValidity.VALID);
                                    }
                                }
                            } else {
                                isValid = (valid == SourceValidity.VALID);
                            }

                            if (isValid) {
                                if (getLogger().isDebugEnabled()) {
                                    getLogger().debug("processReader: using valid cached content for '" +
                                            environment.getURI() + "'.");
                                }
                                byte[] response = cachedObject.getResponse();
                                if (response.length > 0) {
                                    usedCache = true;
                                    if (cachedObject.getContentType() != null) {
                                        environment.setContentType(cachedObject.getContentType());
                                    } else {
                                        setMimeTypeForReader(environment);
                                    }
                                    outputStream = environment.getOutputStream(0);
                                    environment.setContentLength(response.length);
                                    outputStream.write(response);
                                }
                            } else {
                                if (getLogger().isDebugEnabled()) {
                                    getLogger().debug("processReader: cached content is invalid for '" +
                                            environment.getURI() + "'.");
                                }
                                // remove invalid cached object
                                this.cache.remove(pcKey);
                            }
                        }
                    } else {
                        // check if something is being generated right now
                        if(!waitForLock(pcKey)) {
                            finished = false;
                            continue;
                        }
                    }
                }
            }

            if (!usedCache) {
                // make sure lock will be released
                try {
                    if (pcKey != null) {
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("processReader: caching content for further requests of '" +
                                    environment.getURI() + "'.");
                        }
                        generateLock(pcKey);

                        if (readerValidity == null) {
                            if (isCacheableProcessingComponent) {
                                readerValidity = ((CacheableProcessingComponent)super.reader).getValidity();
                            } else {
                                CacheValidity cv = ((Cacheable)super.reader).generateValidity();
                                if ( cv != null ) {
                                    readerValidity = CacheValidityToSourceValidity.createValidity( cv );
                                }
                            }
                        }

                        if (readerValidity != null) {
                            outputStream = environment.getOutputStream(this.outputBufferSize);
                            outputStream = new CachingOutputStream(outputStream);
                        }
                    }

                    setMimeTypeForReader(environment);
                    if (this.reader.shouldSetContentLength()) {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        this.reader.setOutputStream(os);
                        this.reader.generate();
                        environment.setContentLength(os.size());
                        if (outputStream == null) {
                            outputStream = environment.getOutputStream(0);
                        }
                        os.writeTo(outputStream);
                    } else {
                        if (outputStream == null) {
                            outputStream = environment.getOutputStream(this.outputBufferSize);
                        }
                        this.reader.setOutputStream(outputStream);
                        this.reader.generate();
                    }

                    // store the response
                    if (pcKey != null && readerValidity != null) {
                        final CachedResponse res = new CachedResponse(new SourceValidity[] {readerValidity},
                                ((CachingOutputStream)outputStream).getContent());
                        res.setContentType(environment.getContentType());
                        this.cache.store(pcKey, res);
                    }

                } finally {
                    if (pcKey != null) {
                        releaseLock(pcKey);
                    }
                }

            }
        } catch (Exception e) {
            handleException(e);
        }

        return true;
    }


    /**
     * Return valid validity objects for the event pipeline.
     *
     * If the event pipeline (the complete pipeline without the
     * serializer) is cacheable and valid, return all validity objects.
     *
     * Otherwise, return <code>null</code>.
     */
    public SourceValidity getValidityForEventPipeline() {
        if (isInternalError()) {
            return null;
        }

        if (this.cachedResponse != null) {
            if (!this.cacheCompleteResponse &&
                    this.firstNotCacheableTransformerIndex < super.transformers.size()) {
                // Cache contains only partial pipeline.
                return null;
                    }

            if (this.toCacheSourceValidities != null) {
                // This means that the pipeline is valid based on the validities
                // of the individual components
                final AggregatedValidity validity = new AggregatedValidity();
                for (int i=0; i < this.toCacheSourceValidities.length; i++) {
                    validity.add(this.toCacheSourceValidities[i]);
                }

                return validity;
            }

            // This means that the pipeline is valid because it has not yet expired
            return NOPValidity.SHARED_INSTANCE;
        } else {
            int vals = 0;

            if (null != this.toCacheKey
                    && !this.cacheCompleteResponse
                    && this.firstNotCacheableTransformerIndex == super.transformers.size()) {
                vals = this.toCacheKey.size();
            } else if (null != this.fromCacheKey
                    && !this.completeResponseIsCached
                    && this.firstProcessedTransformerIndex == super.transformers.size()) {
                vals = this.fromCacheKey.size();
                    }

            if (vals > 0) {
                final AggregatedValidity validity = new AggregatedValidity();
                for (int i = 0; i < vals; i++) {
                    validity.add(getValidityForInternalPipeline(i));
                }

                return validity;
            }

            return null;
        }
    }

    /**
     * Get generator cache key (null if not cacheable)
     */
    private Serializable getGeneratorKey() {
        Serializable key = null;
        if (super.generator instanceof CacheableProcessingComponent) {
            key = ((CacheableProcessingComponent)super.generator).getKey();
            this.generatorIsCacheableProcessingComponent = true;
        } else if (super.generator instanceof Cacheable) {
            key = new Long(((Cacheable)super.generator).generateKey());
        }
        return key;
    }

    /**
     * Get transformer cache key (null if not cacheable)
     */
    private Serializable getTransformerKey(final Transformer transformer) {
        Serializable key = null;
        if (transformer instanceof CacheableProcessingComponent) {
            key = ((CacheableProcessingComponent)transformer).getKey();
            this.transformerIsCacheableProcessingComponent[this.firstNotCacheableTransformerIndex] = true;
        } else if (transformer instanceof Cacheable) {
            key = new Long(((Cacheable)transformer).generateKey());
        }
        return key;
    }

    /**
     * Get serializer cache key (null if not cacheable)
     */
    private Serializable getSerializerKey() {
        Serializable key = null;
        if (super.serializer instanceof CacheableProcessingComponent) {
            key = ((CacheableProcessingComponent)this.serializer).getKey();
            this.serializerIsCacheableProcessingComponent = true;
        } else if (this.serializer instanceof Cacheable) {
            key = new Long(((Cacheable)this.serializer).generateKey());
        }
        return key;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.pipeline.ProcessingPipeline#getKeyForEventPipeline()
     */
    public String getKeyForEventPipeline() {
        if (isInternalError()) {
            return null;
        }

        if (null != this.toCacheKey
                && !this.cacheCompleteResponse
                && this.firstNotCacheableTransformerIndex == super.transformers.size()) {
            return String.valueOf(HashUtil.hash(this.toCacheKey.toString()));
                }
        if (null != this.fromCacheKey
                && !this.completeResponseIsCached
                && this.firstProcessedTransformerIndex == super.transformers.size()) {
            return String.valueOf(HashUtil.hash(this.fromCacheKey.toString()));
                }

        return null;
    }

    SourceValidity getValidityForInternalPipeline(int index) {
        final SourceValidity validity;

        // if debugging try to tell why something is not cacheable
        final boolean debug = this.getLogger().isDebugEnabled();
        String msg = null;
        if(debug) msg = "getValidityForInternalPipeline(" + index + "): ";

        if (index == 0) {
            // test generator
            if (this.generatorIsCacheableProcessingComponent) {
                validity = ((CacheableProcessingComponent)super.generator).getValidity();
                if(debug) msg += "generator: using getValidity";
            } else {
                validity = CacheValidityToSourceValidity.createValidity(((Cacheable)super.generator).generateValidity());
                if(debug) msg += "generator: using generateValidity";
            }
        } else if (index <= firstNotCacheableTransformerIndex) {
            // test transformer
            final Transformer trans = (Transformer)super.transformers.get(index-1);
            if (this.transformerIsCacheableProcessingComponent[index-1]) {
                validity = ((CacheableProcessingComponent)trans).getValidity();
                if(debug) msg += "transformer: using getValidity";
            } else {
                validity = CacheValidityToSourceValidity.createValidity(((Cacheable)trans).generateValidity());
                if(debug) msg += "transformer: using generateValidity";
            }
        } else {
            // test serializer
            if (this.serializerIsCacheableProcessingComponent) {
                validity = ((CacheableProcessingComponent)super.serializer).getValidity();
                if(debug) msg += "serializer: using getValidity";
            } else {
                validity = CacheValidityToSourceValidity.createValidity(((Cacheable)super.serializer).generateValidity());
                if(debug) msg += "serializer: using generateValidity";
            }
        }

        if(debug) {
            msg += ", validity==" + validity;
            this.getLogger().debug(msg);
        }
        return validity;
    }

    /**
     * Recyclable Interface
     */
    public void recycle() {
        this.generatorRole = null;
        this.transformerRoles.clear();
        this.serializerRole = null;
        this.readerRole = null;

        this.fromCacheKey = null;
        this.cachedResponse = null;

        this.transformerIsCacheableProcessingComponent = null;
        this.toCacheKey = null;
        this.toCacheSourceValidities = null;

        super.recycle();
    }
}

final class DeferredPipelineValidity implements DeferredValidity {

    private final AbstractCachingProcessingPipeline pipeline;
    private final int index;

    public DeferredPipelineValidity(AbstractCachingProcessingPipeline pipeline, int index) {
        this.pipeline = pipeline;
        this.index = index;
    }

    /**
     * @see org.apache.excalibur.source.impl.validity.DeferredValidity#getValidity()
     */
    public SourceValidity getValidity() {
        return pipeline.getValidityForInternalPipeline(this.index);
    }
}
