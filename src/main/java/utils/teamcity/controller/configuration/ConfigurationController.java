package utils.teamcity.controller.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.model.build.BuildTypeData;
import utils.teamcity.model.build.IBuildManager;
import utils.teamcity.model.configuration.Configuration;
import utils.teamcity.model.configuration.SavedBuildData;
import utils.teamcity.model.logger.Loggers;

import javax.inject.Inject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Date: 09/02/14
 *
 * @author Cedric Longo
 */
public final class ConfigurationController implements IConfigurationController {

    public static final Logger LOGGER = LoggerFactory.getLogger( Loggers.MAIN );
    private final Configuration _configuration;
    private final IBuildManager _buildManager;

    @Inject
    public ConfigurationController( final Configuration configuration, final IBuildManager buildManager ) {
        _configuration = configuration;
        _buildManager = buildManager;
    }

    @Override
    public void saveConfiguration( ) {

        updateSavedBuild( );

        final Gson gson = new GsonBuilder( ).setPrettyPrinting( ).create( );
        final Path configFilePath = Paths.get( "config.json" );
        final Path configTmpFilePath = Paths.get( "config.json.tmp" );
        try ( FileWriter writer = new FileWriter( configTmpFilePath.toFile( ) ) ) {
            writer.write( gson.toJson( _configuration ) );
            writer.flush( );
        } catch ( IOException e ) {
            LOGGER.error( "Cannot save configuration file", e );
            return;
        }

        try {
            Files.move( configTmpFilePath, configFilePath, REPLACE_EXISTING );
        } catch ( IOException e ) {
            LOGGER.error( "Cannot save configuration file", e );
        }
        LOGGER.info( "Configuration was saved to " + configFilePath.toAbsolutePath( ) );
    }

    private void updateSavedBuild( ) {
        final List<SavedBuildData> buildToSaved = _buildManager.getMonitoredBuildTypes( ).stream( )
                .map( data -> new SavedBuildData( data.getId( ), data.getName( ), data.getProjectName( ), data.getAliasName( ) ) )
                .collect( Collectors.toList( ) );
        _configuration.setSavedBuilds( buildToSaved );
    }
}
