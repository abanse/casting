package com.hydro.casting.gui.prod.dialog;

import com.google.inject.Injector;
import com.hydro.casting.gui.analysis.control.AnalysisTable;
import com.hydro.casting.gui.model.common.EAnalysisElement;
import com.hydro.casting.gui.prod.dialog.result.ChargingMaterialDetailResult;
import com.hydro.casting.server.contract.dto.MaterialAnalysisElementDTO;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.core.gui.ApplicationManager;
import com.hydro.core.gui.ImagesCore;
import com.hydro.core.gui.comp.TaskButton;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.FutureTask;

public class ChargingMaterialDetailDialog extends Dialog<ChargingMaterialDetailResult>
{
    private final static Logger log = LoggerFactory.getLogger( ChargingMaterialDetailDialog.class );

    public ChargingMaterialDetailDialog( final Window owner, final Injector injector, final MaterialDTO materialDTO )
    {
        initOwner( owner );
        setResizable( true );
        final DialogPane dialogPane = getDialogPane();
        dialogPane.getStyleClass().add( "borderlessDialog" );

        setTitle( "Analysen ändern" );
        dialogPane.setHeaderText( "Hier kann die Analyse für das Material " + materialDTO.getName() + " geändert werden" );
        dialogPane.getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );

        final BorderPane mainPane = new BorderPane();
        final ToolBar toolBar = new ToolBar();
        toolBar.setOrientation( Orientation.VERTICAL );
        final TaskButton selectStandardAnalyseTaskButton = new TaskButton();
        selectStandardAnalyseTaskButton.setGraphic( new ImageView( ImagesCore.SEARCH.load( 21, 21, true, true ) ) );
        toolBar.getItems().add( selectStandardAnalyseTaskButton );

        mainPane.setRight( toolBar );

        final AnalysisTable table = new AnalysisTable()
        {
            @Override
            protected void handleElementValueUpdate( String elementName, Number newValue )
            {
                if ( materialDTO.getMaterialAnalysisElements() == null )
                {
                    materialDTO.setMaterialAnalysisElements( new ArrayList<>() );
                }
                MaterialAnalysisElementDTO materialAnalysisElementDTO = null;
                for ( MaterialAnalysisElementDTO materialAnalysisElement : materialDTO.getMaterialAnalysisElements() )
                {
                    if ( Objects.equals( materialAnalysisElement.getName(), elementName))
                    {
                        materialAnalysisElementDTO = materialAnalysisElement;
                        break;
                    }
                }
                if ( materialAnalysisElementDTO == null && (newValue == null || newValue.doubleValue() <= 0) )
                {
                    // nothing to do;
                }
                else if ( materialAnalysisElementDTO == null )
                {
                    materialAnalysisElementDTO = new MaterialAnalysisElementDTO(elementName, newValue.doubleValue() );
                    materialDTO.getMaterialAnalysisElements().add( materialAnalysisElementDTO );
                }
                else if ( newValue == null || newValue.doubleValue() <= 0)
                {
                    materialDTO.getMaterialAnalysisElements().remove( materialAnalysisElementDTO );
                }
                else
                {
                    materialAnalysisElementDTO.setValue( newValue.doubleValue() );
                }
            }
        };
        table.setPrefHeight( 100 );
        table.getSelectionModel().setCellSelectionEnabled( true );
        table.setEditable( true );
        table.setValuesEditable( true );

        // Fill analysis with default elements
        final Map<String, MaterialAnalysisElementDTO> currentElements = new HashMap<>();
        if ( materialDTO.getMaterialAnalysisElements() != null )
        {
            materialDTO.getMaterialAnalysisElements().forEach( materialAnalysisElementDTO -> currentElements.put( materialAnalysisElementDTO.getName(), materialAnalysisElementDTO ) );
        }
        final List<MaterialAnalysisElementDTO> allElements = new ArrayList<>();
        for ( EAnalysisElement element : EAnalysisElement.STANDARD_ELEMENTS )
        {
            if ( currentElements.containsKey( element.name() ) )
            {
                allElements.add( currentElements.remove( element.name() ) );
            }
            else
            {
                allElements.add( new MaterialAnalysisElementDTO( element.name(), 0 ) );
            }
        }
        if ( materialDTO.getMaterialAnalysisElements() != null )
        {
            materialDTO.getMaterialAnalysisElements().forEach( materialAnalysisElementDTO -> {
                if ( currentElements.containsKey( materialAnalysisElementDTO.getName() ) )
                {
                    allElements.add( materialAnalysisElementDTO );
                }
            } );
        }

        table.setAnalysis( materialDTO.getName(), allElements, false );
        mainPane.setCenter( table );

        //        final ObservableList<SpecificationElementDTO> tableElements = FXCollections.observableArrayList( materialDTO.getMaterialAnalysisElements() );
        //
        //        final SpecificationElementValueCellFactory specificationElementValueCellFactory = new SpecificationElementValueCellFactory();
        //
        //        final TableView<SpecificationElementDTO> table = new TableView<>();
        //        table.setEditable( true );
        //        table.setItems( tableElements );
        //        final TableColumn<SpecificationElementDTO, String> name = new TableColumn<>( "Element" );
        //        name.setCellValueFactory( new PropertyValueFactory<>( "name" ) );
        //        final TableColumn<SpecificationElementDTO, Double> minValue = new TableColumn<>( "Min" );
        //        minValue.setCellValueFactory( new PropertyValueFactory<>( "minValue" ) );
        //        minValue.setCellFactory( specificationElementValueCellFactory );
        //        minValue.setPrefWidth( 60 );
        //        final TableColumn<SpecificationElementDTO, Double> castingMinValue = new TableColumn<>( "Min" );
        //        castingMinValue.setCellValueFactory( new PropertyValueFactory<>( "castingMinValue" ) );
        //        castingMinValue.setCellFactory( specificationElementValueCellFactory );
        //        castingMinValue.setPrefWidth( 60 );
        //        castingMinValue.setEditable( true );
        //        castingMinValue.setGraphic( new ImageView( ImagesCore.EDIT.load( 16, 16, true, false ) ) );
        //        castingMinValue.setOnEditCommit( event -> {
        //            if ( event.getNewValue() == null || event.getNewValue() == 0 )
        //            {
        //                event.getRowValue().setCastingMinValue( null );
        //            }
        //            else
        //            {
        //                event.getRowValue().setCastingMinValue( event.getNewValue() );
        //            }
        //        } );
        //        final TableColumn<SpecificationElementDTO, Double> castingMaxValue = new TableColumn<>( "Max" );
        //        castingMaxValue.setCellValueFactory( new PropertyValueFactory<>( "castingMaxValue" ) );
        //        castingMaxValue.setCellFactory( specificationElementValueCellFactory );
        //        castingMaxValue.setPrefWidth( 60 );
        //        castingMaxValue.setEditable( true );
        //        castingMaxValue.setGraphic( new ImageView( ImagesCore.EDIT.load( 16, 16, true, false ) ) );
        //        castingMaxValue.setOnEditCommit( event -> {
        //            if ( event.getNewValue() == null || event.getNewValue() == 0 )
        //            {
        //                event.getRowValue().setCastingMaxValue( null );
        //            }
        //            else
        //            {
        //                event.getRowValue().setCastingMaxValue( event.getNewValue() );
        //            }
        //        } );
        //        final TableColumn<SpecificationElementDTO, Double> maxValue = new TableColumn<>( "Max" );
        //        maxValue.setCellValueFactory( new PropertyValueFactory<>( "maxValue" ) );
        //        maxValue.setCellFactory( specificationElementValueCellFactory );
        //        maxValue.setPrefWidth( 60 );
        //        final TableColumn<SpecificationElementDTO, Double> elementFactor = new TableColumn<>( "Faktor" );
        //        elementFactor.setCellValueFactory( new PropertyValueFactory<>( "elementFactor" ) );
        //        elementFactor.setCellFactory( TextFieldTableCell.forTableColumn( new DoubleStringConverter()
        //        {
        //            @Override
        //            public Double fromString( String value )
        //            {
        //                String doubleString = value;
        //                if ( doubleString != null && doubleString.contains( "," ) )
        //                {
        //                    doubleString = doubleString.replace( ',', '.' );
        //                }
        //                return super.fromString( doubleString );
        //            }
        //
        //            @Override
        //            public String toString( Double value )
        //            {
        //                String doubleString = super.toString( value );
        //                if ( doubleString != null && doubleString.contains( "." ) )
        //                {
        //                    doubleString = doubleString.replace( '.', ',' );
        //                }
        //                return doubleString;
        //            }
        //        } ) );
        //        elementFactor.setEditable( true );
        //        elementFactor.setGraphic( new ImageView( ImagesCore.EDIT.load( 16, 16, true, false ) ) );
        //        elementFactor.setOnEditCommit( event -> {
        //            event.getRowValue().setElementFactor( event.getNewValue() );
        //        } );
        //        final TableColumn<SpecificationElementDTO, String> unit = new TableColumn<>( "Einheit" );
        //        unit.setCellValueFactory( new PropertyValueFactory<>( "unit" ) );
        //        final TableColumn<SpecificationElementDTO, Integer> precision = new TableColumn<>( "Genauigkeit" );
        //        precision.setCellValueFactory( new PropertyValueFactory<>( "precision" ) );
        //
        //        table.getColumns().addAll( name, minValue, castingMinValue, castingMaxValue, maxValue, elementFactor, unit, precision );
        //
        dialogPane.setContent( mainPane );

        setResultConverter( buttonType -> {

            if ( buttonType != ButtonType.OK )
            {
                return null;
            }
            final ChargingMaterialDetailResult chargingMaterialDetailResult = new ChargingMaterialDetailResult();
            chargingMaterialDetailResult.setElementDTOList( materialDTO.getMaterialAnalysisElements() );

            return chargingMaterialDetailResult;
        } );

    }

    public static ChargingMaterialDetailResult showDialog( final Window owner, final Injector injector, final MaterialDTO materialDTO )
    {
        if ( Platform.isFxApplicationThread() )
        {
            final ChargingMaterialDetailDialog dialog = new ChargingMaterialDetailDialog( owner, injector, materialDTO );
            final Optional<ChargingMaterialDetailResult> result = dialog.showAndWait();
            return result.orElse( null );
        }
        else
        {
            FutureTask<Optional<ChargingMaterialDetailResult>> future = new FutureTask<>( () -> {
                final ChargingMaterialDetailDialog dialog = new ChargingMaterialDetailDialog( owner, injector, materialDTO );
                dialog.initOwner( injector.getInstance( ApplicationManager.class ).getMainStage() );
                return dialog.showAndWait();
            } );
            Platform.runLater( future );
            final Optional<ChargingMaterialDetailResult> result;
            try
            {
                result = future.get();
                return result.orElse( null );
            }
            catch ( Exception ex )
            {
                log.error( "error getting result", ex );
                return null;
            }
        }
    }
}