package utils.teamcity.model.build;


import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public interface IBuildManager {

    void registerBuildTypes( List<BuildTypeData> typeList );

    List<BuildTypeData> getBuildTypes( );

    List<BuildTypeData> getMonitoredBuildTypes( );

    Optional<BuildTypeData> getBuild( String id );

    void activateMonitoring( BuildTypeData buildTypeData );

    void unactivateMonitoring( BuildTypeData buildTypeData );

    List<BuildTypeData> registerBuildTypesInQueue( Set<String> buildTypesInQueue );
}
