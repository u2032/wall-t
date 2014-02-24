package utils.teamcity.view.wall;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import utils.teamcity.view.UIUtils;

import static javafx.beans.binding.Bindings.*;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.scene.text.TextAlignment.CENTER;
import static javafx.scene.text.TextAlignment.LEFT;

/**
 * Date: 23/02/14
 *
 * @author Cedric Longo
 */
final class ProjectTileView extends HBox {

    private final ProjectTileViewModel _model;

    ProjectTileView( final ProjectTileViewModel project ) {
        _model = project;

        setAlignment( Pos.CENTER_LEFT );
        setSpacing( 10 );
        setStyle( "-fx-border-color:white; -fx-border-radius:5;" );
        backgroundProperty( ).bind( project.backgroundProperty( ) );

        createBuildInformation( );
    }

    private void createBuildInformation( ) {
        final Label tileTitle = new Label( );
        tileTitle.setStyle( "-fx-font-weight:bold; -fx-text-fill:white; -fx-font-size:50px;" );
        tileTitle.setPadding( new Insets( 5 ) );
        tileTitle.setWrapText( true );
        tileTitle.textProperty( ).bind( _model.displayedNameProperty( ) );
        tileTitle.prefWidthProperty( ).bind( widthProperty( ) );
        tileTitle.prefHeightProperty( ).bind( heightProperty( ) );
        tileTitle.alignmentProperty( ).bind( createObjectBinding( ( ) -> _model.isLightMode( ) ? Pos.CENTER : CENTER_LEFT, _model.lightModeProperty( ) ) );
        tileTitle.textAlignmentProperty( ).bind( createObjectBinding( ( ) -> _model.isLightMode( ) ? CENTER : LEFT, _model.lightModeProperty( ) ) );
        HBox.setHgrow( tileTitle, Priority.SOMETIMES );
        getChildren( ).add( tileTitle );

        final VBox contextPart = createContextPart( );
        contextPart.visibleProperty( ).bind( _model.lightModeProperty( ).not( ) );
        contextPart.minWidthProperty( ).bind( createIntegerBinding( ( ) -> contextPart.isVisible( ) ? 90 : 0, contextPart.visibleProperty( ) ) );
        contextPart.maxWidthProperty( ).bind( contextPart.minWidthProperty( ) );
        getChildren( ).add( contextPart );
    }


    private VBox createContextPart( ) {
        final VBox contextPart = new VBox( );
        contextPart.setAlignment( Pos.CENTER );

        final StackPane successBox = createSuccessBox( );
        successBox.opacityProperty( ).bind( createDoubleBinding( ( ) -> _model.getFailureCount( ) > 0 ? 0.5 : 1, _model.failureCountProperty( ) ) );

        final StackPane failureBox = createFailureBox( );
        failureBox.opacityProperty( ).bind( createDoubleBinding( ( ) -> _model.getFailureCount( ) > 0 ? 1 : 0.5, _model.failureCountProperty( ) ) );

        contextPart.getChildren( ).addAll( successBox, failureBox );
        return contextPart;
    }

    private StackPane createSuccessBox( ) {
        final StackPane pane = new StackPane( );
        final ImageView background = new ImageView( UIUtils.createImage( "square-green.png" ) );
        background.setPreserveRatio( true );
        background.setFitHeight( 80 );

        final Label label = new Label( "8" );
        label.setStyle( "-fx-font-weight:bold; -fx-text-fill:white; -fx-font-size:40px;" );
        label.textProperty( ).bind( _model.successCountProperty( ).asString( ) );
        pane.getChildren( ).addAll( background, label );
        return pane;
    }

    private StackPane createFailureBox( ) {
        final StackPane pane = new StackPane( );
        final ImageView background = new ImageView( UIUtils.createImage( "square-red.png" ) );
        background.setPreserveRatio( true );
        background.setFitHeight( 80 );

        final Label label = new Label( );
        label.setStyle( "-fx-font-weight:bold; -fx-text-fill:white; -fx-font-size:40px;" );
        label.textProperty( ).bind( _model.failureCountProperty( ).asString( ) );
        pane.getChildren( ).addAll( background, label );
        return pane;
    }

}
