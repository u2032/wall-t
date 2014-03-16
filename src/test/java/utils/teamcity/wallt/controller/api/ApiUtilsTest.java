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

import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Date: 16/03/14
 *
 * @author Cedric Longo
 */
public class ApiUtilsTest {

    @Test
    public void date_is_correctly_parsed_for_expected_format( ) throws Exception {
        // Setup
        final LocalDateTime expectedDate = LocalDateTime.of( 1985, 8, 29, 7, 30, 02 );
        // Exercise
        final LocalDateTime date = LocalDateTime.parse( "19850829T073002+0400", ApiUtils.DATE_TIME_FORMATTER );
        // Verify
        assertThat( date, is( expectedDate ) );
    }
}
