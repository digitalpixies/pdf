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
package org.apache.cocoon.woody.expression;

import org.outerj.expression.Expression;
import org.outerj.expression.ParseException;
import org.outerj.expression.ExpressionException;

/**
 * Work interface for the component that creates Expression objects.
 * The reason for centralising the creation of expressions is so that
 * new functions can be registered in one place.
 * 
 * @version $Id: ExpressionManager.java 433543 2006-08-22 06:22:54Z crossley $
 */
public interface ExpressionManager {
    
    String ROLE = ExpressionManager.class.getName();
    
    Expression parse(String expression) throws ParseException, ExpressionException;
}
