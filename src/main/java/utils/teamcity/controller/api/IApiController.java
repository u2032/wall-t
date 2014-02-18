package utils.teamcity.controller.api;

import com.google.common.util.concurrent.ListenableFuture;
import utils.teamcity.model.build.BuildTypeData;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
public interface IApiController {

    /**
     * Request all build type list, and populate IBuildManager with {@link BuildTypeData}
     *
     * @return Future which can be listened for completion
     */
    ListenableFuture<Void> loadBuildList( );

    /**
     * Request last builds status for specified build type and register them on builtType
     *
     * @param buildType Build type which is concerned
     */
    void requestLastBuildStatus( final BuildTypeData buildType );

}
