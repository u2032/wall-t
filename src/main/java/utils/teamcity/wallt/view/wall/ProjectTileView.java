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

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import utils.teamcity.wallt.view.UIUtils;

import static javafx.beans.binding.Bindings.createIntegerBinding;
import static javafx.beans.binding.Bindings.createObjectBinding;
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

    private final FadeTransition _successRunningAnimation;
    private final FadeTransition _failureRunningAnimation;

    ProjectTileView( final ProjectTileViewModel project ) {
        _model = project;

        setAlignment( CENTER_LEFT );
        setSpacing( 10 );
        setStyle( "-fx-border-color:white; -fx-border-radius:5;" );
        backgroundProperty().bind( project.backgroundProperty() );

        _successRunningAnimation = prepareRunningAnimation();
        _failureRunningAnimation = prepareRunningAnimation();

        createBuildInformation();
    }


    private FadeTransition prepareRunningAnimation() {
        final FadeTransition transition = new FadeTransition( Duration.millis( 1500 ) );
        transition.setFromValue( 1.0 );
        transition.setToValue( 0.5 );
        transition.setCycleCount( Timeline.INDEFINITE );
        transition.setAutoReverse( true );
        return transition;
    }

    private void createBuildInformation() {
        final Label tileTitle = new Label();
        tileTitle.setFont( UIUtils.font( 50, FontWeight.BOLD ) );
        tileTitle.setTextFill( Color.WHITE );
        tileTitle.setPadding( new Insets( 5 ) );
        tileTitle.setWrapText( true );
        tileTitle.textProperty().bind( _model.displayedNameProperty() );
        tileTitle.setEffect( UIUtils.shadowEffect() );
        tileTitle.prefWidthProperty().bind( widthProperty() );
        tileTitle.prefHeightProperty().bind( heightProperty() );
        tileTitle.alignmentProperty().bind( createObjectBinding( () -> _model.isLightMode() ? Pos.CENTER : CENTER_LEFT, _model.lightModeProperty() ) );
        tileTitle.textAlignmentProperty().bind( createObjectBinding( () -> _model.isLightMode() ? CENTER : LEFT, _model.lightModeProperty() ) );
        HBox.setHgrow( tileTitle, Priority.SOMETIMES );
        getChildren().add( tileTitle );

        final VBox contextPart = createContextPart();
        contextPart.visibleProperty().bind( _model.lightModeProperty().not() );
        contextPart.minWidthProperty().bind( createIntegerBinding( () -> contextPart.isVisible() ? 90 : 0, contextPart.visibleProperty() ) );
        contextPart.maxWidthProperty().bind( contextPart.minWidthProperty() );
        getChildren().add( contextPart );
    }


    private VBox createContextPart() {
        final VBox contextPart = new VBox();
        contextPart.setAlignment( Pos.CENTER );

        final StackPane successBox = createSuccessBox();
        successBox.setOpacity( getSuccessBoxOpacity() );
        _successRunningAnimation.setNode( successBox );

        final StackPane failureBox = createFailureBox();
        failureBox.setOpacity( getFailureBoxOpacity() );
        _failureRunningAnimation.setNode( failureBox );

        checkSuccessAnimationRunning( _model.hasSuccessRunning(), successBox );
        _model.hasSuccessRunningProperty().addListener( ( o, oldVallue, newValue ) -> {
            checkSuccessAnimationRunning( newValue, successBox );
        } );

        checkFailureAnimationRunning( _model.hasFailureRunning(), failureBox );
        _model.hasFailureRunningProperty().addListener( ( o, oldVallue, newValue ) -> {
            checkFailureAnimationRunning( newValue, failureBox );
        } );

        _model.failureCountProperty().addListener( ( observable, oldValue, newValue ) -> {
            if ( _successRunningAnimation.getStatus() != Animation.Status.RUNNING ) {
                successBox.setOpacity( getSuccessBoxOpacity() );
            }
            if ( _failureRunningAnimation.getStatus() != Animation.Status.RUNNING ) {
                failureBox.setOpacity( getFailureBoxOpacity() );
            }
        } );

        contextPart.getChildren().addAll( successBox, failureBox );
        return contextPart;
    }

    private StackPane createSuccessBox() {
        final StackPane pane = new StackPane();
        final ImageView background = new ImageView( UIUtils.createImage( "icons/square-green.png" ) );
        background.setPreserveRatio( true );
        background.setFitHeight( 80 );

        final Label label = new Label( "8" );
        label.setFont( UIUtils.font( 40, FontWeight.BOLD ) );
        label.setTextFill( Color.WHITE );
        label.setEffect( UIUtils.shadowEffect() );
        label.textProperty().bind( _model.successCountProperty().asString() );
        pane.getChildren().addAll( background, label );
        return pane;
    }

    private StackPane createFailureBox() {
        final StackPane pane = new StackPane();
        final ImageView background = new ImageView( UIUtils.createImage( "icons/square-red.png" ) );
        background.setPreserveRatio( true );
        background.setFitHeight( 80 );

        final Label label = new Label();
        label.setFont( UIUtils.font( 40, FontWeight.BOLD ) );
        label.setTextFill( Color.WHITE );
        label.setEffect( UIUtils.shadowEffect() );
        label.textProperty().bind( _model.failureCountProperty().asString() );
        pane.getChildren().addAll( background, label );
        return pane;
    }

    private double getFailureBoxOpacity() {
        return _model.getFailureCount() > 0 ? 1 : 0.5;
    }

    private double getSuccessBoxOpacity() {
        return _model.getFailureCount() <= 0 ? 1 : 0.5;
    }

    private void checkSuccessAnimationRunning( final Boolean isRunning, final Pane successBox ) {
        if ( isRunning ) {
            _successRunningAnimation.play();
        } else {
            _successRunningAnimation.stop();
            successBox.setOpacity( getSuccessBoxOpacity() );
        }
    }

    private void checkFailureAnimationRunning( final Boolean isRunning, final Pane failureBox ) {
        if ( isRunning ) {
            _failureRunningAnimation.play();
        } else {
            _failureRunningAnimation.stop();
            failureBox.setOpacity( getFailureBoxOpacity() );
        }
    }

}
