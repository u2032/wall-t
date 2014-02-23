package utils.teamcity;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.controller.api.ApiModule;
import utils.teamcity.controller.api.IApiMonitoringService;
import utils.teamcity.controller.configuration.ConfigurationModule;
import utils.teamcity.model.build.BuildDataModule;
import utils.teamcity.model.event.SceneEvent;
import utils.teamcity.model.logger.Loggers;
import utils.teamcity.view.UIUtils;
import utils.teamcity.view.configuration.ConfigurationScene;
import utils.teamcity.view.configuration.ConfigurationViewModule;
import utils.teamcity.view.wall.WallViewModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Date: 09/02/14
 *
 * @author Cedric Longo
 */
public final class WallApplication extends Application {

    public static final int MIN_WIDTH = 1024;
    public static final int MIN_HEIGHT = 800;
    public static final Logger LOGGER = LoggerFactory.getLogger( Loggers.MAIN );
    private final Injector _injector;
    private final ExecutorService _executorService;
    private final ScheduledExecutorService _scheduledExecutorService;
    private final EventBus _eventBus;
    private Stage _primaryStage;

    public WallApplication( ) {
        LOGGER.info( "Starting ..." );
        _injector = Guice.createInjector( modules( ) );
        _executorService = _injector.getInstance( ExecutorService.class );
        _scheduledExecutorService = _injector.getInstance( ScheduledExecutorService.class );
        _eventBus = _injector.getInstance( EventBus.class );
    }

    public static void main( final String[] args ) {
        Application.launch( WallApplication.class, args );
    }

    private Iterable<Module> modules( ) {
        return ImmutableList.<Module>of(
                new WallApplicationModule( ),
                new ThreadingModule( ),
                new ApiModule( ),
                new BuildDataModule( ),
                new ConfigurationModule( ),
                new ConfigurationViewModule( ),
                new WallViewModule( )
        );
    }

    @Override
    public void init( ) throws Exception {
        _eventBus.register( this );
        super.init( );
    }

    @Override
    public void start( final Stage primaryStage ) throws Exception {
        _primaryStage = primaryStage;

        primaryStage.setTitle( "Teamcity Wall" );
        primaryStage.getIcons( ).addAll( UIUtils.createImage( "icon.png" ) );

        primaryStage.setMinWidth( MIN_WIDTH );
        primaryStage.setMinHeight( MIN_HEIGHT );
        primaryStage.setWidth( MIN_WIDTH );
        primaryStage.setHeight( MIN_HEIGHT );

        _eventBus.post( new SceneEvent( ConfigurationScene.class ) );

        primaryStage.show( );

        _injector.getInstance( IApiMonitoringService.class ).start( );
    }

    @Override
    public void stop( ) throws Exception {
        LOGGER.info( "Stopping ..." );
        _injector.getInstance( AsyncHttpClientConfig.class ).executorService( ).shutdownNow( );
        _injector.getInstance( AsyncHttpClient.class ).close( );

        _executorService.shutdownNow( );
        _scheduledExecutorService.shutdownNow( );
        super.stop( );
    }

    @Subscribe
    public void requestScene( final SceneEvent sceneType ) {
        final Scene scene = _injector.getInstance( sceneType.getType( ) );
        scene.getAccelerators( ).put( new KeyCodeCombination( KeyCode.F11 ),
                ( ) -> {
                    _primaryStage.setFullScreen( !_primaryStage.isFullScreen( ) );
                } );
        scene.getAccelerators( ).put( new KeyCodeCombination( KeyCode.ESCAPE ),
                ( ) -> {
                    _eventBus.post( new SceneEvent( ConfigurationScene.class ) );
                } );
        LOGGER.info( "Change scene to " + sceneType.getType( ).getSimpleName( ) );
        _primaryStage.setScene( scene );
    }
}
