package utils.teamcity.view.wall;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import utils.teamcity.model.build.BuildTypeData;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class WallView extends GridPane {

    public static final int MAX_BY_COLUMN = 5;
    private final WallViewModel _model;


    private final Map<Node, FadeTransition> _registeredTransition = Maps.newHashMap();

    @Inject
    public WallView( final WallViewModel model ) {
        _model = model;

        setHgap( 10 );
        setVgap( 10 );
        setPadding( new Insets( 10 ) );

        setStyle( "-fx-background-color:black;" );

        setAlignment( Pos.CENTER );

        final ObservableList<BuildTypeData> builds = _model.getBuilds();
        final int nbColums = builds.size() / MAX_BY_COLUMN + ( ( builds.size() % MAX_BY_COLUMN > 0 ? 1 : 0 ) );
        final int byColums = builds.size() / nbColums + ( ( builds.size() % nbColums > 0 ? 1 : 0 ) );

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
        final StackPane tile = new StackPane();
        tile.setAlignment( Pos.CENTER_LEFT );
        tile.setStyle( "-fx-border-color:white; -fx-border-radius:5;" );
        tile.backgroundProperty().bind( build.backgroundProperty() );

        tile.prefWidthProperty().bind( widthProperty() );
        tile.prefHeightProperty().bind( heightProperty() );

        final Pane progressPane = new Pane();
        progressPane.backgroundProperty().bind( build.runningBackgroundProperty() );
        progressPane.minWidthProperty().bind( tile.widthProperty().multiply( build.percentageCompleteProperty() ).divide( 100 ) );
        progressPane.maxWidthProperty().bind( progressPane.minWidthProperty() );
        progressPane.visibleProperty().bind( build.runningProperty() );
        build.runningProperty().addListener( ( o, oldVallue, newValue ) -> {
            if ( newValue )
                startAnimationOnNode( tile );
            else
                stopAnimationOnNode( tile );
        } );

        final HBox tileContent = new HBox();
        tileContent.setAlignment( Pos.CENTER_LEFT );
        tileContent.setSpacing( 10 );

        final Label tileTitle = new Label();
        tileTitle.setStyle( "-fx-font-weight:bold; -fx-text-fill:white;" );
        tileTitle.setPadding( new Insets( 5 ) );
        tileTitle.setWrapText( true );
        tileTitle.textProperty().bind( build.displayedNameProperty() );
        tileTitle.prefWidthProperty().bind( tile.widthProperty() );
        tileTitle.prefHeightProperty().bind( tile.heightProperty() );
        tileTitle.setFont( new Font( 50 ) );
        HBox.setHgrow( tileTitle, Priority.SOMETIMES );

        final VBox contextPart = new VBox();
        contextPart.setMinWidth( 100 );
        contextPart.setAlignment( Pos.TOP_CENTER );

        final ImageView image = new ImageView();
        image.setPreserveRatio( true );
        image.setFitHeight( 90 );
        image.imageProperty().bind( build.imageProperty() );

        contextPart.getChildren().add( image );

        tileContent.getChildren().addAll( tileTitle, contextPart );

        tile.getChildren().addAll( progressPane, tileContent );
        add( tile, x, y );
    }

    public void startAnimationOnNode( final Node node ) {
        FadeTransition transition = _registeredTransition.get( node );
        if ( transition == null ) {
            transition = new FadeTransition( Duration.millis( 1500 ), node );
            transition.setFromValue( 1.0 );
            transition.setToValue( 0.5 );
            transition.setCycleCount( Timeline.INDEFINITE );
            transition.setAutoReverse( true );
        }
        transition.play();
    }

    public void stopAnimationOnNode( final Node node ) {
        final FadeTransition transition = _registeredTransition.get( node );
        if ( transition != null ) {
            _registeredTransition.remove( node );
            transition.stop();
        }
    }


}
