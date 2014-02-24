package utils.teamcity.controller.api.json;

import com.google.gson.annotations.SerializedName;
import utils.teamcity.controller.api.ApiResponse;
import utils.teamcity.controller.api.ApiUtils;
import utils.teamcity.model.build.BuildState;
import utils.teamcity.model.build.BuildStatus;

import java.time.LocalDateTime;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class Build implements ApiResponse {

    @SerializedName("id")
    private int _id;

    @SerializedName("buildTypeId")
    private String _buildType;

    @SerializedName("status")
    private BuildStatus _status = BuildStatus.UNKNOWN;

    @SerializedName("state")
    private BuildState _state;

    @SerializedName( "running" )
    private boolean _running;

    @SerializedName("finishDate")
    private String _finishedDate;

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

    public BuildState getState( ) {
        return _state;
    }

    public BuildRunningInfo getRunningInformation( ) {
        return _runningInformation;
    }

    public LocalDateTime getFinishedDate( ) {
        return _finishedDate == null ? null : LocalDateTime.parse( _finishedDate, ApiUtils.DATE_TIME_FORMATTER );
    }


    public boolean isRunning( ) {
        return _running;
    }
}
