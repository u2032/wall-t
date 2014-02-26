package utils.teamcity.wallt;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
final class WallApplicationModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( EventBus.class ).in( Scopes.SINGLETON );
    }
}
