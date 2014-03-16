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

package utils.teamcity.wallt;

import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import utils.teamcity.wallt.controller.configuration.ConfigurationModule;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Date: 01/03/14
 *
 * @author Cedric Longo
 */
public final class TestModules {

    private TestModules( ) {
    }

    public static List<Module> defaultModules( ) {
        return modulesWithOverride( WallApplication.modules( ), ConfigurationModule.class, new TestConfigurationModule( ) );
    }

    public static List<Module> modulesWithOverride( final List<Module> modules, final Class<? extends Module> moduleToOverwrite, final Module module ) {
        return new ImmutableList.Builder<Module>( )
                .addAll( modules.stream( ).filter( m -> m.getClass( ) != moduleToOverwrite ).collect( Collectors.<Module>toList( ) ) )
                .add( module )
                .build( );
    }

}
