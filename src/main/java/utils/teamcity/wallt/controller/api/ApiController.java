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

import com.google.common.base.Function;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.wallt.controller.api.json.*;
import utils.teamcity.wallt.model.build.*;
import utils.teamcity.wallt.model.configuration.Configuration;
import utils.teamcity.wallt.model.logger.Loggers;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Date: 22/02/14
 *
 * @author Cedric Longo
 */
final class ApiController implements IApiController {

    private static final int MAX_BUILDS_TO_CONSIDER = 5;

    private static final Logger LOGGER = LoggerFactory.getLogger( Loggers.MAIN );

    private final IBuildManager _buildManager;
    private final EventBus _eventBus;
    private final ExecutorService _executorService;
    private final Configuration _configuration;
    private final IProjectManager _projectManager;
    private final IApiRequestController _apiRequestController;

    private final Map<ApiVersion, Function<Build, BuildData>> _buildFunctionsByVersion;

    @Inject
    ApiController( final Configuration configuration, final IProjectManager projectManager, final IBuildManager buildManager, final IApiRequestController apiRequestController, final EventBus eventBus, final ExecutorService executorService, final Map<ApiVersion, Function<Build, BuildData>> buildFunctionsByVersion ) {
        _configuration = configuration;
        _projectManager = projectManager;
        _apiRequestController = apiRequestController;
        _buildManager = buildManager;
        _eventBus = eventBus;
        _executorService = executorService;
        _buildFunctionsByVersion = buildFunctionsByVersion;
    }

    private void runInWorkerThread( final Runnable runnable ) {
        _executorService.submit( runnable );
    }

    @Override
    public ListenableFuture<Void> loadBuildTypeList( ) {
        if ( !getApiVersion( ).isSupported( ApiFeature.BUILD_TYPE_STATUS ) )
            return Futures.immediateFuture( null );

        final SettableFuture<Void> ackFuture = SettableFuture.create( );

        runInWorkerThread( ( ) -> {
            final ListenableFuture<BuildTypeList> buildListFuture = _apiRequestController.sendRequest( getApiVersion( ), "buildTypes", BuildTypeList.class );
            addCallback( buildListFuture, new FutureCallback<BuildTypeList>( ) {
                @Override
                public void onSuccess( final BuildTypeList result ) {
                    final List<BuildTypeData> buildTypes = result.getBuildTypes( ).stream( )
                            .map( ( btype ) -> new BuildTypeData( btype.getId( ), btype.getName( ), btype.getProjectId( ), btype.getProjectName( ) ) )
                            .collect( Collectors.toList( ) );
                    _buildManager.registerBuildTypes( buildTypes );
                    _eventBus.post( _buildManager );
                    ackFuture.set( null );

                    for ( final BuildTypeData buildType : _buildManager.getBuildTypes( ) ) {
                        final Optional<ProjectData> project = _projectManager.getProject( buildType.getProjectId( ) );
                        if ( project.isPresent( ) ) {
                            project.get( ).registerBuildType( buildType );
                            _eventBus.post( project.get( ) );
                        }
                        LOGGER.info( "Discovering build type " + buildType.getId( ) + " (" + buildType.getName( ) + ") on project " + buildType.getProjectId( ) + " (" + buildType.getProjectName( ) + ")" );
                    }
                }

                @Override
                public void onFailure( final Throwable t ) {
                    LOGGER.error( "Error during loading build type list:", t );
                    ackFuture.setException( t );
                }
            } );
        } );

        return ackFuture;
    }

    @Override
    public ListenableFuture<Void> loadProjectList( ) {
        if ( !getApiVersion( ).isSupported( ApiFeature.PROJECT_STATUS ) )
            return Futures.immediateFuture( null );

        final SettableFuture<Void> ackFuture = SettableFuture.create( );

        runInWorkerThread( ( ) -> {
            final ListenableFuture<ProjectList> projectListFuture = _apiRequestController.sendRequest( getApiVersion( ), "projects", ProjectList.class );
            addCallback( projectListFuture, new FutureCallback<ProjectList>( ) {
                @Override
                public void onSuccess( final ProjectList result ) {
                    final List<ProjectData> projects = result.getProjects( ).stream( )
                            .map( ( project ) -> new ProjectData( project.getId( ), project.getName( ), Optional.ofNullable( project.getParentId( ) ) ) )
                            .collect( Collectors.toList( ) );
                    _projectManager.registerProjects( projects );
                    _eventBus.post( _projectManager );
                    ackFuture.set( null );

                    for ( final ProjectData project : _projectManager.getProjects( ) ) {
                        LOGGER.info( "Discovering project " + project.getId( ) + " (" + project.getName( ) + ")" );
                    }
                }

                @Override
                public void onFailure( final Throwable t ) {
                    LOGGER.error( "Error during loading project list:", t );
                    ackFuture.setException( t );
                }
            } );
        } );

        return ackFuture;
    }

    @Override
    public void requestQueuedBuilds( ) {
        if ( !getApiVersion( ).isSupported( ApiFeature.QUEUE_STATUS ) )
            return;

        runInWorkerThread( ( ) -> {
            final ListenableFuture<QueuedBuildList> buildQueueFuture = _apiRequestController.sendRequest( getApiVersion( ), "buildQueue", QueuedBuildList.class );
            addCallback( buildQueueFuture, new FutureCallback<QueuedBuildList>( ) {
                @Override
                public void onSuccess( final QueuedBuildList queuedBuildList ) {
                    final Set<String> buildTypesInQueue = queuedBuildList.getQueueBuild( ).stream( )
                            .map( QueueBuild::getBuildTypeId )
                            .collect( Collectors.toSet( ) );
                    final List<BuildTypeData> modifiedStatusBuilds = _buildManager.registerBuildTypesInQueue( buildTypesInQueue );
                    for ( final BuildTypeData buildType : modifiedStatusBuilds )
                        _eventBus.post( buildType );
                }

                @Override
                public void onFailure( final Throwable throwable ) {
                    LOGGER.error( "Error during loading build queue:", throwable );
                }
            } );
        } );
    }

    @Override
    public void requestLastBuildStatus( final BuildTypeData buildType ) {
        runInWorkerThread( ( ) -> {
            final ListenableFuture<BuildList> buildListFuture = _apiRequestController.sendRequest( getApiVersion( ), "builds/?locator=buildType:" + buildType.getId( ) + ",running:any,count:" + MAX_BUILDS_TO_CONSIDER, BuildList.class );
            addCallback( buildListFuture, new FutureCallback<BuildList>( ) {
                @Override
                public void onSuccess( final BuildList result ) {
                    // We consider only 5 last builds
                    final List<Build> buildToRequest = result.getBuilds( ).stream( )
                            .limit( MAX_BUILDS_TO_CONSIDER )
                            .collect( Collectors.toList( ) );

                    // We removed from list builds which status is already known
                    buildToRequest.removeIf( build -> {
                        final Optional<BuildData> previousBuildStatus = buildType.getBuildById( build.getId( ) );
                        return previousBuildStatus.isPresent( ) && previousBuildStatus.get( ).getState( ) == BuildState.finished;
                    } );

                    for ( final Build build : buildToRequest ) {
                        final ListenableFuture<Build> buildStatusFuture = _apiRequestController.sendRequest( getApiVersion( ), "builds/id:" + build.getId( ), Build.class );
                        addCallback( buildStatusFuture, registerBuildStatus( buildType, build ) );
                    }
                    buildType.touch( );
                }

                @Override
                public void onFailure( final Throwable t ) {
                    LOGGER.error( "Error during loading builds list for build type: " + buildType.getId( ), t );
                }
            } );
        } );
    }

    private FutureCallback<Build> registerBuildStatus( final BuildTypeData buildType, final Build build ) {
        return new FutureCallback<Build>( ) {
            @Override
            public void onSuccess( final Build result ) {
                buildType.registerBuild( _buildFunctionsByVersion.get( getApiVersion( ) ).apply( result ) );
                _eventBus.post( buildType );

                final Optional<ProjectData> project = _projectManager.getProject( buildType.getProjectId( ) );
                if ( project.isPresent( ) ) {
                    _eventBus.post( project.get( ) );
                }
            }

            @Override
            public void onFailure( final Throwable t ) {
                LOGGER.error( "Error during loading full information for build with id " + build.getId( ) + ", build type: " + buildType.getId( ), t );
            }
        };
    }

    public ApiVersion getApiVersion( ) {
        return _configuration.getApiVersion( );
    }
}
