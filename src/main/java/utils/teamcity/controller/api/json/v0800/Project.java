package utils.teamcity.controller.api.json.v0800;

import com.google.gson.annotations.SerializedName;

/**
 * Date: 23/02/14
 *
 * @author Cedric Longo
 */
final class Project {

    @SerializedName( "id" )
    private String _id;

    @SerializedName( "name" )
    private String _name;

    public String getId( ) {
        return _id;
    }

    public String getName( ) {
        return _name;
    }
}
