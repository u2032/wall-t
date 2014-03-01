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

package utils.teamcity.wallt;

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
import utils.teamcity.wallt.controller.api.ApiModule;
import utils.teamcity.wallt.controller.api.IApiMonitoringService;
import utils.teamcity.wallt.controller.configuration.ConfigurationModule;
import utils.teamcity.wallt.model.build.BuildDataModule;
import utils.teamcity.wallt.model.event.SceneEvent;
import utils.teamcity.wallt.model.logger.Loggers;
import utils.teamcity.wallt.view.UIUtils;
import utils.teamcity.wallt.view.configuration.ConfigurationScene;
import utils.teamcity.wallt.view.configuration.ConfigurationViewModule;
import utils.teamcity.wallt.view.wall.WallScene;
import utils.teamcity.wallt.view.wall.WallViewModule;

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
    private final IApiMonitoringService _apiMonitoringService;
    private final EventBus _eventBus;

    private Stage _primaryStage;

    public WallApplication( ) {
        LOGGER.info( "Starting ..." );
        _injector = Guice.createInjector( modules( ) );
        _executorService = _injector.getInstance( ExecutorService.class );
        _scheduledExecutorService = _injector.getInstance( ScheduledExecutorService.class );
        _eventBus = _injector.getInstance( EventBus.class );
        _apiMonitoringService = _injector.getInstance( IApiMonitoringService.class );
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

        primaryStage.setTitle( "Wall-T - Teamcity Radiator" );
        primaryStage.getIcons( ).addAll( UIUtils.createImage( "icons/icon.png" ) );

        primaryStage.setMinWidth( MIN_WIDTH );
        primaryStage.setMinHeight( MIN_HEIGHT );
        primaryStage.setWidth( MIN_WIDTH );
        primaryStage.setHeight( MIN_HEIGHT );

        _apiMonitoringService.start( );
        _eventBus.post( new SceneEvent( ConfigurationScene.class ) );

        primaryStage.show( );
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

        if ( scene instanceof WallScene )
            _apiMonitoringService.activate( );
        else
            _apiMonitoringService.pause( );
    }
}
