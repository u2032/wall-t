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

package utils.teamcity.wallt.model.build;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import org.junit.Before;
import org.junit.Test;
import utils.teamcity.wallt.TestModules;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Date: 02/03/14
 *
 * @author Cedric Longo
 */
public class BuildDataManagerTest {

    @Inject
    private IBuildManager _buildManager;

    @Before
    public void setUp( ) throws Exception {
        Guice.createInjector( TestModules.defaultModules( ) )
                .injectMembers( this );
    }

    @Test
    public void registerBuildTypes_records_correctly_build_types( ) throws Exception {
        // Setup
        final List<BuildTypeData> buildTypes = ImmutableList.of( newBuildType( "bt1" ), newBuildType( "bt2" ) );
        // Exercise
        _buildManager.registerBuildTypes( buildTypes );
        // Verify
        final List<BuildTypeData> buildTypesInManager = _buildManager.getBuildTypes( );
        assertThat( buildTypesInManager.size( ), is( 2 ) );
        assertThat( buildTypesInManager.get( 0 ).getId( ), is( "bt1" ) );
        assertThat( buildTypesInManager.get( 1 ).getId( ), is( "bt2" ) );
    }

    @Test
    public void registerBuildTypes_records_correctly_build_types_by_overwritting_previous_data( ) throws Exception {
        // Setup
        final List<BuildTypeData> buildTypes = ImmutableList.of( newBuildType( "bt1" ), newBuildType( "bt2" ) );
        final List<BuildTypeData> newBuildTypes = ImmutableList.of( newBuildType( "bt2" ), newBuildType( "bt3" ) );
        _buildManager.registerBuildTypes( buildTypes );
        // Exercise
        _buildManager.registerBuildTypes( newBuildTypes );
        // Verify
        final List<BuildTypeData> buildTypesInManager = _buildManager.getBuildTypes( );
        assertThat( buildTypesInManager.size( ), is( 2 ) );
        assertThat( buildTypesInManager.get( 0 ).getId( ), is( "bt2" ) );
        assertThat( buildTypesInManager.get( 1 ).getId( ), is( "bt3" ) );
    }

    @Test
    public void registerBuildTypes_keep_monitored_build_types_as_monitored( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = newBuildType( "bt1" );
        final BuildTypeData bt2 = newBuildType( "bt2" );
        final List<BuildTypeData> buildTypes = ImmutableList.of( bt1, bt2 );
        _buildManager.registerBuildTypes( buildTypes );
        _buildManager.activateMonitoring( bt1 );
        _buildManager.activateMonitoring( bt2 );
        final List<BuildTypeData> newBuildTypes = ImmutableList.of( newBuildType( "bt2" ), newBuildType( "bt3" ) );
        // Exercise
        _buildManager.registerBuildTypes( newBuildTypes );
        // Verify
        final List<BuildTypeData> buildTypesInManager = _buildManager.getMonitoredBuildTypes( );
        assertThat( buildTypesInManager.size( ), is( 1 ) );
        assertThat( buildTypesInManager.get( 0 ).getId( ), is( "bt2" ) );
    }

    @Test
    public void registerBuildTypes_keep_monitored_build_types_order( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = newBuildType( "bt1" );
        final BuildTypeData bt2 = newBuildType( "bt2" );
        final List<BuildTypeData> buildTypes = ImmutableList.of( bt2, bt1 );
        _buildManager.registerBuildTypes( buildTypes );
        _buildManager.activateMonitoring( bt2 );
        _buildManager.activateMonitoring( bt1 );
        // Exercise
        _buildManager.registerBuildTypes( ImmutableList.of( bt1, bt2 ) );
        // Verify
        final List<BuildTypeData> buildTypesInManager = _buildManager.getMonitoredBuildTypes( );
        assertThat( buildTypesInManager.size( ), is( 2 ) );
        assertThat( buildTypesInManager.get( 0 ).getId( ), is( "bt2" ) );
        assertThat( buildTypesInManager.get( 1 ).getId( ), is( "bt1" ) );
    }

    @Test
    public void registerBuildTypes_keep_monitored_aliases( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = newBuildType( "bt1" );
        final BuildTypeData bt2 = newBuildType( "bt2" );
        bt2.setAliasName( "myAlias" );
        _buildManager.registerBuildTypes( ImmutableList.of( bt1, bt2 ) );
        _buildManager.activateMonitoring( bt2 );
        // Exercise
        _buildManager.registerBuildTypes( ImmutableList.of( newBuildType( "bt1" ), newBuildType( "bt2" ) ) );
        // Verify
        final List<BuildTypeData> buildTypesInManager = _buildManager.getMonitoredBuildTypes( );
        assertThat( buildTypesInManager.size( ), is( 1 ) );
        assertThat( buildTypesInManager.get( 0 ).getId( ), is( "bt2" ) );
        assertThat( buildTypesInManager.get( 0 ).getAliasName( ), is( "myAlias" ) );
    }

    @Test
    public void when_activating_monitoring_build_is_pushed_into_monitored_build_type_list( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = newBuildType( "bt1" );
        final List<BuildTypeData> buildTypes = ImmutableList.of( bt1, newBuildType( "bt2" ) );
        _buildManager.registerBuildTypes( buildTypes );
        // Exercise
        _buildManager.activateMonitoring( bt1 );
        // Verify
        final List<BuildTypeData> buildTypesInManager = _buildManager.getMonitoredBuildTypes( );
        assertThat( buildTypesInManager.size( ), is( 1 ) );
        assertThat( buildTypesInManager.get( 0 ).getId( ), is( "bt1" ) );
    }

    @Test
    public void when_unactivating_monitoring_build_is_removed_from_monitored_build_type_list( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = newBuildType( "bt1" );
        final BuildTypeData bt2 = newBuildType( "bt2" );
        final List<BuildTypeData> buildTypes = ImmutableList.of( bt1, bt2 );
        _buildManager.registerBuildTypes( buildTypes );
        _buildManager.activateMonitoring( bt1 );
        _buildManager.activateMonitoring( bt2 );
        // Exercise
        _buildManager.unactivateMonitoring( bt1 );
        // Verify
        final List<BuildTypeData> buildTypesInManager = _buildManager.getMonitoredBuildTypes( );
        assertThat( buildTypesInManager.size( ), is( 1 ) );
        assertThat( buildTypesInManager.get( 0 ).getId( ), is( "bt2" ) );
    }

    @Test
    public void requestPosition_changes_monitored_build_type_ordering( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = newBuildType( "bt1" );
        final BuildTypeData bt2 = newBuildType( "bt2" );
        _buildManager.registerBuildTypes( ImmutableList.of( bt1, bt2 ) );
        _buildManager.activateMonitoring( bt1 );
        _buildManager.activateMonitoring( bt2 );
        // Exercise
        _buildManager.requestPosition( bt1, 2 );
        // Verify
        final List<BuildTypeData> buildTypesInManager = _buildManager.getMonitoredBuildTypes( );
        assertThat( buildTypesInManager.size( ), is( 2 ) );
        assertThat( buildTypesInManager.get( 0 ).getId( ), is( "bt2" ) );
        assertThat( buildTypesInManager.get( 1 ).getId( ), is( "bt1" ) );
        assertThat( _buildManager.getPosition( bt1 ), is( 2 ) );
        assertThat( _buildManager.getPosition( bt2 ), is( 1 ) );
    }

    @Test
    public void registerBuildTypesInQueue_changes_queued_status_and_returns_modified_build_types( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = newBuildType( "bt1" );
        final BuildTypeData bt2 = newBuildType( "bt2" );
        final BuildTypeData bt3 = newBuildType( "bt3" );
        bt3.setQueued( true );
        _buildManager.registerBuildTypes( ImmutableList.of( bt1, bt2, bt3 ) );
        _buildManager.activateMonitoring( bt1 );
        _buildManager.activateMonitoring( bt2 );
        _buildManager.activateMonitoring( bt3 );
        // Exercise
        final List<BuildTypeData> modifiedBuildTypes = _buildManager.registerBuildTypesInQueue( ImmutableSet.<String>of( "bt2", "bt3" ) );
        // Verify
        assertThat( modifiedBuildTypes.size( ), is( 1 ) );
        assertThat( modifiedBuildTypes.get( 0 ).getId( ), is( "bt2" ) );
        assertThat( modifiedBuildTypes.get( 0 ).isQueued( ), is( true ) );
        assertThat( bt1.isQueued( ), is( false ) );
        assertThat( bt2.isQueued( ), is( true ) );
        assertThat( bt3.isQueued( ), is( true ) );
    }

    private BuildTypeData newBuildType( final String id ) {
        return new BuildTypeData( id, "btName", "btProjectId", "btProjectName" );
    }

}
