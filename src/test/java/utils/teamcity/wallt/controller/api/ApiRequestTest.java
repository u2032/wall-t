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

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Date: 02/03/14
 *
 * @author Cedric Longo
 */
public class ApiRequestTest {

    @Test
    public void data_is_correctly_returned( ) throws Exception {
        // Setup
        final ApiRequest request = new ApiRequest( new URI( "http://localhost:88?myquery=myValue&bypass=1" ), "cedric", "c3dr1c" );
        // Exercise
        // Verify
        assertThat( request.getUsername( ), is( "cedric" ) );
        assertThat( request.getPassword( ), is( "c3dr1c" ) );
        assertThat( request.getURI( ), is( "http://localhost:88?myquery=myValue&bypass=1" ) );
    }

    @Test
    public void isGuestMode_returns_true_if_username_is_guest_user( ) throws Exception {
        // Setup
        final ApiRequest request = new ApiRequest( new URI( "http://localhost:88?myquery=myValue&bypass=1" ), ApiRequest.GUEST_USER, "c3dr1c" );
        // Exercise
        // Verify
        assertThat( request.isGuestMode( ), is( true ) );
    }

    @Test
    public void isGuestMode_returns_false_if_username_is_defined_to_non_guest_user_value( ) throws Exception {
        // Setup
        final ApiRequest request = new ApiRequest( new URI( "http://localhost:88?myquery=myValue&bypass=1" ), "cedric", "c3dr1c" );
        // Exercise
        // Verify
        assertThat( request.isGuestMode( ), is( false ) );
    }
}
