package utils.teamcity.view.wall;

import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.google.inject.assistedinject.Assisted;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.layout.Background;
import utils.teamcity.model.build.BuildStatus;
import utils.teamcity.model.build.ProjectData;
import utils.teamcity.model.configuration.Configuration;

import javax.inject.Inject;

/**
 * Date: 22/02/14
 *
 * @author Cedric Longo
 */
final class ProjectTileViewModel {

    private final ProjectData _projectData;

    private final StringProperty _displayedName = new SimpleStringProperty();
    private final ObjectProperty<Background> _background = new SimpleObjectProperty<>();

    private final BooleanProperty _lightMode = new SimpleBooleanProperty();

    interface Factory {
        ProjectTileViewModel forProjectData( final ProjectData projectData );
    }

    @Inject
    ProjectTileViewModel( final Configuration configuration, @Assisted final ProjectData projectData ) {
        _projectData = projectData;
        updateConfiguration( configuration );
        updateProjectViewModel( projectData );
    }

    @Subscribe
    public final void updateProjectViewModel( final ProjectData data ) {
        if ( data != _projectData )
            return;

        Platform.runLater( () -> {
            _displayedName.set( Strings.isNullOrEmpty( data.getAliasName() ) ? data.getName() : data.getAliasName() );
            updateBackground();
        } );
    }

    @Subscribe
    public void updateConfiguration( final Configuration configuration ) {
        Platform.runLater( () -> {
            _lightMode.setValue( configuration.isLightMode() );
        } );
    }


    private void updateBackground() {
        final int failureCount = _projectData.getBuildTypeCount( BuildStatus.FAILURE, BuildStatus.ERROR );
        final int successCount = _projectData.getBuildTypeCount( BuildStatus.SUCCESS );
        final int unknownCount = _projectData.getBuildTypeCount( BuildStatus.UNKNOWN );

        if ( unknownCount > 0 || failureCount + successCount == 0 ) {
            _background.setValue( BuildBackground.UNKNOWN.getMain() );
            return;
        }

        // Setting main background according to failure count
        _background.setValue( failureCount == 0 ? BuildBackground.SUCCESS.getMain() : BuildBackground.FAILURE.getMain() );
    }

    String getDisplayedName() {
        return _displayedName.get();
    }

    StringProperty displayedNameProperty() {
        return _displayedName;
    }

    Background getBackground() {
        return _background.get();
    }

    ObjectProperty<Background> backgroundProperty() {
        return _background;
    }

    BooleanProperty lightModeProperty() {
        return _lightMode;
    }

}
