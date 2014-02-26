/*******************************************************************************
 * Copyright 2014 Cedric Longo.
 *
 * This file is part of Wall-T program.
 *
 * Wall-T is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Wall-T is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Wall-T.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package utils.teamcity.wallt.model.build;

import java.util.List;
import java.util.Optional;

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

    Optional<ProjectData> getProject( String id );

    List<ProjectData> getAllChildrenOf( ProjectData data );
}
