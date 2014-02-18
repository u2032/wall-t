package utils.teamcity.controller.configuration;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import org.slf4j.LoggerFactory;
import utils.teamcity.model.configuration.Configuration;
import utils.teamcity.model.logger.Loggers;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Date: 09/02/14
 *
 * @author Cedric Longo
 */
public class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind( IConfigurationController.class ).to( ConfigurationController.class ).in( Scopes.SINGLETON );
    }

    @Provides
    @Singleton
    Configuration loadConfiguration() {
        final Path configFilePath = Paths.get( "config.json" );
        try ( FileReader reader = new FileReader( configFilePath.toFile() ) ) {
            final Gson gson = new Gson();
            return gson.fromJson( reader, Configuration.class );
        } catch ( IOException ignored ) {
            LoggerFactory.getLogger( Loggers.MAIN ).warn( "No configuration file found: starting with empty configuration" );
            return new Configuration();
        }
    }
}
