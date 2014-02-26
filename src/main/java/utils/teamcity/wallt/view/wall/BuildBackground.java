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

package utils.teamcity.wallt.view.wall;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;

/**
 * Date: 17/02/14
 *
 * @author Cedric Longo
 */
enum BuildBackground {

    UNKNOWN(
            new Background( new BackgroundFill( Paint.valueOf( "grey" ), new CornerRadii( 5 ), new Insets( 0 ) ) ),
            null
    ),
    SUCCESS(
            new Background( new BackgroundFill( Paint.valueOf( "green" ), new CornerRadii( 5 ), new Insets( 0 ) ) ),
            new Background( new BackgroundFill( Paint.valueOf( "limegreen" ), new CornerRadii( 5 ), new Insets( 0 ) ) )
    ),
    FAILURE(
            new Background( new BackgroundFill( Paint.valueOf( "firebrick" ), new CornerRadii( 5 ), new Insets( 0 ) ) ),
            new Background( new BackgroundFill( Paint.valueOf( "red" ), new CornerRadii( 5 ), new Insets( 0 ) ) )
    );

    private final Background _main;
    private final Background _runnning;

    BuildBackground( final Background main, final Background runnning ) {
        _main = main;
        _runnning = runnning;
    }

    public Background getMain( ) {
        return _main;
    }

    public Background getRunnning( ) {
        return _runnning;
    }
}
