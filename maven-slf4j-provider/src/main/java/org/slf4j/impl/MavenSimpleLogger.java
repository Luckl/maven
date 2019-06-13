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
import org.slf4j.event.LoggingEvent;
import org.slf4j.event.SubstituteLoggingEvent;

import static org.apache.maven.shared.utils.logging.MessageUtils.level;
import static org.apache.maven.shared.utils.logging.MessageUtils.buffer;

import java.io.PrintStream;

/**
 * Logger for Maven, that support colorization of levels and stacktraces. This class implements 2 methods introduced in
 * slf4j-simple provider local copy.
 *
 * @since 3.5.0
 */
public class MavenSimpleLogger extends SimpleLogger
{
    private MavenSlf4jWrapperFactory factory;

    MavenSimpleLogger( String name )
    {
        super( name );
    }

    MavenSimpleLogger( String name, MavenSlf4jWrapperFactory factory )
    {
        super( name );
        this.factory = factory;
    }

    /**
     * A simple implementation which always logs messages of level WARN
     * according to the format outlined above.
     */
    @Override
    public void warn( String msg ) {
        super.warn( msg );
        recordWarn();
    }

    /**
     * Perform single parameter substitution before logging the message of level
     * WARN according to the format outlined above.
     */
    @Override
    public void warn( String format, Object arg ) {
        super.warn( format, arg );
        recordWarn();
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * WARN according to the format outlined above.
     */
    @Override
    public void warn( String format, Object arg1, Object arg2 ) {
        super.warn( format, arg1, arg2 );
        recordWarn();
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * WARN according to the format outlined above.
     */
    @Override
    public void warn( String format, Object... argArray ) {
        super.warn( format, argArray );
        recordWarn();
    }

    /** Log a message of level WARN, including an exception. */
    @Override
    public void warn( String msg, Throwable t ) {
        super.warn( msg, t );
        recordWarn();
    }

    private void recordWarn() {
        if ( !factory.threwLogsOfBreakingLevel() ) {
            factory.breakingLogOccured();
            super.info( "Breaking log occurred" );
        }
    }

    @Override
    protected String renderLevel( int level )
    {
        switch ( level )
        {
            case LOG_LEVEL_TRACE:
                return level().debug( "TRACE" ).toString();
            case LOG_LEVEL_DEBUG:
                return level().debug( "DEBUG" ).toString();
            case LOG_LEVEL_INFO:
                return level().info( "INFO" ).toString();
            case LOG_LEVEL_WARN:
                return level().warning( "WARNING" ).toString();
            case LOG_LEVEL_ERROR:
            default:
                return level().error( "ERROR" ).toString();
        }
    }

    @Override
    protected void writeThrowable( Throwable t, PrintStream stream )
    {
        if ( t == null )
        {
            return;
        }
        stream.print( buffer().failure( t.getClass().getName() ) );
        if ( t.getMessage() != null )
        {
            stream.print( ": " );
            stream.print( buffer().failure( t.getMessage() ) );
        }
        stream.println();

        while ( t != null )
        {
            for ( StackTraceElement e : t.getStackTrace() )
            {
                stream.print( "    " );
                stream.print( buffer().strong( "at" ) );
                stream.print( " " + e.getClassName() + "." + e.getMethodName() );
                stream.print( buffer().a( " (" ).strong( getLocation( e ) ).a( ")" ) );
                stream.println();
            }

            t = t.getCause();
            if ( t != null )
            {
                stream.print( buffer().strong( "Caused by" ).a( ": " ).a( t.getClass().getName() ) );
                if ( t.getMessage() != null )
                {
                    stream.print( ": " );
                    stream.print( buffer().failure( t.getMessage() ) );
                }
                stream.println();
            }
        }
    }

    protected String getLocation( final StackTraceElement e )
    {
        assert e != null;

        if ( e.isNativeMethod() )
        {
            return "Native Method";
        }
        else if ( e.getFileName() == null )
        {
            return "Unknown Source";
        }
        else if ( e.getLineNumber() >= 0 )
        {
            return String.format( "%s:%s", e.getFileName(), e.getLineNumber() );
        }
        else
        {
            return e.getFileName();
        }
    }
}
