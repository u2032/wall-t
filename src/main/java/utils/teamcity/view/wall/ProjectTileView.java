package utils.teamcity.view.wall;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static javafx.beans.binding.Bindings.createIntegerBinding;

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
        HBox.setHgrow( tileTitle, Priority.SOMETIMES );
        getChildren( ).add( tileTitle );

        final VBox contextPart = createContextPart( );
        contextPart.visibleProperty( ).bind( _model.lightModeProperty( ).not( ) );
        contextPart.minWidthProperty( ).bind( createIntegerBinding( ( ) -> contextPart.isVisible( ) ? 150 : 0, contextPart.visibleProperty( ) ) );
        contextPart.maxWidthProperty( ).bind( contextPart.minWidthProperty( ) );
        getChildren( ).add( contextPart );
    }


    private VBox createContextPart( ) {
        final VBox contextPart = new VBox( );
        contextPart.setAlignment( Pos.CENTER );

        contextPart.getChildren( ).addAll( );
        return contextPart;
    }

}
