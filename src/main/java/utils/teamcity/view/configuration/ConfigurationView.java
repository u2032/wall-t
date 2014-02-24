package utils.teamcity.view.configuration;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;
import utils.teamcity.controller.api.ApiVersion;
import utils.teamcity.view.UIUtils;

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

        final Label buildTypesListLabel = new Label( "Monitored Build types" );
        buildTypesListLabel.setStyle( "-fx-font-weight:bold; -fx-font-size:20px;" );
        final TableView<BuildTypeViewModel> buildTypesList = buildBuildTypesListTableView( );
        content.widthProperty( ).addListener( ( o, oldValue, newValue ) -> buildTypesList.setMaxWidth( newValue.doubleValue( ) * 0.9 ) );
        buildTypesList.disableProperty( ).bind( _model.loadingProperty( ) );
        VBox.setVgrow( buildTypesList, Priority.SOMETIMES );
        content.getChildren( ).addAll( buildTypesListLabel, buildTypesList );

        final Label projectListLabel = new Label( "Monitored Projects" );
        projectListLabel.setStyle( "-fx-font-weight:bold; -fx-font-size:20px;" );
        final TableView<ProjectViewModel> projectList = buildProjectListTableView( );
        content.widthProperty( ).addListener( ( o, oldValue, newValue ) -> projectList.setMaxWidth( newValue.doubleValue( ) * 0.9 ) );
        projectList.disableProperty( ).bind( _model.loadingProperty( ) );
        VBox.setVgrow( projectList, Priority.SOMETIMES );
        content.getChildren( ).addAll( projectListLabel, projectList );


        final Button swithToWallButton = new Button( "Switch to wall" );
        swithToWallButton.setOnAction( ( event ) -> _model.requestSwithToWallScene( ) );
        content.getChildren( ).add( swithToWallButton );

        buildTypesList.disableProperty( ).bind( _model.loadingFailureProperty( ) );
        projectList.disableProperty( ).bind( _model.loadingFailureProperty( ) );
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
                return object.getName( );
            }

            @Override
            public ApiVersion fromString( final String string ) {
                return ApiVersion.fromName( string );
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
        lineField.textProperty( ).bindBidirectional( _model.proxyCredentialsUserProperty( ) );
        lineLabel.setLabelFor( lineField );
        parent.add( lineField, 1, 1 );

        final Label passwordLabel = new Label( "Proxy Password:" );
        parent.add( passwordLabel, 2, 1 );

        final TextField passwordField = new PasswordField( );
        passwordField.textProperty( ).bindBidirectional( _model.proxyCredentialsPasswordProperty( ) );
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
        nbTilesByRowComboBox( grid );

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

        final ComboBox<Integer> comboBox = new ComboBox<>( FXCollections.observableArrayList( 2, 3, 4, 5, 6, 7, 8 ) );
        comboBox.getSelectionModel( ).select( (Integer) _model.maxTilesByColumnProperty( ).get( ) );
        comboBox.getSelectionModel( ).selectedItemProperty( ).addListener( ( o, oldValue, newValue ) -> _model.maxTilesByColumnProperty( ).setValue( newValue ) );
        lineLabel.setLabelFor( comboBox );
        parent.add( comboBox, 1, 1 );
    }

    private void nbTilesByRowComboBox( final GridPane parent ) {
        final Label lineLabel = new Label( "Max tiles by row:" );
        parent.add( lineLabel, 2, 1 );

        final ComboBox<Integer> comboBox = new ComboBox<>( FXCollections.observableArrayList( 2, 3, 4, 5, 6, 7, 8 ) );
        comboBox.getSelectionModel( ).select( (Integer) _model.maxTilesByRowProperty( ).get( ) );
        comboBox.getSelectionModel( ).selectedItemProperty( ).addListener( ( o, oldValue, newValue ) -> _model.maxTilesByRowProperty( ).setValue( newValue ) );
        lineLabel.setLabelFor( comboBox );
        parent.add( comboBox, 3, 1 );
    }


    private TableView<BuildTypeViewModel> buildBuildTypesListTableView( ) {
        final TableView<BuildTypeViewModel> tableview = new TableView<>( _model.getBuildTypes( ) );
        tableview.setMinHeight( 300 );
        tableview.setEditable( true );
        tableview.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

        final TableColumn<BuildTypeViewModel, Boolean> selectedColumn = new TableColumn<>( "" );
        selectedColumn.setSortable( false );
        selectedColumn.setEditable( true );
        selectedColumn.setMinWidth( 40 );
        selectedColumn.setMaxWidth( 40 );
        selectedColumn.setCellValueFactory( param -> param.getValue( ).selectedProperty( ) );
        selectedColumn.setCellFactory( CheckBoxTableCell.forTableColumn( selectedColumn ) );

        final TableColumn<BuildTypeViewModel, IPositionable> moveColumn = new TableColumn<>( "" );
        moveColumn.setSortable( false );
        moveColumn.setMinWidth( 50 );
        moveColumn.setMaxWidth( 50 );
        moveColumn.setCellValueFactory( param -> new SimpleObjectProperty<IPositionable>( param.getValue( ) ) );
        moveColumn.setCellFactory( param -> positionMoveButtonsTableCell( BuildTypeViewModel.class ) );

        final TableColumn<BuildTypeViewModel, String> idColumn = new TableColumn<>( "id" );
        idColumn.setSortable( false );
        idColumn.setCellValueFactory( param -> param.getValue( ).idProperty( ) );

        final TableColumn<BuildTypeViewModel, String> projectColumn = new TableColumn<>( "Project" );
        projectColumn.setSortable( false );
        projectColumn.setCellValueFactory( param -> param.getValue( ).projectNameProperty( ) );

        final TableColumn<BuildTypeViewModel, String> nameColumn = new TableColumn<>( "Name" );
        nameColumn.setSortable( false );
        nameColumn.setCellValueFactory( param -> param.getValue( ).nameProperty( ) );

        final TableColumn<BuildTypeViewModel, String> aliasColumns = new TableColumn<>( "Alias" );
        aliasColumns.setSortable( false );
        aliasColumns.setEditable( true );
        aliasColumns.setCellValueFactory( param -> param.getValue( ).aliasNameProperty( ) );
        aliasColumns.setCellFactory( TextFieldTableCell.forTableColumn( ) );
        aliasColumns.setOnEditCommit(
                event -> {
                    final BuildTypeViewModel buildTypeData = event.getTableView( ).getItems( ).get( event.getTablePosition( ).getRow( ) );
                    buildTypeData.setAliasName( event.getNewValue( ) );
                }
        );

        tableview.getColumns( ).addAll( selectedColumn, moveColumn, idColumn, projectColumn, nameColumn, aliasColumns );
        return tableview;
    }


    private TableView<ProjectViewModel> buildProjectListTableView( ) {
        final TableView<ProjectViewModel> tableview = new TableView<>( _model.getProject( ) );
        tableview.setMinHeight( 200 );
        tableview.setEditable( true );
        tableview.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

        final TableColumn<ProjectViewModel, Boolean> selectedColumn = new TableColumn<>( "" );
        selectedColumn.setSortable( false );
        selectedColumn.setEditable( true );
        selectedColumn.setMinWidth( 40 );
        selectedColumn.setMaxWidth( 40 );
        selectedColumn.setCellValueFactory( param -> param.getValue( ).selectedProperty( ) );
        selectedColumn.setCellFactory( CheckBoxTableCell.forTableColumn( selectedColumn ) );

        final TableColumn<ProjectViewModel, IPositionable> moveColumn = new TableColumn<>( "" );
        moveColumn.setSortable( false );
        moveColumn.setMinWidth( 50 );
        moveColumn.setMaxWidth( 50 );
        moveColumn.setCellValueFactory( param -> new SimpleObjectProperty<IPositionable>( param.getValue( ) ) );
        moveColumn.setCellFactory( param -> positionMoveButtonsTableCell( ProjectViewModel.class ) );

        final TableColumn<ProjectViewModel, String> idColumn = new TableColumn<>( "id" );
        idColumn.setSortable( false );
        idColumn.setCellValueFactory( param -> param.getValue( ).idProperty( ) );

        final TableColumn<ProjectViewModel, String> nameColumn = new TableColumn<>( "Name" );
        nameColumn.setSortable( false );
        nameColumn.setCellValueFactory( param -> param.getValue( ).nameProperty( ) );

        final TableColumn<ProjectViewModel, String> aliasColumns = new TableColumn<>( "Alias" );
        aliasColumns.setSortable( false );
        aliasColumns.setEditable( true );
        aliasColumns.setCellValueFactory( param -> param.getValue( ).aliasNameProperty( ) );
        aliasColumns.setCellFactory( TextFieldTableCell.forTableColumn( ) );
        aliasColumns.setOnEditCommit(
                event -> {
                    final ProjectViewModel buildTypeData = event.getTableView( ).getItems( ).get( event.getTablePosition( ).getRow( ) );
                    buildTypeData.setAliasName( event.getNewValue( ) );
                }
        );

        tableview.getColumns( ).addAll( selectedColumn, moveColumn, idColumn, nameColumn, aliasColumns );
        return tableview;
    }


    private <T> TableCell<T, IPositionable> positionMoveButtonsTableCell( Class<T> type ) {
        return new TableCell<T, IPositionable>( ) {
            @Override
            protected void updateItem( final IPositionable item, final boolean empty ) {
                super.updateItem( item, empty );
                setContentDisplay( ContentDisplay.GRAPHIC_ONLY );
                if ( empty || item.getPosition( ) == Integer.MAX_VALUE ) {
                    setGraphic( null );
                    return;
                }
                final Button upButton = new Button( );
                upButton.setPadding( new Insets( 1 ) );
                upButton.setGraphic( new ImageView( UIUtils.createImage( "up.png" ) ) );
                upButton.setOnAction( event -> {
                    item.setPosition( item.getPosition( ) - 1 );
                } );

                final Button downButton = new Button( );
                downButton.setPadding( new Insets( 1 ) );
                downButton.setGraphic( new ImageView( UIUtils.createImage( "down.png" ) ) );
                downButton.setOnAction( event -> {
                    item.setPosition( item.getPosition( ) + 1 );
                } );

                final HBox hbox = new HBox( 2, upButton, downButton );
                hbox.setAlignment( Pos.CENTER );
                setGraphic( hbox );
            }
        };
    }


}
