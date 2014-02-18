package utils.teamcity.view.wall;

import javafx.scene.Scene;

import javax.inject.Inject;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class WallScene extends Scene {

    @Inject
    public WallScene( final WallView pane ) {
        super( pane );
    }
}
