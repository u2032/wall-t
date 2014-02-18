package utils.teamcity;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.inject.AbstractModule;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
final class ThreadingModule extends AbstractModule {

    @Override
    protected void configure() {
        bind( ListeningExecutorService.class ).toInstance( listeningDecorator( newCachedThreadPool() ) );
        bind( ListeningScheduledExecutorService.class ).toInstance( listeningDecorator( newScheduledThreadPool( Runtime.getRuntime().availableProcessors() ) ) );
    }

}
