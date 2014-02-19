package utils.teamcity.controller.api;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.controller.api.json.ApiVersion;
import utils.teamcity.controller.api.pipeline.ApiPipelineInitializer;
import utils.teamcity.controller.api.pipeline.ApiResponseHandler;
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
    private final EventLoopGroup _eventLoopGroup;

    @Inject
    ApiRequestController( final Configuration configuration, final EventLoopGroup eventLoopGroup ) {
        _configuration = configuration;
        _eventLoopGroup = eventLoopGroup;
    }

    @Override
    public <T extends ApiResponse> ListenableFuture<T> sendRequest( final ApiVersion version, final String path, final Class<T> expectedType ) {

        final ApiRequest request = ApiRequestBuilder.newRequest( )
                .to( _configuration.getServerUrl( ) )
                .forUser( _configuration.getCredentialsUser( ) )
                .withPassword( _configuration.getCredentialsPassword( ) )
                .request( path )
                .apiVersion( version )
                .build( );

        LOGGER.info( "Requesting: {}", request );

        final SettableFuture<T> futureResponse = SettableFuture.create( );

        // Configure the client.
        final Bootstrap b = new Bootstrap( );
        b.group( _eventLoopGroup )
                .channel( NioSocketChannel.class )
                .handler( new ApiPipelineInitializer( new ApiResponseHandler<T>( expectedType, futureResponse ) ) );

        // Prepare the HTTP request.
        final HttpRequest httpRequest = new DefaultFullHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, request.getURI( ) );
        httpRequest.headers( ).set( HttpHeaders.Names.HOST, request.getHost( ) );
        httpRequest.headers( ).set( HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE );
        httpRequest.headers( ).set( HttpHeaders.Names.ACCEPT, "application/json" );

        if ( !request.isGuestMode( ) ) {
            final String authString = request.getUsername( ) + ":" + request.getPassword( );
            final ByteBuf authChannelBuffer = Unpooled.wrappedBuffer( CharsetUtil.UTF_8.encode( authString ) );
            final ByteBuf encodedAuthChannelBuffer = Base64.encode( authChannelBuffer );
            httpRequest.headers( ).set( HttpHeaders.Names.AUTHORIZATION, "Basic " + encodedAuthChannelBuffer.toString( CharsetUtil.UTF_8 ) );
        }

        // Send the HTTP request.
        final ChannelFuture ch = b.connect( request.getHost( ), request.getPort( ) );
        //noinspection Convert2Lambda
        ch.addListener( new ChannelFutureListener( ) {
            @Override
            public void operationComplete( final ChannelFuture future ) throws Exception {
                future.channel( ).writeAndFlush( httpRequest );
            }
        } );

        return futureResponse;
    }


}
