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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.ning.http.client.AsyncHttpClientConfig;
import org.junit.Before;
import org.junit.Test;
import utils.teamcity.wallt.TestModules;
import utils.teamcity.wallt.controller.api.json.Build;
import utils.teamcity.wallt.controller.api.json.BuildType;
import utils.teamcity.wallt.controller.api.json.Project;
import utils.teamcity.wallt.model.build.BuildData;
import utils.teamcity.wallt.model.build.BuildTypeData;
import utils.teamcity.wallt.model.build.ProjectData;

import javax.inject.Inject;
import java.util.Map;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Date: 02/03/14
 *
 * @author Cedric Longo
 */
public class ApiModuleTest {

    @Inject
    private Injector _injector;

    @Before
    public void setUp( ) throws Exception {
        Guice.createInjector( TestModules.defaultModules( ) )
                .injectMembers( this );
    }

    @Test
    public void can_inject_ApiController_as_IApiController_in_singleton( ) throws Exception {
        // Setup
        // Exercise
        final IApiController instance = _injector.getInstance( IApiController.class );
        final IApiController instance2 = _injector.getInstance( IApiController.class );
        // Verify
        assertThat( instance, is( notNullValue( ) ) );
        assertThat( instance, is( instanceOf( ApiController.class ) ) );
        assertThat( instance, is( sameInstance( instance2 ) ) );
    }

    @Test
    public void can_inject_ApiMonitoringService_as_IApiMonitoringService_in_singleton( ) throws Exception {
        // Setup
        // Exercise
        final IApiMonitoringService instance = _injector.getInstance( IApiMonitoringService.class );
        final IApiMonitoringService instance2 = _injector.getInstance( IApiMonitoringService.class );
        // Verify
        assertThat( instance, is( notNullValue( ) ) );
        assertThat( instance, is( instanceOf( ApiMonitoringService.class ) ) );
        assertThat( instance, is( sameInstance( instance2 ) ) );
    }

    @Test
    public void can_inject_ApiRequestController_as_IApiController_in_singleton( ) throws Exception {
        // Setup
        // Exercise
        final IApiRequestController instance = _injector.getInstance( IApiRequestController.class );
        final IApiRequestController instance2 = _injector.getInstance( IApiRequestController.class );
        // Verify
        assertThat( instance, is( notNullValue( ) ) );
        assertThat( instance, is( instanceOf( ApiRequestController.class ) ) );
        assertThat( instance, is( sameInstance( instance2 ) ) );
    }

    @Test
    public void can_inject_AsyncHttpClientConfig_in_singleton( ) throws Exception {
        // Setup
        // Exercise
        final AsyncHttpClientConfig instance = _injector.getInstance( AsyncHttpClientConfig.class );
        final AsyncHttpClientConfig instance2 = _injector.getInstance( AsyncHttpClientConfig.class );
        // Verify
        assertThat( instance, is( notNullValue( ) ) );
        assertThat( instance, is( sameInstance( instance2 ) ) );
    }

    @Test
    public void can_inject_functions_to_build_ProjectData_in_singleton( ) throws Exception {
        // Setup
        // Exercise
        final Map<ApiVersion, Function<Project, ProjectData>> instance = _injector.getInstance( Key.get( new TypeLiteral<Map<ApiVersion, Function<Project, ProjectData>>>( ) {
        } ) );
        final Map<ApiVersion, Function<Project, ProjectData>> instance2 = _injector.getInstance( Key.get( new TypeLiteral<Map<ApiVersion, Function<Project, ProjectData>>>( ) {
        } ) );
        // Verify
        assertThat( instance, is( notNullValue( ) ) );
        assertThat( instance, is( sameInstance( instance2 ) ) );
    }

    @Test
    public void can_inject_functions_to_build_BuildTypeData_in_singleton( ) throws Exception {
        // Setup
        // Exercise
        final Map<ApiVersion, Function<BuildType, BuildTypeData>> instance = _injector.getInstance( Key.get( new TypeLiteral<Map<ApiVersion, Function<BuildType, BuildTypeData>>>( ) {
        } ) );
        final Map<ApiVersion, Function<BuildType, BuildTypeData>> instance2 = _injector.getInstance( Key.get( new TypeLiteral<Map<ApiVersion, Function<BuildType, BuildTypeData>>>( ) {
        } ) );
        // Verify
        assertThat( instance, is( notNullValue( ) ) );
        assertThat( instance, is( sameInstance( instance2 ) ) );
    }

    @Test
    public void can_inject_functions_to_build_BuildData_in_singleton( ) throws Exception {
        // Setup
        // Exercise
        final Map<ApiVersion, Function<Build, BuildData>> instance = _injector.getInstance( Key.get( new TypeLiteral<Map<ApiVersion, Function<Build, BuildData>>>( ) {
        } ) );
        final Map<ApiVersion, Function<Build, BuildData>> instance2 = _injector.getInstance( Key.get( new TypeLiteral<Map<ApiVersion, Function<Build, BuildData>>>( ) {
        } ) );
        // Verify
        assertThat( instance, is( notNullValue( ) ) );
        assertThat( instance, is( sameInstance( instance2 ) ) );
    }


    @Test
    public void has_functions_to_build_ProjectData_for_all_api_versions( ) throws Exception {
        // Setup
        // Exercise
        final Map<ApiVersion, Function<Project, ProjectData>> instance = _injector.getInstance( Key.get( new TypeLiteral<Map<ApiVersion, Function<Project, ProjectData>>>( ) {
        } ) );
        // Verify
        assertThat( instance.keySet( ), containsInAnyOrder( ApiVersion.values( ) ) );
    }

    @Test
    public void has_functions_to_build_BuildTypeData_for_all_api_versions( ) throws Exception {
        // Setup
        // Exercise
        final Map<ApiVersion, Function<BuildType, BuildTypeData>> instance = _injector.getInstance( Key.get( new TypeLiteral<Map<ApiVersion, Function<BuildType, BuildTypeData>>>( ) {
        } ) );
        // Verify
        assertThat( instance.keySet( ), containsInAnyOrder( ApiVersion.values( ) ) );
    }


    @Test
    public void has_functions_to_build_BuildData_for_all_api_versions( ) throws Exception {
        // Setup
        // Exercise
        final Map<ApiVersion, Function<Build, BuildData>> instance = _injector.getInstance( Key.get( new TypeLiteral<Map<ApiVersion, Function<Build, BuildData>>>( ) {
        } ) );
        // Verify
        assertThat( instance.keySet( ), containsInAnyOrder( ApiVersion.values( ) ) );
    }
}
