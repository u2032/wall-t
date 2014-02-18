package utils.teamcity.model.build;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.reverse;
import static utils.teamcity.model.build.BuildImage.*;
import static utils.teamcity.model.build.BuildStatus.SUCCESS;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class BuildTypeData {

    private static final int MAX_BUILD_SIZE_TO_CACHE = 10;

    private final LinkedList<BuildData> _lastBuilds = Lists.newLinkedList( ) ;

    private final StringProperty _id = new SimpleStringProperty( ) ;
    private final StringProperty _name = new SimpleStringProperty( ) ;
    private final StringProperty _projectName = new SimpleStringProperty( ) ;
    private final StringProperty _aliasName = new SimpleStringProperty( ) ;
    private final BooleanProperty _selected = new SimpleBooleanProperty( ) ;
    private final IntegerProperty _percentageComplete = new SimpleIntegerProperty( ) ;
    private final BooleanProperty _running = new SimpleBooleanProperty( ) ;
    private final BooleanProperty _queued = new SimpleBooleanProperty( ) ;
    private final ObjectProperty<LocalDateTime> _lastFinishedDate = new SimpleObjectProperty<>(  );
    private final ObjectProperty<Duration> _timeLeft = new SimpleObjectProperty<>(  );

    // TODO Move this into view model
    private final StringProperty _displayedName = new SimpleStringProperty( ) ;
    private final ObjectProperty<Image> _image = new SimpleObjectProperty<>( ) ;
    private final ObjectProperty<Background> _background = new SimpleObjectProperty<>( ) ;
    private final ObjectProperty<Background> _runningBackground = new SimpleObjectProperty<>( ) ;

    public BuildTypeData( final String id, final String name, final String projectName ) {
        setId( id );
        setName( name );
        setProjectName( projectName );
        _displayedName.bind( Bindings.createStringBinding( ( )  -> Strings.isNullOrEmpty( _aliasName.get( )  ) ? _name.get( )  : _aliasName.get( ) , _aliasName, _name ) );
        updateState( ) ;
    }

    public String getId( )  {
        return _id.get( ) ;
    }

    private void setId( final String id ) {
        _id.set( id );
    }

    public StringProperty idProperty( )  {
        return _id;
    }

    public String getName( )  {
        return _name.get( ) ;
    }

    public void setName( final String name ) {
        Platform.runLater( ( )  -> _name.set( name ) );
    }

    public StringProperty nameProperty( )  {
        return _name;
    }

    public String getProjectName( )  {
        return _projectName.get( ) ;
    }

    public void setProjectName( final String projectName ) {
        Platform.runLater( ( )  -> _projectName.set( projectName ) );
    }

    public StringProperty projectNameProperty( )  {
        return _projectName;
    }

    public String getAliasName( )  {
        return _aliasName.get( ) ;
    }

    public void setAliasName( final String aliasName ) {
        _aliasName.set( aliasName );
    }

    public StringProperty aliasNameProperty( )  {
        return _aliasName;
    }

    public boolean isSelected( )  {
        return _selected.get( ) ;
    }

    public void setSelected( final boolean selected ) {
        _selected.set( selected );
    }

    public BooleanProperty selectedProperty( )  {
        return _selected;
    }

    public StringProperty displayedNameProperty( )  {
        return _displayedName;
    }

    public IntegerProperty percentageCompleteProperty( )  {
        return _percentageComplete;
    }

    public BooleanProperty runningProperty( )  {
        return _running;
    }

    public BooleanProperty queuedProperty() {
        return _queued;
    }

    public synchronized void registerBuild( final BuildData build ) {
        _lastBuilds.removeIf( ( b -> b.getId( )  == build.getId( )  ) );

        _lastBuilds.addFirst( build );
        _lastBuilds.sort( ( o1, o2 ) -> -Integer.compare( o1.getId( ) , o2.getId( )  ) );
        if ( _lastBuilds.size( )  > MAX_BUILD_SIZE_TO_CACHE )
            _lastBuilds.removeLast( ) ;

        Platform.runLater( this::updateState );
    }

    public synchronized boolean hasRunningBuild( )  {
        return getLastBuild( BuildState.running ).isPresent( ) ;
    }


    public ObjectProperty<LocalDateTime> lastFinishedDateProperty() {
        return _lastFinishedDate;
    }

    public ObjectProperty<Duration> timeLeftProperty() {
        return _timeLeft;
    }

    private Optional<BuildData> getLastBuild( final BuildState state ) {
        return _lastBuilds.stream( ) 
                .filter( build -> build.getState( )  == state )
                .findFirst( ) ;
    }

    private Optional<BuildData> getOldestBuild( final BuildState state ) {
        return reverse( _lastBuilds ).stream( ) 
                .filter( build -> build.getState( )  == state )
                .findFirst( ) ;
    }

    public synchronized void updateState( )  {
        _running.setValue( hasRunningBuild( )  );
        updateLastFinishedDate() ;
        updateTimeLeft();
        updatePercentageComplete( ) ;
        updateBackground( ) ;
        updateIcon( ) ;
    }

    private void updateTimeLeft() {
        final Optional<BuildData> lastBuild = getOldestBuild( BuildState.running );
        _timeLeft.setValue( lastBuild.isPresent( ) ? lastBuild.get( ).getTimeLeft( ) : Duration.ZERO );
    }

    private void updateLastFinishedDate( )  {
        final Optional<BuildData> lastBuild = getLastBuild( BuildState.finished );
        if ( lastBuild.isPresent( ) )
            _lastFinishedDate.setValue( lastBuild.get( ).getFinishedDate( ).get( )  );
    }

    private void updatePercentageComplete( )  {
        final Optional<BuildData> lastBuildRunning = getOldestBuild( BuildState.running );
        if ( lastBuildRunning.isPresent( )  )
            _percentageComplete.setValue( lastBuildRunning.get( ) .getPercentageComplete( )  );
    }

    private void updateBackground( )  {
        final Optional<BuildData> lastBuildFinished = getLastBuild( BuildState.finished );
        if ( !lastBuildFinished.isPresent( )  ) {
            _background.setValue( BuildBackground.UNKNOWN.getMain( )  );
            return;
        }

        // Setting main background according to last finished build
        _background.setValue( lastBuildFinished.get( ) .getStatus( )  == SUCCESS ? BuildBackground.SUCCESS.getMain( )  : BuildBackground.FAILURE.getMain( )  );

        // Running background is set to failure if running build state is not success, or is set according to last finished build
        // We assume that status will be the same until this build is clearly in failure or definitive status is known
        final Optional<BuildData> lastBuildRunning = getOldestBuild( BuildState.running );
        if ( lastBuildRunning.isPresent( )  ) {
            if ( lastBuildRunning.get( ) .getStatus( )  != SUCCESS )
                _runningBackground.setValue( BuildBackground.FAILURE.getRunnning( )  );
            else
                _runningBackground.setValue( lastBuildFinished.get( ) .getStatus( )  == SUCCESS ? BuildBackground.SUCCESS.getRunnning( )  : BuildBackground.FAILURE.getRunnning( )  );
        } else
            _runningBackground.setValue( null );
    }

    private void updateIcon( )  {
        final List<BuildData> buildToConsider = _lastBuilds.stream( ) 
                .filter( build -> build.getState( )  == BuildState.finished )
                .limit( 3 )
                .collect( Collectors.toList( )  );

        if ( buildToConsider.isEmpty( )  ) {
            _image.setValue( null );
            return;
        }

        // Last build is failure
        final BuildData lastBuild = buildToConsider.stream( ) .findFirst( ) .get( ) ;
        if ( lastBuild.getStatus( )  != SUCCESS ) {
            _image.setValue( RAIN.getImage( )  );
            return;
        }

        // Less than 3 finished builds : just take the last status
        if ( buildToConsider.size( )  < 3 ) {
            _image.setValue( ( lastBuild.getStatus( )  == SUCCESS ) ? SUN.getImage( )  : RAIN.getImage( )  );
            return;
        }

        // Display icon according to failure count in last 3 finished builds
        final int failureCount = (int) buildToConsider.stream( ) 
                .filter( build -> build.getStatus( )  != SUCCESS )
                .count( ) ;
        switch ( failureCount ) {
            case 1:
                _image.setValue( CLOUDY_SUN.getImage( )  );
                break;
            case 2:
                _image.setValue( CLOUD.getImage( )  );
                break;
            case 3:
                _image.setValue( RAIN.getImage( )  );
                break;
            default:
                _image.setValue( SUN.getImage( )  );
                break;
        }
    }

    public ObservableValue<Image> imageProperty( )  {
        return _image;
    }

    public ObjectProperty<Background> backgroundProperty( )  {
        return _background;
    }

    public ObjectProperty<Background> runningBackgroundProperty( )  {
        return _runningBackground;
    }

    public final synchronized Optional<BuildData> getBuildById( final int id ) {
        return _lastBuilds.stream( ) .filter( b -> b.getId( )  == id ).findFirst( ) ;
    }
}
