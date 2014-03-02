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
import utils.teamcity.wallt.model.configuration.SavedProjectData;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Math.min;

/**
 * Date: 23/02/14
 *
 * @author Cedric Longo
 */
final class ProjectManager implements IProjectManager {

    private final List<ProjectData> _projects = Lists.newArrayList( );
    private final List<ProjectData> _monitoredProjects = Lists.newArrayList( );

    @Inject
    ProjectManager( final Configuration configuration ) {
        for ( final SavedProjectData savedData : configuration.getSavedProjects( ) ) {
            final ProjectData data = new ProjectData( savedData.getId( ), savedData.getName( ), Optional.ofNullable( savedData.getParentId( ) ) );
            data.setAliasName( savedData.getAliasName( ) );
            _projects.add( data );
            activateMonitoring( data );
        }
    }

    @Override
    public synchronized void registerProjects( final List<ProjectData> projects ) {
        final List<ProjectData> previousMonitored = _monitoredProjects.stream( ).collect( Collectors.toList( ) );
        final List<String> previousMonitoredIds = previousMonitored.stream( ).map( ProjectData::getId ).collect( Collectors.toList( ) );

        _projects.clear( );
        _monitoredProjects.clear( );

        _projects.addAll( projects );

        final List<ProjectData> monitoredBuildTypes = _projects.stream( )
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

        _monitoredProjects.addAll( monitoredBuildTypes );
    }


    @Override
    public synchronized List<ProjectData> getProjects( ) {
        return ImmutableList.copyOf( _projects );
    }

    @Override
    public synchronized List<ProjectData> getMonitoredProjects( ) {
        return ImmutableList.copyOf( _monitoredProjects );
    }

    @Override
    public Optional<ProjectData> getProject( final String id ) {
        return getProjects( ).stream( )
                .filter( input -> input.getId( ).equals( id ) )
                .findFirst( );
    }

    @Override
    public synchronized void activateMonitoring( final ProjectData projectData ) {
        if ( !_monitoredProjects.contains( projectData ) )
            _monitoredProjects.add( projectData );
    }

    @Override
    public synchronized void unactivateMonitoring( final ProjectData projectData ) {
        _monitoredProjects.remove( projectData );
    }


    @Override
    public int getPosition( final ProjectData data ) {
        final int index = getMonitoredProjects( ).indexOf( data );
        return index < 0 ? Integer.MAX_VALUE : index + 1;
    }

    @Override
    public synchronized void requestPosition( final ProjectData data, final int position ) {
        final int index = _monitoredProjects.indexOf( data );
        if ( index != -1 )
            _monitoredProjects.remove( index );
        _monitoredProjects.add( min( position - 1, _monitoredProjects.size( ) ), data );
    }

    @Override
    public List<ProjectData> getAllChildrenOf( final ProjectData data ) {
        final ImmutableList.Builder<ProjectData> builder = ImmutableList.builder( );

        final List<ProjectData> directChildren = getProjects( ).stream( ).filter( p -> p.getParentId( ).isPresent( ) && data.getId( ).equals( p.getParentId( ).get( ) ) ).collect( Collectors.toList( ) );
        builder.addAll( directChildren );

        for ( final ProjectData child : directChildren ) {
            builder.addAll( getAllChildrenOf( child ) );
        }

        return builder.build( );
    }


}
