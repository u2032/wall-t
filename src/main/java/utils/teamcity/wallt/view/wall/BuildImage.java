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

import javafx.scene.image.Image;
import utils.teamcity.wallt.view.UIUtils;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
enum BuildImage {

    SUN( "icons/weather/sun.png" ),
    CLOUDY_SUN( "icons/weather/cloudy_sun.png" ),
    CLOUD( "icons/weather/cloud.png" ),
    RAIN( "icons/weather/rain.png" );

    private final Image _image;

    BuildImage( final String path ) {
        _image = UIUtils.createImage( path );
    }

    public Image getImage( ) {
        return _image;
    }
}
