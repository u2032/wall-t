package utils.teamcity.model.build;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import utils.teamcity.model.configuration.Configuration;
import utils.teamcity.model.configuration.SavedBuildData;

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
        for ( final SavedBuildData savedData : configuration.getSavedBuilds( ) ) {
            final BuildTypeData data = new BuildTypeData( savedData.getId( ), savedData.getName( ), savedData.getProjectName( ) );
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
        final Set<String> typeIds = typeList.stream( ).map( BuildTypeData::getId ).collect( Collectors.toSet( ) );

        // Deleting all builds which no more exist
        _buildTypes.removeIf( ( btdata ) -> !typeIds.contains( btdata.getId( ) ) );

        for ( final BuildTypeData btype : typeList ) {
            final Optional<BuildTypeData> previousData = getBuild( btype.getId( ) );
            if ( !previousData.isPresent( ) ) {
                // Adding new build
                _buildTypes.add( new BuildTypeData( btype.getId( ), btype.getName( ), btype.getProjectName( ) ) );
            }
        }
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
