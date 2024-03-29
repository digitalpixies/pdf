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
package org.apache.cocoon.components.flow.java;

import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.components.flow.FlowHelper;
import org.apache.cocoon.components.flow.util.PipelineUtil;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.SourceUtil;

import java.io.OutputStream;
import java.util.Map;

/**
 * Abstract class to add basic methods for flow handling.
 *
 * @author <a href="mailto:tcurdt@apache.org">Torsten Curdt</a>
 * @author <a href="mailto:stephan@apache.org">Stephan Michels</a>
 * @version CVS $Id: AbstractContinuable.java 433543 2006-08-22 06:22:54Z crossley $
 */
public abstract class AbstractContinuable implements Continuable {

    private ContinuationContext getContext() {
        if (Continuation.currentContinuation()==null)
            throw new IllegalStateException("No continuation is running");
        return (ContinuationContext) Continuation.currentContinuation().getContext();
    }
  
    public Logger getLogger() {
        return getContext().getLogger();
    }

    public void sendPageAndWait(String uri) {
        sendPageAndWait(uri, new VarMap());
    }

    public void sendPageAndWait(String uri, Object bizdata) {

        ContinuationContext context = getContext();

				if (context.getLogger()!=null)
            context.getLogger().debug("send page and wait '" + uri + "'");

        FlowHelper.setContextObject(ContextHelper.getObjectModel(context.getAvalonContext()), bizdata);

        if (SourceUtil.indexOfSchemeColon(uri) == -1) {
            uri = "cocoon:/" + uri;
            if (getContext().getRedirector().hasRedirected()) {
                throw new IllegalStateException("Pipeline has already been processed for this request");
            }
            try {
                context.getRedirector().redirect(false, uri);
            } catch (Exception e) {
                throw new CascadingRuntimeException("Cannot redirect to '"+uri+"'", e);
            } 
        } else {
            throw new IllegalArgumentException("uri is not allowed to contain a scheme (cocoon:/ is always automatically used)");
        }

        Continuation.suspend();
    }

    public void sendPage(String uri) {
        sendPage(uri, new VarMap());
    }

    public void sendPage(String uri, Object bizdata) {

        ContinuationContext context = getContext();

				if (context.getLogger()!=null)
            context.getLogger().debug("send page '" + uri + "'");

        FlowHelper.setContextObject(ContextHelper.getObjectModel(context.getAvalonContext()), bizdata);

        if (SourceUtil.indexOfSchemeColon(uri) == -1) {
            uri = "cocoon:/" + uri;
            if (getContext().getRedirector().hasRedirected()) {
                throw new IllegalStateException("Pipeline has already been processed for this request");
            }
            try {
                context.getRedirector().redirect(false, uri);
            } catch (Exception e) {
                throw new CascadingRuntimeException("Cannot redirect to '"+uri+"'", e);
            }
        } else {
            throw new IllegalArgumentException("uri is not allowed to contain a scheme (cocoon:/ is always automatically used)");
        }
    }

    public Request getRequest() {
        return ContextHelper.getRequest(getContext().getAvalonContext());
    }
    
    public Map getObjectModel() {
        return ContextHelper.getObjectModel(getContext().getAvalonContext());
    }

    public Parameters getParameters() {
        return getContext().getParameters();
    }

    public void processPipelineTo(String uri, Object bizdata, OutputStream out) {

        ContinuationContext context = getContext();

        PipelineUtil pipeUtil = new PipelineUtil();
        try {          
            pipeUtil.contextualize(context.getAvalonContext());
            pipeUtil.service(context.getServiceManager());
            pipeUtil.processToStream(uri, bizdata, out);
        } catch (Exception e) {
            throw new CascadingRuntimeException("Cannot process pipeline to '"+uri+"'", e);
        } finally {
            pipeUtil.dispose();
        }
    }

    public void redirectTo(String uri) {
        try {
            getContext().getRedirector().redirect(false, uri);
        } catch (Exception e) {
            throw new CascadingRuntimeException("Cannot redirect to '"+uri+"'", e);
        }
    }

    public void sendStatus(int sc) {
        getContext().getRedirector().sendStatus(sc);
    }

    /**
     * Access components.
     */
    public Object getComponent(String id) {
        try {
            return getContext().getServiceManager().lookup(id);
        } catch (Exception e) {
            throw new CascadingRuntimeException("Cannot lookup component '"+id+"'", e);
        }
    }

    /**
     * Release pooled components.
     *
     * @param component a component
     */
    public void releaseComponent( Object component ) {
        if (component != null) {
            getContext().getServiceManager().release(component);
        }
    }
}
