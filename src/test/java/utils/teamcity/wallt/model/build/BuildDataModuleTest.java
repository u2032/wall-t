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

package utils.teamcity.wallt.model.build;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import utils.teamcity.wallt.TestModules;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Date: 02/03/14
 *
 * @author Cedric Longo
 */
public class BuildDataModuleTest {

    @Inject
    private Injector _injector;

    @Before
    public void setUp( ) throws Exception {
        Guice.createInjector( TestModules.defaultModules( ) )
                .injectMembers( this );
    }

    @Test
    public void can_inject_ProjectManager_as_IProjectManager_in_singleton( ) throws Exception {
        // Setup
        // Exercise
        final IProjectManager instance = _injector.getInstance( IProjectManager.class );
        final IProjectManager instance2 = _injector.getInstance( IProjectManager.class );
        // Verify
        assertThat( instance, is( notNullValue( ) ) );
        assertThat( instance, is( instanceOf( ProjectManager.class ) ) );
        assertThat( instance, is( sameInstance( instance2 ) ) );
    }

    @Test
    public void can_inject_BuildTypeManager_as_IBuildTypeManager_in_singleton( ) throws Exception {
        // Setup
        // Exercise
        final IBuildTypeManager instance = _injector.getInstance( IBuildTypeManager.class );
        final IBuildTypeManager instance2 = _injector.getInstance( IBuildTypeManager.class );
        // Verify
        assertThat( instance, is( notNullValue( ) ) );
        assertThat( instance, is( instanceOf( BuildTypeManager.class ) ) );
        assertThat( instance, is( sameInstance( instance2 ) ) );
    }
}
