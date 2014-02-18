package utils.teamcity.model.build;


import java.util.List;
import java.util.Optional;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public interface IBuildManager {

    Optional<BuildTypeData> getBuild( String id );

    void registerBuildTypes( List<BuildTypeData> typeList );

    List<BuildTypeData> getBuildTypeList();

}
