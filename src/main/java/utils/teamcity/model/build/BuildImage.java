package utils.teamcity.model.build;

import javafx.scene.image.Image;
import org.slf4j.LoggerFactory;
import utils.teamcity.model.logger.Loggers;

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

    private Image _image;

    BuildImage( final String path ) {
        try ( final InputStream is = BuildTypeData.class.getClassLoader().getResourceAsStream( path ) ) {
            _image = new Image( is );
        } catch ( final IOException e ) {
            LoggerFactory.getLogger( Loggers.MAIN ).warn( "Unable to load image: ", e );
        }
    }

    public Image getImage() {
        return _image;
    }
}
