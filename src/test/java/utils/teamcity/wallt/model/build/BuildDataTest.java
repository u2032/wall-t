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

package utils.teamcity.wallt.model.build;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Date: 02/03/14
 *
 * @author Cedric Longo
 */
public class BuildDataTest {

    @Test
    public void data_is_correctly_saved( ) throws Exception {
        // Setup
        final LocalDateTime now = LocalDateTime.now( );
        final BuildData data = new BuildData( 12246, BuildStatus.FAILURE, BuildState.finished, 58, Optional.of( now.minusMinutes( 30 ) ), Duration.ofSeconds( 98 ) );
        // Exercise
        // Verify
        assertThat( data.getId( ), is( 12246 ) );
        assertThat( data.getStatus( ), is( BuildStatus.FAILURE ) );
        assertThat( data.getState( ), is( BuildState.finished ) );
        assertThat( data.getPercentageComplete( ), is( 58 ) );
        assertThat( data.getFinishedDate( ), is( Optional.of( now.minusMinutes( 30 ) ) ) );
        assertThat( data.getTimeLeft( ), is( Duration.ofSeconds( 98 ) ) );
    }

}
