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

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CachedResponse;
import org.apache.cocoon.caching.CachingOutputStream;
import org.apache.cocoon.caching.ComponentCacheKey;
import org.apache.cocoon.components.sax.XMLDeserializer;
import org.apache.cocoon.components.sax.XMLSerializer;
import org.apache.cocoon.components.sax.XMLTeePipe;
import org.apache.cocoon.environment.Environment;
import org.apache.cocoon.xml.XMLConsumer;
import org.apache.cocoon.xml.XMLProducer;
import org.apache.commons.lang.BooleanUtils;
import org.apache.excalibur.source.SourceValidity;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * The caching-point pipeline implements an extended caching algorithm which is
 * of particular benefit for use with those pipelines that utilise cocoon-views
 * and/or provide drill-down functionality.
 *
 * @since 2.1
 * @author <a href="mailto:Michael.Melhem@managesoft.com">Michael Melhem</a>
 * @version CVS $Id: CachingPointProcessingPipeline.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class CachingPointProcessingPipeline
    extends AbstractCachingProcessingPipeline {

    protected ArrayList isCachePoint = new ArrayList();
    protected ArrayList xmlSerializerArray = new ArrayList();
    protected boolean nextIsCachePoint = false;
    protected String autoCachingPointSwitch;
    protected boolean autoCachingPoint = true;

   /**
    * The <code>CachingPointProcessingPipeline</code> is configurable.
    *
    * <p>The autoCachingPoint algorithm (if enabled) will automatically cache
    * common elements of the pipeline currently being processed - as well as the
    * entire cacheable pipeline according to the "longest cacheable key"
    * algorithm.  This feature is especially useful for pipelines that branch at
    * some point (this is the case with <tt>&lt;map:select&gt;</tt> or
    * <tt>&lt;map:act&gt;</tt>).
    *
    * <p>The option <tt>autoCachingPoint</tt> can be switched on/off in the
    * sitemap.xmap (on by default).  For linear pipelines, one can switch "Off"
    * <tt>autoCachingPoint</tt> and use attribute
    * <tt>pipeline-hints="caching-point"</tt> to manually indicate that certain
    * pipeline components (eg on <tt>&lt;map:generator&gt;</tt>) should be
    * considered as cache points.  Both options (automatic at branch points and
    * manual with pipeline hints) can coexist in the same pipeline.</p>
    *
    * <p>Works by requesting the pipeline processor to try shorter keys when
    * looking for a cached content for the pipeline.</p>
    */
    public void parameterize(Parameters config) throws ParameterException {
        super.parameterize(config);

        this.autoCachingPointSwitch = config.getParameter("autoCachingPoint", null);

        if (this.getLogger().isDebugEnabled()) {
            getLogger().debug("Auto caching-point is set to = '" + this.autoCachingPointSwitch + "'");
        }

        // Default is that auto caching-point is on
        if (this.autoCachingPointSwitch == null) {
            this.autoCachingPoint=true;
        } else {
            this.autoCachingPoint = BooleanUtils.toBoolean(this.autoCachingPointSwitch);
        }
    }

    /**
     * Set the generator.
     */
    public void setGenerator (String role, String source, Parameters param, Parameters hintParam)
    throws ProcessingException {
        super.setGenerator(role, source, param, hintParam);

        // check the hint param for a "caching-point" hint
        String pipelinehint = null;
        try {
            pipelinehint = hintParam.getParameter("caching-point", null);

            if (this.getLogger().isDebugEnabled()) {
                getLogger().debug("generator caching-point pipeline-hint is set to: " + pipelinehint);
            }
        } catch (Exception ex) {
            if (this.getLogger().isWarnEnabled()) {
                getLogger().warn("caching-point hint Exception, pipeline-hint ignored: " + ex);
            }
        }

        // if this generator is manually set to "caching-point" (via pipeline-hint)
        // then ensure the next component is caching.
        this.nextIsCachePoint = BooleanUtils.toBoolean(pipelinehint);
    }

    /**
     * Add a transformer.
     */
    public void addTransformer (String role, String source, Parameters param,  Parameters hintParam)
    throws ProcessingException {
        super.addTransformer(role, source, param, hintParam);

        // check the hint param for a "caching-point" hint
        String pipelinehint = null;
        try {
            pipelinehint = hintParam.getParameter("caching-point", null);

            if (this.getLogger().isDebugEnabled()) {
                getLogger().debug("transformer caching-point pipeline-hint is set to: " + pipelinehint);
            }
        } catch (Exception ex) {
            if (this.getLogger().isWarnEnabled()) {
                getLogger().warn("caching-point hint Exception, pipeline-hint ignored: " + ex);
            }
        }

        // add caching point flag
        // default value is false
        this.isCachePoint.add(BooleanUtils.toBooleanObject(this.nextIsCachePoint));

        // if this transformer is manually set to "caching-point" (via pipeline-hint)
        // then ensure the next component is caching.
        this.nextIsCachePoint = BooleanUtils.toBoolean(pipelinehint);
    }

    /**
     * Determine if the given branch-point is a caching-point.  This is called
     * by sitemap components when using cocoon views; it is also called by
     * parent nodes (mainly selectors and actions).
     *
     * Please Note: this method is used by auto caching-point
     * and is of no consequence when auto caching-point is switched off
     *
     * @see org.apache.cocoon.components.treeprocessor.SimpleParentProcessingNode
     */
    public void informBranchPoint() {
        if (this.autoCachingPoint && this.generator != null) {
            this.nextIsCachePoint = true;
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("Informed Pipeline of branch point");
            }
        }
    }

    /**
     * Cache longest cacheable path plus cache points.
     */
    protected void cacheResults(Environment environment, OutputStream os) throws Exception {

        if (this.toCacheKey != null) {
            if (this.cacheCompleteResponse) {
                if (this.getLogger().isDebugEnabled()) {
                    this.getLogger().debug("Cached: caching complete response; pSisze"
                                           + this.toCacheKey.size() + " Key " + this.toCacheKey);
                }
                CachedResponse response = new CachedResponse(this.toCacheSourceValidities,
                                          ((CachingOutputStream)os).getContent());
                response.setContentType(environment.getContentType());
                this.cache.store(this.toCacheKey.copy(), response);
                //
                // Scan back along the pipelineCacheKey for
                // for any cachepoint(s)
                //
                this.toCacheKey.removeUntilCachePoint();

                //
                // adjust the validities object
                // to reflect the new length of the pipeline cache key.
                //
                // REVISIT: Is it enough to simply reduce the length of the validities array?
                //
                if (this.toCacheKey.size() > 0) {
                    SourceValidity[] copy = new SourceValidity[this.toCacheKey.size()];
                    System.arraycopy(this.toCacheSourceValidities, 0, copy, 0, copy.length);
                    this.toCacheSourceValidities = copy;
                }
            }

            if (this.toCacheKey.size() > 0) {
                ListIterator itt = this.xmlSerializerArray.listIterator(this.xmlSerializerArray.size());
                while (itt.hasPrevious()) {
                    XMLSerializer serializer = (XMLSerializer) itt.previous();
                    CachedResponse response = new CachedResponse(this.toCacheSourceValidities,
                                              (byte[])serializer.getSAXFragment());
                    this.cache.store(this.toCacheKey.copy(), response);

                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug("Caching results for the following key: "
                            + this.toCacheKey);
                    }

                    //
                    // Check for further cachepoints
                    //
                    toCacheKey.removeUntilCachePoint();
                    if (this.toCacheKey.size()==0)
                        // no cachePoint found in key
                        break;

                    //
                    // re-calculate validities array
                    //
                    SourceValidity[] copy = new SourceValidity[this.toCacheKey.size()];
                    System.arraycopy(this.toCacheSourceValidities, 0, copy, 0, copy.length);
                    this.toCacheSourceValidities = copy;
                } //end serializer loop
            }
        }
    }

    /**
     * Create a new ComponentCachekey
     * ComponentCacheKeys can be flagged as cachepoints
     */
    protected ComponentCacheKey newComponentCacheKey(int type, String role,Serializable key) {
        boolean cachePoint = false;

        if (type == ComponentCacheKey.ComponentType_Transformer) {
            cachePoint =
                ((Boolean)this.isCachePoint.get(this.firstNotCacheableTransformerIndex)).booleanValue();
        } else if (type == ComponentCacheKey.ComponentType_Serializer) {
            cachePoint = this.nextIsCachePoint;
        }
        return new ComponentCacheKey(type, role, key, cachePoint);
    }

    /**
     * Connect the caching point pipeline.
     */
    protected void connectCachingPipeline(Environment   environment)
    throws ProcessingException {
            try {
                XMLSerializer localXMLSerializer = null;
                XMLSerializer cachePointXMLSerializer = null;
                if (!this.cacheCompleteResponse) {
                    this.xmlSerializer = (XMLSerializer)this.manager.lookup( XMLSerializer.ROLE );
                    localXMLSerializer = this.xmlSerializer;
                }

                if (this.cachedResponse == null) {
                    XMLProducer prev = super.generator;
                    XMLConsumer next;

                    int cacheableTransformerCount = this.firstNotCacheableTransformerIndex;
                    int currentTransformerIndex = 0; //start with the first transformer

                    Iterator itt = this.transformers.iterator();
                    while (itt.hasNext()) {
                        next = (XMLConsumer) itt.next();

                        // if we have cacheable transformers,
                        // check the tranformers for cachepoints
                        if (cacheableTransformerCount > 0) {
                            if ((this.isCachePoint.get(currentTransformerIndex) != null)  &&
                                    ((Boolean)this.isCachePoint.get(currentTransformerIndex)).booleanValue()) {

                                cachePointXMLSerializer = ((XMLSerializer)
                                this.manager.lookup( XMLSerializer.ROLE ));
                                next = new XMLTeePipe(next, cachePointXMLSerializer);
                                this.xmlSerializerArray.add(cachePointXMLSerializer);
                            }
                        }

                        // Serializer is not cacheable,
                        // but we  have the longest cacheable key. Do default longest key caching
                        if (localXMLSerializer != null) {
                            if (cacheableTransformerCount == 0) {
                                next = new XMLTeePipe(next, localXMLSerializer);
                                this.xmlSerializerArray.add(localXMLSerializer);
                                localXMLSerializer = null;
                            } else {
                                cacheableTransformerCount--;
                            }
                        }
                        this.connect(environment, prev, next);
                        prev = (XMLProducer) next;

                        currentTransformerIndex++;
                    }
                    next = super.lastConsumer;

                    // if the serializer is not cacheable, but all the transformers are:
                    // (this is default longest key caching)
                    if (localXMLSerializer != null) {
                        next = new XMLTeePipe(next, localXMLSerializer);
                        this.xmlSerializerArray.add(localXMLSerializer);
                        localXMLSerializer = null;
                    }

                    // else if the serializer is cacheable and has cocoon views
                    else if ((currentTransformerIndex == this.firstNotCacheableTransformerIndex) &&
                            this.nextIsCachePoint) {
                        cachePointXMLSerializer = ((XMLSerializer)this.manager.lookup( XMLSerializer.ROLE ));
                        next = new XMLTeePipe(next, cachePointXMLSerializer);
                        this.xmlSerializerArray.add(cachePointXMLSerializer);
                    }
                    this.connect(environment, prev, next);
                } else {
                    // Here the first part of the pipeline has been retrived from cache
                    // we now check if any part of the rest of the pipeline can be cached
                    this.xmlDeserializer = (XMLDeserializer)this.manager.lookup(XMLDeserializer.ROLE);
                    // connect the pipeline:
                    XMLProducer prev = xmlDeserializer;
                    XMLConsumer next;
                    int cacheableTransformerCount = 0;
                    Iterator itt = this.transformers.iterator();
                    while (itt.hasNext()) {
                        next = (XMLConsumer) itt.next();

                        if (cacheableTransformerCount >= this.firstProcessedTransformerIndex) {

                            // if we have cacheable transformers left,
                            // then check the tranformers for cachepoints
                            if (cacheableTransformerCount < this.firstNotCacheableTransformerIndex) {
                                if (!(prev instanceof XMLDeserializer) &&
                                        (this.isCachePoint.get(cacheableTransformerCount) != null)  &&
                                        ((Boolean)this.isCachePoint.get(cacheableTransformerCount)).booleanValue()) {
                                    cachePointXMLSerializer = ((XMLSerializer)this.manager.lookup( XMLSerializer.ROLE ));
                                    next = new XMLTeePipe(next, cachePointXMLSerializer);
                                    this.xmlSerializerArray.add(cachePointXMLSerializer);
                                }
                            }

                            // Serializer is not cacheable,
                            // but we  have the longest cacheable key. Do default longest key caching
                            if (localXMLSerializer != null && !(prev instanceof XMLDeserializer)
                                    && cacheableTransformerCount == this.firstNotCacheableTransformerIndex) {
                                next = new XMLTeePipe(next, localXMLSerializer);
                                this.xmlSerializerArray.add(localXMLSerializer);
                                localXMLSerializer = null;
                            }
                            this.connect(environment, prev, next);
                            prev = (XMLProducer)next;
                        }
                        cacheableTransformerCount++;
                    }
                    next = super.lastConsumer;

                    //*all* the transformers are cacheable, but the serializer is not!! this is longest key
                    if (localXMLSerializer != null && !(prev instanceof XMLDeserializer)) {
                        next = new XMLTeePipe(next, localXMLSerializer);
                        this.xmlSerializerArray.add(localXMLSerializer);
                        localXMLSerializer = null;
            }
            //	else the serializer is cacheable but has views
            else if (this.nextIsCachePoint && !(prev instanceof XMLDeserializer) &&
                            cacheableTransformerCount == this.firstNotCacheableTransformerIndex) {
                        cachePointXMLSerializer = ((XMLSerializer)this.manager.lookup( XMLSerializer.ROLE ));
                        next = new XMLTeePipe(next,  cachePointXMLSerializer);
                        this.xmlSerializerArray.add(cachePointXMLSerializer);
                    }
                    this.connect(environment, prev, next);
                }

            } catch (ComponentException e) {
                throw new ProcessingException("Could not connect pipeline.", e);
            }
    }

    /**
     * Recyclable Interface
     */
    public void recycle() {
        super.recycle();

        Iterator itt = this.xmlSerializerArray.iterator();
        while (itt.hasNext()) {
            this.manager.release((XMLSerializer) itt.next());
        }
        this.isCachePoint.clear();
        this.xmlSerializerArray.clear();
        this.nextIsCachePoint = false;
        this.autoCachingPointSwitch=null;
    }
    
    boolean setupFromCacheKey() {
        // try a shorter key
        if (this.fromCacheKey.size() > 1) {
            this.fromCacheKey.removeLastKey();
            if (!this.completeResponseIsCached) {
                this.firstProcessedTransformerIndex--;
            }
            return false;
        } else {
            this.fromCacheKey = null;
            return true;
        }
    }
}
