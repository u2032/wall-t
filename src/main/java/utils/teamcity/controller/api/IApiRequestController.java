package utils.teamcity.controller.api;

import com.google.common.util.concurrent.ListenableFuture;
import utils.teamcity.controller.api.json.ApiVersion;

/**
 * Date: 17/02/14
 *
 * @author Cedric Longo
 */
public interface IApiRequestController {

    <T extends ApiResponse> ListenableFuture<T> sendRequest( final ApiVersion version, final String path, final Class<T> expectedType );

}
