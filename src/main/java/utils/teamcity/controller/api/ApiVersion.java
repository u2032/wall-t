package utils.teamcity.controller.api;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;

import static utils.teamcity.controller.api.ApiFeature.*;

/**
 * Date: 17/02/14
 *
 * @author Cedric Longo
 */
public enum ApiVersion {

    API_8_1( "8.1", "8.0", PROJECT_STATUS, BUILD_TYPE_STATUS, QUEUE_STATUS ),
    API_8_0( "8.0", "8.0", PROJECT_STATUS, BUILD_TYPE_STATUS ),
    API_7_1( "7.1", "7.0", PROJECT_STATUS, BUILD_TYPE_STATUS ),
    API_6_0( "6.0", "6.0", PROJECT_STATUS, BUILD_TYPE_STATUS ),;

    /**
     * Identifier used in url to request API
     */
    private final String _identifier;
    private final String _name;
    private final Set<ApiFeature> _supportedFeatures;

    ApiVersion( final String name, final String identifier, final ApiFeature... features ) {
        _name = name;
        _identifier = identifier;
        _supportedFeatures = Sets.newEnumSet( Arrays.asList( features ), ApiFeature.class );
    }

    public static ApiVersion fromName( final String string ) {
        for ( final ApiVersion v : values( ) )
            if ( v.getName( ).equals( string ) )
                return v;
        return null;
    }

    public String getIdentifier( ) {
        return _identifier;
    }

    public String getName( ) {
        return _name;
    }

    public boolean isSupported( final ApiFeature feature ) {
        return _supportedFeatures.contains( feature );
    }

    @Override
    public String toString( ) {
        return "ApiVersion{" +
                "_name='" + _name + "\'" +
                ", _identifier='" + _identifier + "\'" +
                "}";
    }
}
