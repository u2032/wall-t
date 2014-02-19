package utils.teamcity.controller.api.json.v0801;

import com.google.gson.annotations.SerializedName;

/**
 * Date: 17/02/14
 *
 * @author Cedric Longo
 */
final class BuildRunningInfo {

    @SerializedName( "percentageComplete" )
    private int _percentageComplete;

    @SerializedName( "estimatedTotalSeconds" )
    private int _estimatedTotalTime;

    @SerializedName( "elapsedSeconds" )
    private int _elapsedTime;

    int getPercentageComplete( ) {
        return _percentageComplete;
    }

    int getEstimatedTotalTime( ) {
        return _estimatedTotalTime;
    }

    int getElapsedTime( ) {
        return _elapsedTime;
    }
}
