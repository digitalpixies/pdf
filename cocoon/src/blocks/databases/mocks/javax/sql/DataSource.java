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
package javax.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Mock class providing the declarations required to compile the Cocoon code when
 * the actual library is not present.
 *
 * @version $Id: DataSource.java 486542 2006-12-13 08:12:31Z cziegeler $ 
 */
public interface DataSource {

    Connection getConnection() throws SQLException;

    Connection getConnection(String username, String password) 
    throws SQLException;
      
    PrintWriter getLogWriter() throws SQLException;

    void setLogWriter(PrintWriter out) throws SQLException;

    void setLoginTimeout(int seconds) throws SQLException;
     
    int getLoginTimeout() throws SQLException;
} 
