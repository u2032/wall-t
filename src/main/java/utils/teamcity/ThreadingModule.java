package utils.teamcity;

import com.google.inject.AbstractModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
final class ThreadingModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( ExecutorService.class ).toInstance( newCachedThreadPool( ) );
        bind( ScheduledExecutorService.class ).toInstance( newScheduledThreadPool( Runtime.getRuntime( ).availableProcessors( ) ) );
    }

}
