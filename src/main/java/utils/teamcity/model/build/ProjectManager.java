package utils.teamcity.model.build;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import utils.teamcity.model.configuration.Configuration;
import utils.teamcity.model.configuration.SavedProjectData;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.min;

/**
 * Date: 23/02/14
 *
 * @author Cedric Longo
 */
public final class ProjectManager implements IProjectManager {

    private final List<ProjectData> _projects = Lists.newArrayList( );
    private final List<ProjectData> _monitoredProjects = Lists.newArrayList( );


    @Inject
    public ProjectManager( final Configuration configuration ) {
        for ( final SavedProjectData savedData : configuration.getSavedProjects( ) ) {
            final ProjectData data = new ProjectData( savedData.getId( ), savedData.getName( ) );
            data.setAliasName( savedData.getAliasName( ) );
            _projects.add( data );
            activateMonitoring( data );
        }
    }

    @Override
    public synchronized void registerProjects( final List<ProjectData> projects ) {
        final Set<String> typeIds = projects.stream( ).map( ProjectData::getId ).collect( Collectors.toSet( ) );

        // Deleting all builds which no more exist
        _projects.removeIf( ( btdata ) -> !typeIds.contains( btdata.getId( ) ) );

        for ( final ProjectData project : projects ) {
            final Optional<ProjectData> previousData = getProject( project.getId( ) );
            if ( !previousData.isPresent( ) ) {
                // Adding new build
                _projects.add( project );
            }
        }
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


}
