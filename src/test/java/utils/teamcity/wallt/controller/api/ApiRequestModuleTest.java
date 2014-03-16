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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import utils.teamcity.wallt.TestModules;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Date: 16/03/14
 *
 * @author Cedric Longo
 */
public class ApiRequestModuleTest {

    @Inject
    private Injector _injector;

    @Before
    public void setUp( ) throws Exception {
        Guice.createInjector( TestModules.defaultModules( ) )
                .injectMembers( this );
    }

    @Test
    public void can_inject_ApiRequestController_as_IApiController_in_singleton( ) throws Exception {
        // Setup
        // Exercise
        final IApiRequestController instance = _injector.getInstance( IApiRequestController.class );
        final IApiRequestController instance2 = _injector.getInstance( IApiRequestController.class );
        // Verify
        assertThat( instance, is( notNullValue( ) ) );
        assertThat( instance, is( instanceOf( ApiRequestController.class ) ) );
        assertThat( instance, is( sameInstance( instance2 ) ) );
    }

}
