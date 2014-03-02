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

package utils.teamcity.wallt.controller.configuration;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import org.junit.Before;
import org.junit.Test;
import utils.teamcity.wallt.TestModules;
import utils.teamcity.wallt.model.build.BuildTypeData;
import utils.teamcity.wallt.model.build.IBuildTypeManager;
import utils.teamcity.wallt.model.build.IProjectManager;
import utils.teamcity.wallt.model.build.ProjectData;
import utils.teamcity.wallt.model.configuration.Configuration;
import utils.teamcity.wallt.model.configuration.SavedBuildTypeData;
import utils.teamcity.wallt.model.configuration.SavedProjectData;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Date: 02/03/14
 *
 * @author Cedric Longo
 */
public class ConfigurationControllerTest {

    @Inject
    private IConfigurationController _configurationController;

    @Inject
    private IProjectManager _projectManager;

    @Inject
    private IBuildTypeManager _buildTypeManager;

    @Inject
    private Configuration _configuration;

    @Before
    public void setUp( ) throws Exception {
        Guice.createInjector( TestModules.defaultModules( ) )
                .injectMembers( this );
    }

    @Test
    public void saveConfiguration_sets_into_configuration_monitored_projects_in_order( ) throws Exception {
        // Setup
        final ProjectData p1 = newProject( "p1" );
        final ProjectData p2 = newProject( "p2" );
        final ProjectData p3 = newProject( "p3" );
        final ProjectData p4 = newProject( "p4" );
        _projectManager.registerProjects( ImmutableList.of( p1, p2, p3, p4 ) );
        _projectManager.activateMonitoring( p1 );
        _projectManager.activateMonitoring( p4 );
        _projectManager.activateMonitoring( p2 );
        // Exercise
        _configurationController.saveConfiguration( );
        // Verify
        final List<SavedProjectData> savedProjects = _configuration.getSavedProjects( );
        assertThat( savedProjects.size( ), is( 3 ) );
        assertThat( savedProjects.get( 0 ).getId( ), is( "p1" ) );
        assertThat( savedProjects.get( 1 ).getId( ), is( "p4" ) );
        assertThat( savedProjects.get( 2 ).getId( ), is( "p2" ) );
    }

    @Test
    public void saveConfiguration_sets_into_configuration_monitored_build_types_in_order( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = newBuildType( "bt1" );
        final BuildTypeData bt2 = newBuildType( "bt2" );
        final BuildTypeData bt3 = newBuildType( "bt3" );
        final BuildTypeData bt4 = newBuildType( "bt4" );
        _buildTypeManager.registerBuildTypes( ImmutableList.of( bt1, bt2, bt3, bt4 ) );
        _buildTypeManager.activateMonitoring( bt1 );
        _buildTypeManager.activateMonitoring( bt4 );
        _buildTypeManager.activateMonitoring( bt2 );
        // Exercise
        _configurationController.saveConfiguration( );
        // Verify
        final List<SavedBuildTypeData> savedProjects = _configuration.getSavedBuildTypes( );
        assertThat( savedProjects.size( ), is( 3 ) );
        assertThat( savedProjects.get( 0 ).getId( ), is( "bt1" ) );
        assertThat( savedProjects.get( 1 ).getId( ), is( "bt4" ) );
        assertThat( savedProjects.get( 2 ).getId( ), is( "bt2" ) );
    }

    private ProjectData newProject( final String id ) {
        return new ProjectData( id, "pName", Optional.<String>empty( ) );
    }

    private BuildTypeData newBuildType( final String id ) {
        return new BuildTypeData( id, "btName", "btProjectId", "btProjectName" );
    }
}
