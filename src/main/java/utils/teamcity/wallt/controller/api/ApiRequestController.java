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

import com.google.common.base.Charsets;
import com.google.common.net.HttpHeaders;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ning.http.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.wallt.model.configuration.Configuration;
import utils.teamcity.wallt.model.logger.Loggers;

import javax.inject.Inject;

/**
 * Date: 17/02/14
 *
 * @author Cedric Longo
 */
final class ApiRequestController implements IApiRequestController {

    public static final Logger LOGGER = LoggerFactory.getLogger( Loggers.NETWORK );
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

            LOGGER.info( "<< REQUEST: to {}", request );

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
                        LOGGER.error( ">> RESPONSE: for {} has status code {}", request, response.getStatusCode( ) );
                        apiResponseFuture.setException( new ApiException( "Http status code is " + response.getStatusCode( ) + " when requesting uri: " + response.getUri( ) ) );
                        return null;
                    }

                    final String content = response.getResponseBody( Charsets.UTF_8.name( ) );
                    LOGGER.debug( ">> RESPONSE: for {} has content: {}", request, content );

                    final Gson gson = new GsonBuilder( ).create( );
                    final T jsonResponse = gson.fromJson( content, expectedType );
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
