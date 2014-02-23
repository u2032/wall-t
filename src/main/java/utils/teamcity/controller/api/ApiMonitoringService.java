package utils.teamcity.controller.api;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.model.build.BuildTypeData;
import utils.teamcity.model.build.IBuildManager;
import utils.teamcity.model.logger.Loggers;

import javax.inject.Inject;
import javax.inject.Provider;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
    private final Provider<IApiController> _apiController;
    private final IBuildManager _buildManager;
    private final EventBus _eventBus;

    @Inject
    public ApiMonitoringService( final ScheduledExecutorService executorService, final Provider<IApiController> apiController, final IBuildManager buildManager, final EventBus eventBus ) {
        _executorService = executorService;
        _apiController = apiController;
        _buildManager = buildManager;
        _eventBus = eventBus;
    }

    @Override
    public void start( ) {
        _executorService.scheduleWithFixedDelay( checkIdleBuildStatus( ), 10, 30, TimeUnit.SECONDS );
        _executorService.scheduleWithFixedDelay( checkRunningBuildStatus( ), 30, 10, TimeUnit.SECONDS );
        _executorService.scheduleWithFixedDelay( checkQueuedBuildStatus( ), 30, 10, TimeUnit.SECONDS );
        _executorService.scheduleWithFixedDelay( checkDataAreAlwaysSync( ), 2, 10, TimeUnit.MINUTES );
        LOGGER.info( "Monitoring service started." );
    }

    private Runnable checkIdleBuildStatus( ) {
        return ( ) -> {
            final List<BuildTypeData> monitoredBuilds = _buildManager.getMonitoredBuildTypes( ).stream( )
                    .filter( b -> !b.hasRunningBuild( ) )
                    .collect( Collectors.toList( ) );

            for ( final BuildTypeData buildType : monitoredBuilds )
                _apiController.get( ).requestLastBuildStatus( buildType );
        };
    }

    private Runnable checkRunningBuildStatus( ) {
        return ( ) -> {
            final List<BuildTypeData> monitoredBuilds = _buildManager.getMonitoredBuildTypes( ).stream( )
                    .filter( BuildTypeData::hasRunningBuild )
                    .collect( Collectors.toList( ) );

            for ( final BuildTypeData buildType : monitoredBuilds )
                _apiController.get( ).requestLastBuildStatus( buildType );
        };
    }

    private Runnable checkQueuedBuildStatus( ) {
        return ( ) -> {
            _apiController.get( ).requestQueuedBuilds( );
        };
    }

    private Runnable checkDataAreAlwaysSync( ) {
        return ( ) -> {
            final Instant cut = Instant.now( ).minus( 5, ChronoUnit.MINUTES );

            final List<BuildTypeData> monitoredBuilds = _buildManager.getMonitoredBuildTypes( );
            for ( final BuildTypeData buildType : monitoredBuilds ) {
                if ( buildType.clearIfOutdated( cut ) )
                    _eventBus.post( buildType );
            }
        };
    }
}
