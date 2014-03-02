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
public class BuildTypeDataTest {

    private BuildTypeData _data;

    @Before
    public void setUp( ) throws Exception {
        _data = new BuildTypeData( "btId", "btName", "btProjectId", "btProjectName" );
    }

    @Test
    public void data_is_correctly_set_on_objet( ) throws Exception {
        // Setup
        // Exercise
        // Verify
        assertThat( _data.getId( ), is( "btId" ) );
        assertThat( _data.getName( ), is( "btName" ) );
        assertThat( _data.getProjectId( ), is( "btProjectId" ) );
        assertThat( _data.getProjectName( ), is( "btProjectName" ) );
    }

    @Test
    public void when_alias_name_is_set_is_correctly_recorded( ) throws Exception {
        // Setup
        // Exercise
        _data.setAliasName( "btAliasName" );
        // Verify
        assertThat( _data.getAliasName( ), is( "btAliasName" ) );
    }

    @Test
    public void when_queued_status_is_set_is_correctly_recorded( ) throws Exception {
        // Setup
        // Exercise
        _data.setQueued( true );
        // Verify
        assertThat( _data.isQueued( ), is( true ) );
    }

    @Test
    public void getBuildById_returns_the_correct_build_or_empty( ) throws Exception {
        // Setup
        _data.registerBuild( newBuild( 12246, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12247, BuildState.finished, BuildStatus.FAILURE ) );
        // Exercise
        final Optional<BuildData> buildById = _data.getBuildById( 12246 );
        final Optional<BuildData> buildById2 = _data.getBuildById( 12248 );
        // Verify
        assertThat( buildById.isPresent( ), is( true ) );
        assertThat( buildById.get( ).getId( ), is( 12246 ) );
        assertThat( buildById2.isPresent( ), is( false ) );
    }


    @Test
    public void when_a_build_is_register_to_build_type_is_correctly_recorded( ) throws Exception {
        // Setup
        final BuildData build = newBuild( 12246, BuildState.finished, BuildStatus.FAILURE );
        // Exercise
        _data.registerBuild( build );
        // Verify
        final List<BuildData> builds = _data.getBuilds( );
        assertThat( builds.size( ), is( 1 ) );
        assertThat( builds.get( 0 ).getId( ), is( 12246 ) );
    }

    @Test
    public void when_a_build_is_register_with_same_id_data_is_overwritten( ) throws Exception {
        // Setup
        final BuildData build = newBuild( 12246, BuildState.finished, BuildStatus.FAILURE );
        _data.registerBuild( build );
        // Exercise
        _data.registerBuild( newBuild( 12246, BuildState.running, BuildStatus.FAILURE ) );
        // Verify
        final List<BuildData> builds = _data.getBuilds( );
        assertThat( builds.size( ), is( 1 ) );
        assertThat( builds.get( 0 ).getState( ), is( BuildState.running ) );
    }

    @Test
    public void when_a_build_is_running_hasRunningBuild_returns_true( ) throws Exception {
        // Setup
        // Exercise
        _data.registerBuild( newBuild( 12246, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12247, BuildState.running, BuildStatus.FAILURE ) );
        // Verify
        assertThat( _data.hasRunningBuild( ), is( true ) );
    }

    @Test
    public void when_no_build_is_running_hasRunningBuild_returns_false( ) throws Exception {
        // Setup
        // Exercise
        _data.registerBuild( newBuild( 12246, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12247, BuildState.finished, BuildStatus.FAILURE ) );
        // Verify
        assertThat( _data.hasRunningBuild( ), is( false ) );
    }

    @Test
    public void when_a_build_is_register_to_build_type_build_list_is_always_sorted_by_id_desc( ) throws Exception {
        // Setup
        final BuildData build = newBuild( 12246, BuildState.finished, BuildStatus.FAILURE );
        final BuildData build2 = newBuild( 12248, BuildState.finished, BuildStatus.FAILURE );
        final BuildData build3 = newBuild( 12247, BuildState.finished, BuildStatus.FAILURE );
        // Exercise
        _data.registerBuild( build );
        _data.registerBuild( build2 );
        _data.registerBuild( build3 );
        // Verify
        final List<BuildData> builds = _data.getBuilds( );
        assertThat( builds.size( ), is( 3 ) );
        assertThat( builds.get( 0 ).getId( ), is( 12248 ) );
        assertThat( builds.get( 1 ).getId( ), is( 12247 ) );
        assertThat( builds.get( 2 ).getId( ), is( 12246 ) );
    }

    @Test
    public void oldest_builds_is_drop_when_too_much_build_data( ) throws Exception {
        // Setup
        // Exercise
        for ( int i = 0; i < BuildTypeData.MAX_BUILD_SIZE_TO_CACHE * 2; i++ )
            _data.registerBuild( newBuild( 1200 + i, BuildState.running, BuildStatus.FAILURE ) );
        // Verify
        final List<BuildData> builds = _data.getBuilds( );
        assertThat( builds.size( ), is( BuildTypeData.MAX_BUILD_SIZE_TO_CACHE ) );
    }

    @Test
    public void getLastBuilds_returns_the_correct_build_number( ) throws Exception {
        // Setup
        for ( int i = 0; i < BuildTypeData.MAX_BUILD_SIZE_TO_CACHE * 2; i++ )
            _data.registerBuild( newBuild( 1200 - i, i % 2 == 0 ? BuildState.finished : BuildState.running, BuildStatus.FAILURE ) );
        // Exercise
        final List<BuildData> lastBuilds = _data.getLastBuilds( BuildState.finished, 3 );
        // Verify
        assertThat( lastBuilds.size( ), is( 3 ) );
        assertThat( lastBuilds.get( 0 ).getId( ), is( 1200 ) );
        assertThat( lastBuilds.get( 1 ).getId( ), is( 1198 ) );
        assertThat( lastBuilds.get( 2 ).getId( ), is( 1196 ) );
    }


    @Test
    public void getOldestBuild_returns_the_correct_build_according_to_state( ) throws Exception {
        // Setup
        _data.registerBuild( newBuild( 12248, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12246, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12247, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12249, BuildState.running, BuildStatus.FAILURE ) );
        // Exercise
        final Optional<BuildData> oldestBuild = _data.getOldestBuild( BuildState.finished );
        // Verify
        assertThat( oldestBuild.isPresent( ), is( true ) );
        assertThat( oldestBuild.get( ).getId( ), is( 12246 ) );
    }

    @Test
    public void getOldestBuild_returns_empty_if_no_build_for_state( ) throws Exception {
        // Setup
        _data.registerBuild( newBuild( 12246, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12248, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12247, BuildState.finished, BuildStatus.FAILURE ) );
        // Exercise
        final Optional<BuildData> oldestBuild = _data.getOldestBuild( BuildState.running );
        // Verify
        assertThat( oldestBuild.isPresent( ), is( false ) );
    }

    @Test
    public void getOldestBuild_ignores_build_with_status_UNKNOWN( ) throws Exception {
        // Setup
        _data.registerBuild( newBuild( 12248, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12246, BuildState.finished, BuildStatus.UNKNOWN ) );
        _data.registerBuild( newBuild( 12247, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12249, BuildState.running, BuildStatus.FAILURE ) );
        // Exercise
        final Optional<BuildData> oldestBuild = _data.getOldestBuild( BuildState.finished );
        // Verify
        assertThat( oldestBuild.isPresent( ), is( true ) );
        assertThat( oldestBuild.get( ).getId( ), is( 12247 ) );
    }

    @Test
    public void getLastBuild_returns_the_correct_build_according_to_state( ) throws Exception {
        // Setup
        _data.registerBuild( newBuild( 12246, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12248, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12247, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12249, BuildState.running, BuildStatus.FAILURE ) );
        // Exercise
        final Optional<BuildData> lastBuild = _data.getLastBuild( BuildState.finished );
        // Verify
        assertThat( lastBuild.isPresent( ), is( true ) );
        assertThat( lastBuild.get( ).getId( ), is( 12248 ) );
    }

    @Test
    public void getLastBuild_ignores_build_with_status_UNKNOWN( ) throws Exception {
        // Setup
        _data.registerBuild( newBuild( 12246, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12248, BuildState.finished, BuildStatus.UNKNOWN ) );
        _data.registerBuild( newBuild( 12247, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12249, BuildState.running, BuildStatus.FAILURE ) );
        // Exercise
        final Optional<BuildData> lastBuild = _data.getLastBuild( BuildState.finished );
        // Verify
        assertThat( lastBuild.isPresent( ), is( true ) );
        assertThat( lastBuild.get( ).getId( ), is( 12247 ) );
    }

    @Test
    public void getLastBuild_returns_empty_if_no_build_for_state( ) throws Exception {
        // Setup
        _data.registerBuild( newBuild( 12246, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12248, BuildState.finished, BuildStatus.FAILURE ) );
        _data.registerBuild( newBuild( 12247, BuildState.finished, BuildStatus.FAILURE ) );
        // Exercise
        final Optional<BuildData> lastBuild = _data.getLastBuild( BuildState.running );
        // Verify
        assertThat( lastBuild.isPresent( ), is( false ) );
    }

    private BuildData newBuild( final int id, final BuildState state, final BuildStatus status ) {
        return new BuildData( id, status, state, 58, Optional.empty( ), Duration.ofSeconds( 98 ) );
    }
}
