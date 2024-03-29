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
package org.apache.cocoon.components.language.programming.java;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.cocoon.components.language.programming.LanguageCompiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * This class implements the functionality common to all Java compilers.
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @version CVS $Id: AbstractJavaCompiler.java 433543 2006-08-22 06:22:54Z crossley $
 * @since 2.0
 */
public abstract class AbstractJavaCompiler extends AbstractLogEnabled implements LanguageCompiler, Recyclable {

    /**
     * The source program filename
     */
    protected String file;

    /**
     * The name of the directory containing the source program file
     */
    protected String srcDir;

    /**
     * The name of the directory to contain the resulting object program file
     */
    protected String destDir;

    /**
     * The classpath to be used for compilation
     */
    protected String classpath;

    /**
     * The encoding of the source program or <code>null</code> to use the
     * platform's default encoding
     */
    protected String encoding = null;

    /**
     * The version of the JVM for wich the code was written.
     * i.e: 130 = Java 1.3, 140 = Java 1.4 and 150 = Java 1.5
     */
    protected int compilerComplianceLevel;

    /**
     * The input stream to output compilation errors
     */
    protected InputStream errors;

    /**
     * Set the name of the file containing the source program
     *
     * @param file The name of the file containing the source program
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Set the name of the directory containing the source program file
     *
     * @param srcDir The name of the directory containing the source program file
     */
    public void setSource(String srcDir) {
        this.srcDir = srcDir;
    }

    /**
     * Set the name of the directory to contain the resulting object program file
     *
     * @param destDir The name of the directory to contain the resulting object
     * program file
     */
    public void setDestination(String destDir) {
        this.destDir = destDir;
    }

    /**
     * Set the classpath to be used for this compilation
     *
     * @param classpath The classpath to be used for this compilation
     */
    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    /**
     * Set the encoding of the input source file or <code>null</code> to use the
     * platform's default encoding
     *
     * @param encoding The encoding of the input source file or <code>null</code>
     * to use the platform's default encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Set the version of the java source code to be compiled
     *
     * @param compilerComplianceLevel The version of the JVM for wich the code was written.
     * i.e: 130 = Java 1.3, 140 = Java 1.4 and 150 = Java 1.5
     * 
     * @since 2.1.7
     */
    public void setCompilerComplianceLevel(int compilerComplianceLevel) {
        this.compilerComplianceLevel = compilerComplianceLevel;
    }

    /**
     * Return the list of errors generated by this compilation
     *
     * @return The list of errors generated by this compilation
     * @exception IOException If an error occurs during message collection
     */
    public List getErrors() throws IOException {
        return parseStream(new BufferedReader(new InputStreamReader(errors)));
    }

    /**
     * Parse the compiler error stream to produce a list of
     * <code>CompilerError</code>s
     *
     * @param errors The error stream
     * @return The list of compiler error messages
     * @exception IOException If an error occurs during message collection
     */
    protected abstract List parseStream(BufferedReader errors)
            throws IOException;

    /**
     * Fill the arguments taken by the Java compiler
     *
     * @param arguments The list of compilation arguments
     * @return The prepared list of compilation arguments
     */

    protected List fillArguments(List arguments) {
        // add compiler compliance level
        /*arguments.add("-source");
        switch (compilerComplianceLevel) {
            case 150:
                arguments.add("5");
                break;
            case 140:
                //arguments.add("-target");
                arguments.add("1.4");
                break;
            default:
                //arguments.add("-target");
                arguments.add("1.3");
        }*/
        // destination directory
        arguments.add("-d");
        arguments.add(destDir);

        // classpath
        arguments.add("-classpath");
        arguments.add(classpath);

        // sourcepath
        arguments.add("-sourcepath");
        arguments.add(srcDir);

        // add optimization (for what is worth)
        arguments.add("-O");
        
        // add encoding if set
        if (encoding != null) {
            arguments.add("-encoding");
            arguments.add(encoding);
        }
        return arguments;
    }

    /**
     * Copy arguments to a string array
     *
     * @param arguments The compiler arguments
     * @return A string array containing compilation arguments
     */
    protected String[] toStringArray(List arguments) {
        int i;
        String[] args = new String[arguments.size() + 1];

        for (i = 0; i < arguments.size(); i++) {
            args[i] = (String)arguments.get(i);
        }

        args[i] = file;

        return args;
    }

    /** Reset all internal state.
     * This method is called by the component manager before this
     * component is return to its pool.
     */
    public void recycle() {
        file = null;
        srcDir = null;
        destDir = null;
        classpath = null;
        encoding = null;
        errors = null;
    }
}
