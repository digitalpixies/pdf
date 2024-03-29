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

package org.apache.cocoon.components.treeprocessor.variables;

import java.util.HashMap;
import java.util.Map;

import org.apache.cocoon.SitemapComponentTestCase;
import org.apache.cocoon.components.treeprocessor.InvokeContext;
import org.apache.cocoon.environment.mock.MockRequest;
import org.apache.cocoon.sitemap.PatternException;

/**
 * Test case for the nested variant of the PreparedVariableResolver
 *
 * @author <a href="mailto:uv@upaya.co.uk">Upayavira</a>
 * @version CVS $Id: PreparedVariableResolverTestCase.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class PreparedVariableResolverTestCase extends SitemapComponentTestCase {

    public void testNestedExpressions() throws Exception {
        String expr = "{request-param:{request-param:foo}}";
        MockRequest request = getRequest();
        request.reset();
        request.addParameter("foo", "bar");
        request.addParameter("bar", "123");
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());
        
        Map sitemapElements = new HashMap();
        context.pushMap("sitemap", sitemapElements);
        PreparedVariableResolver resolver = new PreparedVariableResolver(expr, getManager());
        assertEquals("123", resolver.resolve(context, getObjectModel()));
    }

    public void testNestedModuleAndSitemapExpressions() throws Exception {
        String expr = "{request-param:f{1}}";
        MockRequest request = getRequest();
        request.reset();
        request.addParameter("foo", "123");
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());
        
        Map sitemapElements = new HashMap();
        sitemapElements.put("1", "oo");
        context.pushMap("sitemap", sitemapElements);
        PreparedVariableResolver resolver = new PreparedVariableResolver(expr, getManager());
        assertEquals("123", resolver.resolve(context, getObjectModel()));
    }
    
    public void testAnchors() throws PatternException {
        String expr = "{#label:name}";
        
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());
        
        Map sitemapElements = new HashMap();
        sitemapElements.put("name", "123");
        context.pushMap("label", sitemapElements);
        PreparedVariableResolver resolver = new PreparedVariableResolver(expr, getManager());
        assertEquals("123", resolver.resolve(context, getObjectModel()));        
    }
    
    public void testSitemapVariables() throws PatternException {
        String expr = "123{1}";
        
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());

        Map sitemapElements = new HashMap();
        sitemapElements.put("1", "abc");
        context.pushMap("label", sitemapElements);
        PreparedVariableResolver resolver = new PreparedVariableResolver(expr, getManager());
        assertEquals("123abc", resolver.resolve(context, getObjectModel()));
    }

    public void testSitemapVariablesWithText() throws PatternException {
        String expr = "123{1}/def";
    
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());

        Map sitemapElements = new HashMap();
        sitemapElements.put("1", "abc");
        context.pushMap("label", sitemapElements);
        PreparedVariableResolver resolver = new PreparedVariableResolver(expr, getManager());
        assertEquals("123abc/def", resolver.resolve(context, getObjectModel()));
    }
    
    public void testPrefixedSitemapVariable() throws PatternException {
        String expr = "123{sitemap:1}/def";
    
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());

        Map sitemapElements = new HashMap();
        sitemapElements.put("1", "abc");
        context.pushMap("label", sitemapElements);
        PreparedVariableResolver resolver = new PreparedVariableResolver(expr, getManager());
        assertEquals("123abc/def", resolver.resolve(context, getObjectModel()));
    }

    public void testMultilevelSitemapVariables() throws PatternException {
        String expr = "from {../1} to {1}";
        
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());

        Map sitemapElements;
        sitemapElements = new HashMap();
        sitemapElements.put("1", "juliet");
        context.pushMap("label1", sitemapElements);
        
        sitemapElements = new HashMap();
        sitemapElements.put("1", "oscar");
        context.pushMap("label2", sitemapElements);

        PreparedVariableResolver resolver = new PreparedVariableResolver(expr, getManager());
        assertEquals("from juliet to oscar", resolver.resolve(context, getObjectModel()));
    }

    public void testRootSitemapVariables() throws PatternException {
        String expr = "from {/1} to {1}";
        
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());

        Map sitemapElements;
        sitemapElements = new HashMap();
        sitemapElements.put("1", "juliet");
        context.pushMap("label1", sitemapElements);
        
        sitemapElements = new HashMap();
        sitemapElements.put("1", "oscar");
        context.pushMap("label2", sitemapElements);

        PreparedVariableResolver resolver = new PreparedVariableResolver(expr, getManager());
        assertEquals("from juliet to oscar", resolver.resolve(context, getObjectModel()));
    }
    
    public void testColonInTextContent() throws PatternException {
        String expr = "http://cocoon.apache.org";
        
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());

        Map sitemapElements;
        sitemapElements = new HashMap();
        context.pushMap("label", sitemapElements);
        
        PreparedVariableResolver resolver = new PreparedVariableResolver(expr, getManager());
        assertEquals("http://cocoon.apache.org", resolver.resolve(context, getObjectModel()));
    }
    
    public void testColonBeginningTextContent() throws PatternException {
        String expr = ":colon-starts-this";
        
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());

        Map sitemapElements;
        sitemapElements = new HashMap();
        context.pushMap("label", sitemapElements);
        
        PreparedVariableResolver resolver = new PreparedVariableResolver(expr, getManager());
        assertEquals(":colon-starts-this", resolver.resolve(context, getObjectModel()));
    }
    
    public void testEmbeddedColon() throws PatternException {
        String expr = "{1}:{1}";
        
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());

        Map sitemapElements;
        sitemapElements = new HashMap();
        sitemapElements.put("1", "abc");
        context.pushMap("label", sitemapElements);
        
        PreparedVariableResolver resolver = new PreparedVariableResolver(expr, getManager());
        assertEquals("abc:abc", resolver.resolve(context, getObjectModel()));
    }

    public void testEscapedBraces() throws PatternException {
        String expr = "This is a \\{brace\\}";
        
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());

        Map sitemapElements;
        sitemapElements = new HashMap();
        context.pushMap("label", sitemapElements);

        PreparedVariableResolver resolver = new PreparedVariableResolver(expr, getManager());
        assertEquals("This is a {brace}", resolver.resolve(context, getObjectModel()));
    }

    public void testModuleWithoutOption() throws PatternException {
        String expr = "{baselink:}";
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());
        
        Map sitemapElements = new HashMap();
        context.pushMap("sitemap", sitemapElements);
        PreparedVariableResolver resolver = new PreparedVariableResolver(expr, getManager());
        assertEquals("", resolver.resolve(context, getObjectModel()));
    }
}
