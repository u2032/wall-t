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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
public final class ThreadingModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( ExecutorService.class ).toInstance( newCachedThreadPool( ) );
        bind( ScheduledExecutorService.class ).toInstance( newScheduledThreadPool( Runtime.getRuntime( ).availableProcessors( ) ) );
    }

}
