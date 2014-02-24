package utils.teamcity.controller.api;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import utils.teamcity.controller.api.json.Build;
import utils.teamcity.model.build.BuildData;
import utils.teamcity.model.build.BuildState;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
public final class ApiModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( IApiRequestController.class ).to( ApiRequestController.class ).in( Scopes.SINGLETON );
        bind( IApiController.class ).to( ApiController.class ).in( Scopes.SINGLETON );
        bind( IApiMonitoringService.class ).to( ApiMonitoringService.class ).asEagerSingleton( );
    }

    @Provides
    @Singleton
    public AsyncHttpClientConfig httpClientConfig( ) {
        return new AsyncHttpClientConfig.Builder( )
                .setFollowRedirects( true )
                .setUserAgent( "TeamCity Wall Client" )
                .setAllowPoolingConnection( true )
                .setAllowSslConnectionPool( true )
                .setMaxConnectionLifeTimeInMs( 60000 )
                .setMaximumNumberOfRedirects( 5 )
                .setRequestTimeoutInMs( 30000 )
                .setMaximumConnectionsPerHost( 5 )
                .build( );
    }

    @Provides
    @Singleton
    public AsyncHttpClient httpClient( final AsyncHttpClientConfig config ) {
        return new AsyncHttpClient( config );
    }

    @Provides
    @Singleton
    public Map<ApiVersion, Function<Build, BuildData>> controllerByApiVersion( ) {
        return ImmutableMap.of(
                ApiVersion.API_8_0,
                build -> new BuildData( build.getId( ), build.getBuildType( ), build.getStatus( ),
                        build.isRunning( ) ? BuildState.running : BuildState.finished,
                        build.isRunning( ) ? build.getRunningInformation( ).getPercentageComplete( ) : 100,
                        Optional.ofNullable( build.getFinishedDate( ) ),
                        build.isRunning( ) ? Duration.of( build.getRunningInformation( ).getEstimatedTotalTime( ) - build.getRunningInformation( ).getElapsedTime( ), ChronoUnit.SECONDS ) : Duration.ZERO ),

                ApiVersion.API_8_1,
                build -> new BuildData( build.getId( ), build.getBuildType( ), build.getStatus( ),
                        build.getState( ),
                        build.getState( ) == BuildState.running ? build.getRunningInformation( ).getPercentageComplete( ) : 100,
                        Optional.ofNullable( build.getFinishedDate( ) ),
                        build.getState( ) == BuildState.running ? Duration.of( build.getRunningInformation( ).getEstimatedTotalTime( ) - build.getRunningInformation( ).getElapsedTime( ), ChronoUnit.SECONDS ) : Duration.ZERO )
        );
    }
}
