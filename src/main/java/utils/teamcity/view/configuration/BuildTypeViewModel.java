package utils.teamcity.view.configuration;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;
import javafx.beans.property.*;
import utils.teamcity.model.build.BuildTypeData;
import utils.teamcity.model.build.IBuildManager;

import javax.inject.Inject;

/**
 * Date: 22/02/14
 *
 * @author Cedric Longo
 */
final class BuildTypeViewModel implements IPositionable {

    private final StringProperty _id = new SimpleStringProperty( );
    private final StringProperty _projectName = new SimpleStringProperty( );
    private final StringProperty _name = new SimpleStringProperty( );
    private final StringProperty _aliasName = new SimpleStringProperty( );
    private final IntegerProperty _position = new SimpleIntegerProperty( );
    private final BooleanProperty _selected = new SimpleBooleanProperty( );

    interface Factory {
        BuildTypeViewModel fromBuildTypeData( final BuildTypeData data );
    }

    @Inject
    BuildTypeViewModel( final IBuildManager buildManager, final EventBus eventBus, @Assisted final BuildTypeData data ) {
        _id.setValue( data.getId( ) );
        _projectName.setValue( data.getProjectName( ) );
        _name.setValue( data.getName( ) );

        _aliasName.setValue( data.getAliasName( ) );
        _aliasName.addListener( ( o, oldValue, newValue ) -> data.setAliasName( newValue ) );

        _selected.setValue( buildManager.getMonitoredBuildTypes( ).contains( data ) );
        _selected.addListener( ( o, oldValue, newValue ) -> {
            if ( newValue )
                buildManager.activateMonitoring( data );
            else
                buildManager.unactivateMonitoring( data );
            eventBus.post( buildManager );
        } );

        _position.setValue( buildManager.getPosition( data ) );
        _position.addListener( ( o, oldValue, newValue ) -> {
            final int position = (int) newValue;
            if ( position > 0 ) {
                buildManager.requestPosition( data, position );
            }
            eventBus.post( buildManager );
        } );
    }

    String getId( ) {
        return _id.get( );
    }

    StringProperty idProperty( ) {
        return _id;
    }

    String getProjectName( ) {
        return _projectName.get( );
    }

    StringProperty projectNameProperty( ) {
        return _projectName;
    }

    String getName( ) {
        return _name.get( );
    }

    StringProperty nameProperty( ) {
        return _name;
    }

    String getAliasName( ) {
        return _aliasName.get( );
    }

    StringProperty aliasNameProperty( ) {
        return _aliasName;
    }

    void setAliasName( final String aliasName ) {
        _aliasName.set( aliasName );
    }

    boolean isSelected( ) {
        return _selected.get( );
    }

    BooleanProperty selectedProperty( ) {
        return _selected;
    }

    void setSelected( final boolean selected ) {
        _selected.set( selected );
    }

    @Override
    public int getPosition( ) {
        return _position.get( );
    }

    IntegerProperty positionProperty( ) {
        return _position;
    }

    @Override
    public void setPosition( final int position ) {
        _position.set( position );
    }
}
