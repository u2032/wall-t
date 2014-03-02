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
import com.google.inject.Guice;
import org.junit.Before;
import org.junit.Test;
import utils.teamcity.wallt.TestModules;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

/**
 * Date: 02/03/14
 *
 * @author Cedric Longo
 */
public class ProjectManagerTest {

    @Inject
    private IProjectManager _projectManager;

    @Before
    public void setUp( ) throws Exception {
        Guice.createInjector( TestModules.defaultModules( ) )
                .injectMembers( this );
    }

    @Test
    public void registerProjects_records_correctly_projects( ) throws Exception {
        // Setup
        final List<ProjectData> projects = ImmutableList.of( newProject( "p1" ), newProject( "p2" ) );
        // Exercise
        _projectManager.registerProjects( projects );
        // Verify
        final List<ProjectData> projectsInManager = _projectManager.getProjects( );
        assertThat( projectsInManager.size( ), is( 2 ) );
        assertThat( projectsInManager.get( 0 ).getId( ), is( "p1" ) );
        assertThat( projectsInManager.get( 1 ).getId( ), is( "p2" ) );
    }

    @Test
    public void registerProjects_records_correctly_projects_by_overwritting_previous_data( ) throws Exception {
        // Setup
        final List<ProjectData> projects = ImmutableList.of( newProject( "p1" ), newProject( "p2" ) );
        final List<ProjectData> newProjects = ImmutableList.of( newProject( "p2" ), newProject( "p3" ) );
        _projectManager.registerProjects( projects );
        // Exercise
        _projectManager.registerProjects( newProjects );
        // Verify
        final List<ProjectData> projectsInManager = _projectManager.getProjects( );
        assertThat( projectsInManager.size( ), is( 2 ) );
        assertThat( projectsInManager.get( 0 ).getId( ), is( "p2" ) );
        assertThat( projectsInManager.get( 1 ).getId( ), is( "p3" ) );
    }

    @Test
    public void registerProjects_keep_monitored_projects_as_monitored( ) throws Exception {
        // Setup
        final ProjectData p1 = newProject( "p1" );
        final ProjectData p2 = newProject( "p2" );
        final List<ProjectData> projects = ImmutableList.of( p1, p2 );
        _projectManager.registerProjects( projects );
        _projectManager.activateMonitoring( p1 );
        _projectManager.activateMonitoring( p2 );
        final List<ProjectData> newProjects = ImmutableList.of( newProject( "p2" ), newProject( "p3" ) );
        // Exercise
        _projectManager.registerProjects( newProjects );
        // Verify
        final List<ProjectData> projectsInManager = _projectManager.getMonitoredProjects( );
        assertThat( projectsInManager.size( ), is( 1 ) );
        assertThat( projectsInManager.get( 0 ).getId( ), is( "p2" ) );
    }

    @Test
    public void registerProjects_keep_monitored_projects_order( ) throws Exception {
        // Setup
        final ProjectData p1 = newProject( "p1" );
        final ProjectData p2 = newProject( "p2" );
        final List<ProjectData> projects = ImmutableList.of( p2, p1 );
        _projectManager.registerProjects( projects );
        _projectManager.activateMonitoring( p2 );
        _projectManager.activateMonitoring( p1 );
        // Exercise
        _projectManager.registerProjects( ImmutableList.of( p1, p2 ) );
        // Verify
        final List<ProjectData> projectsInManager = _projectManager.getMonitoredProjects( );
        assertThat( projectsInManager.size( ), is( 2 ) );
        assertThat( projectsInManager.get( 0 ).getId( ), is( "p2" ) );
        assertThat( projectsInManager.get( 1 ).getId( ), is( "p1" ) );
    }

    @Test
    public void registerProjects_keep_monitored_aliases( ) throws Exception {
        // Setup
        final ProjectData p1 = newProject( "p1" );
        final ProjectData p2 = newProject( "p2" );
        p2.setAliasName( "myAlias" );
        _projectManager.registerProjects( ImmutableList.of( p1, p2 ) );
        _projectManager.activateMonitoring( p2 );
        // Exercise
        _projectManager.registerProjects( ImmutableList.of( newProject( "p1" ), newProject( "p2" ) ) );
        // Verify
        final List<ProjectData> projectsInManager = _projectManager.getMonitoredProjects( );
        assertThat( projectsInManager.size( ), is( 1 ) );
        assertThat( projectsInManager.get( 0 ).getId( ), is( "p2" ) );
        assertThat( projectsInManager.get( 0 ).getAliasName( ), is( "myAlias" ) );
    }

    @Test
    public void when_activating_monitoring_build_is_pushed_into_monitored_project_list( ) throws Exception {
        // Setup
        final ProjectData p1 = newProject( "p1" );
        final List<ProjectData> projects = ImmutableList.of( p1, newProject( "p2" ) );
        _projectManager.registerProjects( projects );
        // Exercise
        _projectManager.activateMonitoring( p1 );
        // Verify
        final List<ProjectData> projectsInManager = _projectManager.getMonitoredProjects( );
        assertThat( projectsInManager.size( ), is( 1 ) );
        assertThat( projectsInManager.get( 0 ).getId( ), is( "p1" ) );
    }

    @Test
    public void when_unactivating_monitoring_build_is_removed_from_monitored_project_list( ) throws Exception {
        // Setup
        final ProjectData p1 = newProject( "p1" );
        final ProjectData p2 = newProject( "p2" );
        final List<ProjectData> projects = ImmutableList.of( p1, p2 );
        _projectManager.registerProjects( projects );
        _projectManager.activateMonitoring( p1 );
        _projectManager.activateMonitoring( p2 );
        // Exercise
        _projectManager.unactivateMonitoring( p1 );
        // Verify
        final List<ProjectData> projectsInManager = _projectManager.getMonitoredProjects( );
        assertThat( projectsInManager.size( ), is( 1 ) );
        assertThat( projectsInManager.get( 0 ).getId( ), is( "p2" ) );
    }

    @Test
    public void requestPosition_changes_monitored_project_ordering( ) throws Exception {
        // Setup
        final ProjectData p1 = newProject( "p1" );
        final ProjectData p2 = newProject( "p2" );
        _projectManager.registerProjects( ImmutableList.of( p1, p2 ) );
        _projectManager.activateMonitoring( p1 );
        _projectManager.activateMonitoring( p2 );
        // Exercise
        _projectManager.requestPosition( p1, 2 );
        // Verify
        final List<ProjectData> projectsInManager = _projectManager.getMonitoredProjects( );
        assertThat( projectsInManager.size( ), is( 2 ) );
        assertThat( projectsInManager.get( 0 ).getId( ), is( "p2" ) );
        assertThat( projectsInManager.get( 1 ).getId( ), is( "p1" ) );
        assertThat( _projectManager.getPosition( p1 ), is( 2 ) );
        assertThat( _projectManager.getPosition( p2 ), is( 1 ) );
    }

    @Test
    public void getProject_returns_the_requested_project_or_empty( ) throws Exception {
        // Setup
        _projectManager.registerProjects( ImmutableList.of( newProject( "p1" ) ) );
        // Exercise
        final Optional<ProjectData> p1 = _projectManager.getProject( "p1" );
        final Optional<ProjectData> p3 = _projectManager.getProject( "p3" );
        // Verify
        assertThat( p1.isPresent( ), is( true ) );
        assertThat( p1.get( ).getId( ), is( "p1" ) );
        assertThat( p3.isPresent( ), is( false ) );
    }

    @Test
    public void getAllChildrenOf_returns_all_children_of_project_recursively( ) throws Exception {
        // Setup
        final ProjectData p00 = newProject( "p00" );
        final ProjectData p02 = newProject( "p02", "p00" );
        final ProjectData p21 = newProject( "p21", "p02" );
        final ProjectData p01 = newProject( "p01", "p00" );
        final ProjectData p11 = newProject( "p11", "p01" );
        final ProjectData p12 = newProject( "p12", "p01" );
        final ProjectData p111 = newProject( "p111", "p11" );
        _projectManager.registerProjects( ImmutableList.of( p00, p01, p02, p21, p11, p12, p111 ) );
        // Exercise
        final List<ProjectData> allChildren = _projectManager.getAllChildrenOf( p01 );
        // Verify
        assertThat( allChildren.size( ), is( 3 ) );
        assertThat( allChildren, containsInAnyOrder( p11, p12, p111 ) );
    }

    private ProjectData newProject( final String id ) {
        return new ProjectData( id, "pName", Optional.<String>empty( ) );
    }

    private ProjectData newProject( final String id, final String parentId ) {
        return new ProjectData( id, "pName", Optional.of( parentId ) );
    }

}
