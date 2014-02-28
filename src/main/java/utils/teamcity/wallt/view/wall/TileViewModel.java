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

package utils.teamcity.wallt.view.wall;

import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.google.inject.assistedinject.Assisted;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import utils.teamcity.wallt.model.build.BuildData;
import utils.teamcity.wallt.model.build.BuildState;
import utils.teamcity.wallt.model.build.BuildStatus;
import utils.teamcity.wallt.model.build.BuildTypeData;
import utils.teamcity.wallt.model.configuration.Configuration;

import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static utils.teamcity.wallt.model.build.BuildStatus.SUCCESS;
import static utils.teamcity.wallt.view.wall.BuildImage.*;

/**
 * Date: 22/02/14
 *
 * @author Cedric Longo
 */
final class TileViewModel {

    private final BuildTypeData _buildTypeData;

    private final IntegerProperty _percentageComplete = new SimpleIntegerProperty( );
    private final BooleanProperty _running = new SimpleBooleanProperty( );
    private final BooleanProperty _queued = new SimpleBooleanProperty( );
    private final ObjectProperty<LocalDateTime> _lastFinishedDate = new SimpleObjectProperty<>( );
    private final ObjectProperty<Duration> _timeLeft = new SimpleObjectProperty<>( Duration.ZERO );
    private final StringProperty _displayedName = new SimpleStringProperty( );
    private final ObjectProperty<Image> _image = new SimpleObjectProperty<>( );
    private final ObjectProperty<Background> _background = new SimpleObjectProperty<>( );
    private final ObjectProperty<Background> _runningBackground = new SimpleObjectProperty<>( );

    private final BooleanProperty _lightMode = new SimpleBooleanProperty( );

    interface Factory {
        TileViewModel forBuildTypeData( final BuildTypeData buildTypeData );
    }

    @Inject
    TileViewModel( final Configuration configuration, @Assisted final BuildTypeData buildTypeData ) {
        _buildTypeData = buildTypeData;
        updateConfiguration( configuration );
        updateTileViewModel( buildTypeData );
    }

    @Subscribe
    public final void updateTileViewModel( final BuildTypeData data ) {
        if ( data != _buildTypeData )
            return;

        Platform.runLater( ( ) -> {
            _displayedName.set( Strings.isNullOrEmpty( data.getAliasName( ) ) ? data.getName( ) : data.getAliasName( ) );
            _running.setValue( data.hasRunningBuild( ) );
            _queued.setValue( data.isQueued( ) );

            updateLastFinishedDate( );
            updateTimeLeft( );
            updatePercentageComplete( );
            updateBackground( );
            updateIcon( );
        } );
    }

    @Subscribe
    public void updateConfiguration( final Configuration configuration ) {
        Platform.runLater( ( ) -> {
            _lightMode.setValue( configuration.isLightMode( ) );
        } );
    }


    private void updateTimeLeft( ) {
        final Optional<BuildData> lastBuild = _buildTypeData.getOldestBuild( BuildState.running );
        _timeLeft.setValue( lastBuild.isPresent( ) ? lastBuild.get( ).getTimeLeft( ) : Duration.ZERO );
    }

    private void updateLastFinishedDate( ) {
        final Optional<BuildData> lastBuild = _buildTypeData.getLastBuild( BuildState.finished );
        if ( lastBuild.isPresent( ) )
            _lastFinishedDate.setValue( lastBuild.get( ).getFinishedDate( ).get( ) );
    }

    private void updatePercentageComplete( ) {
        final Optional<BuildData> lastBuildRunning = _buildTypeData.getOldestBuild( BuildState.running );
        if ( lastBuildRunning.isPresent( ) )
            _percentageComplete.setValue( lastBuildRunning.get( ).getPercentageComplete( ) );
    }

    private void updateBackground( ) {
        final Optional<BuildData> lastBuildFinished = _buildTypeData.getLastBuild( BuildState.finished );
        if ( !lastBuildFinished.isPresent( ) ) {
            _background.setValue( BuildBackground.UNKNOWN.getMain( ) );
            return;
        }

        // Setting main background according to last finished build
        _background.setValue( lastBuildFinished.get( ).getStatus( ) == SUCCESS ? BuildBackground.SUCCESS.getMain( ) : BuildBackground.FAILURE.getMain( ) );

        // Running background is set to failure if running build state is not success, or is set according to last finished build
        // We assume that status will be the same until this build is clearly in failure or definitive status is known
        final Optional<BuildData> lastBuildRunning = _buildTypeData.getOldestBuild( BuildState.running );
        if ( lastBuildRunning.isPresent( ) ) {
            if ( lastBuildRunning.get( ).getStatus( ) != SUCCESS )
                _runningBackground.setValue( BuildBackground.FAILURE.getRunnning( ) );
            else
                _runningBackground.setValue( lastBuildFinished.get( ).getStatus( ) == SUCCESS ? BuildBackground.SUCCESS.getRunnning( ) : BuildBackground.FAILURE.getRunnning( ) );
        } else
            _runningBackground.setValue( null );
    }

    private void updateIcon( ) {
        final List<BuildData> buildToConsider = _buildTypeData.getBuilds( ).stream( )
                .filter( build -> build.getState( ) == BuildState.finished )
                .filter( build -> build.getStatus( ) != BuildStatus.UNKNOWN )
                .limit( 3 )
                .collect( Collectors.toList( ) );

        if ( buildToConsider.isEmpty( ) ) {
            _image.setValue( null );
            return;
        }

        // Last build is failure
        final BuildData lastBuild = buildToConsider.stream( ).findFirst( ).get( );
        if ( lastBuild.getStatus( ) != SUCCESS ) {
            _image.setValue( RAIN.getImage( ) );
            return;
        }

        // Less than 3 finished builds : just take the last status
        if ( buildToConsider.size( ) < 3 ) {
            _image.setValue( ( lastBuild.getStatus( ) == SUCCESS ) ? SUN.getImage( ) : RAIN.getImage( ) );
            return;
        }

        // Display icon according to failure count in last 3 finished builds
        final int failureCount = (int) buildToConsider.stream( )
                .filter( build -> build.getStatus( ) != SUCCESS )
                .count( );
        switch ( failureCount ) {
            case 1:
                _image.setValue( CLOUDY_SUN.getImage( ) );
                break;
            case 2:
                _image.setValue( CLOUD.getImage( ) );
                break;
            case 3:
                _image.setValue( RAIN.getImage( ) );
                break;
            default:
                _image.setValue( SUN.getImage( ) );
                break;
        }
    }

    BuildTypeData getBuildTypeData( ) {
        return _buildTypeData;
    }

    int getPercentageComplete( ) {
        return _percentageComplete.get( );
    }

    IntegerProperty percentageCompleteProperty( ) {
        return _percentageComplete;
    }

    boolean isRunning( ) {
        return _running.get( );
    }

    BooleanProperty runningProperty( ) {
        return _running;
    }

    boolean getQueued( ) {
        return _queued.get( );
    }

    BooleanProperty queuedProperty( ) {
        return _queued;
    }

    LocalDateTime getLastFinishedDate( ) {
        return _lastFinishedDate.get( );
    }

    ObjectProperty<LocalDateTime> lastFinishedDateProperty( ) {
        return _lastFinishedDate;
    }

    Duration getTimeLeft( ) {
        return _timeLeft.get( );
    }

    ObjectProperty<Duration> timeLeftProperty( ) {
        return _timeLeft;
    }

    String getDisplayedName( ) {
        return _displayedName.get( );
    }

    StringProperty displayedNameProperty( ) {
        return _displayedName;
    }

    Image getImage( ) {
        return _image.get( );
    }

    ObjectProperty<Image> imageProperty( ) {
        return _image;
    }

    Background getBackground( ) {
        return _background.get( );
    }

    ObjectProperty<Background> backgroundProperty( ) {
        return _background;
    }

    Background getRunningBackground( ) {
        return _runningBackground.get( );
    }

    ObjectProperty<Background> runningBackgroundProperty( ) {
        return _runningBackground;
    }

    boolean isLightMode( ) {
        return _lightMode.get( );
    }

    BooleanProperty lightModeProperty( ) {
        return _lightMode;
    }

}
