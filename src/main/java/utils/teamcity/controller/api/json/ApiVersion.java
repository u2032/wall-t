package utils.teamcity.controller.api.json;

/**
 * Date: 17/02/14
 *
 * @author Cedric Longo
 */
public enum ApiVersion {

    API_8_1( "8.1" ),
    API_8_0( "8.0" ),;

    private final String _identifier;

    ApiVersion( final String identifier ) {
        _identifier = identifier;
    }

    public static ApiVersion valueFrom( final String string ) {
        for ( final ApiVersion v : values( ) )
            if ( v.getIdentifier( ).equals( string ) )
                return v;
        return null;
    }

    public String getIdentifier( ) {
        return _identifier;
    }
}
