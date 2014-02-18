package utils.teamcity.model.event;

import javafx.scene.Scene;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class SceneEvent {

    private final Class<? extends Scene> _type;

    public SceneEvent( final Class<? extends Scene> type ) {
        _type = type;
    }

    public Class<? extends Scene> getType( ) {
        return _type;
    }
}
