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

    @SerializedName( "id" )
    private int _id;

    @SerializedName( "buildType" )
    private BuildType _buildType;

    @SerializedName( "status" )
    private BuildStatus _status;

    @SerializedName( "state" )
    private BuildState _state;

    @SerializedName( "running" )
    private boolean _running;

    @SerializedName( "startDate" )
    private String _startDate;

    @SerializedName( "finishDate" )
    private String _finishDate;

    @SerializedName( "running-info" )
    private BuildRunningInfo _runningInformation;

    public int getId( ) {
        return _id;
    }

    public BuildType getBuildType( ) {
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

    public LocalDateTime getFinishDate( ) {
        return _finishDate == null ? null : LocalDateTime.parse( _finishDate, ApiUtils.DATE_TIME_FORMATTER );
    }

    public LocalDateTime getStartDate( ) {
        return _startDate == null ? null : LocalDateTime.parse( _startDate, ApiUtils.DATE_TIME_FORMATTER );
    }

    public boolean isRunning( ) {
        return _running;
    }
}
