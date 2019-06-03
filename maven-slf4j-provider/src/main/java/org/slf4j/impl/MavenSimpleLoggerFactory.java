package org.slf4j.impl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.logwrapper.MavenSlf4jWrapperFactory;
import org.slf4j.Logger;
import org.slf4j.event.Level;

/**
 * MavenSimpleLoggerFactory
 */
public class MavenSimpleLoggerFactory extends SimpleLoggerFactory implements MavenSlf4jWrapperFactory
{
    private boolean brokenOnLogLevel = false;
    private boolean shouldBreakOnLogLevel = false;
    private Level logLevel = null;

    @Override
    public boolean threwLogsOfBreakingLevel()
    {
        return brokenOnLogLevel;
    }

    @Override
    public void breakingLogOccured()
    {
        this.brokenOnLogLevel = true;
    }

    @Override
    public void breakOnLogsOfLevel( String logLevelTobreakOn )
    {
        shouldBreakOnLogLevel = true;
        logLevel = Level.valueOf( logLevelTobreakOn );
    }

    @Override
    public boolean shouldBreakOnLogLevel()
    {
        return shouldBreakOnLogLevel;
    }

    @Override
    public Level getLevelToBreakOn()
    {
        return logLevel;
    }

    /**
     * Return an appropriate {@link MavenSimpleLogger} instance by name.
     */
    public Logger getLogger( String name )
    {
        Logger simpleLogger = loggerMap.get( name );
        if ( simpleLogger != null )
        {
            return simpleLogger;
        }
        else
        {
            Logger newInstance = new MavenSimpleLogger( name, this );
            Logger oldInstance = loggerMap.putIfAbsent( name, newInstance );
            return oldInstance == null ? newInstance : oldInstance;
        }
    }
}
