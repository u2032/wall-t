package utils.teamcity.model.build;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class BuildDataModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( IBuildManager.class ).to( BuildDataManager.class ).in( Scopes.SINGLETON );
    }
}
