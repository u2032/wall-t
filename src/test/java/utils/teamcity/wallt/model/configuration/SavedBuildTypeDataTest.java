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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Date: 01/03/14
 *
 * @author Cedric Longo
 */
public class SavedBuildTypeDataTest {

    @Test
    public void data_is_correctly_set_into_object( ) {
        // Setup
        final SavedBuildTypeData data = new SavedBuildTypeData( "btId", "btName", "btProjectId", "btProjectName", "btAlias" );
        // Exercise
        // Verify
        assertThat( data.getId( ), is( "btId" ) );
        assertThat( data.getName( ), is( "btName" ) );
        assertThat( data.getProjectId( ), is( "btProjectId" ) );
        assertThat( data.getProjectName( ), is( "btProjectName" ) );
        assertThat( data.getAliasName( ), is( "btAlias" ) );
    }

}
