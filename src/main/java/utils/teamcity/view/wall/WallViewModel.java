package utils.teamcity.view.wall;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.teamcity.model.build.BuildTypeData;
import utils.teamcity.model.build.IBuildManager;

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

    @Inject
    public WallViewModel( final IBuildManager buildManager ) {
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
}
