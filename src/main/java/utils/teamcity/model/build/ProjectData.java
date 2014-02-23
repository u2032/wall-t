package utils.teamcity.model.build;

/**
 * Date: 23/02/14
 *
 * @author Cedric Longo
 */
public final class ProjectData {

    private final String _id;
    private final String _name;

    private String _aliasName;

    public ProjectData( String id, String name ) {
        _id = id;
        _name = name;
    }

    public String getId( ) {
        return _id;
    }

    public String getName( ) {
        return _name;
    }

    public String getAliasName( ) {
        return _aliasName;
    }

    public void setAliasName( String aliasName ) {
        _aliasName = aliasName;
    }
}
