package utils.teamcity.view.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
public final class ConfigurationViewModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( ConfigurationScene.class ).in( Scopes.SINGLETON );
        bind( ConfigurationView.class ).in( Scopes.SINGLETON );
        bind( ConfigurationViewModel.class ).in( Scopes.SINGLETON );

        install( new FactoryModuleBuilder( )
                .implement( BuildTypeViewModel.class, BuildTypeViewModel.class )
                .build( BuildTypeViewModel.Factory.class ) );

        install( new FactoryModuleBuilder( )
                .implement( ProjectViewModel.class, ProjectViewModel.class )
                .build( ProjectViewModel.Factory.class ) );
    }

}
