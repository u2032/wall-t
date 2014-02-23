package utils.teamcity.controller.api.json.v0800;

import com.google.gson.annotations.SerializedName;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
final class BuildType {

    @SerializedName("id")
    private String _id;

    @SerializedName("name")
    private String _name;

    @SerializedName("projectName")
    private String _projectName;

    @SerializedName( "projectId" )
    private String _projectId;


    public String getId( ) {
        return _id;
    }

    public String getName( ) {
        return _name;
    }

    public String getProjectName( ) {
        return _projectName;
    }

    String getProjectId( ) {
        return _projectId;
    }
}
