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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Sets.newEnumSet;
import static java.util.Arrays.asList;

/**
 * Date: 23/02/14
 *
 * @author Cedric Longo
 */
public final class ProjectData {

    private final String _id;
    private final String _name;
    private final Optional<String> _parentId;

    private final List<BuildTypeData> _buildTypes = Lists.newArrayList( );
    private String _aliasName;

    public ProjectData( final String id, final String name, final Optional<String> parentId ) {
        _id = id;
        _name = name;
        _parentId = parentId;
    }

    public String getId( ) {
        return _id;
    }

    public String getName( ) {
        return _name;
    }

    public Optional<String> getParentId( ) {
        return _parentId;
    }

    public String getAliasName( ) {
        return _aliasName;
    }

    public void setAliasName( final String aliasName ) {
        _aliasName = aliasName;
    }

    public synchronized void registerBuildType( final BuildTypeData buildTypeData ) {
        _buildTypes.removeIf( bt -> bt.getId( ).equals( buildTypeData.getId( ) ) );
        _buildTypes.add( buildTypeData );
    }

    public synchronized List<BuildTypeData> getBuildTypes( ) {
        return ImmutableList.copyOf( _buildTypes );
    }

    public int getBuildTypeCount( final BuildStatus... statusForLastFinished ) {
        final List<BuildStatus> keptStatus = asList( statusForLastFinished );
        return (int) getBuildTypes( ).stream( )
                .filter( bt -> {
                    final Optional<BuildData> lastBuild = bt.getLastBuild( BuildState.finished );
                    return lastBuild.isPresent( ) && keptStatus.contains( lastBuild.get( ).getStatus( ) );
                } )
                .count( );
    }

    public boolean hasBuildTypeRunning( final BuildStatus... statusForLastFinished ) {
        final Set<BuildStatus> keptStatus = newEnumSet( asList( statusForLastFinished ), BuildStatus.class );
        return getBuildTypes( ).stream( )
                .anyMatch( bt -> {
                    if ( !bt.hasRunningBuild( ) )
                        return false;
                    final Optional<BuildData> lastBuild = bt.getLastBuild( BuildState.finished );
                    return lastBuild.isPresent( ) && keptStatus.contains( lastBuild.get( ).getStatus( ) );
                } );
    }
}
