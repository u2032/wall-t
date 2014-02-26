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

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;

import static utils.teamcity.wallt.controller.api.ApiFeature.*;

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
