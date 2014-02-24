package utils.teamcity.controller.configuration;

import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.model.build.BuildTypeData;
import utils.teamcity.model.build.IBuildManager;
import utils.teamcity.model.build.IProjectManager;
import utils.teamcity.model.build.ProjectData;
import utils.teamcity.model.configuration.Configuration;
import utils.teamcity.model.configuration.SavedBuildTypeData;
import utils.teamcity.model.configuration.SavedProjectData;
import utils.teamcity.model.logger.Loggers;

import javax.inject.Inject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
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
    private final IProjectManager _projectManager;

    @Inject
    public ConfigurationController( final Configuration configuration, final IBuildManager buildManager, final IProjectManager projectManager ) {
        _configuration = configuration;
        _buildManager = buildManager;
        _projectManager = projectManager;
    }

    @Override
    public void saveConfiguration( ) {

        updateSavedBuildTypes( );
        updateSavedProjects( );

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

    private void updateSavedBuildTypes( ) {
        final List<BuildTypeData> monitoredBuildTypes = _buildManager.getMonitoredBuildTypes( );

        final Ordering<BuildTypeData> ordering = Ordering.from( Comparator.comparingInt( _buildManager::getPosition ) );

        final List<SavedBuildTypeData> buildToSaved = ordering.sortedCopy( monitoredBuildTypes ).stream( )
                .map( data -> new SavedBuildTypeData( data.getId( ), data.getName( ), data.getProjectId( ), data.getProjectName( ), data.getAliasName( ) ) )
                .collect( Collectors.toList( ) );

        _configuration.setSavedBuilds( buildToSaved );
    }


    private void updateSavedProjects( ) {
        final List<ProjectData> monitoredProjects = _projectManager.getMonitoredProjects( );

        final Ordering<ProjectData> ordering = Ordering.from( Comparator.comparingInt( _projectManager::getPosition ) );

        final List<SavedProjectData> projectToSaved = ordering.sortedCopy( monitoredProjects ).stream( )
                .map( data -> new SavedProjectData( data.getId( ), data.getName( ), data.getParentId( ), data.getAliasName( ) ) )
                .collect( Collectors.toList( ) );

        _configuration.setSavedProjects( projectToSaved );
    }
}
