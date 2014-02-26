package utils.teamcity.wallt.view.wall;

import javafx.scene.image.Image;
import utils.teamcity.wallt.view.UIUtils;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
enum BuildImage {

    SUN( "weather/sun.png" ),
    CLOUDY_SUN( "weather/cloudy_sun.png" ),
    CLOUD( "weather/cloud.png" ),
    RAIN( "weather/rain.png" );

    private final Image _image;

    BuildImage( final String path ) {
        _image = UIUtils.createImage( path );
    }

    public Image getImage( ) {
        return _image;
    }
}
