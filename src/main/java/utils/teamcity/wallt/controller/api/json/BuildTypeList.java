package utils.teamcity.wallt.controller.api.json;

import com.google.gson.annotations.SerializedName;
import utils.teamcity.wallt.controller.api.ApiResponse;

import java.util.Collections;
import java.util.List;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class BuildTypeList implements ApiResponse {

    @SerializedName( "buildType" )
    private List<BuildType> _builds = Collections.emptyList( );

    public List<BuildType> getBuildTypes( ) {
        return _builds;
    }
}
