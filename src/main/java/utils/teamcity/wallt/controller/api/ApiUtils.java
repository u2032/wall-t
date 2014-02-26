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

package utils.teamcity.wallt.controller.api;

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
