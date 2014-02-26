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
     */
    void requestLastBuildStatus( final BuildTypeData buildType );


    /**
     * Request build types in queue and flag them into IBuildManager
     * Moreover, this method must dispath to {@link EventBus} all {@link BuildTypeData} which need view update
     */
    void requestQueuedBuilds( );

    ListenableFuture<Void> loadProjectList( );
}
