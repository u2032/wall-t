/*******************************************************************************
 * Copyright 2014 Cedric Longo.
 *
 * This file is part of Wall-T program.
 *
 * Wall-T is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Wall-T is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Wall-T.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package utils.teamcity.wallt.controller.configuration;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import org.slf4j.LoggerFactory;
import utils.teamcity.wallt.model.configuration.Configuration;
import utils.teamcity.wallt.model.logger.Loggers;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Date: 09/02/14
 *
 * @author Cedric Longo
 */
public final class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( IConfigurationController.class ).to( ConfigurationController.class ).in( Scopes.SINGLETON );
    }

    @Provides
    @Singleton
    Configuration loadConfiguration( ) {
        final Path configFilePath = Paths.get( "config.json" );
        try ( FileReader reader = new FileReader( configFilePath.toFile( ) ) ) {
            final Gson gson = new Gson( );
            return gson.fromJson( reader, Configuration.class );
        } catch ( IOException ignored ) {
            LoggerFactory.getLogger( Loggers.MAIN ).warn( "No configuration file found: starting with empty configuration" );
            return new Configuration( );
        }
    }
}
