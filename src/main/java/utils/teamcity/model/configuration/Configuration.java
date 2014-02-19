package utils.teamcity.model.configuration;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import utils.teamcity.controller.api.json.ApiVersion;

import java.util.List;

/**
 * Date: 09/02/14
 *
 * @author Cedric Longo
 */
public final class Configuration {

    @SerializedName("server_url")
    private String _serverUrl;

    @SerializedName("credentials_user")
    private String _credentialsUser = "guest";

    @SerializedName("credentials_password")
    private String _credentialsPassword;

    @SerializedName("api_version")
    private ApiVersion _apiVersion = ApiVersion.API_8_0;

    @SerializedName( "pref_max_rows_by_column" )
    private int _maxRowsByColumn = 4;

    @SerializedName("monitored_builds")
    private List<SavedBuildData> _savedBuilds = Lists.newArrayList( );

    public String getServerUrl( ) {
        return _serverUrl;
    }

    public void setServerUrl( final String serverUrl ) {
        _serverUrl = serverUrl;
    }

    public String getCredentialsUser( ) {
        return _credentialsUser;
    }

    public void setCredentialsUser( final String credentialsUser ) {
        _credentialsUser = credentialsUser;
    }

    public String getCredentialsPassword( ) {
        return _credentialsPassword;
    }

    public void setCredentialsPassword( final String credentialsPassword ) {
        _credentialsPassword = credentialsPassword;
    }

    public List<SavedBuildData> getSavedBuilds( ) {
        return _savedBuilds;
    }

    public void setSavedBuilds( final List<SavedBuildData> savedBuilds ) {
        _savedBuilds = savedBuilds;
    }

    public ApiVersion getApiVersion( ) {
        return _apiVersion;
    }

    public int getMaxRowsByColumn( ) {
        return _maxRowsByColumn;
    }

    public void setMaxRowsByColumn( final int maxRowsByColumn ) {
        _maxRowsByColumn = maxRowsByColumn;
    }

    public void setApiVersion( final ApiVersion apiVersion ) {
        _apiVersion = apiVersion;
    }
}
