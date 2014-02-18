package utils.teamcity.model.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class SavedBuildData {

    @SerializedName("id")
    private String _id;

    @SerializedName("name")
    private String _name;

    @SerializedName("projectName")
    private String _projectName;

    @SerializedName("alias_name")
    private String _aliasName;

    public SavedBuildData( final String id, final String name, final String projectName, final String aliasName ) {
        _id = id;
        _name = name;
        _projectName = projectName;
        _aliasName = aliasName;
    }

    public String getId( ) {
        return _id;
    }

    public String getName( ) {
        return _name;
    }

    public String getProjectName( ) {
        return _projectName;
    }

    public String getAliasName( ) {
        return _aliasName;
    }
}
