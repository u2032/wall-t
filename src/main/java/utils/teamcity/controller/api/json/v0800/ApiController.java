package utils.teamcity.controller.api.json.v0800;

import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.controller.api.IApiController;
import utils.teamcity.controller.api.IApiRequestController;
import utils.teamcity.model.build.BuildData;
import utils.teamcity.model.build.BuildState;
import utils.teamcity.model.build.BuildTypeData;
import utils.teamcity.model.build.IBuildManager;
import utils.teamcity.model.logger.Loggers;

import javax.inject.Inject;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.util.concurrent.Futures.addCallback;
import static utils.teamcity.controller.api.json.ApiVersion.API_8_0;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
public final class ApiController implements IApiController {

    public static final Logger LOGGER = LoggerFactory.getLogger( Loggers.MAIN );
    private final IBuildManager _buildManager;
    private final IApiRequestController _apiRequestController;

    @Inject
    public ApiController( final IBuildManager buildManager, final IApiRequestController apiRequestController ) {
        _apiRequestController = apiRequestController;
        _buildManager = buildManager;
    }

    @Override
    public ListenableFuture<Void> loadBuildList( ) {
        final ListenableFuture<BuildTypeList> buildListFuture = _apiRequestController.sendRequest( API_8_0, "buildTypes", BuildTypeList.class );
        addCallback( buildListFuture, new FutureCallback<BuildTypeList>( ) {
            @Override
            public void onSuccess( final BuildTypeList result ) {
                final List<BuildTypeData> buildTypes = Arrays.asList( result.getBuildTypes( ) ).stream( )
                        .map( ( btype ) -> new BuildTypeData( btype.getId( ), btype.getName( ), btype.getProjectName( ) ) )
                        .collect( Collectors.toList( ) );
                _buildManager.registerBuildTypes( buildTypes );
            }

            @Override
            public void onFailure( final Throwable t ) {
                LOGGER.error( "Error during loading build type list:", t );
            }
        } );

        return Futures.transform( buildListFuture, (Function<BuildTypeList, Void>) buildTypeList -> null );
    }

    @Override
    public void requestLastBuildStatus( final BuildTypeData buildType ) {
        final ListenableFuture<BuildList> buildListFuture = _apiRequestController.sendRequest( API_8_0, "builds/?locator=buildType:" + buildType.getId( ) + ",running:any", BuildList.class );
        addCallback( buildListFuture, new FutureCallback<BuildList>( ) {
            @Override
            public void onSuccess( final BuildList result ) {
                if ( result.getBuilds( ) == null )
                    return;

                // We consider only 5 last builds
                final List<Build> buildToRequest = Arrays.asList( result.getBuilds( ) ).stream( )
                        .limit( 5 )
                        .collect( Collectors.toList( ) );

                // We removed from list builds which status is already known
                buildToRequest.removeIf( build -> {
                    final Optional<BuildData> previousBuildStatus = buildType.getBuildById( build.getId( ) );
                    return previousBuildStatus.isPresent( ) && previousBuildStatus.get( ).getState( ) == BuildState.finished;
                } );

                for ( final Build build : buildToRequest ) {
                    final ListenableFuture<Build> buildStatusFuture = _apiRequestController.sendRequest( API_8_0, "builds/id:" + build.getId( ), Build.class );
                    addCallback( buildStatusFuture, registerBuildStatus( buildType, build ) );
                }
            }

            @Override
            public void onFailure( final Throwable t ) {
                LOGGER.error( "Error during loading builds list for build type: " + buildType.getId( ), t );
            }
        } );
    }

    private FutureCallback<Build> registerBuildStatus( final BuildTypeData buildType, final Build build ) {
        return new FutureCallback<Build>( ) {
            @Override
            public void onSuccess( final Build result ) {
                buildType.registerBuild( _toBuildData.apply( result ) );
            }

            @Override
            public void onFailure( final Throwable t ) {
                LOGGER.error( "Error during loading ull information for build with id " + build.getId( ) + ", build type: " + buildType.getId( ), t );
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
