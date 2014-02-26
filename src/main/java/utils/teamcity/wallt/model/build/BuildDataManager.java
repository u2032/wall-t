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
        final List<String> previousMonitored = _monitoredBuildTypes.stream( ).map( BuildTypeData::getId ).collect( Collectors.toList( ) );

        _buildTypes.clear( );
        _monitoredBuildTypes.clear( );

        _buildTypes.addAll( typeList );

        final List<BuildTypeData> monitoredBuildTypes = _buildTypes.stream( )
                .filter( ( t ) -> previousMonitored.contains( t.getId( ) ) )
                .sorted( ( o1, o2 ) -> Integer.compare( previousMonitored.indexOf( o1.getId( ) ), previousMonitored.indexOf( o2.getId( ) ) ) )
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
