package utils.teamcity.view.wall;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.teamcity.model.build.BuildTypeData;
import utils.teamcity.model.logger.Loggers;
import utils.teamcity.view.UIUtils;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;
import static javafx.beans.binding.Bindings.createStringBinding;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class WallView extends GridPane {

    public static final int MAX_BY_COLUMN = 4;

    private final WallViewModel _model;
    private final Map<Node, FadeTransition> _registeredTransition = Maps.newHashMap( );

    @Inject
    public WallView( final WallViewModel model ) {
        _model = model;

        setHgap( 10 );
        setVgap( 10 );
        setPadding( new Insets( 10 ) );

        setStyle( "-fx-background-color:black;" );

        setAlignment( Pos.CENTER );

        final ObservableList<BuildTypeData> builds = _model.getBuilds( );
        final int nbColums = builds.size( ) / MAX_BY_COLUMN + ( ( builds.size( ) % MAX_BY_COLUMN > 0 ? 1 : 0 ) );
        final int byColums = builds.size( ) / nbColums + ( ( builds.size( ) % nbColums > 0 ? 1 : 0 ) );

        final Iterable<List<BuildTypeData>> partition = Iterables.partition( builds, byColums );
        int x = 0;
        int y = 0;
        for ( final List<BuildTypeData> buildList : partition ) {
            for ( final BuildTypeData build : buildList ) {
                createTileForBuildType( build, x, y );
                y++;
            }
            y = 0;
            x++;
        }
    }

    private void createTileForBuildType( final BuildTypeData build, final int x, final int y ) {
        final StackPane tile = new StackPane( );
        tile.setAlignment( Pos.CENTER_LEFT );
        tile.setStyle( "-fx-border-color:white; -fx-border-radius:5;" );
        tile.backgroundProperty( ).bind( build.backgroundProperty( ) );

        tile.prefWidthProperty( ).bind( widthProperty( ) );
        tile.prefHeightProperty( ).bind( heightProperty( ) );

        final Pane progressPane = new Pane( );
        progressPane.backgroundProperty( ).bind( build.runningBackgroundProperty( ) );
        progressPane.minWidthProperty( ).bind( tile.widthProperty( ).multiply( build.percentageCompleteProperty( ) ).divide( 100 ) );
        progressPane.maxWidthProperty( ).bind( progressPane.minWidthProperty( ) );
        progressPane.visibleProperty( ).bind( build.runningProperty( ) );
        build.runningProperty( ).addListener( ( o, oldVallue, newValue ) -> {
            if ( newValue )
                startAnimationOnNode( tile );
            else
                stopAnimationOnNode( tile );
        } );

        final HBox tileContent = new HBox( );
        tileContent.setAlignment( Pos.CENTER_LEFT );
        tileContent.setSpacing( 10 );

        final Label tileTitle = new Label( );
        tileTitle.setStyle( "-fx-font-weight:bold; -fx-text-fill:white; -fx-font-size:50px;" );
        tileTitle.setPadding( new Insets( 5 ) );
        tileTitle.setWrapText( true );
        tileTitle.textProperty( ).bind( build.displayedNameProperty( ) );
        tileTitle.prefWidthProperty( ).bind( tile.widthProperty( ) );
        tileTitle.prefHeightProperty( ).bind( tile.heightProperty( ) );
        HBox.setHgrow( tileTitle, Priority.SOMETIMES );

        final VBox contextPart = createContextPart( build );
        tileContent.getChildren( ).addAll( tileTitle, contextPart );

        tile.getChildren( ).addAll( progressPane, tileContent );
        add( tile, x, y );
    }

    private VBox createContextPart( final BuildTypeData build ) {
        final VBox contextPart = new VBox( );
        contextPart.setMinWidth( 150 );
        contextPart.setMaxWidth( 150 );
        contextPart.setAlignment( Pos.CENTER );

        final HBox statusBox = new HBox( );
        statusBox.setAlignment( Pos.CENTER );
        statusBox.setSpacing( 5 );

        final ImageView queuedIcon = new ImageView( UIUtils.createImage( "queued.png" ) );
        queuedIcon.setPreserveRatio( true );
        queuedIcon.setFitWidth( 50 );
        queuedIcon.visibleProperty( ).bind( build.queuedProperty( ) );

        final RotateTransition transition = new RotateTransition( Duration.seconds( 3 ), queuedIcon );
        transition.setByAngle( 360 );
        transition.setCycleCount( Timeline.INDEFINITE );
        transition.play( );

        final ImageView image = new ImageView( );
        image.setPreserveRatio( true );
        image.setFitWidth( 90 );
        image.imageProperty( ).bind( build.imageProperty( ) );
        statusBox.getChildren( ).addAll( queuedIcon, image );

        final HBox lastBuildInfoPart = createLastBuildInfoBox( build );
        lastBuildInfoPart.visibleProperty( ).bind( build.runningProperty( ).not( ) );

        final HBox timeLeftInfoBox = createTimeLeftInfoBox( build );
        timeLeftInfoBox.visibleProperty( ).bind( build.runningProperty( ) );

        final StackPane infoBox = new StackPane( lastBuildInfoPart, timeLeftInfoBox );
        infoBox.setAlignment( Pos.CENTER );

        contextPart.getChildren( ).addAll( statusBox, infoBox );
        return contextPart;
    }

    private HBox createLastBuildInfoBox( final BuildTypeData build ) {
        final HBox lastBuildInfoPart = new HBox( );
        lastBuildInfoPart.setSpacing( 5 );
        lastBuildInfoPart.setAlignment( Pos.CENTER );

        final ImageView lastBuildIcon = new ImageView( UIUtils.createImage( "lastBuild.png" ) );
        lastBuildIcon.setPreserveRatio( true );
        lastBuildIcon.setFitWidth( 32 );

        final Label lastBuildDate = new Label( );
        lastBuildDate.setMinWidth( 110 );
        lastBuildDate.setTextAlignment( TextAlignment.CENTER );
        lastBuildDate.setAlignment( Pos.CENTER );
        lastBuildDate.setStyle( "-fx-font-weight:bold; -fx-text-fill:white; -fx-font-size:32px;" );
        lastBuildDate.setWrapText( true );
        lastBuildDate.textProperty( ).bind( createStringBinding( ( ) -> {
            final LocalDateTime localDateTime = build.lastFinishedDateProperty( ).get( );
            if ( localDateTime == null )
                return "00/00\n00:00";
            return localDateTime.format( DateTimeFormatter.ofPattern( "dd/MM\nHH:mm" ) );
        }, build.lastFinishedDateProperty( ) ) );

        lastBuildInfoPart.getChildren( ).addAll( lastBuildIcon, lastBuildDate );
        return lastBuildInfoPart;
    }

    private HBox createTimeLeftInfoBox( final BuildTypeData build ) {
        final HBox lastBuildInfoPart = new HBox( );
        lastBuildInfoPart.setSpacing( 5 );
        lastBuildInfoPart.setAlignment( Pos.CENTER );
        final ImageView lastBuildIcon = new ImageView( UIUtils.createImage( "timeLeft.png" ) );
        lastBuildIcon.setPreserveRatio( true );
        lastBuildIcon.setFitWidth( 32 );

        final Label timeLeftLabel = new Label( );
        timeLeftLabel.setMinWidth( 110 );
        timeLeftLabel.setTextAlignment( TextAlignment.CENTER );
        timeLeftLabel.setAlignment( Pos.CENTER );
        timeLeftLabel.setStyle( "-fx-font-weight:bold; -fx-text-fill:white; -fx-font-size:32px;" );
        timeLeftLabel.setWrapText( true );
        timeLeftLabel.textProperty( ).bind( createStringBinding( ( ) -> {
            final java.time.Duration timeLeft = build.timeLeftProperty( ).get( );
            return ( timeLeft.isNegative( ) ? "+ " : "" ) + ( abs( timeLeft.toMinutes( ) ) + 1 ) + "\nmin";
        }, build.timeLeftProperty( ) ) );

        lastBuildInfoPart.getChildren( ).addAll( lastBuildIcon, timeLeftLabel );
        return lastBuildInfoPart;
    }


    public void startAnimationOnNode( final Node node ) {
        FadeTransition transition = _registeredTransition.get( node );
        if ( transition == null ) {
            transition = new FadeTransition( Duration.millis( 1500 ), node );
            transition.setFromValue( 1.0 );
            transition.setToValue( 0.5 );
            transition.setCycleCount( Timeline.INDEFINITE );
            transition.setAutoReverse( true );
            transition.setOnFinished( ( ae ) -> node.setOpacity( 1 ) );
            _registeredTransition.put( node, transition );
        }
        transition.play( );
    }

    public void stopAnimationOnNode( final Node node ) {
        final FadeTransition transition = _registeredTransition.get( node );
        if ( transition != null ) {
            _registeredTransition.remove( node );
            transition.stop( );
        }
    }


}
