package utils.teamcity.controller.api.json.v0801;

import com.google.gson.annotations.SerializedName;
import utils.teamcity.controller.api.ApiResponse;
import utils.teamcity.controller.api.json.ApiUtils;
import utils.teamcity.model.build.BuildState;
import utils.teamcity.model.build.BuildStatus;

import java.time.LocalDateTime;

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

    @SerializedName("state")
    private BuildState _state;

    @SerializedName("finishDate")
    private String _finishedDate;

    @SerializedName("running-info")
    private BuildRunningInfo _runningInformation;

    int getId( ) {
        return _id;
    }

    String getBuildType( ) {
        return _buildType;
    }

    BuildStatus getStatus( ) {
        return _status;
    }

    BuildState getState( ) {
        return _state;
    }

    BuildRunningInfo getRunningInformation( ) {
        return _runningInformation;
    }

    LocalDateTime getFinishedDate( ) {
        return _finishedDate == null ? null : LocalDateTime.parse( _finishedDate, ApiUtils.DATE_TIME_FORMATTER );
    }


}
