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

package utils.teamcity.wallt.controller.api;

import com.google.common.util.concurrent.ListenableFuture;
import utils.teamcity.wallt.model.build.BuildTypeData;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
public interface IApiController {

    /**
     * Request all project list, and populate {@link utils.teamcity.wallt.model.build.IProjectManager} with {@link utils.teamcity.wallt.model.build.ProjectData}
     * Moreover, this method must dispath to {@link EventBus} the {@link utils.teamcity.wallt.model.build.IProjectManager} if needs view update
     *
     * @return Future which can be listened for completion
     */
    ListenableFuture<Void> loadProjectList( );

    /**
     * Request all build type list, and populate {@link IBuildManager} with {@link BuildTypeData}
     * Moreover, this method must dispath to {@link EventBus} the {@link IBuildManager} if needs view update
     *
     * @return Future which can be listened for completion
     */
    ListenableFuture<Void> loadBuildTypeList( );


    /**
     * Request last builds status for specified build type and register them on builtType
     * Moreover, this method must dispath to {@link EventBus} the {@link BuildTypeData} if needs view update
     *
     * @param buildType Build type which is concerned
     * @return Future which can be listened for completion
     */
    ListenableFuture<Void> requestLastBuildStatus( final BuildTypeData buildType );

    /**
     * Request build types in queue and flag them into IBuildManager
     * Moreover, this method must dispath to {@link EventBus} all {@link BuildTypeData} which need view update
     *
     * @return Future which can be listened for completion
     */
    ListenableFuture<Void> requestQueuedBuilds( );
}
