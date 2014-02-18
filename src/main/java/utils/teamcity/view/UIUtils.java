package utils.teamcity.view;

import javafx.scene.image.Image;
import org.slf4j.LoggerFactory;
import utils.teamcity.model.build.BuildTypeData;
import utils.teamcity.model.logger.Loggers;

import java.io.IOException;
import java.io.InputStream;

/**
 * Date: 18/02/14
 *
 * @author Cedric Longo
 */
public final class UIUtils {

    public static Image createImage( final String path ) {
        try ( final InputStream is = BuildTypeData.class.getClassLoader( ).getResourceAsStream( path ) ) {
            return new Image( is );
        } catch ( final IOException e ) {
            LoggerFactory.getLogger( Loggers.MAIN ).warn( "Unable to load image: ", e );
        }
        return null;
    }

    private UIUtils() {
        throw new UnsupportedOperationException(  );
    }
}
