package utils.teamcity.view.wall;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import javafx.scene.layout.Pane;

import java.util.Map;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
public final class WallViewModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( WallScene.class ).in( Scopes.SINGLETON );
        bind( WallView.class ).in( Scopes.SINGLETON );
        bind( WallViewModel.class ).in( Scopes.SINGLETON );

        install( new FactoryModuleBuilder( )
                .implement( TileViewModel.class, TileViewModel.class )
                .build( TileViewModel.Factory.class ) );

        install( new FactoryModuleBuilder( )
                .implement( ProjectTileViewModel.class, ProjectTileViewModel.class )
                .build( ProjectTileViewModel.Factory.class ) );
    }

    @Provides
    @Singleton
    Map<Class<?>, TileViewProvider> modelToView( ) {
        return ImmutableMap.<Class<?>, TileViewProvider>builder( )
                .put( TileViewModel.class, from -> new TileView( (TileViewModel) from ) )
                .put( ProjectTileViewModel.class, from -> new ProjectTileView( (ProjectTileViewModel) from ) )
                .build( );
    }

    public interface TileViewProvider {
        Pane get( Object from );
    }


}
