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

import java.io.IOException;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.generation.FileGenerator;
import org.xml.sax.SAXException;

/**
 * This generator extends the usual FileGenerator with a pause parameter.
 * During generation of the content, this generator pauses for the given
 * amount of time.
 * This is very usefull for caching tests.
 * 
 *  @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 *  @version CVS $Id: PauseGenerator.java 433543 2006-08-22 06:22:54Z crossley $
 *  @since   2.1
 */
public class PauseGenerator 
    extends FileGenerator {


    public void generate()
    throws IOException, SAXException, ProcessingException {
        long secs = this.parameters.getParameterAsLong("pause", 60);
        this.getLogger().debug("Waiting for " + secs + " secs.");
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException ie) {
        }
        this.getLogger().debug("Finished waiting.");
        super.generate();
    }

}
