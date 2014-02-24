package utils.teamcity.controller.api.json;

import com.google.gson.annotations.SerializedName;
import utils.teamcity.controller.api.ApiResponse;

import java.util.Collections;
import java.util.List;

/**
 * Date: 19/02/14
 *
 * @author Cedric Longo
 */
public final class QueuedBuildList implements ApiResponse {

    @SerializedName("build")
    private List<QueueBuild> _builds = Collections.emptyList( );

    public List<QueueBuild> getQueueBuild( ) {
        return _builds;
    }

}
