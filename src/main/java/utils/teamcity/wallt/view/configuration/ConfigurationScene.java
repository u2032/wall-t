package utils.teamcity.wallt.view.configuration;

import javafx.scene.Scene;

import javax.inject.Inject;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class ConfigurationScene extends Scene {

    @Inject
    public ConfigurationScene( final ConfigurationView pane ) {
        super( pane );
    }
}
