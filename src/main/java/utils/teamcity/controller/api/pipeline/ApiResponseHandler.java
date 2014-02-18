package utils.teamcity.controller.api.pipeline;

import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.controller.api.ApiException;
import utils.teamcity.controller.api.ApiResponse;
import utils.teamcity.model.logger.Loggers;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public class ApiResponseHandler<T extends ApiResponse> extends SimpleChannelInboundHandler<HttpObject> {

    public static final Logger LOGGER = LoggerFactory.getLogger( Loggers.MAIN );
    private final Class<T> _class;
    private final SettableFuture<T> _responseFuture;

    public ApiResponseHandler( final Class<T> expectedType, final SettableFuture<T> responseFuture ) {
        _class = expectedType;
        _responseFuture = responseFuture;
    }

    @Override
    protected void channelRead0( final ChannelHandlerContext channelHandlerContext, final HttpObject httpObject ) throws Exception {
        if ( httpObject instanceof HttpResponse ) {
            final HttpResponse response = (HttpResponse) httpObject;
            final HttpResponseStatus status = response.getStatus( );
            if ( !HttpResponseStatus.OK.equals( status ) ) {
                _responseFuture.setException( new ApiException( "Api response status code: " + status.code( ) + " (" + status.reasonPhrase( ) + ")" ) );
                return;
            }
        }

        if ( httpObject instanceof HttpContent ) {
            final HttpContent content = (HttpContent) httpObject;
            final String jsonContent = content.content( ).toString( CharsetUtil.UTF_8 );
            LOGGER.debug( "Response received: " + jsonContent );

            final Gson gson = new GsonBuilder( ).create( );
            final T jsonResponse = gson.fromJson( jsonContent, _class );
            _responseFuture.set( jsonResponse );
        }
    }

}
