package utils.teamcity.view.wall;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.teamcity.model.build.IBuildManager;
import utils.teamcity.model.configuration.Configuration;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
final class WallViewModel {

    private final EventBus _eventBus;
    private final TileViewModel.Factory _tileViewModeFactory;

    private final ObservableList<TileViewModel> _builds = FXCollections.observableArrayList( );
    private final BooleanProperty _lightMode = new SimpleBooleanProperty( );
    private final int _maxRowsByColumn;

    @Inject
    WallViewModel( final EventBus eventBus, final Configuration configuration, final IBuildManager buildManager, final TileViewModel.Factory tileViewModeFactory ) {
        _eventBus = eventBus;
        _tileViewModeFactory = tileViewModeFactory;
        _maxRowsByColumn = configuration.getMaxRowsByColumn( );
        _lightMode.setValue( configuration.isLightMode( ) );

        updateBuildList( buildManager );
    }

    @Subscribe
    public void updateBuildList( final IBuildManager buildManager ) {
        Platform.runLater( ( ) -> {
            _builds.forEach( _eventBus::unregister );
            _builds.setAll( (List<TileViewModel>) buildManager.getMonitoredBuildTypes( ).stream( )
                    .map( _tileViewModeFactory::forBuildTypeData )
                    .collect( Collectors.toList( ) ) );
            _builds.forEach( _eventBus::register );
        } );
    }

    public ObservableList<TileViewModel> getBuilds( ) {
        return _builds;
    }

    public BooleanProperty lightModeProperty( ) {
        return _lightMode;
    }

    public int getMaxRowsByColumn( ) {
        return _maxRowsByColumn;
    }

    @Inject
    public void registerToEventBus( final EventBus eventBus ) {
        eventBus.register( this );
    }
}
