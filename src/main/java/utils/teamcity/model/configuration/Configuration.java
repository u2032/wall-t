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

    @SerializedName("server.url")
    private String _serverUrl;

    @SerializedName("credentials.user")
    private String _credentialsUser = "guest";

    @SerializedName("credentials.password")
    private String _credentialsPassword;

    @SerializedName("api.version")
    private ApiVersion _apiVersion = ApiVersion.API_8_0;

    @SerializedName("pref.max.tiles.by.column")
    private int _maxRowsByColumn = 4;

    @SerializedName( "pref.max.tiles.by.screen" )
    private int _maxTilesByScreen = 16;

    @SerializedName( "pref.light.mode" )
    private boolean _lightMode;

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

    public int getMaxTilesByScreen( ) {
        return _maxTilesByScreen;
    }

    public void setMaxTilesByScreen( final int maxTilesByScreen ) {
        _maxTilesByScreen = maxTilesByScreen;
    }

    public boolean isLightMode( ) {
        return _lightMode;
    }

    public void setLightMode( final boolean lightMode ) {
        _lightMode = lightMode;
    }
}
