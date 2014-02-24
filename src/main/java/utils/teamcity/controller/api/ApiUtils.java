package utils.teamcity.controller.api;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Date: 19/02/14
 *
 * @author Cedric Longo
 */
public final class ApiUtils {

    private ApiUtils( ) {
        throw new UnsupportedOperationException( );
    }

    public static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder( )
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

}
