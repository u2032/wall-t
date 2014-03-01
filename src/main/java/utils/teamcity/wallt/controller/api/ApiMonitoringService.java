/*******************************************************************************
 * Copyright 2014 Cedric Longo.
 *
 * This file is part of Wall-T program.
 *
 * Wall-T is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Wall-T is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Wall-T.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package utils.teamcity.wallt.controller.api;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.wallt.model.build.BuildTypeData;
import utils.teamcity.wallt.model.build.IBuildManager;
import utils.teamcity.wallt.model.build.IProjectManager;
import utils.teamcity.wallt.model.build.ProjectData;
import utils.teamcity.wallt.model.logger.Loggers;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class ApiMonitoringService implements IApiMonitoringService {

    public static final Logger LOGGER = LoggerFactory.getLogger( Loggers.MAIN );
    private final ScheduledExecutorService _executorService;
    private final IApiController _apiController;
    private final IProjectManager _projectManager;
    private final IBuildManager _buildManager;

    private boolean _active;


    @Inject
    public ApiMonitoringService( final ScheduledExecutorService executorService, final IApiController apiController, final IProjectManager projectManager, final IBuildManager buildManager ) {
        _executorService = executorService;
        _apiController = apiController;
        _projectManager = projectManager;
        _buildManager = buildManager;
    }

    @Override
    public void start( ) {
        _executorService.scheduleWithFixedDelay( checkIdleBuildStatus( ), 10, 60, TimeUnit.SECONDS );
        _executorService.scheduleWithFixedDelay( checkRunningBuildStatus( ), 10, 20, TimeUnit.SECONDS );
        _executorService.scheduleWithFixedDelay( checkQueuedBuildStatus( ), 10, 60, TimeUnit.SECONDS );
        LOGGER.info( "Monitoring service configured." );
    }

    public synchronized boolean isActive( ) {
        return _active;
    }

    @Override
    public synchronized void pause( ) {
        _active = false;
        LOGGER.info( "Monitoring service paused." );
    }

    @Override
    public synchronized void activate( ) {
        _active = true;
        LOGGER.info( "Monitoring service started." );
    }

    private Collection<BuildTypeData> getAllMonitoredBuildTypes( ) {
        final Set<BuildTypeData> allMonitoredBuildTypes = Sets.newLinkedHashSet( );
        allMonitoredBuildTypes.addAll( _buildManager.getMonitoredBuildTypes( ) );

        for ( final ProjectData projectData : _projectManager.getMonitoredProjects( ) ) {
            allMonitoredBuildTypes.addAll( projectData.getBuildTypes( ) );
            for ( final ProjectData child : _projectManager.getAllChildrenOf( projectData ) ) {
                allMonitoredBuildTypes.addAll( child.getBuildTypes( ) );
            }
        }

        return allMonitoredBuildTypes;
    }

    private Runnable checkIdleBuildStatus( ) {
        return ( ) -> {
            if ( !isActive( ) )
                return;

            final Instant before = Instant.now( );
            final List<BuildTypeData> monitoredBuilds = getAllMonitoredBuildTypes( ).stream( )
                    .filter( b -> !b.hasRunningBuild( ) )
                    .collect( Collectors.toList( ) );

            checkBuildStatus( monitoredBuilds );
            LOGGER.info( "Checking idle build status: done in {} s", Duration.between( before, Instant.now( ) ).getSeconds( ) );
        };
    }

    private Runnable checkRunningBuildStatus( ) {
        return ( ) -> {
            if ( !isActive( ) )
                return;

            final Instant before = Instant.now( );
            final List<BuildTypeData> monitoredBuilds = getAllMonitoredBuildTypes( ).stream( )
                    .filter( BuildTypeData::hasRunningBuild )
                    .collect( Collectors.toList( ) );

            checkBuildStatus( monitoredBuilds );
            LOGGER.info( "Checking running build status: done in {} s", Duration.between( before, Instant.now( ) ).getSeconds( ) );
        };
    }

    private void checkBuildStatus( final Iterable<BuildTypeData> monitoredBuilds ) {
        try {
            ListenableFuture<Void> future = Futures.immediateFuture( null );
            for ( final BuildTypeData buildType : monitoredBuilds )
                future = Futures.transform( future, (AsyncFunction<Void, Void>) o -> _apiController.requestLastBuildStatus( buildType ) );
            future.get( );
        } catch ( InterruptedException | ExecutionException ignored ) {
        }
    }

    private Runnable checkQueuedBuildStatus( ) {
        return ( ) -> {
            if ( !isActive( ) )
                return;

            try {
                final Instant before = Instant.now( );
                ListenableFuture<Void> future = _apiController.requestQueuedBuilds( );
                future.get( );
                LOGGER.info( "Checking queued builds: done in {} s", Duration.between( before, Instant.now( ) ).getSeconds( ) );
            } catch ( InterruptedException | ExecutionException ignored ) {
            }
        };
    }

}
