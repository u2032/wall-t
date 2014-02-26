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

package utils.teamcity.wallt.view;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.slf4j.LoggerFactory;
import utils.teamcity.wallt.model.build.BuildTypeData;
import utils.teamcity.wallt.model.logger.Loggers;

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
