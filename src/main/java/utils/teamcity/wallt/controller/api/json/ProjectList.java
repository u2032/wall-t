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

package utils.teamcity.wallt.controller.api.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import utils.teamcity.wallt.controller.api.ApiResponse;

import java.util.List;

/**
 * Date: 23/02/14
 *
 * @author Cedric Longo
 */
public final class ProjectList implements ApiResponse {

    @SerializedName("project")
    private List<Project> _projects = Lists.newLinkedList( );

    public List<Project> getProjects( ) {
        return _projects;
    }

    @VisibleForTesting
    public void addProject( final Project project ) {
        _projects.add( project );
    }
}
