package utils.teamcity.controller.api.json;

import com.google.gson.annotations.SerializedName;

/**
 * Date: 17/02/14
 *
 * @author Cedric Longo
 */
public final class BuildRunningInfo {

    @SerializedName("percentageComplete")
    private int _percentageComplete;

    @SerializedName("estimatedTotalSeconds")
    private int _estimatedTotalTime;

    @SerializedName("elapsedSeconds")
    private int _elapsedTime;

    public int getPercentageComplete( ) {
        return _percentageComplete;
    }

    public int getEstimatedTotalTime( ) {
        return _estimatedTotalTime;
    }

    public int getElapsedTime( ) {
        return _elapsedTime;
    }
}
