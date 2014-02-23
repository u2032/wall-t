package utils.teamcity.view.configuration;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.controller.api.IApiController;
import utils.teamcity.controller.api.json.ApiVersion;
import utils.teamcity.model.build.IBuildManager;
import utils.teamcity.model.configuration.Configuration;
import utils.teamcity.model.event.SceneEvent;
import utils.teamcity.model.logger.Loggers;
import utils.teamcity.view.wall.WallScene;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;
import java.util.stream.Collectors;

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
    private final IntegerProperty _maxRowByColumn = new SimpleIntegerProperty( );
    private final BooleanProperty _lightMode = new SimpleBooleanProperty( );

    private final BooleanProperty _loading = new SimpleBooleanProperty( );
    private final BooleanProperty _loadingFailure = new SimpleBooleanProperty( true );
    private final StringProperty _loadingInformation = new SimpleStringProperty( );

    private final ObservableList<BuildTypeViewModel> _buildTypes = FXCollections.observableArrayList( );

    private final Configuration _configuration;
    private final Provider<IApiController> _apiController;
    private final BuildTypeViewModel.Factory _buildTypeViewModelFactory;
    private final EventBus _eventBus;

    @Inject
    ConfigurationViewModel( final Configuration configuration, final Provider<IApiController> apiController, final EventBus eventBus, final IBuildManager buildManager, final BuildTypeViewModel.Factory buildTypeViewModelFactory ) {
        _configuration = configuration;
        _eventBus = eventBus;
        _apiController = apiController;
        _buildTypeViewModelFactory = buildTypeViewModelFactory;

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

        updateBuildTypeList( buildManager );
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

    BooleanProperty loadingProperty( ) {
        return _loading;
    }

    StringProperty loadingInformationProperty( ) {
        return _loadingInformation;
    }

    boolean isLoadingFailure( ) {
        return _loadingFailure.get( );
    }

    BooleanProperty loadingFailureProperty( ) {
        return _loadingFailure;
    }

    IntegerProperty maxRowByColumnProperty( ) {
        return _maxRowByColumn;
    }

    BooleanProperty lightModeProperty( ) {
        return _lightMode;
    }

    ObservableList<BuildTypeViewModel> getBuildTypes( ) {
        return _buildTypes;
    }

    public void requestLoadingBuilds( ) {
        _loading.setValue( true );
        _loadingInformation.setValue( "Trying to connect..." );

        final ListenableFuture<Void> future = _apiController.get( ).loadBuildList( );
        addCallback( future, buildListLoadedCallback( ) );
    }

    private FutureCallback<Void> buildListLoadedCallback( ) {
        return new FutureCallback<Void>( ) {
            @Override
            public void onSuccess( final Void result ) {
                Platform.runLater( ( ) -> {
                    _loadingFailure.setValue( false );
                    _loadingInformation.setValue( null );
                    _loading.setValue( false );
                } );
            }

            @Override
            public void onFailure( final Throwable cause ) {
                Platform.runLater( ( ) -> {
                    _loadingFailure.setValue( true );
                    _loadingInformation.setValue( "Connection failure\n(" + cause.getClass( ).getSimpleName( ) + ": " + cause.getMessage( ) + ")" );
                    _loading.setValue( false );
                } );
            }
        };
    }

    @Subscribe
    public void updateBuildTypeList( final IBuildManager buildManager ) {
        Platform.runLater( ( ) -> {
            _buildTypes.setAll(
                    (List<BuildTypeViewModel>) buildManager.getBuildTypes( ).stream( )
                            .map( _buildTypeViewModelFactory::fromBuildTypeData )
                            .collect( Collectors.toList( ) ) );
        } );
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

    @Inject
    public void registerToEventBus( final EventBus eventBus ) {
        eventBus.register( this );
    }

}
