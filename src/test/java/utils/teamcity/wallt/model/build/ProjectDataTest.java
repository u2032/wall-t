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

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Date: 02/03/14
 *
 * @author Cedric Longo
 */
public class ProjectDataTest {

    private ProjectData _data;

    @Before
    public void setUp( ) throws Exception {
        _data = new ProjectData( "pId", "pName", Optional.of( "pParentId" ) );
    }

    @Test
    public void data_is_correctly_set_on_object( ) throws Exception {
        // Setup
        // Exercise
        // Verify
        assertThat( _data.getId( ), is( "pId" ) );
        assertThat( _data.getName( ), is( "pName" ) );
        assertThat( _data.getParentId( ).isPresent( ), is( true ) );
        assertThat( _data.getParentId( ).get( ), is( "pParentId" ) );
    }

    @Test
    public void alias_name_is_correctly_recorded( ) throws Exception {
        // Setup
        // Exercise
        _data.setAliasName( "pAliasName" );
        // Verify
        assertThat( _data.getAliasName( ), is( "pAliasName" ) );
    }

    @Test
    public void build_type_is_correctly_recorded_on_project( ) throws Exception {
        // Setup
        // Exercise
        _data.registerBuildType( newBuildType( "bt1" ) );
        _data.registerBuildType( newBuildType( "bt2" ) );
        // Verify
        final List<BuildTypeData> buildTypes = _data.getBuildTypes( );
        assertThat( buildTypes.size( ), is( 2 ) );
        assertThat( buildTypes.get( 0 ).getId( ), is( "bt1" ) );
        assertThat( buildTypes.get( 1 ).getId( ), is( "bt2" ) );
    }

    @Test
    public void registering_a_build_with_same_id_overwrite_data( ) throws Exception {
        // Setup
        final BuildTypeData bt = newBuildType( "bt1" );
        bt.setAliasName( "new" );
        // Exercise
        _data.registerBuildType( newBuildType( "bt1" ) );
        _data.registerBuildType( bt );
        // Verify
        final List<BuildTypeData> buildTypes = _data.getBuildTypes( );
        assertThat( buildTypes.size( ), is( 1 ) );
        assertThat( buildTypes.get( 0 ).getAliasName( ), is( "new" ) );
    }

    @Test
    public void hasBuildTypeRunning_returns_false_when_no_buildType_is_running( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = newBuildType( "bt1" );
        bt1.registerBuild( newBuild( 1200, BuildState.finished, BuildStatus.SUCCESS ) );
        bt1.registerBuild( newBuild( 1201, BuildState.finished, BuildStatus.SUCCESS ) );

        final BuildTypeData bt2 = newBuildType( "bt2" );
        bt1.registerBuild( newBuild( 1202, BuildState.finished, BuildStatus.SUCCESS ) );
        bt1.registerBuild( newBuild( 1203, BuildState.finished, BuildStatus.SUCCESS ) );

        _data.registerBuildType( bt1 );
        _data.registerBuildType( bt2 );
        // Exercise
        final boolean hasBuildTypeRunning = _data.hasBuildTypeRunning( BuildStatus.SUCCESS );
        // Verify
        assertThat( hasBuildTypeRunning, is( false ) );
    }

    @Test
    public void hasBuildTypeRunning_returns_false_when_no_buildType_is_running_with_last_finished_status( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = newBuildType( "bt1" );
        bt1.registerBuild( newBuild( 1200, BuildState.finished, BuildStatus.FAILURE ) );
        bt1.registerBuild( newBuild( 1201, BuildState.running, BuildStatus.SUCCESS ) );

        final BuildTypeData bt2 = newBuildType( "bt2" );
        bt2.registerBuild( newBuild( 1202, BuildState.finished, BuildStatus.SUCCESS ) );
        bt2.registerBuild( newBuild( 1203, BuildState.finished, BuildStatus.SUCCESS ) );

        _data.registerBuildType( bt1 );
        _data.registerBuildType( bt2 );
        // Exercise
        final boolean hasBuildTypeRunning = _data.hasBuildTypeRunning( BuildStatus.SUCCESS );
        // Verify
        assertThat( hasBuildTypeRunning, is( false ) );
    }

    @Test
    public void hasBuildTypeRunning_returns_true_when_buildType_is_running_with_last_finished_status( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = newBuildType( "bt1" );
        bt1.registerBuild( newBuild( 1200, BuildState.finished, BuildStatus.SUCCESS ) );
        bt1.registerBuild( newBuild( 1201, BuildState.running, BuildStatus.FAILURE ) );

        final BuildTypeData bt2 = newBuildType( "bt2" );
        bt2.registerBuild( newBuild( 1202, BuildState.finished, BuildStatus.SUCCESS ) );
        bt2.registerBuild( newBuild( 1203, BuildState.finished, BuildStatus.SUCCESS ) );

        _data.registerBuildType( bt1 );
        _data.registerBuildType( bt2 );
        // Exercise
        final boolean hasBuildTypeRunning = _data.hasBuildTypeRunning( BuildStatus.SUCCESS );
        // Verify
        assertThat( hasBuildTypeRunning, is( true ) );
    }

    @Test
    public void getBuildTypeCount_returns_zero_when_no_buildType( ) throws Exception {
        // Setup
        // Exercise
        final int buildTypeCount = _data.getBuildTypeCount( BuildStatus.SUCCESS );
        // Verify
        assertThat( buildTypeCount, is( 0 ) );
    }


    @Test
    public void getBuildTypeCount_returns_zero_when_no_buildType_with_last_finished_status( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = newBuildType( "bt1" );
        bt1.registerBuild( newBuild( 1200, BuildState.finished, BuildStatus.FAILURE ) );
        bt1.registerBuild( newBuild( 1201, BuildState.finished, BuildStatus.FAILURE ) );

        final BuildTypeData bt2 = newBuildType( "bt2" );
        bt1.registerBuild( newBuild( 1202, BuildState.running, BuildStatus.SUCCESS ) );
        bt1.registerBuild( newBuild( 1203, BuildState.running, BuildStatus.SUCCESS ) );

        _data.registerBuildType( bt1 );
        _data.registerBuildType( bt2 );
        // Exercise
        final int buildTypeCount = _data.getBuildTypeCount( BuildStatus.SUCCESS );
        // Verify
        assertThat( buildTypeCount, is( 0 ) );
    }

    @Test
    public void getBuildTypeCount_returns_correct_value_when_buildType_with_last_finished_status( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = newBuildType( "bt1" );
        bt1.registerBuild( newBuild( 1200, BuildState.finished, BuildStatus.SUCCESS ) );
        bt1.registerBuild( newBuild( 1201, BuildState.finished, BuildStatus.FAILURE ) );

        final BuildTypeData bt2 = newBuildType( "bt2" );
        bt1.registerBuild( newBuild( 1202, BuildState.finished, BuildStatus.FAILURE ) );
        bt1.registerBuild( newBuild( 1203, BuildState.finished, BuildStatus.SUCCESS ) );

        _data.registerBuildType( bt1 );
        _data.registerBuildType( bt2 );
        // Exercise
        final int buildTypeCount = _data.getBuildTypeCount( BuildStatus.SUCCESS );
        // Verify
        assertThat( buildTypeCount, is( 1 ) );
    }


    private BuildTypeData newBuildType( final String id ) {
        return new BuildTypeData( id, "btName", "btProjectId", "btProjectName" );
    }

    private BuildData newBuild( final int id, final BuildState state, final BuildStatus status ) {
        return new BuildData( id, status, state, 58, Optional.empty( ), Duration.ofSeconds( 98 ) );
    }
}
