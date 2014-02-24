package utils.teamcity.controller.api.json.v0801;

import com.google.gson.annotations.SerializedName;

/**
 * Date: 19/02/14
 *
 * @author Cedric Longo
 */
final class QueueBuild {

    @SerializedName("buildTypeId")
    private String _buildTypeId;

    String getBuildTypeId( ) {
        return _buildTypeId;
    }
}
