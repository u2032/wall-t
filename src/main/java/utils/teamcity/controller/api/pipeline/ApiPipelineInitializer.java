package utils.teamcity.controller.api.pipeline;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import utils.teamcity.controller.api.ApiResponse;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public class ApiPipelineInitializer extends ChannelInitializer<SocketChannel> {

    private final ApiResponseHandler<? extends ApiResponse> _responseHandler;

    public ApiPipelineInitializer( final ApiResponseHandler<? extends ApiResponse> responseHandler ) {
        _responseHandler = responseHandler;
    }

    @Override
    protected void initChannel( final SocketChannel socketChannel ) throws Exception {
        final ChannelPipeline pipeline = socketChannel.pipeline( );
//        pipeline.addLast( "loggingHandler", new LoggingHandler( LogLevel.INFO ) );
        // FIXME SSL Problem
//                        if ( ssl ) {
//                            final SSLEngine engine =
//                                    SecureChatSslContextFactory.getClientContext().createSSLEngine();
//                            engine.setUseClientMode( true );
//                            pipeline.addLast( "ssl", new SslHandler( engine ) );
//                        }

        pipeline.addLast( "httpCodecHandler", new HttpClientCodec( ) );
        pipeline.addLast( "inflaterHandler", new HttpContentDecompressor( ) );
        pipeline.addLast( "aggregatorHandler", new HttpObjectAggregator( 1048576 ) );
        // TODO Handler to handle redirect 301
        pipeline.addLast( "apiResponseHandler", _responseHandler );
    }
}
