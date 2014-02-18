package utils.teamcity.model.build;

/**
 * Date: 17/02/14
 *
 * @author Cedric Longo
 */
public final class BuildData {

    private final int _id;
    private final String _buildTypeId;
    private final BuildStatus _status;
    private final BuildState _state;
    private final int _percentageComplete;

    public BuildData( final int id, final String buildTypeId, final BuildStatus status, final BuildState state, final int percentageComplete ) {
        _id = id;
        _buildTypeId = buildTypeId;
        _status = status;
        _state = state;
        _percentageComplete = percentageComplete;
    }

    public BuildState getState() {
        return _state;
    }

    public BuildStatus getStatus() {
        return _status;
    }

    public int getId() {
        return _id;
    }

    public String getBuildTypeId() {
        return _buildTypeId;
    }

    public int getPercentageComplete() {
        return _percentageComplete;
    }
}
