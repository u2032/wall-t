package utils.teamcity.wallt.controller.api;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.wallt.model.build.BuildTypeData;
import utils.teamcity.wallt.model.build.IBuildManager;
import utils.teamcity.wallt.model.build.IProjectManager;
import utils.teamcity.wallt.model.build.ProjectData;
import utils.teamcity.wallt.model.logger.Loggers;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class ApiMonitoringService implements IApiMonitoringService {

    public static final Logger LOGGER = LoggerFactory.getLogger( Loggers.MAIN );
    private final ScheduledExecutorService _executorService;
    private final IApiController _apiController;
    private final IProjectManager _projectManager;
    private final IBuildManager _buildManager;
    private final EventBus _eventBus;

    private boolean _active;


    @Inject
    public ApiMonitoringService( final ScheduledExecutorService executorService, final IApiController apiController, final IProjectManager projectManager, final IBuildManager buildManager, final EventBus eventBus ) {
        _executorService = executorService;
        _apiController = apiController;
        _projectManager = projectManager;
        _buildManager = buildManager;
        _eventBus = eventBus;
    }

    @Override
    public void start( ) {
        _executorService.scheduleWithFixedDelay( checkIdleBuildStatus( ), 20, 30, TimeUnit.SECONDS );
        _executorService.scheduleWithFixedDelay( checkRunningBuildStatus( ), 30, 10, TimeUnit.SECONDS );
        _executorService.scheduleWithFixedDelay( checkQueuedBuildStatus( ), 30, 10, TimeUnit.SECONDS );
        _executorService.scheduleWithFixedDelay( checkDataAreAlwaysSync( ), 2, 10, TimeUnit.MINUTES );
        LOGGER.info( "Monitoring service configured." );
    }

    public synchronized boolean isActive( ) {
        return _active;
    }

    @Override
    public synchronized void pause( ) {
        _active = false;
        LOGGER.info( "Monitoring service paused." );
    }

    @Override
    public synchronized void activate( ) {
        _active = true;
        LOGGER.info( "Monitoring service started." );
    }

    private Collection<BuildTypeData> getAllMonitoredBuildTypes( ) {
        final Set<BuildTypeData> allMonitoredBuildTypes = Sets.newHashSet( );
        allMonitoredBuildTypes.addAll( _buildManager.getMonitoredBuildTypes( ) );

        for ( final ProjectData projectData : _projectManager.getMonitoredProjects( ) ) {
            allMonitoredBuildTypes.addAll( projectData.getBuildTypes( ) );
            for ( final ProjectData child : _projectManager.getAllChildrenOf( projectData ) ) {
                allMonitoredBuildTypes.addAll( child.getBuildTypes( ) );
            }
        }

        return allMonitoredBuildTypes;
    }

    private Runnable checkIdleBuildStatus( ) {
        return ( ) -> {
            if ( !isActive( ) )
                return;

            final List<BuildTypeData> monitoredBuilds = getAllMonitoredBuildTypes( ).stream( )
                    .filter( b -> !b.hasRunningBuild( ) )
                    .collect( Collectors.toList( ) );

            for ( final BuildTypeData buildType : monitoredBuilds )
                _apiController.requestLastBuildStatus( buildType );
        };
    }

    private Runnable checkRunningBuildStatus( ) {
        return ( ) -> {
            if ( !isActive( ) )
                return;

            final List<BuildTypeData> monitoredBuilds = getAllMonitoredBuildTypes( ).stream( )
                    .filter( BuildTypeData::hasRunningBuild )
                    .collect( Collectors.toList( ) );

            for ( final BuildTypeData buildType : monitoredBuilds )
                _apiController.requestLastBuildStatus( buildType );
        };
    }

    private Runnable checkQueuedBuildStatus( ) {
        return ( ) -> {
            if ( !isActive( ) )
                return;
            _apiController.requestQueuedBuilds( );
        };
    }

    private Runnable checkDataAreAlwaysSync( ) {
        return ( ) -> {
            if ( !isActive( ) )
                return;

            final Instant cut = Instant.now( ).minus( 5, ChronoUnit.MINUTES );

            final Collection<BuildTypeData> monitoredBuilds = getAllMonitoredBuildTypes( );
            for ( final BuildTypeData buildType : monitoredBuilds ) {
                if ( buildType.clearIfOutdated( cut ) ) {
                    _eventBus.post( buildType );
                    final Optional<ProjectData> project = _projectManager.getProject( buildType.getProjectId( ) );
                    if ( project.isPresent( ) )
                        _eventBus.post( project );
                }
            }
        };
    }
}
