package utils.teamcity.controller.api;

import utils.teamcity.controller.api.json.ApiVersion;
import utils.teamcity.model.configuration.Configuration;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Date: 17/02/14
 *
 * @author Cedric Longo
 */
public class ApiControllerProvider implements Provider<IApiController> {

    private final Configuration _configuration;
    private final Map<ApiVersion, IApiController> _controllerByVersion;

    @Inject
    public ApiControllerProvider( final Configuration configuration, final Map<ApiVersion, IApiController> controllerByVersion ) {
        _configuration = configuration;
        _controllerByVersion = controllerByVersion;
    }

    @Override
    public IApiController get() {
        return checkNotNull( _controllerByVersion.get( _configuration.getApiVersion() ), "No controller defined for API version %s", _configuration.getApiVersion() );
    }
}
