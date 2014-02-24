package utils.teamcity.view.wall;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.teamcity.model.build.IBuildManager;
import utils.teamcity.model.build.IProjectManager;
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
    private final ProjectTileViewModel.Factory _projectTileViewModeFactory;

    private final ObservableList<TileViewModel> _displayedBuilds = FXCollections.observableArrayList();
    private final ObservableList<ProjectTileViewModel> _displayedProjects = FXCollections.observableArrayList();

    private final IntegerProperty _maxTilesByColumn = new SimpleIntegerProperty();
    private final IntegerProperty _maxTilesByRow = new SimpleIntegerProperty();

    @Inject
    WallViewModel( final EventBus eventBus, final Configuration configuration, final IBuildManager buildManager, final IProjectManager projectManager, final TileViewModel.Factory tileViewModeFactory, final ProjectTileViewModel.Factory projectTileViewModeFactory ) {
        _eventBus = eventBus;
        _tileViewModeFactory = tileViewModeFactory;
        _projectTileViewModeFactory = projectTileViewModeFactory;
        updateConfiguration( configuration );
        updateBuildList( buildManager );
        updateProjectList( projectManager );
    }

    @Subscribe
    public void updateBuildList( final IBuildManager buildManager ) {
        Platform.runLater( () -> {
            _displayedBuilds.forEach( _eventBus::unregister );
            _displayedBuilds.setAll( (List<TileViewModel>) buildManager.getMonitoredBuildTypes().stream()
                    .map( _tileViewModeFactory::forBuildTypeData )
                    .collect( Collectors.toList() ) );
            _displayedBuilds.forEach( _eventBus::register );
        } );
    }

    @Subscribe
    public void updateProjectList( final IProjectManager projectManager ) {
        Platform.runLater( () -> {
            _displayedProjects.forEach( _eventBus::unregister );
            _displayedProjects.setAll( (List<ProjectTileViewModel>) projectManager.getMonitoredProjects().stream()
                    .map( _projectTileViewModeFactory::forProjectData )
                    .collect( Collectors.toList() ) );
            _displayedProjects.forEach( _eventBus::register );
        } );
    }

    @Subscribe
    public void updateConfiguration( final Configuration configuration ) {
        Platform.runLater( () -> {
            _maxTilesByColumn.setValue( configuration.getMaxTilesByColumn() );
            _maxTilesByRow.setValue( configuration.getMaxTilesByRow() );
        } );
    }

    public ObservableList<TileViewModel> getDisplayedBuilds() {
        return _displayedBuilds;
    }

    ObservableList<ProjectTileViewModel> getDisplayedProjects() {
        return _displayedProjects;
    }

    public IntegerProperty getMaxTilesByColumnProperty() {
        return _maxTilesByColumn;
    }

    public IntegerProperty getMaxTilesByRowProperty() {
        return _maxTilesByRow;
    }

    @Inject
    public void registerToEventBus( final EventBus eventBus ) {
        eventBus.register( this );
    }
}
