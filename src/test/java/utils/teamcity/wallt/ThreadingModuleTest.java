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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Date: 01/03/14
 *
 * @author Cedric Longo
 */
public class ThreadingModuleTest {

    @Inject
    private Injector _injector;

    @Before
    public void setUp( ) throws Exception {
        Guice.createInjector( TestModules.defaultModules( ) )
                .injectMembers( this );
    }

    @Test
    public void can_inject_ExecutorService_in_singleton( ) {
        // Setup
        // Exercise
        final ExecutorService instance = _injector.getInstance( ExecutorService.class );
        final ExecutorService instance2 = _injector.getInstance( ExecutorService.class );
        // Verify
        assertThat( instance, notNullValue( ) );
        assertThat( instance, sameInstance( instance2 ) );
    }

    @Test
    public void can_inject_ScheduledExecutorService_in_singleton( ) {
        // Setup
        // Exercise
        final ScheduledExecutorService instance = _injector.getInstance( ScheduledExecutorService.class );
        final ScheduledExecutorService instance2 = _injector.getInstance( ScheduledExecutorService.class );
        // Verify
        assertThat( instance, notNullValue( ) );
        assertThat( instance, sameInstance( instance2 ) );
    }
}
