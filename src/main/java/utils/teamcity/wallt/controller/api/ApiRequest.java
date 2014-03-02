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

import java.net.URI;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class ApiRequest {

    static final String GUEST_USER = "guest";

    private final URI _serverUrl;
    private final String _username;
    private final String _password;

    ApiRequest( final URI serverUrl, final String username, final String password ) {
        _serverUrl = serverUrl;
        _username = username;
        _password = password;
    }

    public String getURI( ) {
        return _serverUrl.toString( );
    }

    public String getUsername( ) {
        return _username;
    }

    public String getPassword( ) {
        return _password;
    }

    public boolean isGuestMode( ) {
        return GUEST_USER.equals( _username );
    }

    @Override
    public String toString( ) {
        return "ApiRequest{" + " '" + _serverUrl + "', user:" + _username + ( isNullOrEmpty( getPassword( ) ) || isGuestMode( ) ? " with no password" : " with password" ) + " }";
    }
}
