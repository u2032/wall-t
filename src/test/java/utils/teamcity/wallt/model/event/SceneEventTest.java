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

package utils.teamcity.wallt.model.event;

import javafx.scene.Scene;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import utils.teamcity.wallt.view.configuration.ConfigurationScene;
import utils.teamcity.wallt.view.wall.WallScene;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Date: 01/03/14
 *
 * @author Cedric Longo
 */
public class SceneEventTest {

    @Test
    public void event_returns_the_correct_defined_type( ) {
        // Setup
        final SceneEvent event1 = new SceneEvent( ConfigurationScene.class );
        final SceneEvent event2 = new SceneEvent( WallScene.class );
        // Exercise
        // Verify
        assertThat( event1.getType( ), CoreMatchers.<Class<? extends Scene>>is( ConfigurationScene.class ) );
        assertThat( event2.getType( ), CoreMatchers.<Class<? extends Scene>>is( WallScene.class ) );
    }

}
