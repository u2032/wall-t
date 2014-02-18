package utils.teamcity.controller.api.json.v0800;

import com.google.gson.annotations.SerializedName;
import utils.teamcity.controller.api.ApiResponse;
import utils.teamcity.model.build.BuildStatus;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
final class Build implements ApiResponse {

    @SerializedName("id")
    private int _id;

    @SerializedName("buildTypeId")
    private String _buildType;

    @SerializedName("status")
    private BuildStatus _status = BuildStatus.UNKNOWN;

    @SerializedName("running")
    private boolean _running;

    @SerializedName("running-info")
    private BuildRunningInfo _runningInformation;

    public int getId( ) {
        return _id;
    }

    public String getBuildType( ) {
        return _buildType;
    }

    public BuildStatus getStatus( ) {
        return _status;
    }

    public boolean isRunning( ) {
        return _running;
    }

    public BuildRunningInfo getRunningInformation( ) {
        return _runningInformation;
    }
}
