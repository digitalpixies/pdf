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
package org.apache.cocoon.components.cron;

/**
 * This is the interface a class has to implement to enable it to be  scheduled by a JobScheduler
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Id: CronJob.java 433543 2006-08-22 06:22:54Z crossley $
 *
 * @since 2.1.1
 */
public interface CronJob {
    /** The component role */
    String ROLE = CronJob.class.getName();

    /**
     * This methods get called to allow an implementing class to do its  supposed job
     *
     * @param jobname the name given to this job
     */
    void execute(String jobname);
}
