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

package utils.teamcity.wallt.controller.configuration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import utils.teamcity.wallt.TestModules;
import utils.teamcity.wallt.model.configuration.Configuration;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Date: 01/03/14
 *
 * @author Cedric Longo
 */
public class ConfigurationModuleTest {

    @Inject
    private Injector _injector;

    @Before
    public void setUp( ) throws Exception {
        Guice.createInjector( TestModules.defaultModulesWithOverride( ConfigurationModule.class, new ConfigurationModule( ) ) )
                .injectMembers( this );
    }

    @Test
    public void can_inject_Configuration_in_singleton( ) {
        // Setup
        // Exercise
        final Configuration instance = _injector.getInstance( Configuration.class );
        final Configuration instance2 = _injector.getInstance( Configuration.class );
        // Verify
        assertThat( instance, notNullValue( ) );
        assertThat( instance, sameInstance( instance2 ) );
    }

    @Test
    public void can_inject_ConfigurationController_as_IConfigurationController_in_singleton( ) {
        // Setup
        // Exercise
        final IConfigurationController instance = _injector.getInstance( IConfigurationController.class );
        final IConfigurationController instance2 = _injector.getInstance( IConfigurationController.class );
        // Verify
        assertThat( instance, notNullValue( ) );
        assertThat( instance, instanceOf( ConfigurationController.class ) );
        assertThat( instance, sameInstance( instance2 ) );
    }
}
