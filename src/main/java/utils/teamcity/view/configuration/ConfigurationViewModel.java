package utils.teamcity.view.configuration;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.controller.api.IApiController;
import utils.teamcity.controller.api.json.ApiVersion;
import utils.teamcity.model.build.BuildTypeData;
import utils.teamcity.model.build.IBuildManager;
import utils.teamcity.model.configuration.Configuration;
import utils.teamcity.model.event.SceneEvent;
import utils.teamcity.model.logger.Loggers;
import utils.teamcity.view.wall.WallScene;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.concurrent.Callable;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
final class ConfigurationViewModel {

    public static final Logger LOGGER = LoggerFactory.getLogger( Loggers.MAIN );
    private final StringProperty _serverUrl = new SimpleStringProperty( );
    private final StringProperty _credentialsUser = new SimpleStringProperty( );
    private final StringProperty _credentialsPassword = new SimpleStringProperty( );
    private final BooleanProperty _loadingBuild = new SimpleBooleanProperty( );
    private final IntegerProperty _maxRowByColumn = new SimpleIntegerProperty( );
    private final BooleanProperty _lightMode = new SimpleBooleanProperty( );

    private final ObservableList<BuildTypeData> _buildTypes = FXCollections.observableArrayList( );

    private final Configuration _configuration;
    private final Provider<IApiController> _apiController;
    private final ListeningExecutorService _executorService;
    private final IBuildManager _buildManager;
    private final EventBus _eventBus;

    @Inject
    ConfigurationViewModel( final Configuration configuration, final Provider<IApiController> apiController, final ListeningExecutorService executorService, final IBuildManager buildManager, final EventBus eventBus ) {
        _configuration = configuration;
        _eventBus = eventBus;
        _apiController = apiController;
        _executorService = executorService;
        _buildManager = buildManager;
        updateBuildTypeList( );

        _serverUrl.setValue( configuration.getServerUrl( ) );
        _serverUrl.addListener( ( object, oldValue, newValue ) -> configuration.setServerUrl( newValue ) );

        _credentialsUser.setValue( configuration.getCredentialsUser( ) );
        _credentialsUser.addListener( ( object, oldValue, newValue ) -> configuration.setCredentialsUser( newValue ) );

        _credentialsPassword.setValue( configuration.getCredentialsPassword( ) );
        _credentialsPassword.addListener( ( object, oldValue, newValue ) -> configuration.setCredentialsPassword( newValue ) );

        _maxRowByColumn.setValue( configuration.getMaxRowsByColumn( ) );
        _maxRowByColumn.addListener( ( object, oldValue, newValue ) -> configuration.setMaxRowsByColumn( newValue.intValue( ) ) );

        _lightMode.setValue( configuration.isLightMode( ) );
        _lightMode.addListener( ( object, oldValue, newValue ) -> configuration.setLightMode( newValue ) );
    }

    public StringProperty serverUrlProperty( ) {
        return _serverUrl;
    }

    StringProperty credentialsPasswordProperty( ) {
        return _credentialsPassword;
    }

    StringProperty credentialsUserProperty( ) {
        return _credentialsUser;
    }

    BooleanProperty loadingBuildProperty( ) {
        return _loadingBuild;
    }

    IntegerProperty maxRowByColumnProperty( ) {
        return _maxRowByColumn;
    }

    BooleanProperty lightModeProperty( ) {
        return _lightMode;
    }

    ObservableList<BuildTypeData> getBuildTypes( ) {
        return _buildTypes;
    }

    public void requestLoadingBuilds( ) {
        _loadingBuild.setValue( true );

        final ListenableFuture<ListenableFuture<Void>> future = _executorService.submit( (Callable<ListenableFuture<Void>>) _apiController.get( )::loadBuildList );
        addCallback( future, new FutureCallback<ListenableFuture<Void>>( ) {
            @Override
            public void onSuccess( final ListenableFuture<Void> result ) {
                addCallback( result, buildListLoadedCallback( ) );
            }

            @Override
            public void onFailure( final Throwable t ) {

            }
        } );

    }

    private FutureCallback<Void> buildListLoadedCallback( ) {
        return new FutureCallback<Void>( ) {
            @Override
            public void onSuccess( final Void result ) {
                Platform.runLater( ( ) -> {
                    updateBuildTypeList( );
                    _loadingBuild.setValue( false );
                } );
            }

            @Override
            public void onFailure( final Throwable t ) {

            }
        };
    }

    private void updateBuildTypeList( ) {
        _buildTypes.clear( );
        _buildTypes.addAll( _buildManager.getBuildTypeList( ) );
    }

    public void setAliasName( final BuildTypeData buildTypeData, final String aliasName ) {
        buildTypeData.setAliasName( aliasName );
    }

    public void requestSwithToWallScene( ) {
        _eventBus.post( new SceneEvent( WallScene.class ) );
    }

    public ApiVersion getApiVersion( ) {
        return _configuration.getApiVersion( );
    }

    public void requestNewApiVersion( final ApiVersion newValue ) {
        LOGGER.info( "Switching to api version: " + newValue.getIdentifier( ) );
        _configuration.setApiVersion( newValue );
    }

}
