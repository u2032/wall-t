package utils.teamcity.wallt.controller.api.json;

import com.google.gson.annotations.SerializedName;

/**
 * Date: 19/02/14
 *
 * @author Cedric Longo
 */
public final class QueueBuild {

    @SerializedName( "buildTypeId" )
    private String _buildTypeId;

    public String getBuildTypeId( ) {
        return _buildTypeId;
    }
}
