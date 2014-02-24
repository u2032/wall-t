package utils.teamcity.controller.api.json;

import com.google.gson.annotations.SerializedName;
import utils.teamcity.controller.api.ApiResponse;

import java.util.Collections;
import java.util.List;

/**
 * Date: 23/02/14
 *
 * @author Cedric Longo
 */
public final class ProjectBuildTypeList implements ApiResponse {

    @SerializedName("buildTypes")
    private BuildTypeList _buildTypes;

    public List<BuildType> getBuildTypes( ) {
        return _buildTypes == null ? Collections.emptyList( ) : _buildTypes.getBuildTypes( );
    }
}
