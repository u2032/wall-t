package utils.teamcity.model.build;

import javafx.scene.image.Image;
import org.slf4j.LoggerFactory;
import utils.teamcity.model.logger.Loggers;
import utils.teamcity.view.UIUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public enum BuildImage {

    SUN( "weather/sun.png" ),
    CLOUDY_SUN( "weather/cloudy_sun.png" ),
    CLOUD( "weather/cloud.png" ),
    RAIN( "weather/rain.png" ),;

    private final Image _image;

    BuildImage( final String path ) {
        _image = UIUtils.createImage( path );
    }

    public Image getImage( ) {
        return _image;
    }
}
