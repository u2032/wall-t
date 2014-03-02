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

/**
 * Date: 02/03/14
 *
 * @author Cedric Longo
 */
public class ApiRequestBuilderTest {

    @Test( expected = NullPointerException.class )
    public void build_throws_exception_if_api_is_not_defined( ) throws Exception {
        // Setup
        // Exercise
        final ApiRequest request = ApiRequestBuilder.newRequest( ).build( );
        // Verify
    }

    @Test( expected = IllegalArgumentException.class )
    public void build_throws_exception_if_api_url_is_not_defined( ) throws Exception {
        // Setup
        // Exercise
        final ApiRequest request = ApiRequestBuilder.newRequest( )
                .apiVersion( ApiVersion.API_6_0 )
                .build( );
        // Verify
    }

    @Test( expected = IllegalArgumentException.class )
    public void build_throws_exception_if_api_url_is_not_uri_valid( ) throws Exception {
        // Setup
        // Exercise
        final ApiRequest request = ApiRequestBuilder.newRequest( )
                .apiVersion( ApiVersion.API_6_0 )
                .to( "http\\\\\\::::/::/dsqd:/dsq/" )
                .build( );
        // Verify
    }


    @Test( expected = IllegalArgumentException.class )
    public void build_throws_exception_if_api_url_is_not_http_or_https_protocol( ) throws Exception {
        // Setup
        // Exercise
        final ApiRequest request = ApiRequestBuilder.newRequest( )
                .apiVersion( ApiVersion.API_6_0 )
                .to( "ftp://localhost:99" )
                .build( );
        // Verify
    }

    @Test
    public void build_return_correct_request_with_expected_parameters( ) throws Exception {
        // Setup
        // Exercise
        final ApiRequest request = ApiRequestBuilder.newRequest( )
                .apiVersion( ApiVersion.API_6_0 )
                .to( "http://localhost:80" )
                .forUser( "cedric" )
                .withPassword( "c3dr1c" )
                .request( "builds?myQuery=myValue&byPass=1" )
                .build( );
        // Verify
        assertThat( request.getUsername( ), is( "cedric" ) );
        assertThat( request.getPassword( ), is( "c3dr1c" ) );
        assertThat( request.getURI( ), is( "http://localhost:80/httpAuth/app/rest/6.0/builds?myQuery=myValue&byPass=1" ) );
    }

    @Test
    public void build_return_correct_request_with_expected_parameters_when_guest_mode( ) throws Exception {
        // Setup
        // Exercise
        final ApiRequest request = ApiRequestBuilder.newRequest( )
                .apiVersion( ApiVersion.API_6_0 )
                .to( "http://localhost:80" )
                .forUser( "" )
                .withPassword( "c3dr1c" )
                .request( "builds?myQuery=myValue&byPass=1" )
                .build( );
        // Verify
        assertThat( request.getUsername( ), is( ApiRequest.GUEST_USER ) );
        assertThat( request.getPassword( ), is( "c3dr1c" ) );
        assertThat( request.getURI( ), is( "http://localhost:80/guestAuth/app/rest/6.0/builds?myQuery=myValue&byPass=1" ) );
    }
}
