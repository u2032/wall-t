package utils.teamcity.controller.api.json;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.controller.api.IApiController;
import utils.teamcity.controller.api.IApiRequestController;
import utils.teamcity.model.build.IBuildManager;
import utils.teamcity.model.logger.Loggers;

import java.util.concurrent.ExecutorService;

/**
 * Date: 22/02/14
 *
 * @author Cedric Longo
 */
public abstract class ApiControllerBase implements IApiController {

    protected static final int MAX_BUILDS_TO_CONSIDER = 5;

    private static final Logger LOGGER = LoggerFactory.getLogger( Loggers.MAIN );
    private final IBuildManager _buildManager;
    private final EventBus _eventBus;
    private final ExecutorService _executorService;
    private final IApiRequestController _apiRequestController;


    protected ApiControllerBase( final IBuildManager buildManager, final IApiRequestController apiRequestController, final EventBus eventBus, final ExecutorService executorService ) {
        _apiRequestController = apiRequestController;
        _buildManager = buildManager;
        _eventBus = eventBus;
        _executorService = executorService;
    }

    protected IApiRequestController getApiRequestController( ) {
        return _apiRequestController;
    }

    protected EventBus getEventBus( ) {
        return _eventBus;
    }

    protected IBuildManager getBuildManager( ) {
        return _buildManager;
    }

    protected Logger getLogger( ) {
        return LOGGER;
    }

    protected void runInWorkerThread( final Runnable callable ) {
        _executorService.submit( callable );
    }

}
