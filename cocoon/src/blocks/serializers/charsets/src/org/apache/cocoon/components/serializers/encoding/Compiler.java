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
package org.apache.cocoon.components.serializers.encoding;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Collection;

/**
 * 
 * 
 * @author <a href="mailto:pier@apache.org">Pier Fumagalli</a>, February 2003
 * @version CVS $Id: Compiler.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class Compiler extends CompiledCharset {

    /** The class name to be generated. */
    private String clazz = null;

    /** The <code>CharsetEncoder</code> instance. */
    private CharsetEncoder encoder = null;

    /** Create a new instance of this <code>Compiler</code>. */
    private Compiler(String name, String aliases[], CharsetEncoder encoder) {
        super(name, aliases);
        this.clazz = "cs_" + name.replace('-', '_').toUpperCase();
        this.encoder = encoder;
        this.compile();
    }

    /**
     * Return true or false wether this encoding can encode the specified
     * character or not.
     * <p>
     * This method is equivalent to the <code>allows(...)</code> method, but
     * it will be called upon construction of the encoding table.
     * </p>
     */
    protected boolean compile(char c) {
        return(this.encoder.canEncode((char)c));
    }

    /**
     * Save this <code>Charset</code> into a Java source file.
     */
    public void save()
    throws IOException {
        this.save(new File(System.getProperty("user.dir")));
    }
   /**
     * Save this <code>Charset</code> into a Java source file.
     */
    public void save(File directory)
    throws IOException {
        File file = new File(directory, this.clazz + ".java");
        OutputStream out = new FileOutputStream(file);
        this.save(out);
        out.flush();
        out.close();
    }

    /**
     * Save this <code>Charset</code> as a Java source file to the specified
     * <code>OutputStream</code>.
     */
    public void save(OutputStream stream)
    throws IOException {
        PrintStream out = new PrintStream(new BufferedOutputStream(stream));

        out.println("/*");
        out.println(" * Copyright 1999-2004 The Apache Software Foundation.");
        out.println(" *");
        out.println(" * Licensed under the Apache License, Version 2.0 (the \"License\");");
        out.println(" * you may not use this file except in compliance with the License.");
        out.println(" * You may obtain a copy of the License at");
        out.println(" *");
        out.println(" *      http://www.apache.org/licenses/LICENSE-2.0");
        out.println(" *");
        out.println(" * Unless required by applicable law or agreed to in writing, software");
        out.println(" * distributed under the License is distributed on an \"AS IS\" BASIS,");
        out.println(" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
        out.println(" * See the License for the specific language governing permissions and");
        out.println(" * limitations under the License.");
        out.println(" */");
        out.println("/* Generated by " + this.getClass().getName() + " */");
        out.println();
        out.println("package org.apache.cocoon.components.serializers.encoding;");
        out.println();
        out.println("/**");
        out.println(" * The <b>" + this.getName() + "</b> character set encoding representation.");
        out.println(" *");
        out.println(" * @author Generated by <code>" + this.getClass().getName() + "</code>");
        out.println(" */");
        out.println("class " + this.clazz + " extends CompiledCharset {");
        out.println();
        out.println("    /** The name of this charset (<b>" + this.getName() + "</b>). */");
        out.println("    public static final String CS_NAME = \"" + this.getName() + "\";");
        out.println();
        out.println("    /** The array of alias names of this charset. */");
        out.println("    public static final String CS_ALIASES[] = {");
        String aliases[] = this.getAliases();
        for (int x = 0; x < aliases.length; x++) out.println("        \"" + aliases[x] + "\",");
        out.println("    };");
        out.println();
        out.println("    /** The array all characters encoded by this encoding. */");
        out.print("    public static final byte CS_ENCODING[] = {");
        for (int x = 0; x < this.encoding.length; x++) {
            if ((x & 0x0F) == 0) {
                out.println();
                out.print("       ");
            }
            String value = Integer.toString(this.encoding[x]);
            value = "    ".substring(value.length()) + value;
            out.print(value);
            if ((x + 1) != this.encoding.length) out.print(",");
        }
        out.println();
        out.println("    };");
        out.println();
        out.println("    /**");
        out.println("     * Create a new instance of the <b>" + this.getName() + "</b> caracter");
        out.println("     * encoding as a <code>Charset</code>.");
        out.println("     */");
        out.println("    public " + this.clazz + "() {");
        out.println("        super(CS_NAME, CS_ALIASES, CS_ENCODING);");
        out.println("    }");
        out.println();
        out.println("    /**");
        out.println("     * Operation not supported.");
        out.println("     */");
        out.println("    public boolean compile(char c) {");
        out.println("        throw new UnsupportedOperationException();");
        out.println("    }");
        out.println();
        out.println("}");
        out.flush();
    }

    /**
     * Process a NIO <code>Charset</code> producing a java source file.
     */
    public static Compiler process(Charset charset)
    throws IOException {
        CharsetEncoder encoder = charset.newEncoder();
        String name = charset.displayName();

        String aliases[] = new String[charset.aliases().size()];
        Iterator iterator = charset.aliases().iterator();
        for (int k = 0; k < aliases.length; k++) {
            aliases[k] = iterator.next().toString();
        }

        return(new Compiler(name, aliases, encoder));
    }

    /**
     * Compile all <code>java.nio.charset.Charset</code> classes and generate
     * the main holding encodings table.
     */
    public static void main(String args[])
    throws IOException {
        File directory = new File(System.getProperty("user.dir"));
        if (args.length > 0) directory=new File(args[0]);
        if (!directory.isDirectory()) {
            throw new IOException("Invalid output directory \""
                                  + directory.getName() + "\"");
        }
        Collection charsets = Charset.availableCharsets().values();
        Iterator iterator = charsets.iterator();
        int pos = 0;
        int len = charsets.size();

        while (iterator.hasNext()) {
            Charset charset = (Charset)iterator.next();
            try {
                Compiler compiler = process(charset);
                compiler.save(directory);
                System.out.println("Generating \"" + compiler.clazz + ".java\" "
                                   + "for \"" + compiler.getName() + "\" charset ("
                                   + (++pos) + " of " + len + ")");
            } catch (Exception exception) {
                System.err.println("Error generating charset \"" + charset + "\"");
                System.err.print(exception.getClass().getName() + ": ");
                System.err.println(exception.getMessage());
            }
        }
    }
}
