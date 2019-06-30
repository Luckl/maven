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
 * LogFactory for Maven which can create a simple logger or a one which, if set, fails the build on a threshold.
 */
public class MavenLoggerFactory extends SimpleLoggerFactory implements MavenSlf4jWrapperFactory
{
    private MavenFailLevelLoggerState failLevelLoggerState = null;

    @Override
    public void breakOnLogsOfLevel( String logLevelToBreakOn )
    {
        if ( failLevelLoggerState != null )
        {
            throw new IllegalStateException( "Maven logger fail level has already been set." );
        }

        Level level = Level.valueOf( logLevelToBreakOn );
        if ( level.toInt() < Level.WARN.toInt() )
        {
            throw new IllegalArgumentException( "Logging level thresholds can only be set to WARN or ERROR" );
        }

        failLevelLoggerState = new MavenFailLevelLoggerState( level );
    }

    @Override
    public boolean threwLogsOfBreakingLevel()
    {
        if ( failLevelLoggerState != null )
        {
            return failLevelLoggerState.threwLogsOfBreakingLevel();
        }

        return false;
    }

    /**
     * Return an appropriate {@link MavenSimpleLogger} instance by name.
     */
    @Override
    public Logger getLogger( String name )
    {
        Logger simpleLogger = loggerMap.get( name );
        if ( simpleLogger != null )
        {
            return simpleLogger;
        }
        else
        {
            Logger newInstance = getNewLoggingInstance( name );
            Logger oldInstance = loggerMap.putIfAbsent( name, newInstance );
            return oldInstance == null ? newInstance : oldInstance;
        }
    }

    private Logger getNewLoggingInstance( String name )
    {
        if ( failLevelLoggerState == null )
        {
            return new MavenSimpleLogger( name );
        }
        else
        {
            return new MavenFailLevelLogger( name, failLevelLoggerState );
        }
    }
}
