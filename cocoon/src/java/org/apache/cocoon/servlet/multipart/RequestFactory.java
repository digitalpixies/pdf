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

package org.apache.cocoon.servlet.multipart;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

/**
 * This is the interface of Request Wrapper in Cocoon.
 *
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version CVS $Id: RequestFactory.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class RequestFactory {

    private boolean saveUploadedFilesToDisk;

    private File uploadDirectory;

    private boolean allowOverwrite;

    private boolean silentlyRename;

    private String defaultCharEncoding;
    
    private int maxUploadSize;
    
    public RequestFactory (boolean saveUploadedFilesToDisk, 
                           File uploadDirectory, 
                           boolean allowOverwrite, 
                           boolean silentlyRename, 
                           int maxUploadSize,
                           String defaultCharEncoding) {
       this.saveUploadedFilesToDisk = saveUploadedFilesToDisk;
       this.uploadDirectory = uploadDirectory;
       this.allowOverwrite = allowOverwrite;
       this.silentlyRename = silentlyRename;
       this.maxUploadSize = maxUploadSize;
       this.defaultCharEncoding = defaultCharEncoding;
       
       if (saveUploadedFilesToDisk) {
           // Empty the contents of the upload directory
           File[] files = uploadDirectory.listFiles();
           for (int i = 0; i < files.length; i++) {
               files[i].delete();
           }
       }
    }

    /**
     * If the request includes a "multipart/form-data", then wrap it with
     * methods that allow easier connection to those objects since the servlet
     * API doesn't provide those methods directly.
     */
    public HttpServletRequest getServletRequest(HttpServletRequest request) throws IOException, MultipartException {
        HttpServletRequest req = request;
        String contentType = request.getContentType();
        
        if ((contentType != null) && (contentType.toLowerCase().indexOf("multipart/form-data") > -1)) {
 
            String charEncoding = request.getCharacterEncoding();
            if (charEncoding == null || charEncoding.length() == 0) {
                charEncoding = this.defaultCharEncoding;
            }
            
            MultipartParser parser = new MultipartParser(
                    this.saveUploadedFilesToDisk, 
                    this.uploadDirectory, 
                    this.allowOverwrite, 
                    this.silentlyRename, 
                    this.maxUploadSize,
                    charEncoding);
                    
            Hashtable parts = parser.getParts(request);
            
            req = new MultipartHttpServletRequest(request,parts);
        }

        return req;
    }
    
}