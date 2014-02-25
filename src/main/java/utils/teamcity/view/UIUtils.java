package utils.teamcity.view;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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

    public static Font font( final int size, final FontWeight weight ) {
        return Font.font( "System", weight, size );
    }

    public static Font font( final int size ) {
        return Font.font( "System", size );
    }

    private static final DropShadow SHADOW_EFFECT = new DropShadow( 10, 3.0f, 3.0f, Color.BLACK );

    public static DropShadow shadowEffect( ) {
        return SHADOW_EFFECT;
    }

    private UIUtils( ) {
        throw new UnsupportedOperationException( );
    }
}
