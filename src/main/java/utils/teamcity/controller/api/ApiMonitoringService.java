package utils.teamcity.controller.api;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.model.build.BuildTypeData;
import utils.teamcity.model.build.IBuildManager;
import utils.teamcity.model.logger.Loggers;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class ApiMonitoringService implements IApiMonitoringService {

    public static final Logger LOGGER = LoggerFactory.getLogger( Loggers.MAIN );
    private final ListeningScheduledExecutorService _executorService;
    private final Provider<IApiController> _apiController;
    private final IBuildManager _buildManager;

    @Inject
    public ApiMonitoringService( final ListeningScheduledExecutorService executorService, final Provider<IApiController> apiController, final IBuildManager buildManager ) {
        _executorService = executorService;
        _apiController = apiController;
        _buildManager = buildManager;
    }

    @Override
    public void start() {
        _executorService.scheduleWithFixedDelay( checkIdleBuildStatus(), 30, 30, TimeUnit.SECONDS );
        _executorService.scheduleWithFixedDelay( checkRunningBuildStatus(), 30, 10, TimeUnit.SECONDS );
        LOGGER.info( "Monitoring service started." );
    }

    private Runnable checkIdleBuildStatus() {
        return () -> {
            final List<BuildTypeData> monitoredBuilds = _buildManager.getBuildTypeList().stream()
                    .filter( BuildTypeData::isSelected )
                    .filter( b -> !b.hasRunningBuild() )
                    .collect( Collectors.toList() );

            for ( final BuildTypeData buildType : monitoredBuilds )
                _apiController.get().requestLastBuildStatus( buildType );
        };
    }

    private Runnable checkRunningBuildStatus() {
        return () -> {
            final List<BuildTypeData> monitoredBuilds = _buildManager.getBuildTypeList().stream()
                    .filter( BuildTypeData::isSelected )
                    .filter( BuildTypeData::hasRunningBuild )
                    .collect( Collectors.toList() );

            for ( final BuildTypeData buildType : monitoredBuilds )
                _apiController.get().requestLastBuildStatus( buildType );
        };
    }
}
