package utils.teamcity.model.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class SavedBuildTypeData {

    @SerializedName( "id" )
    private String _id;

    @SerializedName( "name" )
    private String _name;

    @SerializedName( "projectName" )
    private String _projectName;

    @SerializedName( "projectId" )
    private String _projectId;

    @SerializedName( "alias_name" )
    private String _aliasName;

    public SavedBuildTypeData( final String id, final String name, final String projectId, final String projectName, final String aliasName ) {
        _id = id;
        _name = name;
        _projectId = projectId;
        _projectName = projectName;
        _aliasName = aliasName;
    }

    public String getId( ) {
        return _id;
    }

    public String getName( ) {
        return _name;
    }

    public String getProjectId( ) {
        return _projectId;
    }

    public String getProjectName( ) {
        return _projectName;
    }

    public String getAliasName( ) {
        return _aliasName;
    }
}
