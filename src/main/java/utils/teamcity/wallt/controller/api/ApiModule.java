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

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import utils.teamcity.wallt.controller.api.json.Build;
import utils.teamcity.wallt.controller.api.json.BuildType;
import utils.teamcity.wallt.controller.api.json.Project;
import utils.teamcity.wallt.model.build.BuildData;
import utils.teamcity.wallt.model.build.BuildState;
import utils.teamcity.wallt.model.build.BuildTypeData;
import utils.teamcity.wallt.model.build.ProjectData;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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
                .setUserAgent( "TeamCity Wall Client" )
                .setFollowRedirects( true )
                .setRemoveQueryParamsOnRedirect( false )
                .setAllowPoolingConnection( true )
                .setAllowSslConnectionPool( true )
                .setMaximumNumberOfRedirects( 5 )
                .setMaximumConnectionsPerHost( 10 )
                .setConnectionTimeoutInMs( 60000 )
                .setRequestTimeoutInMs( 30000 )
                .setIdleConnectionInPoolTimeoutInMs( 600000 ) // 10 min idle
                .build( );
    }

    @Provides
    @Singleton
    public AsyncHttpClient httpClient( final AsyncHttpClientConfig config ) {
        return new AsyncHttpClient( config );
    }

    @Provides
    @Singleton
    public Map<ApiVersion, Function<BuildType, BuildTypeData>> buildTypeByApiVersion( ) {
        return ImmutableMap.of(
                ApiVersion.API_6_0,
                btype -> new BuildTypeData( btype.getId( ), btype.getName( ), btype.getProjectId( ), btype.getProjectName( ) ),

                ApiVersion.API_7_0,
                btype -> new BuildTypeData( btype.getId( ), btype.getName( ), btype.getProjectId( ), btype.getProjectName( ) ),

                ApiVersion.API_8_0,
                btype -> new BuildTypeData( btype.getId( ), btype.getName( ), btype.getProjectId( ), btype.getProjectName( ) ),

                ApiVersion.API_8_1,
                btype -> new BuildTypeData( btype.getId( ), btype.getName( ), btype.getProjectId( ), btype.getProjectName( ) )
        );
    }

    @Provides
    @Singleton
    public Map<ApiVersion, Function<Project, ProjectData>> projectByApiVersion( ) {
        return ImmutableMap.of(
                ApiVersion.API_6_0,
                project -> new ProjectData( project.getId( ), project.getName( ), Optional.empty( ) ),

                ApiVersion.API_7_0,
                project -> new ProjectData( project.getId( ), project.getName( ), Optional.empty( ) ),

                ApiVersion.API_8_0,
                project -> new ProjectData( project.getId( ), project.getName( ), Optional.empty( ) ),

                ApiVersion.API_8_1,
                project -> new ProjectData( project.getId( ), project.getName( ), Optional.ofNullable( project.getParentId( ) ) )
        );
    }

    @Provides
    @Singleton
    public Map<ApiVersion, Function<Build, BuildData>> buildByApiVersion( ) {
        return ImmutableMap.of(
                ApiVersion.API_6_0,
                build -> new BuildData( build.getId( ), build.getStatus( ),
                        build.isRunning( ) ? BuildState.running : BuildState.finished,
                        build.isRunning( ) ? build.getRunningInformation( ).getPercentageComplete( ) : 100,
                        Optional.ofNullable( build.getFinishDate( ) ),
                        build.isRunning( ) ? Duration.of( build.getRunningInformation( ).getEstimatedTotalTime( ) - build.getRunningInformation( ).getElapsedTime( ), ChronoUnit.SECONDS ) : Duration.ZERO ),


                ApiVersion.API_7_0,
                build -> new BuildData( build.getId( ), build.getStatus( ),
                        build.isRunning( ) ? BuildState.running : BuildState.finished,
                        build.isRunning( ) ? build.getRunningInformation( ).getPercentageComplete( ) : 100,
                        Optional.ofNullable( build.getFinishDate( ) ),
                        build.isRunning( ) ? Duration.of( build.getRunningInformation( ).getEstimatedTotalTime( ) - build.getRunningInformation( ).getElapsedTime( ), ChronoUnit.SECONDS ) : Duration.ZERO ),

                ApiVersion.API_8_0,
                build -> new BuildData( build.getId( ), build.getStatus( ),
                        build.isRunning( ) ? BuildState.running : BuildState.finished,
                        build.isRunning( ) ? build.getRunningInformation( ).getPercentageComplete( ) : 100,
                        Optional.ofNullable( build.getFinishDate( ) ),
                        build.isRunning( ) ? Duration.of( build.getRunningInformation( ).getEstimatedTotalTime( ) - build.getRunningInformation( ).getElapsedTime( ), ChronoUnit.SECONDS ) : Duration.ZERO ),

                ApiVersion.API_8_1,
                build -> new BuildData( build.getId( ), build.getStatus( ),
                        build.getState( ),
                        build.getState( ) == BuildState.running ? build.getRunningInformation( ).getPercentageComplete( ) : 100,
                        Optional.ofNullable( build.getFinishDate( ) ),
                        build.getState( ) == BuildState.running ? Duration.of( build.getRunningInformation( ).getEstimatedTotalTime( ) - build.getRunningInformation( ).getElapsedTime( ), ChronoUnit.SECONDS ) : Duration.ZERO )
        );
    }
}
