package utils.teamcity.view.wall;

import com.google.common.collect.Iterables;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.max;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
final class WallView extends StackPane {

    public static final int GAP_SPACE = 10;

    private final WallViewModel _model;

    private Node _currentDisplayedScreen;

    @Inject
    WallView( final WallViewModel model ) {
        _model = model;
        setStyle( "-fx-background-color:black;" );

        final ObservableList<TileViewModel> builds = _model.getDisplayedBuilds( );
        builds.addListener( (ListChangeListener<TileViewModel>) c -> updateLayout( ) );

        _model.getMaxTilesByColumnProperty( ).addListener( ( o, oldValue, newalue ) -> updateLayout( ) );
        _model.getMaxTilesByRowProperty( ).addListener( ( o, oldValue, newalue ) -> updateLayout( ) );

        final Timer screenAnimationTimer = new Timer( "WallView Screen switcher", true );
        screenAnimationTimer.scheduleAtFixedRate( new TimerTask( ) {
            @Override
            public void run( ) {
                Platform.runLater( ( ) -> displayNextScreen( ) );
            }
        }, 10000, 10000 );
    }

    private void displayNextScreen( ) {
        if ( getChildren( ).isEmpty( ) )
            return;

        final Node previousScreen = _currentDisplayedScreen;

        final int index = previousScreen == null ? -1 : getChildren( ).indexOf( previousScreen );
        final int nextIndex = ( index == -1 ? 0 : index + 1 ) % getChildren( ).size( );

        final Node nextScreen = getChildren( ).get( nextIndex );

        nextScreen.setVisible( true );
        if ( previousScreen != null && previousScreen != nextScreen )
            previousScreen.setVisible( false );

        _currentDisplayedScreen = nextScreen;
    }

    private void updateLayout( ) {
        getChildren( ).clear( );

        final Collection<TileViewModel> builds = _model.getDisplayedBuilds( );

        final int maxTilesByColumn = _model.getMaxTilesByColumnProperty( ).get( );
        final int maxTilesByRow = _model.getMaxTilesByRowProperty( ).get( );

        final int maxByScreens = max( 1, maxTilesByColumn * maxTilesByRow );

        final int nbScreen = max( 1, builds.size( ) / maxByScreens + ( ( builds.size( ) % maxByScreens > 0 ? 1 : 0 ) ) );
        final int byScreen = max( 1, builds.size( ) / nbScreen + ( ( builds.size( ) % nbScreen > 0 ? 1 : 0 ) ) );

        final Iterable<List<TileViewModel>> screenPartition = Iterables.partition( builds, byScreen );

        final int nbColums = max( 1, byScreen / maxTilesByColumn + ( ( byScreen % maxTilesByColumn > 0 ? 1 : 0 ) ) );
        final int byColums = max( 1, byScreen / nbColums + ( ( byScreen % nbColums > 0 ? 1 : 0 ) ) );

        for ( final List<TileViewModel> buildsInScreen : screenPartition ) {
            final GridPane screenPane = buildScreenPane( buildsInScreen, nbColums, byColums );
            screenPane.setVisible( false );
            getChildren( ).add( screenPane );
        }
        displayNextScreen( );
    }

    private GridPane buildScreenPane( final Iterable<TileViewModel> buildsInScreen, final int nbColums, final int byColums ) {
        final GridPane screenPane = new GridPane( );
        screenPane.setHgap( GAP_SPACE );
        screenPane.setVgap( GAP_SPACE );
        screenPane.setPadding( new Insets( GAP_SPACE ) );
        screenPane.setStyle( "-fx-background-color:black;" );
        screenPane.setAlignment( Pos.CENTER );

        final Iterable<List<TileViewModel>> partition = Iterables.partition( buildsInScreen, byColums );
        int x = 0;
        int y = 0;
        for ( final List<TileViewModel> buildList : partition ) {
            for ( final TileViewModel build : buildList ) {
                createTileForBuildType( screenPane, build, x, y, nbColums, byColums );
                y++;
            }
            y = 0;
            x++;
        }
        return screenPane;
    }

    private void createTileForBuildType( final GridPane screenPane, final TileViewModel build, final int x, final int y, final int nbColumns, final int nbRows ) {
        final StackPane tile = new TileView( build );
        tile.prefWidthProperty( ).bind( widthProperty( ).add( -( nbColumns + 1 ) * GAP_SPACE ).divide( nbColumns ) );
        tile.prefHeightProperty( ).bind( heightProperty( ).add( -( nbRows + 1 ) * GAP_SPACE ).divide( nbRows ) );
        screenPane.add( tile, x, y );
    }


}
