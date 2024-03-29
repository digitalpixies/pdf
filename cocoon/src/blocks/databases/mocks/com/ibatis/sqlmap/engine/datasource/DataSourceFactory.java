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
package com.ibatis.sqlmap.engine.datasource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Mock class providing the declarations required to compile the Cocoon code when
 * the actual library is not present.
 *
 * @version $Id: DataSourceFactory.java 485610 2006-12-11 11:17:22Z cziegeler $
 * @since 2.1.10
 */
public interface DataSourceFactory {

    void initialize(Map map);

    DataSource getDataSource();
}
