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
package org.apache.cocoon.servlet;

import org.apache.avalon.excalibur.logger.Log4JLoggerManager;
import org.apache.avalon.excalibur.logger.LogKitLoggerManager;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;

import org.apache.cocoon.Cocoon;
import org.apache.cocoon.ConnectionResetException;
import org.apache.cocoon.Constants;
import org.apache.cocoon.ResourceNotFoundException;
import org.apache.cocoon.components.notification.DefaultNotifyingBuilder;
import org.apache.cocoon.components.notification.Notifier;
import org.apache.cocoon.components.notification.Notifying;
import org.apache.cocoon.environment.Environment;
import org.apache.cocoon.environment.http.HttpContext;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.cocoon.servlet.multipart.MultipartHttpServletRequest;
import org.apache.cocoon.servlet.multipart.RequestFactory;
import org.apache.cocoon.util.ClassUtils;
import org.apache.cocoon.util.Deprecation;
import org.apache.cocoon.util.IOUtils;
import org.apache.cocoon.util.StringUtils;
import org.apache.cocoon.util.log.CocoonLogFormatter;
import org.apache.cocoon.util.log.Log4JConfigurator;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.manager.impl.DefaultInstrumentManagerImpl;
import org.apache.log.ContextMap;
import org.apache.log.ErrorHandler;
import org.apache.log.Hierarchy;
import org.apache.log.Priority;
import org.apache.log.output.ServletOutputLogTarget;
import org.apache.log.util.DefaultErrorHandler;
import org.apache.log4j.LogManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * This is the entry point for Cocoon execution as an HTTP Servlet.
 *
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 *         (Apache Software Foundation)
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:nicolaken@apache.org">Nicola Ken Barozzi</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @author <a href="mailto:leo.sutic@inspireinfrastructure.com">Leo Sutic</a>
 * @version $Id: CocoonServlet.java 606745 2007-12-24 22:26:08Z solprovider $
 */
public class CocoonServlet extends HttpServlet {

    /**
     * Application <code>Context</code> Key for the servlet configuration
     * @since 2.1.3
     */
    public static final String CONTEXT_SERVLET_CONFIG = "servlet-config";

    // Processing time message
    protected static final String PROCESSED_BY = "Processed by "
            + Constants.COMPLETE_NAME + " in ";

    // Used by "show-time"
    static final float SECOND = 1000;
    static final float MINUTE = 60 * SECOND;
    static final float HOUR   = 60 * MINUTE;

    private Logger log;
    private LoggerManager loggerManager;

    /**
     * The time the cocoon instance was created
     */
    protected long creationTime;

    /**
     * The <code>Cocoon</code> instance
     */
    protected Cocoon cocoon;

    /**
     * Holds exception happened during initialization (if any)
     */
    protected Exception exception;

    /**
     * Avalon application context
     */
    protected DefaultContext appContext = new DefaultContext();


    /**
     * Default value for {@link #allowReload} parameter (false)
     */
    protected static final boolean ALLOW_RELOAD = false;

    /**
     * Allow reloading of cocoon by specifying the
     * <code>cocoon-reload=true</code> parameter with a request
     */
    protected boolean allowReload;


    /**
     * Allow adding processing time to the response
     */
    protected boolean showTime;

    /**
     * If true, processing time will be added as an HTML comment
     */
    protected boolean hiddenShowTime;

    /** Flag to enable/disable X-Cocoon-Version header */
    private boolean showCocoonVersion;

    /**
     * Default value for {@link #enableUploads} parameter (false)
     */
    private static final boolean ENABLE_UPLOADS = false;
    private static final boolean SAVE_UPLOADS_TO_DISK = true;
    private static final int MAX_UPLOAD_SIZE = 10000000; // 10Mb

    /**
     * Allow processing of upload requests (mime/multipart)
     */
    private boolean enableUploads;
    private boolean autoSaveUploads;
    private boolean allowOverwrite;
    private boolean silentlyRename;
    private int maxUploadSize;

    private File uploadDir;
    private File workDir;
    private File cacheDir;
    private String containerEncoding;
    private String defaultFormEncoding;

    protected ServletContext servletContext;

    /** The classloader that will be set as the context classloader if init-classloader is true */
    protected ClassLoader classLoader = this.getClass().getClassLoader();
    protected boolean initClassLoader = false;

    private String parentComponentManagerClass;
    private String parentComponentManagerInitParam;

    /** The parent ComponentManager, if any. Stored here in order to be able to dispose it in destroy(). */
    private ComponentManager parentComponentManager;

    protected String forceLoadParameter;
    protected String forceSystemProperty;

    /**
     * If true or not set, this class will try to catch and handle all Cocoon exceptions.
     * If false, it will rethrow them to the servlet container.
     */
    private boolean manageExceptions;

    /**
     * Flag to enable avalon excalibur instrumentation of Cocoon.
     */
    private boolean enableInstrumentation;

    /**
     * The <code>InstrumentManager</code> instance
     */
    private InstrumentManager instrumentManager;

    /**
     * This is the path to the servlet context (or the result
     * of calling getRealPath('/') on the ServletContext.
     * Note, that this can be null.
     */
    protected String servletContextPath;

    /**
     * This is the url to the servlet context directory
     */
    protected String servletContextURL;

    /**
     * The RequestFactory is responsible for wrapping multipart-encoded
     * forms and for handing the file payload of incoming requests
     */
    protected RequestFactory requestFactory;

    /**
     * Initialize this <code>CocoonServlet</code> instance.  You will
     * notice that I have broken the init into sub methods to make it
     * easier to maintain (BL).  The context is passed to a couple of
     * the subroutines.  This is also because it is better to explicitly
     * pass variables than implicitely.  It is both more maintainable,
     * and more elegant.
     *
     * @param conf The ServletConfig object from the servlet engine.
     *
     * @throws ServletException
     */
    public void init(ServletConfig conf)
    throws ServletException {

        super.init(conf);

        // Check the init-classloader parameter only if it's not already true.
        // This is useful for subclasses of this servlet that override the value
        // initially set by this class (i.e. false).
        if (!this.initClassLoader) {
            this.initClassLoader = getInitParameterAsBoolean("init-classloader", false);
        }

        if (this.initClassLoader) {
            // Force context classloader so that JAXP can work correctly
            // (see javax.xml.parsers.FactoryFinder.findClassLoader())
            try {
                Thread.currentThread().setContextClassLoader(this.classLoader);
            } catch (Exception e) {
                // ignore this
            }
        }

        try {
            // FIXME (VG): We shouldn't have to specify these. Need to override
            // jaxp implementation of weblogic before initializing logger.
            // This piece of code is also required in the Cocoon class.
            String value = System.getProperty("javax.xml.parsers.SAXParserFactory");
            if (value != null && value.startsWith("weblogic")) {
                System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
                System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
            }
        } catch (Exception e) {
            // Ignore security exception
            System.out.println("CocoonServlet: Could not check system properties, got: " + e);
        }

        this.servletContext = conf.getServletContext();
        this.appContext.put(Constants.CONTEXT_ENVIRONMENT_CONTEXT, new HttpContext(this.servletContext));
        this.servletContextPath = this.servletContext.getRealPath("/");

        // first init the work-directory for the logger.
        // this is required if we are running inside a war file!
        final String workDirParam = getInitParameter("work-directory");
        if (workDirParam != null) {
            if (this.servletContextPath == null) {
                // No context path : consider work-directory as absolute
                this.workDir = new File(workDirParam);
            } else {
                // Context path exists : is work-directory absolute ?
                File workDirParamFile = new File(workDirParam);
                if (workDirParamFile.isAbsolute()) {
                    // Yes : keep it as is
                    this.workDir = workDirParamFile;
                } else {
                    // No : consider it relative to context path
                    this.workDir = new File(servletContextPath, workDirParam);
                }
            }
        } else {
            this.workDir = (File) this.servletContext.getAttribute("javax.servlet.context.tempdir");
            this.workDir = new File(workDir, "cocoon-files");
        }
        this.workDir.mkdirs();
        this.appContext.put(Constants.CONTEXT_WORK_DIR, workDir);

        String path = this.servletContextPath;
        // these two variables are just for debugging. We can't log at this point
        // as the logger isn't initialized yet.
        String debugPathOne = null, debugPathTwo = null;
        if (path == null) {
            // Try to figure out the path of the root from that of WEB-INF
            try {
                path = this.servletContext.getResource("/WEB-INF").toString();
            } catch (MalformedURLException me) {
                throw new ServletException("Unable to get resource 'WEB-INF'.", me);
            }
            debugPathOne = path;
            path = path.substring(0, path.length() - "WEB-INF".length());
            debugPathTwo = path;
        }
        try {
            if (path.indexOf(':') > 1) {
                this.servletContextURL = path;
            } else {
                this.servletContextURL = new File(path).toURL().toExternalForm();
            }
        } catch (MalformedURLException me) {
            // VG: Novell has absolute file names starting with the
            // volume name which is easily more then one letter.
            // Examples: sys:/apache/cocoon or sys:\apache\cocoon
            try {
                this.servletContextURL = new File(path).toURL().toExternalForm();
            } catch (MalformedURLException ignored) {
                throw new ServletException("Unable to determine servlet context URL.", me);
            }
        }
        try {
            this.appContext.put("context-root", new URL(this.servletContextURL));
        } catch (MalformedURLException ignore) {
            // we simply ignore this
        }

        // Init logger
        initLogger();

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("getRealPath for /: " + this.servletContextPath);
            if (this.servletContextPath == null) {
                getLogger().debug("getResource for /WEB-INF: " + debugPathOne);
                getLogger().debug("Path for Root: " + debugPathTwo);
            }
        }

        this.forceLoadParameter = getInitParameter("load-class", null);
        this.forceSystemProperty = getInitParameter("force-property", null);

        // Output some debug info
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Servlet Context URL: " + this.servletContextURL);
            if (workDirParam != null) {
                getLogger().debug("Using work-directory " + this.workDir);
            } else {
                getLogger().debug("Using default work-directory " + this.workDir);
            }
        }

        final String uploadDirParam = conf.getInitParameter("upload-directory");
        if (uploadDirParam != null) {
            if (this.servletContextPath == null) {
                this.uploadDir = new File(uploadDirParam);
            } else {
                // Context path exists : is upload-directory absolute ?
                File uploadDirParamFile = new File(uploadDirParam);
                if (uploadDirParamFile.isAbsolute()) {
                    // Yes : keep it as is
                    this.uploadDir = uploadDirParamFile;
                } else {
                    // No : consider it relative to context path
                    this.uploadDir = new File(servletContextPath, uploadDirParam);
                }
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Using upload-directory " + this.uploadDir);
            }
        } else {
            this.uploadDir = new File(workDir, "upload-dir" + File.separator);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Using default upload-directory " + this.uploadDir);
            }
        }
        this.uploadDir.mkdirs();
        this.appContext.put(Constants.CONTEXT_UPLOAD_DIR, this.uploadDir);

        this.enableUploads = getInitParameterAsBoolean("enable-uploads", ENABLE_UPLOADS);

        this.autoSaveUploads = getInitParameterAsBoolean("autosave-uploads", SAVE_UPLOADS_TO_DISK);

        String overwriteParam = getInitParameter("overwrite-uploads", "rename");
        // accepted values are deny|allow|rename - rename is default.
        if ("deny".equalsIgnoreCase(overwriteParam)) {
            this.allowOverwrite = false;
            this.silentlyRename = false;
        } else if ("allow".equalsIgnoreCase(overwriteParam)) {
            this.allowOverwrite = true;
            this.silentlyRename = false; // ignored in this case
        } else {
            // either rename is specified or unsupported value - default to rename.
            this.allowOverwrite = false;
            this.silentlyRename = true;
        }

        this.maxUploadSize = getInitParameterAsInteger("upload-max-size", MAX_UPLOAD_SIZE);

        String cacheDirParam = conf.getInitParameter("cache-directory");
        if (cacheDirParam != null) {
            if (this.servletContextPath == null) {
                this.cacheDir = new File(cacheDirParam);
            } else {
                // Context path exists : is cache-directory absolute ?
                File cacheDirParamFile = new File(cacheDirParam);
                if (cacheDirParamFile.isAbsolute()) {
                    // Yes : keep it as is
                    this.cacheDir = cacheDirParamFile;
                } else {
                    // No : consider it relative to context path
                    this.cacheDir = new File(servletContextPath, cacheDirParam);
                }
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Using cache-directory " + this.cacheDir);
            }
        } else {
            this.cacheDir = IOUtils.createFile(workDir, "cache-dir" + File.separator);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("cache-directory was not set - defaulting to " + this.cacheDir);
            }
        }
        this.cacheDir.mkdirs();
        this.appContext.put(Constants.CONTEXT_CACHE_DIR, this.cacheDir);

        this.appContext.put(Constants.CONTEXT_CONFIG_URL,
                            getConfigFile(conf.getInitParameter("configurations")));
        if (conf.getInitParameter("configurations") == null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("configurations was not set - defaulting to... ?");
            }
        }

        // get allow reload parameter, default is true
        this.allowReload = getInitParameterAsBoolean("allow-reload", ALLOW_RELOAD);

        String value = conf.getInitParameter("show-time");
        this.showTime = BooleanUtils.toBoolean(value) || (this.hiddenShowTime = "hide".equals(value));
        if (value == null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("show-time was not set - defaulting to false");
            }
        }

        this.showCocoonVersion = getInitParameterAsBoolean("show-cocoon-version", true);

        parentComponentManagerClass = getInitParameter("parent-component-manager", null);
        if (parentComponentManagerClass != null) {
            int dividerPos = parentComponentManagerClass.indexOf('/');
            if (dividerPos != -1) {
                parentComponentManagerInitParam = parentComponentManagerClass.substring(dividerPos + 1);
                parentComponentManagerClass = parentComponentManagerClass.substring(0, dividerPos);
            }
        }

        this.containerEncoding = getInitParameter("container-encoding", "ISO-8859-1");
        this.defaultFormEncoding = getInitParameter("form-encoding", "ISO-8859-1");
        this.appContext.put(Constants.CONTEXT_DEFAULT_ENCODING, this.defaultFormEncoding);

        this.manageExceptions = getInitParameterAsBoolean("manage-exceptions", true);

        this.enableInstrumentation = getInitParameterAsBoolean("enable-instrumentation", false);

        this.requestFactory = new RequestFactory(this.autoSaveUploads,
                                                 this.uploadDir,
                                                 this.allowOverwrite,
                                                 this.silentlyRename,
                                                 this.maxUploadSize,
                                                 this.containerEncoding);
        // Add the servlet configuration
        this.appContext.put(CONTEXT_SERVLET_CONFIG, conf);
        this.createCocoon();
    }

    /**
     * Dispose Cocoon when servlet is destroyed
     */
    public void destroy() {
        if (this.initClassLoader) {
            try {
                Thread.currentThread().setContextClassLoader(this.classLoader);
            } catch (Exception e) {
                // Ignored
            }
        }

        if (this.cocoon != null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Servlet destroyed - disposing Cocoon");
            }
            disposeCocoon();
        }

        if (this.instrumentManager instanceof Disposable) {
            ((Disposable) this.instrumentManager).dispose();
        }

        if (this.parentComponentManager != null && this.parentComponentManager instanceof Disposable) {
            ((Disposable) this.parentComponentManager).dispose();
        }

        this.appContext = null;
        this.classLoader = null;
        this.log = null;
        this.loggerManager = null;
    }

    /**
     * Adds an URL to the classloader. Does nothing here, but is
     * overriden in {@link ParanoidCocoonServlet}.
     */
    protected void addClassLoaderURL(URL URL) {
        // Nothing
    }

    /**
     * Adds a directory to the classloader. Does nothing here, but is
     * overriden in {@link ParanoidCocoonServlet}.
     */
    protected void addClassLoaderDirectory(String dir) {
        // Nothing
    }

    /**
     * This builds the important ClassPath used by this Servlet.  It
     * does so in a Servlet Engine neutral way.  It uses the
     * <code>ServletContext</code>'s <code>getRealPath</code> method
     * to get the Servlet 2.2 identified classes and lib directories.
     * It iterates in alphabetical order through every file in the
     * lib directory and adds it to the classpath.
     *
     * Also, we add the files to the ClassLoader for the Cocoon system.
     * In order to protect ourselves from skitzofrantic classloaders,
     * we need to work with a known one.
     *
     * We need to get this to work properly when Cocoon is in a war.
     *
     * @throws ServletException
     */
    protected String getClassPath() throws ServletException {
        StringBuffer buildClassPath = new StringBuffer();

        File root = null;
        if (servletContextPath != null) {
            // Old method.  There *MUST* be a better method than this...

            String classDir = this.servletContext.getRealPath("/WEB-INF/classes");
            String libDir = this.servletContext.getRealPath("/WEB-INF/lib");

            if (libDir != null) {
                root = new File(libDir);
            }

            if (classDir != null) {
                buildClassPath.append(classDir);

                addClassLoaderDirectory(classDir);
            }
        } else {
            // New(ish) method for war'd deployments
            URL classDirURL = null;
            URL libDirURL = null;

            try {
                classDirURL = this.servletContext.getResource("/WEB-INF/classes");
            } catch (MalformedURLException me) {
                if (getLogger().isWarnEnabled()) {
                    this.getLogger().warn("Unable to add WEB-INF/classes to the classpath", me);
                }
            }

            try {
                libDirURL = this.servletContext.getResource("/WEB-INF/lib");
            } catch (MalformedURLException me) {
                if (getLogger().isWarnEnabled()) {
                    this.getLogger().warn("Unable to add WEB-INF/lib to the classpath", me);
                }
            }

            if (libDirURL != null && libDirURL.toExternalForm().startsWith("file:")) {
                root = new File(libDirURL.toExternalForm().substring("file:".length()));
            }

            if (classDirURL != null) {
                buildClassPath.append(classDirURL.toExternalForm());

                addClassLoaderURL(classDirURL);
            }
        }

        // Unable to find lib directory. Going the hard way.
        if (root == null) {
            root = extractLibraries();
        }

        if (root != null && root.isDirectory()) {
            File[] libraries = root.listFiles();
            Arrays.sort(libraries);
            for (int i = 0; i < libraries.length; i++) {
                String fullName = IOUtils.getFullFilename(libraries[i]);
                buildClassPath.append(File.pathSeparatorChar).append(fullName);

                addClassLoaderDirectory(fullName);
            }
        }

        buildClassPath.append(File.pathSeparatorChar)
                      .append(SystemUtils.JAVA_CLASS_PATH);

        buildClassPath.append(File.pathSeparatorChar)
                      .append(getExtraClassPath());
        return buildClassPath.toString();
    }

    private File extractLibraries() {
        try {
            URL manifestURL = this.servletContext.getResource("/META-INF/MANIFEST.MF");
            if (manifestURL == null) {
                this.getLogger().fatalError("Unable to get Manifest");
                return null;
            }

            Manifest mf = new Manifest(manifestURL.openStream());
            Attributes attr = mf.getMainAttributes();
            String libValue = attr.getValue("Cocoon-Libs");
            if (libValue == null) {
                this.getLogger().fatalError("Unable to get 'Cocoon-Libs' attribute from the Manifest");
                return null;
            }

            List libList = new ArrayList();
            for (StringTokenizer st = new StringTokenizer(libValue, " "); st.hasMoreTokens();) {
                libList.add(st.nextToken());
            }

            File root = new File(this.workDir, "lib");
            root.mkdirs();

            File[] oldLibs = root.listFiles();
            for (int i = 0; i < oldLibs.length; i++) {
                String oldLib = oldLibs[i].getName();
                if (!libList.contains(oldLib)) {
                    this.getLogger().debug("Removing old library " + oldLibs[i]);
                    oldLibs[i].delete();
                }
            }

            this.getLogger().warn("Extracting libraries into " + root);
            byte[] buffer = new byte[65536];
            for (Iterator i = libList.iterator(); i.hasNext();) {
                String libName = (String) i.next();

                long lastModified = -1;
                try {
                    lastModified = Long.parseLong(attr.getValue("Cocoon-Lib-" + libName.replace('.', '_')));
                } catch (Exception e) {
                    this.getLogger().debug("Failed to parse lastModified: " + attr.getValue("Cocoon-Lib-" + libName.replace('.', '_')));
                }

                File lib = new File(root, libName);
                if (lib.exists() && lib.lastModified() != lastModified) {
                    this.getLogger().debug("Removing modified library " + lib);
                    lib.delete();
                }

                InputStream is = null;
                OutputStream os = null;
                try {
                    is = this.servletContext.getResourceAsStream("/WEB-INF/lib/" + libName);
                    if (is != null) {
                        this.getLogger().debug("Extracting " + libName);
                        os = new FileOutputStream(lib);
                        int count;
                        while ((count = is.read(buffer)) > 0) {
                            os.write(buffer, 0, count);
                        }
                    } else {
                        this.getLogger().warn("Skipping " + libName);
                    }
                } finally {
                    if (os != null) os.close();
                    if (is != null) is.close();
                }

                if (lastModified != -1) {
                    lib.setLastModified(lastModified);
                }
            }

            return root;
        } catch (IOException e) {
            this.getLogger().fatalError("Exception while processing Manifest file", e);
            return null;
        }
    }

    /**
     * Retreives the "extra-classpath" attribute, that needs to be
     * added to the class path.
     *
     * @throws ServletException
     */
    protected String getExtraClassPath() throws ServletException {
        String extraClassPath = this.getInitParameter("extra-classpath");
        if (extraClassPath != null) {
            StringBuffer sb = new StringBuffer();
            StringTokenizer st = new StringTokenizer(extraClassPath, SystemUtils.PATH_SEPARATOR, false);
            int i = 0;
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (i++ > 0) {
                    sb.append(File.pathSeparatorChar);
                }
                if ((s.charAt(0) == File.separatorChar) ||
                        (s.charAt(1) == ':')) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("extraClassPath is absolute: " + s);
                    }
                    sb.append(s);

                    addClassLoaderDirectory(s);
                } else {
                    if (s.indexOf("${") != -1) {
                        String path = StringUtils.replaceToken(s);
                        sb.append(path);
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("extraClassPath is not absolute replacing using token: [" + s + "] : " + path);
                        }
                        addClassLoaderDirectory(path);
                    } else {
                        String path = null;
                        if (this.servletContextPath != null) {
                            path = this.servletContextPath + s;
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug("extraClassPath is not absolute pre-pending context path: " + path);
                            }
                        } else {
                            path = this.workDir.toString() + s;
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug("extraClassPath is not absolute pre-pending work-directory: " + path);
                            }
                        }
                        sb.append(path);
                        addClassLoaderDirectory(path);
                    }
                }
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * Set up the log level and path.  The default log level is
     * Priority.ERROR, although it can be overwritten by the parameter
     * "log-level".  The log system goes to both a file and the Servlet
     * container's log system.  Only messages that are Priority.ERROR
     * and above go to the servlet context.  The log messages can
     * be as restrictive (Priority.FATAL_ERROR and above) or as liberal
     * (Priority.DEBUG and above) as you want that get routed to the
     * file.
     */
    protected void initLogger() {
        final String logLevel = getInitParameter("log-level", "INFO");

        final String accesslogger = getInitParameter("servlet-logger", "cocoon");

        final Priority logPriority = Priority.getPriorityForName(logLevel);

        final CocoonLogFormatter formatter = new CocoonLogFormatter();
        formatter.setFormat("%7.7{priority} %{time}   [%8.8{category}] " +
                            "(%{uri}) %{thread}/%{class:short}: %{message}\\n%{throwable}");
        final ServletOutputLogTarget servTarget = new ServletOutputLogTarget(this.servletContext, formatter);

        final Hierarchy defaultHierarchy = Hierarchy.getDefaultHierarchy();
        final ErrorHandler errorHandler = new DefaultErrorHandler();
        defaultHierarchy.setErrorHandler(errorHandler);
        defaultHierarchy.setDefaultLogTarget(servTarget);
        defaultHierarchy.setDefaultPriority(logPriority);
        final Logger logger = new LogKitLogger(Hierarchy.getDefaultHierarchy().getLoggerFor(""));
        final String loggerManagerClass =
            this.getInitParameter("logger-class", LogKitLoggerManager.class.getName());

        // the log4j support requires currently that the log4j system is already configured elsewhere

        final LoggerManager loggerManager =
                newLoggerManager(loggerManagerClass, defaultHierarchy);
        ContainerUtil.enableLogging(loggerManager, logger);

        final DefaultContext subcontext = new DefaultContext(this.appContext);
        subcontext.put("servlet-context", this.servletContext);
        subcontext.put("context-work", this.workDir);
        if (this.servletContextPath == null) {
            File logSCDir = new File(this.workDir, "log");
            logSCDir.mkdirs();
            if (logger.isWarnEnabled()) {
                logger.warn("Setting context-root for LogKit to " + logSCDir);
            }
            subcontext.put("context-root", logSCDir.toString());
        } else {
            subcontext.put("context-root", this.servletContextPath);
        }

        try {
            ContainerUtil.contextualize(loggerManager, subcontext);
            this.loggerManager = loggerManager;

            if (loggerManager instanceof Configurable) {
                //Configure the logkit management
                String logkitConfig = getInitParameter("logkit-config", "/WEB-INF/logkit.xconf");

                // test if this is a qualified url
                InputStream is = null;
                if (logkitConfig.indexOf(':') == -1) {
                    is = this.servletContext.getResourceAsStream(logkitConfig);
                    if (is == null) is = new FileInputStream(logkitConfig);
                } else {
                    URL logkitURL = new URL(logkitConfig);
                    is = logkitURL.openStream();
                }
                final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
                final Configuration conf = builder.build(is);
                ContainerUtil.configure(loggerManager, conf);
            }

            // let's configure log4j
            final String log4jConfig = getInitParameter("log4j-config", null);
            if ( log4jConfig != null ) {
                final Log4JConfigurator configurator = new Log4JConfigurator(subcontext);

                // test if this is a qualified url
                InputStream is = null;
                if ( log4jConfig.indexOf(':') == -1) {
                    is = this.servletContext.getResourceAsStream(log4jConfig);
                    if (is == null) is = new FileInputStream(log4jConfig);
                } else {
                    final URL log4jURL = new URL(log4jConfig);
                    is = log4jURL.openStream();
                }
                configurator.doConfigure(is, LogManager.getLoggerRepository());
            }

            ContainerUtil.initialize(loggerManager);
        } catch (Exception e) {
            errorHandler.error("Could not set up Cocoon Logger, will use screen instead", e, null);
        }

        this.log = this.loggerManager.getLoggerForCategory(accesslogger);

        final String deprecationLevel = getInitParameter("forbidden-deprecation-level", "ERROR");
        Deprecation.setForbiddenLevel(Deprecation.LogLevel.getLevel(deprecationLevel));
    }

    private LoggerManager newLoggerManager(String loggerManagerClass, Hierarchy hierarchy) {
        if (loggerManagerClass.equals(LogKitLoggerManager.class.getName())) {
            return new LogKitLoggerManager(hierarchy);
        } else if (loggerManagerClass.equals(Log4JLoggerManager.class.getName()) ||
                   loggerManagerClass.equalsIgnoreCase("LOG4J")) {
            return new Log4JLoggerManager();
        } else {
            try {
                Class clazz = Class.forName(loggerManagerClass);
                return (LoggerManager)clazz.newInstance();
            } catch (Exception e) {
                return new LogKitLoggerManager(hierarchy);
            }
        }
    }

    /**
     * Set the ConfigFile for the Cocoon object.
     *
     * @param configFileName The file location for the cocoon.xconf
     *
     * @throws ServletException
     */
    private URL getConfigFile(final String configFileName)
    throws ServletException {
        final String usedFileName;

        if (configFileName == null) {
            if (getLogger().isWarnEnabled()) {
                getLogger().warn("Servlet initialization argument 'configurations' not specified, attempting to use '/WEB-INF/cocoon.xconf'");
            }
            usedFileName = "/WEB-INF/cocoon.xconf";
        } else {
            usedFileName = configFileName;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Using configuration file: " + usedFileName);
        }

        URL result;
        try {
            // test if this is a qualified url
            if (usedFileName.indexOf(':') == -1) {
                result = this.servletContext.getResource(usedFileName);
            } else {
                result = new URL(usedFileName);
            }
        } catch (Exception mue) {
            String msg = "Init parameter 'configurations' is invalid : " + usedFileName;
            getLogger().error(msg, mue);
            throw new ServletException(msg, mue);
        }

        if (result == null) {
            File resultFile = new File(usedFileName);
            if (resultFile.isFile()) {
                try {
                    result = resultFile.getCanonicalFile().toURL();
                } catch (Exception e) {
                    String msg = "Init parameter 'configurations' is invalid : " + usedFileName;
                    getLogger().error(msg, e);
                    throw new ServletException(msg, e);
                }
            }
        }

        if (result == null) {
            String msg = "Init parameter 'configurations' doesn't name an existing resource : " + usedFileName;
            getLogger().error(msg);
            throw new ServletException(msg);
        }
        return result;
    }

    /**
     * Handle the <code>load-class</code> parameter. This overcomes
     * limits in many classpath issues. One of the more notorious
     * ones is a bug in WebSphere that does not load the URL handler
     * for the <code>classloader://</code> protocol. In order to
     * overcome that bug, set <code>load-class</code> parameter to
     * the <code>com.ibm.servlet.classloader.Handler</code> value.
     *
     * <p>If you need to load more than one class, then separate each
     * entry with whitespace, a comma, or a semi-colon. Cocoon will
     * strip any whitespace from the entry.</p>
     */
    private void forceLoad() {
        if (this.forceLoadParameter != null) {
            StringTokenizer fqcnTokenizer = new StringTokenizer(forceLoadParameter, " \t\r\n\f;,", false);

            while (fqcnTokenizer.hasMoreTokens()) {
                final String fqcn = fqcnTokenizer.nextToken().trim();

                try {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Loading: " + fqcn);
                    }
                    ClassUtils.loadClass(fqcn).newInstance();
                } catch (Exception e) {
                    if (getLogger().isWarnEnabled()) {
                        getLogger().warn("Could not load class: " + fqcn, e);
                    }
                    // Do not throw an exception, because it is not a fatal error.
                }
            }
        }
    }

    /**
     * Handle the "force-property" parameter.
     *
     * If you need to force more than one property to load, then
     * separate each entry with whitespace, a comma, or a semi-colon.
     * Cocoon will strip any whitespace from the entry.
     */
    private void forceProperty() {
        if (this.forceSystemProperty != null) {
            StringTokenizer tokenizer = new StringTokenizer(forceSystemProperty, " \t\r\n\f;,", false);

            while (tokenizer.hasMoreTokens()) {
                final String property = tokenizer.nextToken().trim();
                if (property.indexOf('=') == -1) {
                    continue;
                }
                try {
                    String key = property.substring(0, property.indexOf('='));
                    String value = property.substring(property.indexOf('=') + 1);
                    if (value.indexOf("${") != -1) {
                        value = StringUtils.replaceToken(value);
                    }
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Setting " + key + "=" + value);
                    }
                    System.setProperty(key, value);
                } catch (Exception e) {
                    if (getLogger().isWarnEnabled()) {
                        getLogger().warn("Could not set property: " + property, e);
                    }
                    // Do not throw an exception, because it is not a fatal error.
                }
            }
        }
    }

    /**
     * Process the specified <code>HttpServletRequest</code> producing output
     * on the specified <code>HttpServletResponse</code>.
     */
    public void service(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {

        /* HACK for reducing class loader problems.                                     */
        /* example: xalan extensions fail if someone adds xalan jars in tomcat3.2.1/lib */
        if (this.initClassLoader) {
            try {
                Thread.currentThread().setContextClassLoader(this.classLoader);
            } catch (Exception e) {
            }
        }

        // used for timing the processing
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // add the cocoon header timestamp
        if (this.showCocoonVersion) {
            res.addHeader("X-Cocoon-Version", Constants.VERSION);
        }

        // get the request (wrapped if contains multipart-form data)
        HttpServletRequest request;
        try{
            if (this.enableUploads) {
                request = requestFactory.getServletRequest(req);
            } else {
                request = req;
            }
        } catch (Exception e) {
            if (getLogger().isErrorEnabled()) {
                getLogger().error("Problem with Cocoon servlet", e);
            }

            manageException(req, res, null, null,
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Problem in creating the Request", null, null, e);
            return;
        }

        // Get the cocoon engine instance

        if (reloadCocoon(request.getPathInfo(), request.getParameter(Constants.RELOAD_PARAM))) {
            disposeCocoon();
            initLogger();
            createCocoon();
        }

        // Check if cocoon was initialized
        if (this.cocoon == null) {
            manageException(request, res, null, null,
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Initialization Problem",
                            null /* "Cocoon was not initialized" */,
                            null /* "Cocoon was not initialized, cannot process request" */,
                            this.exception);
            return;
        }

        // We got it... Process the request
        String uri = request.getServletPath();
        if (uri == null) {
            uri = "";
        }
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            // VG: WebLogic fix: Both uri and pathInfo starts with '/'
            // This problem exists only in WL6.1sp2, not in WL6.0sp2 or WL7.0b.
            if (uri.length() > 0 && uri.charAt(0) == '/') {
                uri = uri.substring(1);
            }
            uri += pathInfo;
        }

        if (uri.length() == 0) {
            /* empty relative URI
                 -> HTTP-redirect from /cocoon to /cocoon/ to avoid
                    StringIndexOutOfBoundsException when calling
                    "".charAt(0)
               else process URI normally
            */
            String prefix = request.getRequestURI();
            if (prefix == null) {
                prefix = "";
            }

            res.sendRedirect(res.encodeRedirectURL(prefix + "/"));
            return;
        }

        String contentType = null;
        ContextMap ctxMap = null;

        Environment env;
        try{
            if (uri.charAt(0) == '/') {
                uri = uri.substring(1);
            }
            // Pass uri into environment without URLDecoding, as it is already decoded.
            env = getEnvironment(uri, request, res);
        } catch (Exception e) {
            if (getLogger().isErrorEnabled()) {
                getLogger().error("Problem with Cocoon servlet", e);
            }

            manageException(request, res, null, uri,
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Problem in creating the Environment", null, null, e);
            return;
        }

        try {
            try {
                // Initialize a fresh log context containing the object model: it
                // will be used by the CocoonLogFormatter
                ctxMap = ContextMap.getCurrentContext();
                // Add thread name (default content for empty context)
                String threadName = Thread.currentThread().getName();
                ctxMap.set("threadName", threadName);
                // Add the object model
                ctxMap.set("objectModel", env.getObjectModel());
                // Add a unique request id (threadName + currentTime
                ctxMap.set("request-id", threadName + System.currentTimeMillis());

                if (this.cocoon.process(env)) {
                    contentType = env.getContentType();
                } else {
                    // We reach this when there is nothing in the processing change that matches
                    // the request. For example, no matcher matches.
                    getLogger().fatalError("The Cocoon engine failed to process the request.");
                    manageException(request, res, env, uri,
                                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                    "Request Processing Failed",
                                    "Cocoon engine failed in process the request",
                                    "The processing engine failed to process the request. This could be due to lack of matching or bugs in the pipeline engine.",
                                    null);
                    return;
                }
            } catch (ResourceNotFoundException e) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().warn(e.getMessage(), e);
                } else if (getLogger().isWarnEnabled()) {
                    getLogger().warn(e.getMessage());
                }

                manageException(request, res, env, uri,
                                HttpServletResponse.SC_NOT_FOUND,
                                "Resource Not Found",
                                "Resource Not Found",
                                "The requested resource \"" + request.getRequestURI() + "\" could not be found",
                                e);
                return;

            } catch (ConnectionResetException e) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(e.toString(), e);
                } else if (getLogger().isWarnEnabled()) {
                    getLogger().warn(e.toString());
                }

            } catch (IOException e) {
                // Tomcat5 wraps SocketException into ClientAbortException which extends IOException.
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(e.toString(), e);
                } else if (getLogger().isWarnEnabled()) {
                    getLogger().warn(e.toString());
                }

            } catch (Exception e) {
                if (getLogger().isErrorEnabled()) {
                    getLogger().error("Internal Cocoon Problem", e);
                }

                manageException(request, res, env, uri,
                                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                "Internal Server Error", null, null, e);
                return;
            }

            stopWatch.stop();
            String timeString = null;
            if (getLogger().isInfoEnabled()) {
                timeString = processTime(stopWatch.getTime());
                getLogger().info("'" + uri + "' " + timeString);
            }

            if (contentType != null && contentType.equals("text/html")) {
                String showTime = request.getParameter(Constants.SHOWTIME_PARAM);
                boolean show = this.showTime;
                if (showTime != null) {
                    show = !showTime.equalsIgnoreCase("no");
                }
                if (show) {
                    if (timeString == null) {
                        timeString = processTime(stopWatch.getTime());
                    }
                    boolean hide = this.hiddenShowTime;
                    if (showTime != null) {
                        hide = showTime.equalsIgnoreCase("hide");
                    }
                    ServletOutputStream out = res.getOutputStream();
                    out.print((hide) ? "<!-- " : "<p>");
                    out.print(timeString);
                    out.println((hide) ? " -->" : "</p>");
                }
            }
        } finally {
            if (ctxMap != null) {
                ctxMap.clear();
            }

            try {
                if (request instanceof MultipartHttpServletRequest) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Deleting uploaded file(s).");
                    }
                    ((MultipartHttpServletRequest) request).cleanup();
                }
            } catch (IOException e) {
                getLogger().error("Cocoon got an Exception while trying to cleanup the uploaded files.", e);
            }

            /*
             * Servlet Specification 2.2, 6.5 Closure of Response Object:
             *
             *   A number of events can indicate that the servlet has provided all of the
             *   content to satisfy the request and that the response object can be
             *   considered to be closed. The events are:
             *     o The termination of the service method of the servlet.
             *     o When the amount of content specified in the setContentLength method
             *       of the response has been written to the response.
             *     o The sendError method is called.
             *     o The sendRedirect method is called.
             *   When a response is closed, all content in the response buffer, if any remains,
             *   must be immediately flushed to the client.
             *
             * Due to the above, out.flush() and out.close() are not necessary, and sometimes
             * (if sendError or sendRedirect were used) request may be already closed.
             */
        }
    }

    protected void manageException(HttpServletRequest req, HttpServletResponse res, Environment env,
                                   String uri, int errorStatus,
                                   String title, String message, String description,
                                   Exception e)
            throws IOException {
        if (this.manageExceptions) {
            if (env != null) {
                env.tryResetResponse();
            } else {
                res.reset();
            }

            String type = Notifying.FATAL_NOTIFICATION;
            HashMap extraDescriptions = null;

            if (errorStatus == HttpServletResponse.SC_NOT_FOUND) {
                type = "resource-not-found";
                // Do not show the exception stacktrace for such common errors.
                e = null;
            } else {
                extraDescriptions = new HashMap(2);
                extraDescriptions.put(Notifying.EXTRA_REQUESTURI, req.getRequestURI());
                if (uri != null) {
                     extraDescriptions.put("Request URI", uri);
                }

                // Do not show exception stack trace when log level is WARN or above. Show only message.
                if (!getLogger().isInfoEnabled()) {
                    Throwable t = DefaultNotifyingBuilder.getRootCause(e);
                    if (t != null) extraDescriptions.put(Notifying.EXTRA_CAUSE, t.getMessage());
                    e = null;
                }
            }

            Notifying n = new DefaultNotifyingBuilder().build(this,
                                                              e,
                                                              type,
                                                              title,
                                                              "Cocoon Servlet",
                                                              message,
                                                              description,
                                                              extraDescriptions);

            res.setContentType("text/html");
            res.setStatus(errorStatus);
            Notifier.notify(n, res.getOutputStream(), "text/html");
        } else {
            res.sendError(errorStatus, title);
            res.flushBuffer();
        }
    }

    /**
     * Create the environment for the request
     */
    protected Environment getEnvironment(String uri,
                                         HttpServletRequest req,
                                         HttpServletResponse res)
    throws Exception {
        HttpEnvironment env;

        String formEncoding = req.getParameter("cocoon-form-encoding");
        if (formEncoding == null) {
            formEncoding = this.defaultFormEncoding;
        }
        env = new HttpEnvironment(uri,
                                  this.servletContextURL,
                                  req,
                                  res,
                                  this.servletContext,
                                  (HttpContext) this.appContext.get(Constants.CONTEXT_ENVIRONMENT_CONTEXT),
                                  this.containerEncoding,
                                  formEncoding);
        env.enableLogging(getLogger());
        return env;
    }

    /**
     * Instatiates the parent component manager, as specified in the
     * parent-component-manager init parameter.
     *
     * If none is specified, the method returns <code>null</code>.
     *
     * @return the parent component manager, or <code>null</code>.
     */
    protected synchronized ComponentManager getParentComponentManager() {
        if (parentComponentManager != null && parentComponentManager instanceof Disposable) {
            ((Disposable) parentComponentManager).dispose();
        }

        parentComponentManager = null;
        if (parentComponentManagerClass != null) {
            try {
                Class pcm = ClassUtils.loadClass(parentComponentManagerClass);
                Constructor pcmc = pcm.getConstructor(new Class[]{String.class});
                parentComponentManager = (ComponentManager) pcmc.newInstance(new Object[]{parentComponentManagerInitParam});

                if (parentComponentManager instanceof LogEnabled) {
                    ((LogEnabled) parentComponentManager).enableLogging(getLogger());
                }
                if (parentComponentManager instanceof Contextualizable) {
                    ((Contextualizable) parentComponentManager).contextualize(this.appContext);
                }
                if (parentComponentManager instanceof Initializable) {
                    ((Initializable) parentComponentManager).initialize();
                }
            } catch (Exception e) {
                if (getLogger().isErrorEnabled()) {
                    getLogger().error("Could not initialize parent component manager.", e);
                }
            }
        }
        return parentComponentManager;
    }

    /**
     * Creates the Cocoon object and handles exception handling.
     */
    protected synchronized void createCocoon()
    throws ServletException {

        // Recheck that we need to create the cocoon object. It can have been created by
        // a concurrent invocation to this method.
        if (this.cocoon != null) {
            return;
        }

        /* HACK for reducing class loader problems.                                     */
        /* example: xalan extensions fail if someone adds xalan jars in tomcat3.2.1/lib */
        if (this.initClassLoader) {
            try {
                Thread.currentThread().setContextClassLoader(this.classLoader);
            } catch (Exception e) {
            }
        }

        updateEnvironment();
        forceLoad();
        forceProperty();

        try {
            this.exception = null;
            URL configFile = (URL) this.appContext.get(Constants.CONTEXT_CONFIG_URL);
            if (getLogger().isInfoEnabled()) {
                getLogger().info("Reloading from: " + configFile.toExternalForm());
            }
            Cocoon c = (Cocoon) ClassUtils.newInstance("org.apache.cocoon.Cocoon");
            ContainerUtil.enableLogging(c, getCocoonLogger());
            c.setLoggerManager(getLoggerManager());
            ContainerUtil.contextualize(c, this.appContext);
            final ComponentManager parent = this.getParentComponentManager();
            if (parent != null) {
                ContainerUtil.compose(c, parent);
            }
            if (this.enableInstrumentation) {
                c.setInstrumentManager(getInstrumentManager());
            }
            ContainerUtil.initialize(c);
            this.creationTime = System.currentTimeMillis();

            this.cocoon = c;
        } catch (Exception e) {
            if (getLogger().isErrorEnabled()) {
                getLogger().error("Exception reloading", e);
            }
            this.exception = e;
            disposeCocoon();
        }
    }

    private Logger getCocoonLogger() {
        final String rootlogger = getInitParameter("cocoon-logger");
        if (rootlogger != null) {
            return this.getLoggerManager().getLoggerForCategory(rootlogger);
        } else {
            return getLogger();
        }
    }

    /**
     * Method to update the environment before Cocoon instances are created.
     *
     * This is also useful if you wish to customize any of the 'protected'
     * variables from this class before a Cocoon instance is built in a derivative
     * of this class (eg. Cocoon Context).
     */
    protected void updateEnvironment() throws ServletException {
        this.appContext.put(Constants.CONTEXT_CLASS_LOADER, classLoader);
        this.appContext.put(Constants.CONTEXT_CLASSPATH, getClassPath());
    }

    /**
     * Helper method to obtain an <code>InstrumentManager</code> instance
     *
     * @return an <code>InstrumentManager</code> instance
     */
    private InstrumentManager getInstrumentManager()
    throws Exception {
        String imConfig = getInitParameter("instrumentation-config");
        if (imConfig == null) {
            throw new ServletException("Please define the init-param 'instrumentation-config' in your web.xml");
        }

        final InputStream is = this.servletContext.getResourceAsStream(imConfig);
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        final Configuration conf = builder.build(is);

        // Get the logger for the instrument manager
        final String imLoggerCategory = conf.getAttribute("logger", "core.instrument");
        Logger imLogger = this.loggerManager.getLoggerForCategory(imLoggerCategory);

        // Set up the Instrument Manager
        DefaultInstrumentManagerImpl instrumentManager = new DefaultInstrumentManagerImpl();
        instrumentManager.enableLogging(imLogger);
        instrumentManager.configure(conf);
        instrumentManager.initialize();

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Instrument manager created " + instrumentManager);
        }

        this.instrumentManager = instrumentManager;
        return instrumentManager;
    }

    private String processTime(long time) {
        StringBuffer out = new StringBuffer(PROCESSED_BY);
        if (time <= SECOND) {
            out.append(time);
            out.append(" milliseconds.");
        } else if (time <= MINUTE) {
            out.append(time / SECOND);
            out.append(" seconds.");
        } else if (time <= HOUR) {
            out.append(time / MINUTE);
            out.append(" minutes.");
        } else {
            out.append(time / HOUR);
            out.append(" hours.");
        }
        return out.toString();
    }

    /**
     * Gets the current cocoon object.  Reload cocoon if configuration
     * changed or we are reloading.
     */
    private boolean reloadCocoon(final String pathInfo, final String reloadParam)
    throws ServletException {
        if (this.allowReload) {
            boolean reload = false;

            if (this.cocoon != null) {
                if (this.cocoon.modifiedSince(this.creationTime)) {
                    if (getLogger().isInfoEnabled()) {
                        getLogger().info("Configuration changed reload attempt");
                    }
                    reload = true;
                } else if (pathInfo == null && reloadParam != null) {
                    if (getLogger().isInfoEnabled()) {
                        getLogger().info("Forced reload attempt");
                    }
                    reload = true;
                }
            } else if (pathInfo == null && reloadParam != null) {
                if (getLogger().isInfoEnabled()) {
                    getLogger().info("Invalid configurations reload");
                }
                reload = true;
            }

            return reload;
        } else {
            return false;
        }
    }

    /**
     * Destroy Cocoon
     */
    protected final void disposeCocoon() {
        if (this.cocoon != null) {
            ContainerUtil.dispose(this.cocoon);
            this.cocoon = null;
        }
    }

    /**
     * Get an initialisation parameter. The value is trimmed, and null is returned if the trimmed value
     * is empty.
     */
    public String getInitParameter(String name) {
        String result = super.getInitParameter(name);
        if (result != null) {
            result = result.trim();
            if (result.length() == 0) {
                result = null;
            }
        }

        return result;
    }

    /** Convenience method to access servlet parameters */
    protected String getInitParameter(String name, String defaultValue) {
        String result = getInitParameter(name);
        if (result == null) {
            if (getLogger() != null && getLogger().isDebugEnabled()) {
                getLogger().debug(name + " was not set - defaulting to '" + defaultValue + "'");
            }
            return defaultValue;
        } else {
            return result;
        }
    }

    /** Convenience method to access boolean servlet parameters */
    protected boolean getInitParameterAsBoolean(String name, boolean defaultValue) {
        String value = getInitParameter(name);
        if (value == null) {
            if (getLogger() != null && getLogger().isDebugEnabled()) {
                getLogger().debug(name + " was not set - defaulting to '" + defaultValue + "'");
            }
            return defaultValue;
        }

        return BooleanUtils.toBoolean(value);
    }

    protected int getInitParameterAsInteger(String name, int defaultValue) {
        String value = getInitParameter(name);
        if (value == null) {
            if (getLogger() != null && getLogger().isDebugEnabled()) {
                getLogger().debug(name + " was not set - defaulting to '" + defaultValue + "'");
            }
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }

    protected Logger getLogger() {
        return this.log;
    }

    protected LoggerManager getLoggerManager() {
        return this.loggerManager;
    }
}
