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

package utils.teamcity.wallt.model.build;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import utils.teamcity.wallt.model.configuration.Configuration;
import utils.teamcity.wallt.model.configuration.SavedBuildTypeData;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.min;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
final class BuildDataManager implements IBuildManager {

    private final List<BuildTypeData> _buildTypes = Lists.newArrayList( );
    private final List<BuildTypeData> _monitoredBuildTypes = Lists.newArrayList( );

    @Inject
    BuildDataManager( final Configuration configuration ) {
        for ( final SavedBuildTypeData savedData : configuration.getSavedBuilds( ) ) {
            final BuildTypeData data = new BuildTypeData( savedData.getId( ), savedData.getName( ), savedData.getProjectId( ), savedData.getProjectName( ) );
            data.setAliasName( savedData.getAliasName( ) );
            _buildTypes.add( data );
            activateMonitoring( data );
        }
    }

    @Override
    public Optional<BuildTypeData> getBuild( final String id ) {
        return getBuildTypes( ).stream( )
                .filter( input -> input.getId( ).equals( id ) )
                .findFirst( );
    }

    @Override
    public synchronized void registerBuildTypes( final List<BuildTypeData> typeList ) {
        final List<BuildTypeData> previousMonitored = _monitoredBuildTypes.stream( ).collect( Collectors.toList( ) );
        final List<String> previousMonitoredIds = previousMonitored.stream( ).map( BuildTypeData::getId ).collect( Collectors.toList( ) );

        _buildTypes.clear( );
        _monitoredBuildTypes.clear( );

        _buildTypes.addAll( typeList );

        final List<BuildTypeData> monitoredBuildTypes = _buildTypes.stream( )
                .filter( ( t ) -> previousMonitoredIds.contains( t.getId( ) ) )
                .sorted( ( o1, o2 ) -> Integer.compare( previousMonitoredIds.indexOf( o1.getId( ) ), previousMonitoredIds.indexOf( o2.getId( ) ) ) )
                .map( ( bt ) -> {
                    bt.setAliasName( previousMonitored.stream( )
                            .filter( ( t ) -> t.getId( ).equals( bt.getId( ) ) )
                            .findFirst( ).get( ).getAliasName( )
                    );
                    return bt;
                } )
                .collect( Collectors.toList( ) );

        _monitoredBuildTypes.addAll( monitoredBuildTypes );
    }

    @Override
    public List<BuildTypeData> registerBuildTypesInQueue( final Set<String> buildTypesIdInQueue ) {
        final List<BuildTypeData> modifiedQueuedStatusBuilds = Lists.newLinkedList( );

        for ( final BuildTypeData build : getMonitoredBuildTypes( ) ) {
            final boolean isNowInQueue = buildTypesIdInQueue.contains( build.getId( ) );
            if ( build.isQueued( ) != isNowInQueue ) {
                build.setQueued( isNowInQueue );
                modifiedQueuedStatusBuilds.add( build );
            }
        }

        return modifiedQueuedStatusBuilds;
    }

    @Override
    public synchronized void activateMonitoring( final BuildTypeData buildTypeData ) {
        _monitoredBuildTypes.add( buildTypeData );
    }

    @Override
    public synchronized void unactivateMonitoring( final BuildTypeData buildTypeData ) {
        _monitoredBuildTypes.remove( buildTypeData );
    }

    @Override
    public int getPosition( final BuildTypeData data ) {
        final int index = getMonitoredBuildTypes( ).indexOf( data );
        return index < 0 ? Integer.MAX_VALUE : index + 1;
    }

    @Override
    public synchronized void requestPosition( final BuildTypeData data, final int position ) {
        final int index = _monitoredBuildTypes.indexOf( data );
        if ( index != -1 )
            _monitoredBuildTypes.remove( index );
        _monitoredBuildTypes.add( min( position - 1, _monitoredBuildTypes.size( ) ), data );
    }

    @Override
    public synchronized List<BuildTypeData> getBuildTypes( ) {
        return ImmutableList.copyOf( _buildTypes );
    }

    @Override
    public synchronized List<BuildTypeData> getMonitoredBuildTypes( ) {
        return ImmutableList.copyOf( _monitoredBuildTypes );
    }
}
