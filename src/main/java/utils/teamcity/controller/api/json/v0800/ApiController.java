package utils.teamcity.controller.api.json.v0800;

import com.google.common.base.Function;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import utils.teamcity.controller.api.ApiControllerBase;
import utils.teamcity.controller.api.IApiRequestController;
import utils.teamcity.model.build.*;

import javax.inject.Inject;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.google.common.util.concurrent.Futures.addCallback;
import static utils.teamcity.controller.api.json.ApiVersion.API_8_0;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
public final class ApiController extends ApiControllerBase {

    @Inject
    public ApiController( final IProjectManager projectManager, final IBuildManager buildManager, final IApiRequestController apiRequestController, final EventBus eventBus, final ExecutorService executorService ) {
        super( projectManager, buildManager, apiRequestController, eventBus, executorService );
    }

    @Override
    public ListenableFuture<Void> loadBuildTypeList( ) {
        final SettableFuture<Void> ackFuture = SettableFuture.create( );

        runInWorkerThread( ( ) -> {
            final ListenableFuture<BuildTypeList> buildListFuture = getApiRequestController( ).sendRequest( API_8_0, "buildTypes", BuildTypeList.class );
            addCallback( buildListFuture, new FutureCallback<BuildTypeList>( ) {
                @Override
                public void onSuccess( final BuildTypeList result ) {
                    final List<BuildTypeData> buildTypes = result.getBuildTypes( ).stream( )
                            .map( ( btype ) -> new BuildTypeData( btype.getId( ), btype.getName( ), btype.getProjectName( ) ) )
                            .collect( Collectors.toList( ) );
                    getBuildManager( ).registerBuildTypes( buildTypes );
                    getEventBus( ).post( getBuildManager( ) );
                    ackFuture.set( null );
                }

                @Override
                public void onFailure( final Throwable t ) {
                    getLogger( ).error( "Error during loading build type list:", t );
                    ackFuture.setException( t );
                }
            } );
        } );

        return ackFuture;
    }

    @Override
    public ListenableFuture<Void> loadProjectList( ) {
        final SettableFuture<Void> ackFuture = SettableFuture.create( );

        runInWorkerThread( ( ) -> {
            final ListenableFuture<ProjectList> buildListFuture = getApiRequestController( ).sendRequest( API_8_0, "projects", ProjectList.class );
            addCallback( buildListFuture, new FutureCallback<ProjectList>( ) {
                @Override
                public void onSuccess( final ProjectList result ) {
                    final List<ProjectData> projects = result.getProjects( ).stream( )
                            .map( ( project ) -> new ProjectData( project.getId( ), project.getName( ) ) )
                            .collect( Collectors.toList( ) );
                    getProjectManager( ).registerProjects( projects );
                    getEventBus( ).post( getProjectManager( ) );
                    ackFuture.set( null );
                }

                @Override
                public void onFailure( final Throwable t ) {
                    getLogger( ).error( "Error during loading project list:", t );
                    ackFuture.setException( t );
                }
            } );
        } );

        return ackFuture;
    }

    @Override
    public void requestQueuedBuilds( ) {
        // Do nothing : API 8.0 doesn't support requesting build queue
    }

    @Override
    public void requestLastBuildStatus( final BuildTypeData buildType ) {
        runInWorkerThread( ( ) -> {
            final ListenableFuture<BuildList> buildListFuture = getApiRequestController( ).sendRequest( API_8_0, "builds/?locator=buildType:" + buildType.getId( ) + ",running:any,count:" + MAX_BUILDS_TO_CONSIDER, BuildList.class );
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
                        final ListenableFuture<Build> buildStatusFuture = getApiRequestController( ).sendRequest( API_8_0, "builds/id:" + build.getId( ), Build.class );
                        addCallback( buildStatusFuture, registerBuildStatus( buildType, build ) );
                    }
                    buildType.touch( );
                }

                @Override
                public void onFailure( final Throwable t ) {
                    getLogger( ).error( "Error during loading builds list for build type: " + buildType.getId( ), t );
                }
            } );
        } );
    }

    private FutureCallback<Build> registerBuildStatus( final BuildTypeData buildType, final Build build ) {
        return new FutureCallback<Build>( ) {
            @Override
            public void onSuccess( final Build result ) {
                buildType.registerBuild( _toBuildData.apply( result ) );
                getEventBus( ).post( buildType );
            }

            @Override
            public void onFailure( final Throwable t ) {
                getLogger( ).error( "Error during loading full information for build with id " + build.getId( ) + ", build type: " + buildType.getId( ), t );
            }
        };
    }

    private final Function<Build, BuildData> _toBuildData = build ->
            new BuildData( build.getId( ), build.getBuildType( ), build.getStatus( ),
                    build.isRunning( ) ? BuildState.running : BuildState.finished,
                    build.isRunning( ) ? build.getRunningInformation( ).getPercentageComplete( ) : 100,
                    Optional.ofNullable( build.getFinishedDate( ) ),
                    build.isRunning( ) ? Duration.of( build.getRunningInformation( ).getEstimatedTotalTime( ) - build.getRunningInformation( ).getElapsedTime( ), ChronoUnit.SECONDS ) : Duration.ZERO );

}
