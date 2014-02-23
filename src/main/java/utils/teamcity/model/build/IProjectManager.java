package utils.teamcity.model.build;

import java.util.List;

/**
 * Date: 23/02/14
 *
 * @author Cedric Longo
 */
public interface IProjectManager {

    void registerProjects( List<ProjectData> projects );

    List<ProjectData> getProjects( );

    List<ProjectData> getMonitoredProjects( );

    void activateMonitoring( ProjectData projectData );

    void unactivateMonitoring( ProjectData projectData );

    int getPosition( ProjectData data );

    void requestPosition( ProjectData data, int newValue );

}
