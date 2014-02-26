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
