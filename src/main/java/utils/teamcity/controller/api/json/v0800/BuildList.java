package utils.teamcity.controller.api.json.v0800;

import com.google.gson.annotations.SerializedName;
import utils.teamcity.controller.api.ApiResponse;

import java.util.Collections;
import java.util.List;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
final class BuildList implements ApiResponse {

    @SerializedName("build")
    private List<Build> _builds = Collections.emptyList( );

    public List<Build> getBuilds( ) {
        return _builds;
    }
}
