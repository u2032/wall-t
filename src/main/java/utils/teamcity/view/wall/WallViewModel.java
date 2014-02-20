package utils.teamcity.view.wall;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.teamcity.model.build.BuildTypeData;
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
public final class WallViewModel {

    private final ObservableList<BuildTypeData> _builds = FXCollections.observableArrayList( );
    private final IBuildManager _buildManager;

    private final BooleanProperty _lightMode = new SimpleBooleanProperty( );


    @Inject
    public WallViewModel( final IBuildManager buildManager, final Configuration configuration ) {
        _lightMode.setValue( configuration.isLightMode( ) );

        _buildManager = buildManager;
        updateBuildList( );
    }

    private void updateBuildList( ) {
        final List<BuildTypeData> builds = _buildManager.getBuildTypeList( ).stream( )
                .filter( BuildTypeData::isSelected )
                .collect( Collectors.toList( ) );
        _builds.clear( );
        _builds.addAll( builds );
    }

    public ObservableList<BuildTypeData> getBuilds( ) {
        return _builds;
    }

    public BooleanProperty lightModeProperty( ) {
        return _lightMode;
    }
}
