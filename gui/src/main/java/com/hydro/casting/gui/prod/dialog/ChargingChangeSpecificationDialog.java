package com.hydro.casting.gui.prod.dialog;

import com.hydro.casting.gui.analysis.util.AnalysisCompositionHelper;
import com.hydro.casting.gui.prod.dialog.result.ChargingChangeSpecificationResult;
import com.hydro.casting.gui.prod.table.cell.SpecificationElementValueCellFactory;
import com.hydro.casting.server.contract.dto.ChargeSpecificationDTO;
import com.hydro.casting.server.contract.dto.SpecificationElementDTO;
import com.hydro.core.gui.ImagesCore;
import com.hydro.core.gui.comp.TaskButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import javafx.util.converter.DoubleStringConverter;

import java.util.Optional;
import java.util.concurrent.FutureTask;

public class ChargingChangeSpecificationDialog extends Dialog<ChargingChangeSpecificationResult>
{

    public ChargingChangeSpecificationDialog( final ChargeSpecificationDTO chargeSpecificationDTO )
    {
        final DialogPane dialogPane = getDialogPane();
        dialogPane.getStyleClass().add( "borderlessDialog" );

        setTitle( "Vorgaben ändern" );
        dialogPane.setHeaderText( "Hier kann die Spezifikation für die Charge " + chargeSpecificationDTO.getCharge() + " geändert werden" );
        dialogPane.getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );

        final ObservableList<SpecificationElementDTO> tableElements = FXCollections.observableArrayList( chargeSpecificationDTO.getElements() );

        final SpecificationElementValueCellFactory specificationElementValueCellFactory = new SpecificationElementValueCellFactory();

        final TableView<SpecificationElementDTO> table = new TableView<>();
        table.setEditable( true );

        AnalysisCompositionHelper.switchToPPM( chargeSpecificationDTO );

        table.setItems( tableElements );
        final TableColumn<SpecificationElementDTO, String> name = new TableColumn<>( "Element" );
        name.setCellValueFactory( new PropertyValueFactory<>( "name" ) );
        final TableColumn<SpecificationElementDTO, Double> minValue = new TableColumn<>( "Min" );
        minValue.setCellValueFactory( new PropertyValueFactory<>( "minValue" ) );
        minValue.setCellFactory( specificationElementValueCellFactory );
        minValue.setPrefWidth( 60 );
        final TableColumn<SpecificationElementDTO, Double> castingMinValue = new TableColumn<>( "Min" );
        castingMinValue.setCellValueFactory( new PropertyValueFactory<>( "castingMinValue" ) );
        castingMinValue.setCellFactory( specificationElementValueCellFactory );
        castingMinValue.setPrefWidth( 60 );
        castingMinValue.setEditable( true );
        castingMinValue.setGraphic( new ImageView( ImagesCore.EDIT.load( 16, 16, true, false ) ) );
        castingMinValue.setOnEditCommit( event -> {
            if ( event.getNewValue() == null || event.getNewValue() == 0 )
            {
                event.getRowValue().setCastingMinValue( null );
            }
            else
            {
                event.getRowValue().setCastingMinValue( event.getNewValue() );
            }
        } );
        final TableColumn<SpecificationElementDTO, Double> castingMaxValue = new TableColumn<>( "Max" );
        castingMaxValue.setCellValueFactory( new PropertyValueFactory<>( "castingMaxValue" ) );
        castingMaxValue.setCellFactory( specificationElementValueCellFactory );
        castingMaxValue.setPrefWidth( 60 );
        castingMaxValue.setEditable( true );
        castingMaxValue.setGraphic( new ImageView( ImagesCore.EDIT.load( 16, 16, true, false ) ) );
        castingMaxValue.setOnEditCommit( event -> {
            if ( event.getNewValue() == null || event.getNewValue() == 0 )
            {
                event.getRowValue().setCastingMaxValue( null );
            }
            else
            {
                event.getRowValue().setCastingMaxValue( event.getNewValue() );
            }
        } );
        final TableColumn<SpecificationElementDTO, Double> maxValue = new TableColumn<>( "Max" );
        maxValue.setCellValueFactory( new PropertyValueFactory<>( "maxValue" ) );
        maxValue.setCellFactory( specificationElementValueCellFactory );
        maxValue.setPrefWidth( 60 );
        final TableColumn<SpecificationElementDTO, Double> elementFactor = new TableColumn<>( "Faktor" );
        elementFactor.setCellValueFactory( new PropertyValueFactory<>( "elementFactor" ) );
        elementFactor.setCellFactory( TextFieldTableCell.forTableColumn( new DoubleStringConverter()
        {
            @Override
            public Double fromString( String value )
            {
                String doubleString = value;
                if ( doubleString != null && doubleString.contains( "," ) )
                {
                    doubleString = doubleString.replace( ',', '.' );
                }
                return super.fromString( doubleString );
            }

            @Override
            public String toString( Double value )
            {
                String doubleString = super.toString( value );
                if ( doubleString != null && doubleString.contains( "." ) )
                {
                    doubleString = doubleString.replace( '.', ',' );
                }
                return doubleString;
            }
        } ) );
        elementFactor.setEditable( true );
        elementFactor.setGraphic( new ImageView( ImagesCore.EDIT.load( 16, 16, true, false ) ) );
        elementFactor.setOnEditCommit( event -> {
            event.getRowValue().setElementFactor( event.getNewValue() );
        } );
        final TableColumn<SpecificationElementDTO, String> unit = new TableColumn<>( "Einheit" );
        unit.setCellValueFactory( new PropertyValueFactory<>( "unit" ) );
        final TableColumn<SpecificationElementDTO, Integer> precision = new TableColumn<>( "Genauigkeit" );
        precision.setCellValueFactory( new PropertyValueFactory<>( "precision" ) );

        table.getColumns().addAll( name, minValue, castingMinValue, castingMaxValue, maxValue, elementFactor, unit, precision );

        final BorderPane mainPane = new BorderPane();
        mainPane.setCenter( table );
        final ToolBar toolBar = new ToolBar();
        toolBar.setOrientation( Orientation.VERTICAL );
        final TaskButton selectStandardAnalyseTaskButton = new TaskButton();
        selectStandardAnalyseTaskButton.setGraphic( new ImageView( ImagesCore.REMOVE.load( 21, 21, true, true ) ) );
        selectStandardAnalyseTaskButton.setTooltip( new Tooltip( "Alle Eingaben löschen" ) );
        selectStandardAnalyseTaskButton.setOnAction( event -> {
            tableElements.forEach( specificationElementDTO -> {
                specificationElementDTO.setCastingMinValue( null );
                specificationElementDTO.setCastingMaxValue( null );
                specificationElementDTO.setElementFactor( 1.0 );
            } );
            table.refresh();
        } );
        toolBar.getItems().add( selectStandardAnalyseTaskButton );

        mainPane.setRight( toolBar );

        dialogPane.setContent( mainPane );

        setResultConverter( buttonType -> {

            if ( buttonType != ButtonType.OK )
            {
                return null;
            }
            ChargingChangeSpecificationResult result = new ChargingChangeSpecificationResult();
            AnalysisCompositionHelper.switchBackFromPPM( chargeSpecificationDTO );
            result.setChargeSpecificationDTO( chargeSpecificationDTO );

            return result;
        } );

    }

    public static ChargingChangeSpecificationResult showDialog( Window owner, ChargeSpecificationDTO chargeSpecificationDTO )
    {
        if ( Platform.isFxApplicationThread() )
        {
            final ChargingChangeSpecificationDialog dialog = new ChargingChangeSpecificationDialog( chargeSpecificationDTO );
            dialog.initOwner( owner );
            final Optional<ChargingChangeSpecificationResult> result = dialog.showAndWait();
            return result.orElse( null );
        }
        else
        {
            FutureTask<Optional<ChargingChangeSpecificationResult>> future = new FutureTask<>( () -> {
                final ChargingChangeSpecificationDialog dialog = new ChargingChangeSpecificationDialog( chargeSpecificationDTO );
                dialog.initOwner( owner );
                return dialog.showAndWait();
            } );
            Platform.runLater( future );
            final Optional<ChargingChangeSpecificationResult> result;
            try
            {
                result = future.get();
            }
            catch ( Exception ex )
            {
                return null;
            }
            return result.orElse( null );
        }
    }
}