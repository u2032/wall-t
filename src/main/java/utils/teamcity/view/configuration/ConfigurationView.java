package utils.teamcity.view.configuration;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import utils.teamcity.controller.api.json.ApiVersion;
import utils.teamcity.model.build.BuildTypeData;

import javax.inject.Inject;

import static javafx.beans.binding.Bindings.createStringBinding;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
public final class ConfigurationView extends BorderPane {

    private final ConfigurationViewModel _model;

    @Inject
    ConfigurationView( final ConfigurationViewModel model ) {
        _model = model;
        setStyle( "-fx-background-color:gainsboro;" );

        setTop( titlePane( ) );
        setCenter( scollPane( ) );
    }

    private Node titlePane( ) {
        final HBox titlePane = new HBox( );
        titlePane.setAlignment( Pos.CENTER );

        final Label label = new Label( "Configuration" );
        label.setFont( Font.font( 50 ) );

        titlePane.getChildren( ).add( label );
        return titlePane;
    }

    private Node scollPane( ) {
        final VBox content = new VBox( );
        content.setStyle( "-fx-background-color:lightgrey;" );
        content.setAlignment( Pos.TOP_CENTER );
        content.setPadding( new Insets( 30, 0, 50, 0 ) );
        content.setSpacing( 20 );

        final GridPane serverConfigurationPane = serverConfigurationPane( );
        content.widthProperty( ).addListener( ( o, oldValue, newValue ) -> serverConfigurationPane.setMaxWidth( newValue.doubleValue( ) * 0.8 ) );
        content.getChildren( ).add( serverConfigurationPane );

        final Button loadBuildsButton = new Button( );
        loadBuildsButton.setOnAction( ( event ) -> _model.requestLoadingBuilds( ) );
        loadBuildsButton.textProperty( ).bind( createStringBinding( ( ) -> _model.loadingBuildProperty( ).get( ) ? "Loading ..." : "Load Builds", _model.loadingBuildProperty( ) ) );
        loadBuildsButton.disableProperty( ).bind( _model.loadingBuildProperty( ) );
        content.getChildren( ).add( loadBuildsButton );

        final TableView<BuildTypeData> buildList = buildTableView( );
        content.widthProperty( ).addListener( ( o, oldValue, newValue ) -> buildList.setMaxWidth( newValue.doubleValue( ) * 0.8 ) );
        buildList.disableProperty( ).bind( _model.loadingBuildProperty( ) );
        VBox.setVgrow( buildList, Priority.SOMETIMES );
        content.getChildren( ).add( buildList );

        final GridPane preferenceConfigurationPane = preferenceConfigurationPane( );
        content.widthProperty( ).addListener( ( o, oldValue, newValue ) -> preferenceConfigurationPane.setMaxWidth( newValue.doubleValue( ) * 0.8 ) );
        content.getChildren( ).add( preferenceConfigurationPane );

        final Button swithToWallButton = new Button( "Switch to wall" );
        swithToWallButton.setOnAction( ( event ) -> _model.requestSwithToWallScene( ) );
        content.getChildren( ).add( swithToWallButton );

        final ScrollPane scrollPane = new ScrollPane( content );
        scrollPane.setFitToWidth( true );
        scrollPane.setFitToHeight( true );
        return scrollPane;
    }

    private GridPane serverConfigurationPane( ) {
        final GridPane grid = new GridPane( );
        grid.setAlignment( Pos.CENTER );
        grid.setPadding( new Insets( 10 ) );
        grid.setHgap( 10 );
        grid.setVgap( 20 );

        serverUrlLine( grid );
        credentialsLine( grid );
        apiVersionLine( grid );

        final ColumnConstraints noConstraint = new ColumnConstraints( );
        final ColumnConstraints rightAlignementConstraint = new ColumnConstraints( );
        rightAlignementConstraint.setHalignment( HPos.RIGHT );
        grid.getColumnConstraints( ).add( rightAlignementConstraint );
        grid.getColumnConstraints( ).add( noConstraint );
        grid.getColumnConstraints( ).add( rightAlignementConstraint );
        grid.getColumnConstraints( ).add( noConstraint );

        grid.setStyle( "-fx-border-color:white; -fx-border-radius:5;" );

        return grid;
    }

    private void serverUrlLine( final GridPane parent ) {
        final Label lineLabel = new Label( "Server URL:" );
        parent.add( lineLabel, 0, 0 );

        final TextField lineField = new TextField( );
        lineField.textProperty( ).bindBidirectional( _model.serverUrlProperty( ) );
        lineLabel.setLabelFor( lineField );
        parent.add( lineField, 1, 0, 3, 1 );
    }

    private void credentialsLine( final GridPane parent ) {
        final Label lineLabel = new Label( "User:" );
        parent.add( lineLabel, 0, 1 );

        final TextField lineField = new TextField( );
        lineField.textProperty( ).bindBidirectional( _model.credentialsUserProperty( ) );
        lineLabel.setLabelFor( lineField );
        parent.add( lineField, 1, 1 );

        final Label passwordLabel = new Label( "Password:" );
        parent.add( passwordLabel, 2, 1 );

        final TextField passwordField = new PasswordField( );
        passwordField.textProperty( ).bindBidirectional( _model.credentialsPasswordProperty( ) );
        passwordLabel.setLabelFor( passwordField );
        parent.add( passwordField, 3, 1 );
    }

    private void apiVersionLine( final GridPane parent ) {
        final Label lineLabel = new Label( "Api Version:" );
        parent.add( lineLabel, 0, 2 );

        final ComboBox<ApiVersion> apiVersionBox = new ComboBox<>( FXCollections.observableArrayList( ApiVersion.values( ) ) );
        apiVersionBox.converterProperty( ).setValue( new StringConverter<ApiVersion>( ) {
            @Override
            public String toString( final ApiVersion object ) {
                return object.getIdentifier( );
            }

            @Override
            public ApiVersion fromString( final String string ) {
                return ApiVersion.valueFrom( string );
            }
        } );
        apiVersionBox.getSelectionModel( ).select( _model.getApiVersion( ) );
        apiVersionBox.getSelectionModel( ).selectedItemProperty( ).addListener( ( o, oldValue, newValue ) -> _model.requestNewApiVersion( newValue ) );
        lineLabel.setLabelFor( apiVersionBox );
        parent.add( apiVersionBox, 1, 2 );
    }


    private GridPane preferenceConfigurationPane( ) {
        final GridPane grid = new GridPane( );
        grid.setAlignment( Pos.CENTER );
        grid.setPadding( new Insets( 10 ) );
        grid.setHgap( 10 );
        grid.setVgap( 20 );

        lightModeCheckBox( grid );
        nbTilesByColumnComboBox( grid );

        final ColumnConstraints noConstraint = new ColumnConstraints( );
        final ColumnConstraints rightAlignementConstraint = new ColumnConstraints( );
        rightAlignementConstraint.setHalignment( HPos.RIGHT );
        grid.getColumnConstraints( ).add( rightAlignementConstraint );
        grid.getColumnConstraints( ).add( noConstraint );
        grid.getColumnConstraints( ).add( rightAlignementConstraint );
        grid.getColumnConstraints( ).add( noConstraint );

        grid.setStyle( "-fx-border-color:white; -fx-border-radius:5;" );

        return grid;
    }

    private void lightModeCheckBox( final GridPane parent ) {
        final Label lineLabel = new Label( "Light Mode:" );
        parent.add( lineLabel, 0, 0 );

        final CheckBox lightModeCheckbox = new CheckBox( );
        lightModeCheckbox.selectedProperty( ).bindBidirectional( _model.lightModeProperty( ) );
        lineLabel.setLabelFor( lightModeCheckbox );
        parent.add( lightModeCheckbox, 1, 0 );
    }

    private void nbTilesByColumnComboBox( final GridPane parent ) {
        final Label lineLabel = new Label( "Max tiles by column:" );
        parent.add( lineLabel, 0, 1 );

        final ComboBox<Integer> apiVersionBox = new ComboBox<>( FXCollections.observableArrayList( 2, 3, 4, 5, 6, 7, 8 ) );
        apiVersionBox.getSelectionModel( ).select( (Integer) _model.maxRowByColumnProperty( ).get( ) );
        apiVersionBox.getSelectionModel( ).selectedItemProperty( ).addListener( ( o, oldValue, newValue ) -> _model.maxRowByColumnProperty( ).setValue( newValue ) );
        lineLabel.setLabelFor( apiVersionBox );
        parent.add( apiVersionBox, 1, 1 );
    }


    private TableView<BuildTypeData> buildTableView( ) {
        final TableView<BuildTypeData> tableview = new TableView<>( _model.getBuildTypes( ) );
        tableview.setEditable( true );
        tableview.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

        final TableColumn<BuildTypeData, Boolean> selectedColumn = new TableColumn<>( "" );
        selectedColumn.setEditable( true );
        selectedColumn.setMinWidth( 40 );
        selectedColumn.setMaxWidth( 50 );
        selectedColumn.setCellValueFactory( param -> param.getValue( ).selectedProperty( ) );
        selectedColumn.setCellFactory( CheckBoxTableCell.forTableColumn( selectedColumn ) );

        final TableColumn<BuildTypeData, String> idColumn = new TableColumn<>( "id" );
        idColumn.setCellValueFactory( param -> param.getValue( ).idProperty( ) );

        final TableColumn<BuildTypeData, String> projectColumn = new TableColumn<>( "Project" );
        projectColumn.setCellValueFactory( param -> param.getValue( ).projectNameProperty( ) );

        final TableColumn<BuildTypeData, String> nameColumn = new TableColumn<>( "Name" );
        nameColumn.setCellValueFactory( param -> param.getValue( ).nameProperty( ) );

        final TableColumn<BuildTypeData, String> aliasColumns = new TableColumn<>( "Alias" );
        aliasColumns.setEditable( true );
        aliasColumns.setCellValueFactory( param -> param.getValue( ).aliasNameProperty( ) );
        aliasColumns.setCellFactory( TextFieldTableCell.forTableColumn( ) );
        aliasColumns.setOnEditCommit(
                event -> {
                    final BuildTypeData buildTypeData = event.getTableView( ).getItems( ).get( event.getTablePosition( ).getRow( ) );
                    _model.setAliasName( buildTypeData, event.getNewValue( ) );
                }
        );

        tableview.getColumns( ).addAll( selectedColumn, idColumn, projectColumn, nameColumn, aliasColumns );
        return tableview;
    }

}
