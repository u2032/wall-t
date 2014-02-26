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

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.reverse;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class BuildTypeData {

    private static final int MAX_BUILD_SIZE_TO_CACHE = 10;

    private final LinkedList<BuildData> _lastBuilds = Lists.newLinkedList( );

    private final String _id;
    private final String _name;
    private final String _projectId;
    private final String _projectName;

    private String _aliasName;

    private boolean _queued;
    private Instant _lastDataReceived = Instant.now( );

    public BuildTypeData( final String id, final String name, final String projectId, final String projectName ) {
        _id = id;
        _name = name;
        _projectId = projectId;
        _projectName = projectName;
    }

    public String getId( ) {
        return _id;
    }

    public String getName( ) {
        return _name;
    }

    public String getProjectName( ) {
        return _projectName;
    }

    public String getProjectId( ) {
        return _projectId;
    }

    public String getAliasName( ) {
        return _aliasName;
    }

    public void setAliasName( final String aliasName ) {
        _aliasName = aliasName;
    }


    public boolean hasRunningBuild( ) {
        return getLastBuild( BuildState.running ).isPresent( );
    }

    public Optional<BuildData> getLastBuild( final BuildState state ) {
        return getBuilds( ).stream( )
                .filter( build -> build.getState( ) == state )
                .findFirst( );
    }

    public Optional<BuildData> getOldestBuild( final BuildState state ) {
        return reverse( getBuilds( ) ).stream( )
                .filter( build -> build.getState( ) == state )
                .findFirst( );
    }


    public final Optional<BuildData> getBuildById( final int id ) {
        return getBuilds( ).stream( ).filter( b -> b.getId( ) == id ).findFirst( );
    }

    public synchronized boolean isQueued( ) {
        return _queued;
    }

    public synchronized void setQueued( final boolean queued ) {
        _queued = queued;
    }

    public synchronized void touch( ) {
        _lastDataReceived = Instant.now( );
    }

    public synchronized boolean clearIfOutdated( final Instant cut ) {
        if ( _lastDataReceived.isBefore( cut ) ) {
            _queued = false;
            _lastBuilds.clear( );
            return true;
        }
        return false;
    }

    public synchronized void registerBuild( final BuildData build ) {
        _lastBuilds.removeIf( ( b -> b.getId( ) == build.getId( ) ) );

        _lastBuilds.addFirst( build );
        _lastBuilds.sort( ( o1, o2 ) -> -Integer.compare( o1.getId( ), o2.getId( ) ) );
        if ( _lastBuilds.size( ) > MAX_BUILD_SIZE_TO_CACHE )
            _lastBuilds.removeLast( );
    }


    public synchronized List<BuildData> getBuilds( ) {
        return ImmutableList.copyOf( _lastBuilds );
    }


}
