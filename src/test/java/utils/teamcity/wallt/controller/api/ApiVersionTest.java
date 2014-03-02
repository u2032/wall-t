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

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Date: 02/03/14
 *
 * @author Cedric Longo
 */
public class ApiVersionTest {

    @Test
    public void getIdentifier_returns_correct_value( ) throws Exception {
        // Setup
        // Exercise
        // Verify
        assertThat( ApiVersion.API_6_0.getIdentifier( ), is( "6.0" ) );
        assertThat( ApiVersion.API_8_0.getIdentifier( ), is( "8.0" ) );
        assertThat( ApiVersion.API_8_1.getIdentifier( ), is( "8.0" ) );
    }

    @Test
    public void getName_returns_correct_value( ) throws Exception {
        // Setup
        // Exercise
        // Verify
        assertThat( ApiVersion.API_6_0.getName( ), is( "6.0" ) );
        assertThat( ApiVersion.API_8_0.getName( ), is( "8.0" ) );
        assertThat( ApiVersion.API_8_1.getName( ), is( "8.1" ) );
    }

    @Test
    public void isSupported_returns_correct_value( ) throws Exception {
        // Setup
        // Exercise
        // Verify
        assertThat( ApiVersion.API_6_0.isSupported( ApiFeature.QUEUE_STATUS ), is( false ) );
        assertThat( ApiVersion.API_8_0.isSupported( ApiFeature.QUEUE_STATUS ), is( false ) );
        assertThat( ApiVersion.API_8_1.isSupported( ApiFeature.QUEUE_STATUS ), is( true ) );
        assertThat( ApiVersion.API_8_0.isSupported( ApiFeature.QUEUE_STATUS, ApiFeature.BUILD_TYPE_STATUS ), is( false ) );
    }

    @Test
    public void fromName_returns_correct_value( ) throws Exception {
        // Setup
        // Exercise
        // Verify
        assertThat( ApiVersion.fromName( "6.0" ), is( ApiVersion.API_6_0 ) );
        assertThat( ApiVersion.fromName( "8.1" ), is( ApiVersion.API_8_1 ) );
        assertThat( ApiVersion.fromName( "x.y" ), is( nullValue( ) ) );
    }

}
