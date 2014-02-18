package utils.teamcity.controller.api.json.v0800;

import com.google.gson.annotations.SerializedName;
import utils.teamcity.controller.api.ApiResponse;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
final class BuildTypeList implements ApiResponse {

    @SerializedName("buildType")
    private BuildType[] _builds;

    public BuildType[] getBuildTypes() {
        return _builds;
    }
}
