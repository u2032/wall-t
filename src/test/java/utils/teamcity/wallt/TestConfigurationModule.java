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

package utils.teamcity.wallt;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import utils.teamcity.wallt.controller.configuration.ConfigurationController;
import utils.teamcity.wallt.controller.configuration.IConfigurationController;
import utils.teamcity.wallt.model.configuration.Configuration;

/**
 * Date: 02/03/14
 *
 * @author Cedric Longo
 */
public final class TestConfigurationModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( IConfigurationController.class ).to( ConfigurationController.class ).in( Scopes.SINGLETON );
    }

    @Provides
    @Singleton
    Configuration loadConfiguration( ) {
        return new Configuration( );
    }

}
