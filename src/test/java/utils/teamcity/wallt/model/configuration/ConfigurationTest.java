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

package utils.teamcity.wallt.model.configuration;

import com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Test;
import utils.teamcity.wallt.controller.api.ApiVersion;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Date: 01/03/14
 *
 * @author Cedric Longo
 */
public class ConfigurationTest {

    @Test
    public void configuration_records_correctly_data( ) {
        // Setup
        final Configuration configuration = new Configuration( );
        final SavedBuildTypeData buildType1 = new SavedBuildTypeData( "id1", "name1", "projectId1", "projectName1", "alias1" );
        final SavedBuildTypeData buildType2 = new SavedBuildTypeData( "id2", "name2", "projectId2", "projectName2", "alias2" );
        final SavedProjectData project1 = new SavedProjectData( "id1", "name1", "parentId1", "alias1" );
        final SavedProjectData project2 = new SavedProjectData( "id2", "name2", "parentId2", "alias2" );
        // Exercise
        configuration.setApiVersion( ApiVersion.API_7_0 );
        configuration.setLightMode( true );
        configuration.setCredentialsUser( "Toto" );
        configuration.setCredentialsPassword( "t0t0" );
        configuration.setServerUrl( "http://mylocalhost" );
        configuration.setMaxTilesByColumn( 44 );
        configuration.setMaxTilesByRow( 45 );
        configuration.setProxyHost( "http://myproxy" );
        configuration.setProxyPort( 88 );
        configuration.setProxyCredentialsUser( "Titi" );
        configuration.setProxyCredentialsPassword( "t1t1" );
        configuration.setUseProxy( true );
        configuration.setSavedBuilds( ImmutableList.of( buildType1, buildType2 ) );
        configuration.setSavedProjects( ImmutableList.of( project1, project2 ) );
        // Verify
        assertThat( configuration.getApiVersion( ), is( ApiVersion.API_7_0 ) );
        assertThat( configuration.isLightMode( ), is( true ) );
        assertThat( configuration.getCredentialsUser( ), is( "Toto" ) );
        assertThat( configuration.getCredentialsPassword( ), is( "t0t0" ) );
        assertThat( configuration.getServerUrl( ), is( "http://mylocalhost" ) );
        assertThat( configuration.getMaxTilesByColumn( ), is( 44 ) );
        assertThat( configuration.getMaxTilesByRow( ), is( 45 ) );
        assertThat( configuration.getProxyHost( ), is( "http://myproxy" ) );
        assertThat( configuration.getProxyPort( ), is( 88 ) );
        assertThat( configuration.getProxyCredentialsUser( ), is( "Titi" ) );
        assertThat( configuration.getProxyCredentialsPassword( ), is( "t1t1" ) );
        assertThat( configuration.isUseProxy( ), is( true ) );
        assertThat( configuration.getSavedBuildTypes( ), Matchers.contains( buildType1, buildType2 ) );
        assertThat( configuration.getSavedProjects( ), Matchers.contains( project1, project2 ) );
    }
}
