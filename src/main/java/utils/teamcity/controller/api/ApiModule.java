package utils.teamcity.controller.api;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import utils.teamcity.controller.api.json.ApiVersion;
import utils.teamcity.controller.api.json.v0800.ApiController;

import java.util.Map;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
public final class ApiModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( IApiRequestController.class ).to( ApiRequestController.class ).in( Scopes.SINGLETON );
        bind( ApiControllerProvider.class ).in( Scopes.SINGLETON );
        bind( IApiController.class ).toProvider( ApiControllerProvider.class );
        bind( IApiMonitoringService.class ).to( ApiMonitoringService.class ).asEagerSingleton( );
    }

    @Provides
    @Singleton
    public EventLoopGroup eventLoopGroup( ) {
        return new NioEventLoopGroup( Runtime.getRuntime( ).availableProcessors( ) * 4 );
    }

    @Provides
    @Singleton
    public Map<ApiVersion, IApiController> controllerByApiVersion( final ApiController apiController0800 ) {
        return ImmutableMap.<ApiVersion, IApiController>of(
                ApiVersion.API_8_0, apiController0800 );
    }
}
