package utils.teamcity.model.build;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import utils.teamcity.model.configuration.Configuration;
import utils.teamcity.model.configuration.SavedBuildData;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
final class BuildDataManager implements IBuildManager {

    private final Collection<BuildTypeData> _buildTypes = Lists.newArrayList( );

    @Inject
    BuildDataManager( final Configuration configuration ) {
        for ( final SavedBuildData savedData : configuration.getSavedBuilds( ) ) {
            final BuildTypeData data = new BuildTypeData( savedData.getId( ), savedData.getName( ), savedData.getProjectName( ) );
            data.setAliasName( savedData.getAliasName( ) );
            data.setSelected( true );
            _buildTypes.add( data );
        }
    }

    @Override
    public Optional<BuildTypeData> getBuild( final String id ) {
        return _buildTypes.stream( )
                .filter( input -> input.getId( ).equals( id ) )
                .findFirst( );
    }

    @Override
    public void registerBuildTypes( final List<BuildTypeData> typeList ) {
        final Set<String> typeIds = typeList.stream( ).map( BuildTypeData::getId ).collect( Collectors.toSet( ) );

        // On supprime tous les builds qui ne sont plus connus
        _buildTypes.removeIf( ( btdata ) -> !typeIds.contains( btdata.getId( ) ) );

        for ( final BuildTypeData btype : typeList ) {
            final Optional<BuildTypeData> previousData = getBuild( btype.getId( ) );
            if ( previousData.isPresent( ) ) {
                previousData.get( ).setName( btype.getName( ) );
                previousData.get( ).setProjectName( btype.getProjectName( ) );
            } else {
                _buildTypes.add( new BuildTypeData( btype.getId( ), btype.getName( ), btype.getProjectName( ) ) );
            }
        }
    }

    @Override
    public void registerBuildTypesInQueue( final List<String> buildTypesIdInQueue ) {
        // Setting all queued property to false
        _buildTypes.stream( ).forEach( buildTypeData -> {
            buildTypeData.queuedProperty( ).setValue( false );
        } );
        // Setting queued property for builds in queue
        for ( final String buildTypeId : buildTypesIdInQueue ) {
            final Optional<BuildTypeData> build = getBuild( buildTypeId );
            if ( !build.isPresent( ) )
                continue;
            build.get( ).queuedProperty( ).setValue( true );
        }
    }

    @Override
    public List<BuildTypeData> getBuildTypeList( ) {
        return ImmutableList.copyOf( _buildTypes );
    }
}
