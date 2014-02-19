package utils.teamcity.controller.api.json.v0800;

import com.google.gson.annotations.SerializedName;
import utils.teamcity.controller.api.ApiResponse;
import utils.teamcity.model.build.BuildStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
final class Build implements ApiResponse {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder( )
            .parseCaseInsensitive( )
            .appendValue( ChronoField.YEAR, 4 )
            .appendValue( ChronoField.MONTH_OF_YEAR, 2 )
            .appendValue( ChronoField.DAY_OF_MONTH, 2 )
            .optionalStart( )
            .appendLiteral( "T" )
            .appendValue( ChronoField.HOUR_OF_DAY, 2 )
            .appendValue( ChronoField.MINUTE_OF_HOUR, 2 )
            .appendValue( ChronoField.SECOND_OF_MINUTE, 2 )
            .optionalEnd( )
            .optionalStart( )
            .appendOffset( "+HHMM", "Z" )
            .optionalEnd( )
            .toFormatter( );


    @SerializedName("id")
    private int _id;

    @SerializedName("buildTypeId")
    private String _buildType;

    @SerializedName("status")
    private BuildStatus _status = BuildStatus.UNKNOWN;

    @SerializedName("finishDate")
    private String _finishedDate;

    @SerializedName( "running" )
    private boolean _running;

    @SerializedName( "running-info" )
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

    boolean isRunning( ) {
        return _running;
    }

    BuildRunningInfo getRunningInformation( ) {
        return _runningInformation;
    }

    LocalDateTime getFinishedDate( ) {
        return _finishedDate == null ? null : LocalDateTime.parse( _finishedDate, DATE_TIME_FORMATTER );
    }


}
