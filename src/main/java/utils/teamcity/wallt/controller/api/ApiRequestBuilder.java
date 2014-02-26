package utils.teamcity.wallt.controller.api;

import com.google.common.base.Strings;

import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class ApiRequestBuilder {


    private static final String API_URL_FORMAT = "%s/%s/app/rest/%s/%s";

    private String _serverUrl = "";
    private String _path = "";
    private String _username = ApiRequest.GUEST_USER;
    private String _password = "";
    private ApiVersion _version;

    private ApiRequestBuilder( ) {
    }

    public static ApiRequestBuilder newRequest( ) {
        return new ApiRequestBuilder( );
    }

    public ApiRequestBuilder to( final String serverUrl ) {
        _serverUrl = checkNotNull( serverUrl, "Server URL is not specified" );
        if ( _serverUrl.endsWith( "/" ) )
            _serverUrl = _serverUrl.substring( 0, _serverUrl.length( ) - 1 );
        return this;
    }

    public ApiRequestBuilder request( final String path ) {
        _path = nullToEmpty( path );
        if ( _path.endsWith( "/" ) )
            _path = _path.substring( 0, _path.length( ) - 1 );
        return this;
    }

    public ApiRequestBuilder forUser( final String username ) {
        if ( !Strings.isNullOrEmpty( username ) )
            _username = username;
        else _username = ApiRequest.GUEST_USER;
        return this;
    }

    public ApiRequestBuilder withPassword( final String password ) {
        _password = nullToEmpty( password );
        return this;
    }

    public ApiRequestBuilder apiVersion( final ApiVersion version ) {
        _version = version;
        return this;
    }

    public ApiRequest build( ) {
        checkNotNull( _version, "Api version is not defined." );

        final String apiAuthMode = isGuestMode( ) ? "guestAuth" : "httpAuth";
        final String url = String.format( API_URL_FORMAT, _serverUrl, apiAuthMode, _version.getIdentifier( ), _path );

        try {
            final URI uri = new URI( url );
            final String s = uri.getScheme( );
            checkArgument( "http".equalsIgnoreCase( s ) || "https".equalsIgnoreCase( s ), "Only HTTP & HTTPS protocols are supported for API" );

            return new ApiRequest( uri, _username, _password );

        } catch ( URISyntaxException e ) {
            throw new IllegalArgumentException( "Unable to build api request: format is not uri-valid for '" + url + "'", e );
        }

    }

    private boolean isGuestMode( ) {
        return ApiRequest.GUEST_USER.equals( _username );
    }

}
