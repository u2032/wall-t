/*******************************************************************************
 * Copyright 2014 Cedric Longo.
 *
 * This file is part of Wall-T program.
 *
 * Wall-T is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Wall-T is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Wall-T.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package utils.teamcity.wallt.controller.api.json;

import com.google.gson.annotations.SerializedName;
import utils.teamcity.wallt.controller.api.ApiResponse;
import utils.teamcity.wallt.controller.api.ApiUtils;
import utils.teamcity.wallt.model.build.BuildState;
import utils.teamcity.wallt.model.build.BuildStatus;

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
