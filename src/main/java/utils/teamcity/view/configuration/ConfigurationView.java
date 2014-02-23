package utils.teamcity.view.configuration;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;
import utils.teamcity.controller.api.json.ApiVersion;

import javax.inject.Inject;

import static javafx.beans.binding.Bindings.*;

/**
 * Date: 15/02/14
 *
 * @author Cedric Longo
 */
final class ConfigurationView extends BorderPane {

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
        content.widthProperty( ).addListener( ( o, oldValue, newValue ) -> serverConfigurationPane.setMaxWidth( newValue.doubleValue( ) * 0.9 ) );
        content.getChildren( ).add( serverConfigurationPane );

        final GridPane proxyConfigurationPane = proxyConfigurationPane( );
        content.widthProperty( ).addListener( ( o, oldValue, newValue ) -> proxyConfigurationPane.setMaxWidth( newValue.doubleValue( ) * 0.9 ) );
        content.getChildren( ).add( proxyConfigurationPane );

        final GridPane preferenceConfigurationPane = preferenceConfigurationPane( );
        content.widthProperty( ).addListener( ( o, oldValue, newValue ) -> preferenceConfigurationPane.setMaxWidth( newValue.doubleValue( ) * 0.9 ) );
        content.getChildren( ).add( preferenceConfigurationPane );

        final Button loadBuildsButton = new Button( );
        loadBuildsButton.setOnAction( ( event ) -> _model.requestLoadingBuilds( ) );
        loadBuildsButton.textProperty( ).bind( createStringBinding( ( ) -> _model.loadingProperty( ).get( ) ? "Loading ..." : "Connect to server", _model.loadingProperty( ) ) );
        loadBuildsButton.disableProperty( ).bind( _model.loadingProperty( ) );
        content.getChildren( ).add( loadBuildsButton );

        final HBox connectionInformation = connexionStatusInformation( );
        content.getChildren( ).add( connectionInformation );

        final TableView<BuildTypeViewModel> buildList = buildTableView( );
        content.widthProperty( ).addListener( ( o, oldValue, newValue ) -> buildList.setMaxWidth( newValue.doubleValue( ) * 0.9 ) );
        buildList.disableProperty( ).bind( _model.loadingProperty( ) );
        VBox.setVgrow( buildList, Priority.SOMETIMES );
        content.getChildren( ).add( buildList );

        final Button swithToWallButton = new Button( "Switch to wall" );
        swithToWallButton.setOnAction( ( event ) -> _model.requestSwithToWallScene( ) );
        content.getChildren( ).add( swithToWallButton );

        buildList.disableProperty( ).bind( _model.loadingFailureProperty( ) );
        swithToWallButton.disableProperty( ).bind( _model.loadingFailureProperty( ) );

        final ScrollPane scrollPane = new ScrollPane( content );
        scrollPane.setFitToWidth( true );
        scrollPane.setFitToHeight( true );
        scrollPane.setHbarPolicy( ScrollPane.ScrollBarPolicy.NEVER );
        scrollPane.setVbarPolicy( ScrollPane.ScrollBarPolicy.AS_NEEDED );
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
        proxyConfigurationLine( grid );

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


    private void proxyConfigurationLine( final GridPane grid ) {
        final CheckBox useProxyCheckbox = new CheckBox( "Use HTTP Proxy" );
        useProxyCheckbox.selectedProperty( ).bindBidirectional( _model.proxyUseProperty( ) );

        grid.add( useProxyCheckbox, 2, 2 );
    }

    private GridPane proxyConfigurationPane( ) {
        final GridPane grid = new GridPane( );
        grid.setAlignment( Pos.CENTER );
        grid.setPadding( new Insets( 10 ) );
        grid.setHgap( 10 );
        grid.setVgap( 20 );
        grid.visibleProperty( ).bind( _model.proxyUseProperty( ) );
        grid.maxHeightProperty( ).bind( grid.minHeightProperty( ) );
        grid.minHeightProperty( ).bind( createDoubleBinding( ( ) -> {
            if ( _model.proxyUseProperty( ).get( ) )
                return USE_COMPUTED_SIZE;
            return 0d;
        }, _model.proxyUseProperty( ) ) );


        proxyServerUrlLine( grid );
        proxyCredentialsLine( grid );


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

    private void proxyServerUrlLine( final GridPane parent ) {
        final Label lineLabel = new Label( "Proxy Host:" );
        parent.add( lineLabel, 0, 0 );

        final TextField lineField = new TextField( );
        lineField.textProperty( ).bindBidirectional( _model.proxyServerUrlProperty( ) );
        lineLabel.setLabelFor( lineField );
        parent.add( lineField, 1, 0 );

        final Label portLabel = new Label( "Proxy port:" );
        parent.add( portLabel, 2, 0 );

        final TextField portField = new TextField( );
        portField.textProperty( ).addListener( ( o, oldValue, newValue ) -> {
            if ( !oldValue.equals( newValue ) )
                portField.textProperty( ).setValue( newValue.replaceAll( "[^0-9]", "" ) );
        } );
        portField.textProperty( ).bindBidirectional( _model.proxyServerPortProperty( ) );
        portLabel.setLabelFor( portField );
        parent.add( portField, 3, 0 );
    }

    private void proxyCredentialsLine( final GridPane parent ) {
        final Label lineLabel = new Label( "Proxy User:" );
        parent.add( lineLabel, 0, 1 );

        final TextField lineField = new TextField( );
        lineField.textProperty( ).bindBidirectional( _model.credentialsUserProperty( ) );
        lineLabel.setLabelFor( lineField );
        parent.add( lineField, 1, 1 );

        final Label passwordLabel = new Label( "Proxy Password:" );
        parent.add( passwordLabel, 2, 1 );

        final TextField passwordField = new PasswordField( );
        passwordField.textProperty( ).bindBidirectional( _model.credentialsUserProperty( ) );
        passwordLabel.setLabelFor( passwordField );
        parent.add( passwordField, 3, 1 );
    }


    private HBox connexionStatusInformation( ) {
        final HBox container = new HBox( );
        container.setAlignment( Pos.CENTER );
        container.setSpacing( 10 );

        final ProgressIndicator indicator = new ProgressIndicator( );
        indicator.setPrefSize( 20, 20 );
        indicator.visibleProperty( ).bind( _model.loadingProperty( ) );

        final Label connectionInformation = new Label( );
        connectionInformation.setTextAlignment( TextAlignment.CENTER );
        connectionInformation.setWrapText( true );
        connectionInformation.textProperty( ).bind( _model.loadingInformationProperty( ) );
        connectionInformation.visibleProperty( ).bind( _model.loadingInformationProperty( ).isEmpty( ).not( ) );
        connectionInformation.textFillProperty( ).bind( Bindings.<Paint>createObjectBinding( ( ) -> {
            if ( _model.loadingProperty( ).get( ) )
                return Paint.valueOf( "black" );
            return _model.isLoadingFailure( ) ? Paint.valueOf( "red" ) : Paint.valueOf( "green" );
        }, _model.loadingFailureProperty( ), _model.loadingProperty( ) ) );

        connectionInformation.minHeightProperty( ).bind( createIntegerBinding( ( ) -> {
            if ( _model.loadingInformationProperty( ).isEmpty( ).get( ) ) {
                return 0;
            } else {
                return 50;
            }
        }, _model.loadingInformationProperty( ) ) );
        connectionInformation.maxHeightProperty( ).bind( connectionInformation.minHeightProperty( ) );

        container.getChildren( ).addAll( indicator, connectionInformation );
        return container;
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


    private TableView<BuildTypeViewModel> buildTableView( ) {
        final TableView<BuildTypeViewModel> tableview = new TableView<>( _model.getBuildTypes( ) );
        tableview.setMinHeight( 500 );
        tableview.setEditable( true );
        tableview.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

        final TableColumn<BuildTypeViewModel, Boolean> selectedColumn = new TableColumn<>( "" );
        selectedColumn.setEditable( true );
        selectedColumn.setMinWidth( 40 );
        selectedColumn.setMaxWidth( 50 );
        selectedColumn.setCellValueFactory( param -> param.getValue( ).selectedProperty( ) );
        selectedColumn.setCellFactory( CheckBoxTableCell.forTableColumn( selectedColumn ) );

        final TableColumn<BuildTypeViewModel, String> idColumn = new TableColumn<>( "id" );
        idColumn.setCellValueFactory( param -> param.getValue( ).idProperty( ) );

        final TableColumn<BuildTypeViewModel, String> projectColumn = new TableColumn<>( "Project" );
        projectColumn.setCellValueFactory( param -> param.getValue( ).projectNameProperty( ) );

        final TableColumn<BuildTypeViewModel, String> nameColumn = new TableColumn<>( "Name" );
        nameColumn.setCellValueFactory( param -> param.getValue( ).nameProperty( ) );

        final TableColumn<BuildTypeViewModel, String> aliasColumns = new TableColumn<>( "Alias" );
        aliasColumns.setEditable( true );
        aliasColumns.setCellValueFactory( param -> param.getValue( ).aliasNameProperty( ) );
        aliasColumns.setCellFactory( TextFieldTableCell.forTableColumn( ) );
        aliasColumns.setOnEditCommit(
                event -> {
                    final BuildTypeViewModel buildTypeData = event.getTableView( ).getItems( ).get( event.getTablePosition( ).getRow( ) );
                    buildTypeData.setAliasName( event.getNewValue( ) );
                }
        );

        tableview.getColumns( ).addAll( selectedColumn, idColumn, projectColumn, nameColumn, aliasColumns );
        return tableview;
    }

}
