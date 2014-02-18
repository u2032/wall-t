package utils.teamcity.controller.api.json.v0800;

import com.google.gson.annotations.SerializedName;
import utils.teamcity.controller.api.ApiResponse;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
final class BuildList implements ApiResponse {

    @SerializedName("build")
    private Build[] _builds;

    public Build[] getBuilds( ) {
        return _builds;
    }
}
