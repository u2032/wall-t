package utils.teamcity.controller.api;

import com.google.common.base.Charsets;
import com.google.common.net.HttpHeaders;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ning.http.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.controller.api.json.ApiVersion;
import utils.teamcity.model.configuration.Configuration;
import utils.teamcity.model.logger.Loggers;

import javax.inject.Inject;

/**
 * Date: 17/02/14
 *
 * @author Cedric Longo
 */
final class ApiRequestController implements IApiRequestController {

    public static final Logger LOGGER = LoggerFactory.getLogger( Loggers.MAIN );
    private final Configuration _configuration;
    private final AsyncHttpClient _httpClient;

    @Inject
    ApiRequestController( final Configuration configuration, final AsyncHttpClient httpClient ) {
        _configuration = configuration;
        _httpClient = httpClient;
    }

    @Override
    public <T extends ApiResponse> ListenableFuture<T> sendRequest( final ApiVersion version, final String path, final Class<T> expectedType ) {
        final SettableFuture<T> apiResponseFuture = SettableFuture.create( );
        try {
            final ApiRequest request = ApiRequestBuilder.newRequest( )
                    .to( _configuration.getServerUrl( ) )
                    .forUser( _configuration.getCredentialsUser( ) )
                    .withPassword( _configuration.getCredentialsPassword( ) )
                    .request( path )
                    .apiVersion( version )
                    .build( );

            LOGGER.info( "Requesting: {}", request );

            final AsyncHttpClient.BoundRequestBuilder httpRequest = _httpClient
                    .prepareGet( request.getURI( ) )
                    .addHeader( HttpHeaders.ACCEPT, "application/json" );

            if ( !request.isGuestMode( ) ) {
                final Realm realm = new Realm.RealmBuilder( )
                        .setPrincipal( request.getUsername( ) )
                        .setPassword( request.getPassword( ) )
                        .setUsePreemptiveAuth( true )
                        .setScheme( Realm.AuthScheme.DIGEST )
                        .build( );
                httpRequest.setRealm( realm );
            }

            if ( _configuration.isUseProxy( ) ) {
                // CODEREVIEW Let the user choose the protocol ?
                final ProxyServer proxyServer = new ProxyServer( ProxyServer.Protocol.HTTP, _configuration.getProxyHost( ), _configuration.getProxyPort( ), _configuration.getProxyCredentialsUser( ), _configuration.getProxyCredentialsPassword( ) );
                httpRequest.setProxyServer( proxyServer );
            }

            httpRequest.execute( new AsyncCompletionHandler<Void>( ) {
                @Override
                public void onThrowable( final Throwable t ) {
                    super.onThrowable( t );
                    apiResponseFuture.setException( t );
                }

                @Override
                public Void onCompleted( final Response response ) throws Exception {

                    if ( response.getStatusCode( ) != 200 ) {
                        apiResponseFuture.setException( new ApiException( "Http status code is " + response.getStatusCode( ) + " when requesting uri: " + response.getUri( ) ) );
                        return null;
                    }

                    final Gson gson = new GsonBuilder( ).create( );
                    final T jsonResponse = gson.fromJson( response.getResponseBody( Charsets.UTF_8.name( ) ), expectedType );
                    apiResponseFuture.set( jsonResponse );

                    return null;
                }
            } );
        } catch ( Exception e ) {
            apiResponseFuture.setException( e );
        }

        return apiResponseFuture;
    }


}
