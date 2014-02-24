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
public final class ProjectList implements ApiResponse {

    @SerializedName("project")
    private List<Project> _projects = Collections.emptyList( );

    public List<Project> getProjects( ) {
        return _projects;
    }
}
