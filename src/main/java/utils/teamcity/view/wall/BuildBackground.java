package utils.teamcity.view.wall;

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
