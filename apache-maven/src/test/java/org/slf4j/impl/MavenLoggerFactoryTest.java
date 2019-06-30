package org.slf4j.impl;

import org.junit.Test;
import org.slf4j.Logger;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class MavenLoggerFactoryTest
{
    @Test
    public void createsSimpleLogger()
    {
        MavenLoggerFactory mavenLoggerFactory = new MavenLoggerFactory();

        Logger logger = mavenLoggerFactory.getLogger( "Test" );

        assertThat( logger, instanceOf( MavenSimpleLogger.class ) );
    }

    @Test
    public void loggerCachingWorks()
    {
        MavenLoggerFactory mavenLoggerFactory = new MavenLoggerFactory();

        Logger logger = mavenLoggerFactory.getLogger( "Test" );
        Logger logger2 = mavenLoggerFactory.getLogger( "Test" );
        Logger differentLogger = mavenLoggerFactory.getLogger( "TestWithDifferentName" );

        assertNotNull( logger );
        assertNotNull( differentLogger );
        assertSame( logger, logger2 );
        assertNotSame( logger, differentLogger );
    }

    @Test
    public void createsFailLevelLogger()
    {
        MavenLoggerFactory mavenLoggerFactory = new MavenLoggerFactory();
        mavenLoggerFactory.breakOnLogsOfLevel( "WARN" );

        Logger logger = mavenLoggerFactory.getLogger( "Test" );

        assertThat( logger, instanceOf( MavenFailLevelLogger.class ) );
    }

    @Test
    public void reportsWhenFailLevelHasBeenHit()
    {
        MavenLoggerFactory mavenLoggerFactory = new MavenLoggerFactory();
        mavenLoggerFactory.breakOnLogsOfLevel( "ERROR" );

        MavenFailLevelLogger logger = ( MavenFailLevelLogger ) mavenLoggerFactory.getLogger( "Test" );
        assertFalse( mavenLoggerFactory.threwLogsOfBreakingLevel() );

        logger.warn( "This should not hit the fail level" );
        assertFalse( mavenLoggerFactory.threwLogsOfBreakingLevel() );

        logger.error( "This should hit the fail level" );
        assertTrue( mavenLoggerFactory.threwLogsOfBreakingLevel() );

        logger.warn( "This should not reset the fail level" );
        assertTrue( mavenLoggerFactory.threwLogsOfBreakingLevel() );
    }

    @Test( expected = IllegalStateException.class )
    public void failLevelThresholdCanOnlyBeSetOnce()
    {
        MavenLoggerFactory mavenLoggerFactory = new MavenLoggerFactory();
        mavenLoggerFactory.breakOnLogsOfLevel( "WARN" );
        mavenLoggerFactory.breakOnLogsOfLevel( "ERROR" );
    }

    @Test( expected = IllegalArgumentException.class )
    public void onlyWarningOrHigherFailLevelsCanBeSet()
    {
        MavenLoggerFactory mavenLoggerFactory = new MavenLoggerFactory();
        mavenLoggerFactory.breakOnLogsOfLevel( "INFO" );
    }
}