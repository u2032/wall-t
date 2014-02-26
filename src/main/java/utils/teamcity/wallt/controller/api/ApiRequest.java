package utils.teamcity.wallt.controller.api;

import java.net.URI;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class ApiRequest {

    public static final String GUEST_USER = "guest";

    private final URI _serverUrl;
    private final String _username;
    private final String _password;

    ApiRequest( final URI serverUrl, final String username, final String password ) {
        _serverUrl = serverUrl;
        _username = username;
        _password = password;
    }

    public String getHost( ) {
        return _serverUrl.getHost( );
    }

    public int getPort( ) {
        final int port = _serverUrl.getPort( );
        if ( port == -1 )
            return useSSL( ) ? 443 : 80;
        return port;
    }

    public boolean useSSL( ) {
        return "https".equalsIgnoreCase( _serverUrl.getScheme( ) );
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
