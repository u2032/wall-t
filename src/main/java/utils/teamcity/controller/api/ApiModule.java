package utils.teamcity.controller.api;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import utils.teamcity.controller.api.json.ApiVersion;

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

    @SuppressWarnings( { "UnnecessaryFullyQualifiedName", "TypeMayBeWeakened" } )
    @Provides
    @Singleton
    public Map<ApiVersion, IApiController> controllerByApiVersion(
            final utils.teamcity.controller.api.json.v0800.ApiController apiController0800,
            final utils.teamcity.controller.api.json.v0801.ApiController apiController0801 ) {
        return ImmutableMap.of(
                ApiVersion.API_8_0, apiController0800,
                ApiVersion.API_8_1, apiController0801 );
    }
}
