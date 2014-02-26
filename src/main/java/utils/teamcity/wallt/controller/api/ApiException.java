package utils.teamcity.wallt.controller.api;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class ApiException extends RuntimeException {

    public ApiException( final String message ) {
        super( message );
    }
}
