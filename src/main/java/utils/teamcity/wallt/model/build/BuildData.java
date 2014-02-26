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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Date: 17/02/14
 *
 * @author Cedric Longo
 */
public final class BuildData {

    private final int _id;
    private final BuildStatus _status;
    private final BuildState _state;
    private final int _percentageComplete;
    private final Optional<LocalDateTime> _finishedDate;
    private final Duration _timeLeft;

    public BuildData( final int id, final BuildStatus status, final BuildState state, final int percentageComplete, final Optional<LocalDateTime> finishedDate, Duration timeLeft ) {
        _id = id;
        _status = status;
        _state = state;
        _percentageComplete = percentageComplete;
        _finishedDate = finishedDate;
        _timeLeft = timeLeft;
    }

    public BuildState getState( ) {
        return _state;
    }

    public BuildStatus getStatus( ) {
        return _status;
    }

    public int getId( ) {
        return _id;
    }

    public int getPercentageComplete( ) {
        return _percentageComplete;
    }

    public Optional<LocalDateTime> getFinishedDate( ) {
        return _finishedDate;
    }

    public Duration getTimeLeft( ) {
        return _timeLeft;
    }
}
