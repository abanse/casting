package com.hydro.casting.gui.analysis.control;

import com.hydro.casting.gui.analysis.control.cell.CompositionElementValueFactory;
import com.hydro.casting.gui.analysis.control.cell.FormattedTableCellFactory;
import com.hydro.casting.gui.analysis.control.row.AnalysisTableRow;
import com.hydro.casting.gui.model.*;
import com.hydro.casting.gui.model.common.AnalysisCalculator;
import com.hydro.casting.gui.model.common.EAnalysisElement;
import com.hydro.casting.gui.model.common.ETransferMaterialType;
import com.hydro.casting.server.contract.dto.MaterialAnalysisElementDTO;
import com.hydro.core.common.util.StringTools;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AnalysisTable extends TableView<Composition>
{
    private final static Logger log = LoggerFactory.getLogger( AnalysisTable.class );

    protected FormattedTableCellFactory<Composition, Number> weightFTCF;
    protected FormattedTableCellFactory<Composition, Number> elementFTCF;
    protected FormattedTableCellFactory<Composition, String> nameFTCF;

    protected TableColumn<Composition, String> name;
    //private TableColumn<Composition, String> alloyNo;

    protected TableColumn<Composition, Number> weight;

    private ObservableList<Composition> compositions;

    private Transfer currentTransfer;
    private Batch currentBatch;
    private Transfer selectedTransfer;

    protected List<Integer> currentLineIndex = new ArrayList<>();

    private ContextMenu transferContextMenu;
    private ContextMenu batchContextMenu;

    protected boolean disableAverageLineColor = false;

    private boolean valuesEditable = false;

    private Analysis averageAnalysis = null;

    public AnalysisTable()
    {
        setEditable( false );

        setRowFactory( tv -> new AnalysisTableRow( currentLineIndex, disableAverageLineColor ) );

        nameFTCF = new FormattedTableCellFactory<Composition, String>();
        nameFTCF.setAlignment( TextAlignment.LEFT );

        name = new TableColumn<Composition, String>( "Name" );
        name.setId( "0" );
        name.setPrefWidth( 160.0 );
        name.setCellValueFactory( new PropertyValueFactory<Composition, String>( "name" ) );
        name.setCellFactory( nameFTCF );
        name.setEditable( false );
        name.setSortable( false );
        getColumns().add( name );

        /*
        alloyNo = new TableColumn<Composition, String>( "Sorten-Nr." );
        alloyNo.setId( "0" );
        alloyNo.setPrefWidth( 65.0 );
        alloyNo.setCellValueFactory( new PropertyValueFactory<Composition, String>( "apk" ) );
        alloyNo.setCellFactory( nameFTCF );
        alloyNo.setEditable( false );
        alloyNo.setSortable( false );
        getColumns().add( alloyNo );
         */

        weightFTCF = new FormattedTableCellFactory<>();
        weightFTCF.setFormat( Material.WEIGHT_KG_FORMATTER );
        weightFTCF.setAlignment( TextAlignment.RIGHT );
        weightFTCF.setShowNullValues( false );

        elementFTCF = new FormattedTableCellFactory<>();
        //elementFTCF.setFormat( CompositionElement.ELEMENT_FORMATTER );
        elementFTCF.setAlignment( TextAlignment.RIGHT );
        elementFTCF.setShowNullValues( false );

        weight = new TableColumn<>( "Gew.[kg]" );
        weight.setId( "1" );
        weight.setPrefWidth( 70.0 );
        weight.setCellValueFactory( new PropertyValueFactory<>( "weight" ) );
        weight.setCellFactory( weightFTCF );
        weight.setEditable( true );
        weight.setSortable( false );

        weight.setOnEditCommit( new EventHandler<CellEditEvent<Composition, Number>>()
        {
            @Override
            public void handle( CellEditEvent<Composition, Number> event )
            {
                Number newWeight = event.getNewValue();
                if ( newWeight == null )
                {
                    event.getTableView().refresh();
                    return;
                }

                int row = event.getTablePosition().getRow();

                handleWeightUpdate( row, newWeight );
                /*
                TransferMaterial rowTransferMaterial = getTransferMaterialAtRow( row );
                if ( rowTransferMaterial == null )
                {
                    event.getTableView().refresh();
                    return;
                }

                if ( currentTransfer != null )
                {
                    UpdateTransferMaterialWeight updateTransferMaterialWeight = new UpdateTransferMaterialWeight( currentTransfer, rowTransferMaterial, newWeight.doubleValue() );
                    CommandManager.getInstance().executeCommand( updateTransferMaterialWeight );
                }
                else if ( currentBatch != null )
                {
                    UpdateBatchMaterialWeight updateBatchMaterialWeight = new UpdateBatchMaterialWeight( currentBatch, rowTransferMaterial, newWeight.doubleValue() );
                    CommandManager.getInstance().executeCommand( updateBatchMaterialWeight );
                }
                 */
            }
        } );
        getColumns().add( weight );

        compositions = FXCollections.observableArrayList();

        // Set Row Factory for coloring

        setItems( compositions );

        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        getSelectionModel().selectedIndexProperty().addListener( ( observable, oldvalue, newValue ) -> {
            if ( elementFTCF.getSpecification() != null && newValue != null && newValue.intValue() >= 0 && newValue.intValue() < 3 )
            {
                Platform.runLater( () -> {
                    getSelectionModel().clearSelection();
                } );
            }
            //                    if ( isFocused() )
            //                    {
            //                        setWindowSelection();
            //                    }
        } );
        //        focusedProperty().addListener( ( p, o, n ) -> {
        //            if ( n == true )
        //            {
        //                setWindowSelection();
        //            }
        //        } );

        final MenuItem mnuDelTransfer = new MenuItem( "Löschen" );
        mnuDelTransfer.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                List<TransferMaterial> selectedTransferMaterials = getSelectedTransferMaterials();

                if ( selectedTransferMaterials.isEmpty() )
                {
                    return;
                }

                /*
                final Command deleteCommand = new DeleteMaterialFromTransfer( selectedTransferMaterials, currentTransfer );
                CommandManager.getInstance().executeCommand( deleteCommand );
                 */
            }
        } );
        final MenuItem mnuDelBatch = new MenuItem( "Löschen" );
        mnuDelBatch.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                List<TransferMaterial> selectedTransferMaterials = getSelectedTransferMaterials();

                if ( selectedTransferMaterials.isEmpty() )
                {
                    return;
                }

                /*
                final Command deleteCommand = new DeleteMaterialFromBatch( selectedTransferMaterials, currentBatch );
                CommandManager.getInstance().executeCommand( deleteCommand );
                 */
            }
        } );
        final MenuItem mnuCalcAmount = new MenuItem( "Mengen berechnen" );
        mnuCalcAmount.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                List<TransferMaterial> selectedTransferMaterials = getSelectedTransferMaterials();

                if ( selectedTransferMaterials.isEmpty() )
                {
                    return;
                }

                /*
                CalculateTransferMaterialWeight calculateTransferMaterialWeight = new CalculateTransferMaterialWeight( owner, AnalysisTable.this, selectedTransferMaterials, currentTransfer );
                CommandManager.getInstance().executeCommand( calculateTransferMaterialWeight );
                 */
            }
        } );
        final MenuItem moveToFixed = new MenuItem( "Zum Einsatz verschieben" );
        moveToFixed.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                List<TransferMaterial> selectedTransferMaterials = getSelectedTransferMaterials();

                if ( selectedTransferMaterials.isEmpty() )
                {
                    return;
                }
                /*
                ChangeTransferMaterialType changeTransferMaterialType = new ChangeTransferMaterialType( currentTransfer, selectedTransferMaterials );
                CommandManager.getInstance().executeCommand( changeTransferMaterialType );
                 */
            }
        } );
        final MenuItem movePartToFixed = new MenuItem( "Teilmenge zum Einsatz verschieben" );
        movePartToFixed.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                List<TransferMaterial> selectedTransferMaterials = getSelectedTransferMaterials();

                if ( selectedTransferMaterials.isEmpty() || selectedTransferMaterials.size() > 1 )
                {
                    return;
                }
                /*
                TransferMaterial transferMaterial = selectedTransferMaterials.iterator().next();
                MoveTransferMaterialDialog moveTransferMaterialDialog = new MoveTransferMaterialDialog();
                double fixedWeight = moveTransferMaterialDialog.showDialog( getScene().getWindow(), transferMaterial );

                if ( fixedWeight <= 0 )
                {
                    return;
                }

                SplitTransferMaterial splitTransferMaterial = new SplitTransferMaterial( currentTransfer, transferMaterial, fixedWeight );
                CommandManager.getInstance().executeCommand( splitTransferMaterial );
                 */
            }
        } );

        transferContextMenu = new ContextMenu( mnuDelTransfer, mnuCalcAmount, moveToFixed, movePartToFixed );
        transferContextMenu.setOnShowing( new EventHandler<WindowEvent>()
        {
            @Override
            public void handle( WindowEvent event )
            {
                if ( isEditable() == false || getSelectionModel().getSelectedItems().isEmpty() )
                {
                    mnuDelTransfer.setDisable( true );
                    mnuCalcAmount.setDisable( true );
                    moveToFixed.setDisable( true );
                    movePartToFixed.setDisable( true );
                    return;
                }
                boolean onlyMaterialsSelected = true;
                boolean oneFurnaceTransferMaterial = false;
                boolean withAS = false;
                boolean onlyVariableMaterialsSelected = true;

                List<TransferMaterial> selectedTransferMaterials = getSelectedTransferMaterials();
                for ( TransferMaterial selectedTransferMaterial : selectedTransferMaterials )
                {
                    if ( selectedTransferMaterial instanceof FurnaceTransferMaterial )
                    {
                        oneFurnaceTransferMaterial = true;
                        break;
                    }
                }

                List<Composition> selectedItems = getSelectionModel().getSelectedItems();
                for ( Composition selectedComposition : selectedItems )
                {
                    if ( selectedComposition == null )
                    {
                        continue;
                    }
                    if ( AnalysisCalculator.isASAnalysis( selectedComposition.getName() ) )
                    {
                        onlyMaterialsSelected = false;
                        withAS = true;
                        onlyVariableMaterialsSelected = false;
                        continue;
                    }
                    if ( AnalysisCalculator.isAnalyse( selectedComposition.getName() ) )
                    {
                        onlyMaterialsSelected = false;
                        onlyVariableMaterialsSelected = false;
                        break;
                    }
                    if ( selectedComposition instanceof Analysis == false || ETransferMaterialType.VARIABLE.getApk().equals( ( (Analysis) selectedComposition ).getType() ) == false )
                    {
                        onlyVariableMaterialsSelected = false;
                    }
                }

                if ( onlyMaterialsSelected && oneFurnaceTransferMaterial == false )
                {
                    mnuCalcAmount.setDisable( false );
                    mnuDelTransfer.setDisable( false );
                }
                else
                {
                    if ( withAS && selectedItems.size() > 1 && oneFurnaceTransferMaterial == false )
                    {
                        mnuCalcAmount.setDisable( false );
                    }
                    else
                    {
                        mnuCalcAmount.setDisable( true );
                    }
                    mnuDelTransfer.setDisable( true );
                }
                moveToFixed.setDisable( !onlyVariableMaterialsSelected );
                movePartToFixed.setDisable( !onlyVariableMaterialsSelected || selectedItems.size() > 1 );
                if ( currentBatch != null )
                {
                    mnuCalcAmount.setDisable( true );
                }
            }
        } );

        setContextMenu( transferContextMenu );

        final MenuItem calcMixedSpec = new MenuItem( "Berechne Mischungsziel" );
        calcMixedSpec.setOnAction( ( event ) -> {
            if ( selectedTransfer == null )
            {
                return;
            }
            final List<String> selectedNames = new ArrayList<>();
            getSelectionModel().getSelectedItems().forEach( ( item ) -> {
                // Eigene Analyse aus der selektion entfernen
                if ( item.getName() == null || item.getName().contains( selectedTransfer.getName() ) == false )
                {
                    selectedNames.add( item.getName() );
                }
            } );
            Callback<Analysis, Boolean> filter = ( Analysis analysis ) -> {
                if ( selectedNames.contains( analysis.getName() ) )
                {
                    return Boolean.TRUE;
                }
                else
                {
                    return Boolean.FALSE;
                }
            };

            /*
            WizardMixTransferMaterialWeights wizardMixTransferMaterialWeights = new WizardMixTransferMaterialWeights( owner, this, selectedTransfer, filter );
            CommandManager.getInstance().executeCommand( wizardMixTransferMaterialWeights );
             */
        } );

        final MenuItem calcTargetSpec = new MenuItem( "Berechne auf Ziellegierung" );
        calcTargetSpec.setOnAction( ( event ) -> {
            if ( selectedTransfer == null )
            {
                return;
            }
            final List<String> selectedNames = new ArrayList<>();
            getSelectionModel().getSelectedItems().forEach( ( item ) -> {
                selectedNames.add( item.getName() );
            } );
            boolean bottomAdded = false;
            for ( String selectedName : selectedNames )
            {
                if ( selectedName.startsWith( "Sumpf" ) )
                {
                    bottomAdded = true;
                }
            }
            if ( !bottomAdded )
            {
                // Sumpf vom Batch immer mit hinzufügen
                for ( Composition item : getItems() )
                {
                    if ( item.getName().startsWith( "Sumpf" ) )
                    {
                        selectedNames.add( 0, item.getName() );
                        break;
                    }
                }
            }

            Callback<Analysis, Boolean> filter = ( Analysis analysis ) -> {
                if ( selectedNames.contains( analysis.getName() ) )
                {
                    return Boolean.TRUE;
                }
                else
                {
                    return Boolean.FALSE;
                }
            };

            /*
            WizardBatchTransferMaterialWeights wizardBatchTransferMaterialWeights = new WizardBatchTransferMaterialWeights( owner, this, currentBatch, filter );
            CommandManager.getInstance().executeCommand( wizardBatchTransferMaterialWeights );
             */
        } );
        //        final MenuItem calcAverage = new MenuItem( "Berechne Analyse" );
        //        calcAverage.setOnAction( ( event ) -> {
        //            final List<Analysis> selectedAnalysis = new ArrayList<>();
        //
        //            getSelectionModel().getSelectedItems().forEach( ( item ) -> {
        //                if ( item instanceof Analysis )
        //                {
        //                    selectedAnalysis.add( (Analysis) item );
        //                }
        //            } );
        //
        //            PopOver popup = createAverageAnalysisPopOver( currentBatch, selectedAnalysis );
        //            popup.show( this );
        //        } );

        //        batchContextMenu = new ContextMenu( mnuDelBatch, calcMixedSpec, calcTargetSpec, calcAverage );
        batchContextMenu = new ContextMenu( mnuDelBatch, calcMixedSpec, calcTargetSpec );
        batchContextMenu.setOnShowing( new EventHandler<WindowEvent>()
        {
            @Override
            public void handle( WindowEvent event )
            {
                if ( isEditable() == false || getSelectedTransferMaterials().isEmpty() )
                {
                    mnuDelBatch.setDisable( true );
                }
                else
                {
                    mnuDelBatch.setDisable( false );
                }
                //                if ( getSelectionModel().getSelectedItems().size() < 2 )
                //                {
                //                    calcAverage.setDisable( true );
                //                }
                //                else
                //                {
                //                    calcAverage.setDisable( false );
                //                }

                if ( isEditable() == false || getSelectionModel().getSelectedItems().size() < 1 && selectedTransfer != null )
                {
                    calcMixedSpec.setText( "Einsatzoptimierung" );
                    calcMixedSpec.setDisable( true );
                    calcTargetSpec.setText( "Zieloptimierung" );
                    return;
                }
                calcMixedSpec.setText( "Optimiere " + selectedTransfer.getName() + " für selektierte Zeilen" );
                calcMixedSpec.setDisable( false );
                calcTargetSpec.setText( "Optimiere selektierte Zeilen auf Ziellegierung (mit Sumpf)" );
                calcTargetSpec.setDisable( false );
            }
        } );

        clear();
    }

    protected void handleWeightUpdate( int row, Number newWeight )
    {
    }

    //    private void setWindowSelection()
    //    {
    //        if ( windowSelectionModel != null )
    //        {
    //            final int selectedIndex = getSelectionModel().getSelectedIndex();
    //
    //            if ( selectedIndex < 0 )
    //            {
    //                windowSelectionModel.clearSelection();
    //                return;
    //            }
    //
    //            final Composition selectedComposition = getSelectionModel().getSelectedItem();
    //            if ( selectedComposition != null && "Sumpf".equals( selectedComposition.getName() ) )
    //            {
    //                if ( currentBatch != null )
    //                {
    //                    windowSelectionModel.select( currentBatch );
    //                }
    //                else if ( currentTransfer != null )
    //                {
    //                    windowSelectionModel.select( currentTransfer );
    //                }
    //                else
    //                {
    //                    windowSelectionModel.clearSelection();
    //                }
    //            }
    //            else
    //            {
    //                TransferMaterial selectedTransferMaterial = getTransferMaterialAtRow( selectedIndex );
    //                if ( selectedTransferMaterial != null )
    //                {
    //                    windowSelectionModel.select( selectedTransferMaterial );
    //                }
    //                else
    //                {
    //                    windowSelectionModel.clearSelection();
    //                }
    //            }
    //        }
    //    }

    protected void setLineIndex( int... lineIndex )
    {
        this.currentLineIndex.clear();

        if ( lineIndex != null )
        {
            for ( int line : lineIndex )
            {
                this.currentLineIndex.add( line );
            }
        }
        // weightFTCF.setLineIndex(lineIndex);
        // elementFTCF.setLineIndex(lineIndex);
        // nameFTCF.setLineIndex(lineIndex);
    }

    public synchronized void clear()
    {
        setContextMenu( null );
        compositions.clear();
        elementFTCF.setSpecification( null );
        setLineIndex( -1 );

        setDisable( true );
    }

    public boolean isValuesEditable()
    {
        return valuesEditable;
    }

    public void setValuesEditable( boolean valuesEditable )
    {
        this.valuesEditable = valuesEditable;
    }

    public synchronized void setTransfer( Transfer transfer )
    {
        if ( transfer == null )
        {
            clear();
            return;
        }
        setTransfer( transfer, transfer.getBatch().isSingleFurnace() );
    }

    public synchronized void setTransfer( Transfer transfer, boolean singleFurnace )
    {
        setContextMenu( transferContextMenu );
        compositions.clear();
        elementFTCF.setSpecification( null );
        setLineIndex( -1 );

        setDisable( false );

        currentTransfer = transfer;
        currentBatch = null;

        displayCalculatedAnalysis( transfer, singleFurnace, false );
    }

    public synchronized void setBatch( Batch batch, boolean compressAnalysis )
    {
        setContextMenu( batchContextMenu );
        compositions.clear();
        elementFTCF.setSpecification( null );
        weightFTCF.setBatch( batch );
        setLineIndex( -1 );

        setDisable( false );

        currentTransfer = null;
        currentBatch = batch;

        displayCalculatedAnalysis( batch, compressAnalysis );
    }

    public synchronized void setBatch( Batch batch, List<Analysis> analysis, String averageLineName, int[] lineIndex, boolean compressAnalysis )
    {
        setBatch( batch, analysis, averageLineName, lineIndex, true, compressAnalysis );
    }

    public synchronized void setBatch( Batch batch, List<Analysis> analysis, String averageLineName, int[] lineIndex, boolean showSpecification, boolean compressAnalysis )
    {
        setContextMenu( null );
        compositions.clear();
        elementFTCF.setSpecification( null );
        setLineIndex( lineIndex );

        setDisable( false );

        currentTransfer = null;
        currentBatch = null;

        displayCalculatedAnalysis( batch, analysis, averageLineName, lineIndex, showSpecification, compressAnalysis );
    }

    public void setSpecification( Specification specification )
    {
        setContextMenu( null );
        compositions.clear();
        elementFTCF.setSpecification( null );
        setLineIndex( -1 );

        setDisable( false );

        averageAnalysis = null;
        restructureTable( specification.getMin().getCompositionElements(), specification, false, null );

        compositions.add( specification.getMin() );
        compositions.add( specification.getMax() );

        computeSizes( specification );
    }

    public void setAnalysis( Analysis analysis, Specification specification, boolean compressAnalysis )
    {
        setContextMenu( null );
        compositions.clear();
        elementFTCF.setSpecification( null );
        setLineIndex( -1 );

        setDisable( false );

        weight.setVisible( false );

        currentTransfer = null;
        currentBatch = null;

        displayAnalysis( analysis, specification, compressAnalysis );
    }

    public void setAnalysis( String name, List<MaterialAnalysisElementDTO> materialAnalysisElements, boolean compressAnalysis )
    {
        setContextMenu( null );
        compositions.clear();

        elementFTCF.setSpecification( null );
        setLineIndex( -1 );

        setDisable( false );

        weight.setVisible( false );

        currentTransfer = null;
        currentBatch = null;

        final Analysis analysis = new Analysis();
        analysis.setName( name );
        int index = 0;
        for ( MaterialAnalysisElementDTO materialAnalysisElement : materialAnalysisElements )
        {
            analysis.setCompositionElementValue( materialAnalysisElement.getName(), materialAnalysisElement.getValue() );
            analysis.getCompositionElement( materialAnalysisElement.getName() ).setSortOrderId( (long) index );
            index++;
        }

        displayAnalysis( analysis, null, compressAnalysis );
    }

    private void restructureTable( List<CompositionElement> elements, Specification spec, boolean compressAnalysis, Analysis compressRelevantAnalyse )
    {
        List<CompositionElement> toAdd = new ArrayList<>();
        for ( CompositionElement compositionElement : elements )
        {
            boolean found = false;
            List<TableColumn<Composition, ?>> existingColumns = getColumns();
            for ( TableColumn<Composition, ?> existingColumn : existingColumns )
            {
                if ( existingColumn.getId() != null && existingColumn.getId().equals( compositionElement.getName() ) )
                {
                    found = true;
                    break;
                }
            }
            if ( found == false )
            {
                toAdd.add( compositionElement );
            }
        }
        List<TableColumn<Composition, ?>> toRemoveColumns = new ArrayList<>();
        List<TableColumn<Composition, ?>> existingColumns = getColumns();
        for ( TableColumn<Composition, ?> existingColumn : existingColumns )
        {
            //if ( existingColumn.equals( name ) || existingColumn.equals( alloyNo ) || existingColumn.equals( weight ) )
            if ( existingColumn.equals( name ) || existingColumn.equals( weight ) )
            {
                continue;
            }

            boolean found = false;
            for ( CompositionElement compositionElement : elements )
            {
                if ( compositionElement.getName().equals( existingColumn.getId() ) )
                {
                    found = true;
                    break;
                }
            }
            if ( found == false )
            {
                toRemoveColumns.add( existingColumn );
            }
        }

        for ( TableColumn<Composition, ?> toRemoveColumn : toRemoveColumns )
        {
            getColumns().remove( toRemoveColumn );
        }

        // Inner class overwriting JavaFX TableColumn to allow for custom sorting of table columns.
        class ComparableTableColumn<S, T> extends TableColumn<S, T>
        {
            private Long sortOrderId;

            public ComparableTableColumn( String displayName )
            {
                super( displayName );
            }

            public Long getSortOrderId()
            {
                return sortOrderId;
            }

            public void setSortOrderId( Long sortOrderId )
            {
                this.sortOrderId = sortOrderId;
            }
        }

        for ( CompositionElement compositionElement : toAdd )
        {
            ComparableTableColumn<Composition, Number> elementColumn = new ComparableTableColumn<>( compositionElement.getName() );
            elementColumn.setPrefWidth( 45.0 );
            elementColumn.setCellFactory( elementFTCF );
            elementColumn.setCellValueFactory( new CompositionElementValueFactory( compositionElement.getName() ) );
            elementColumn.setId( compositionElement.getName() );
            elementColumn.setEditable( valuesEditable );
            elementColumn.setSortable( false );
            elementColumn.setSortOrderId( compositionElement.getSortOrderId() );
            if ( valuesEditable )
            {
                elementColumn.setOnEditCommit( event -> {
                    handleElementValueUpdate( compositionElement.getName(), event.getNewValue() );
                } );
            }
            getColumns().add( elementColumn );
        }

        // Sorting mechanism for analysis table columns
        getColumns().sort( new Comparator<TableColumn<Composition, ?>>()
        {
            @Override
            public int compare( TableColumn<Composition, ?> o1, TableColumn<Composition, ?> o2 )
            {
                // Explicit casting necessary. Sorts by sortOrderId if present. Otherwise, sort by Id.
                if ( o1 instanceof ComparableTableColumn && o2 instanceof ComparableTableColumn )
                {
                    ComparableTableColumn<?, ?> customO1 = (ComparableTableColumn<?, ?>) o1;
                    ComparableTableColumn<?, ?> customO2 = (ComparableTableColumn<?, ?>) o2;
                    if ( customO1.getSortOrderId() != null )
                    {
                        if ( customO2.getSortOrderId() != null )
                        {
                            return customO1.getSortOrderId().compareTo( customO2.getSortOrderId() );
                        }
                        // Element with sortOrderId always comes before element without sortOrderId
                        return -1;
                    }
                    else if ( customO2.getSortOrderId() != null )
                    {
                        // Element with sortOrderId always comes before element without sortOrderId
                        return 1;
                    }
                }
                return o1.getId().compareTo( o2.getId() );
            }
        } );

        if ( compressAnalysis && spec != null && compressRelevantAnalyse != null )
        {
            final Set<String> elementNamesToShow = new HashSet<>();
            for ( TableColumn<Composition, ?> column : getColumns() )
            {
                if ( !( column instanceof ComparableTableColumn ) )
                {
                    continue;
                }
                final String elementName = column.getId();
                if ( Arrays.stream( EAnalysisElement.STANDARD_ELEMENTS ).anyMatch( standardElement -> elementName.startsWith( standardElement.name() ) ) )
                {
                    elementNamesToShow.add( elementName );
                    continue;
                }
                int decimalPlaces = AnalysisCalculator.getDecimalPlaces( spec, elementName );
                double maxValue = 0;
                double minValue = 0;
                if ( spec.getMax() != null )
                {
                    maxValue = AnalysisCalculator.round( spec.getMax().getCompositionElementValue( elementName ), decimalPlaces );
                }

                if ( spec.getMin() != null )
                {
                    minValue = AnalysisCalculator.round( spec.getMin().getCompositionElementValue( elementName ), decimalPlaces );
                }

                if ( minValue > 0 )
                {
                    elementNamesToShow.add( elementName );
                    continue;
                }

                final double analysisValue = AnalysisCalculator.round( compressRelevantAnalyse.getCompositionElementValue( elementName ), decimalPlaces );

                // Checking for 0 values to prevent NaN / 0 values being displayed as below minimum
                if ( analysisValue != 0d )
                {
                    if ( maxValue != 0d && analysisValue > maxValue )
                    {
                        elementNamesToShow.add( elementName );
                    }
                    else if ( analysisValue < minValue )
                    {
                        elementNamesToShow.add( elementName );
                    }
                }
            }

            for ( TableColumn<Composition, ?> column : getColumns() )
            {
                if ( !( column instanceof ComparableTableColumn ) )
                {
                    continue;
                }
                column.setVisible( elementNamesToShow.contains( column.getId() ) );
            }
        }
        else
        {
            for ( TableColumn<Composition, ?> column : getColumns() )
            {
                if ( !( column instanceof ComparableTableColumn ) )
                {
                    continue;
                }
                column.setVisible( true );
            }
        }

        if ( spec != null )
        {
            getColumns().forEach( ( column ) -> {
                if ( column.getId() == null || Character.isDigit( column.getId().charAt( 0 ) ) )
                {
                    return;
                }
                Node header = lookup( "#" + column.getId() );
                if ( header != null )
                {
                    Tooltip tooltip = new Tooltip();
                    VBox textBox = new VBox();
                    Text elementName = new Text( column.getId() );
                    elementName.setFont( Font.font( 18.0 ) );
                    textBox.getChildren().add( elementName );
                    final Composition min = spec.getMin();
                    if ( min != null )
                    {
                        final CompositionElement minElement = min.getCompositionElement( column.getId() );
                        if ( minElement != null )
                        {
                            if ( minElement.getErrorElementValue() >= 0 )
                            {
                                textBox.getChildren().add( new Text( "Untergrenze (Fehler): " + CompositionElement.ELEMENT_FORMATTER.format( minElement.getErrorElementValue() ) ) );
                            }
                            if ( minElement.getWarningElementValue() >= 0 )
                            {
                                textBox.getChildren().add( new Text( "Untergrenze (Warnung): " + CompositionElement.ELEMENT_FORMATTER.format( minElement.getWarningElementValue() ) ) );
                            }
                            if ( minElement.getOriginalElementValue() > 0 )
                            {
                                textBox.getChildren().add( new Text( "Min: " + CompositionElement.ELEMENT_FORMATTER.format( minElement.getOriginalElementValue() ) ) );
                            }
                            if ( minElement.getStatisticElementValue() >= 0 )
                            {
                                textBox.getChildren().add( new Text( "Min (Statistik): " + CompositionElement.ELEMENT_FORMATTER.format( minElement.getStatisticElementValue() ) ) );
                            }
                        }
                    }
                    final Composition max = spec.getMax();
                    if ( max != null )
                    {
                        final CompositionElement maxElement = max.getCompositionElement( column.getId() );
                        if ( maxElement != null )
                        {
                            if ( maxElement.getStatisticElementValue() >= 0 )
                            {
                                textBox.getChildren().add( new Text( "Max (Statistik): " + CompositionElement.ELEMENT_FORMATTER.format( maxElement.getStatisticElementValue() ) ) );
                            }
                            if ( maxElement.getOriginalElementValue() > 0 )
                            {
                                textBox.getChildren().add( new Text( "Max: " + CompositionElement.ELEMENT_FORMATTER.format( maxElement.getOriginalElementValue() ) ) );
                            }
                            if ( maxElement.getWarningElementValue() >= 0 )
                            {
                                textBox.getChildren().add( new Text( "Obergrenze (Warnung): " + CompositionElement.ELEMENT_FORMATTER.format( maxElement.getWarningElementValue() ) ) );
                            }
                            if ( maxElement.getErrorElementValue() >= 0 )
                            {
                                textBox.getChildren().add( new Text( "Obergrenze (Fehler): " + CompositionElement.ELEMENT_FORMATTER.format( maxElement.getErrorElementValue() ) ) );
                            }
                        }
                    }
                    tooltip.setGraphic( textBox );
                    Tooltip.install( header, tooltip );
                }
            } );
        }
    }

    protected void handleElementValueUpdate( String elementName, Number newValue )
    {
        log.info( "element " + elementName + " new value " + newValue );
        // overwrite
    }

    private void computeSizes( Specification specification )
    {
        // Size setzen
        if ( specification != null )
        {
            for ( TableColumn<Composition, ?> tableColumn : getColumns() )
            {
                if ( tableColumn.getId().length() > 8 )
                {
                    tableColumn.setPrefWidth( 80.0 );
                    continue;
                }
                else if ( tableColumn.getId().length() > 5 )
                {
                    tableColumn.setPrefWidth( 60.0 );
                    continue;
                }
                CompositionElement minElement = specification.getMin().getCompositionElement( tableColumn.getId() );
                if ( minElement != null )
                {
                    final int decimalUnits = minElement.getDecimalPlaces();
                    if ( decimalUnits > 2 )
                    {
                        tableColumn.setPrefWidth( 60.0 );
                    }
                }
                CompositionElement maxElement = specification.getMax().getCompositionElement( tableColumn.getId() );
                if ( maxElement != null )
                {
                    final int decimalUnits = maxElement.getDecimalPlaces();
                    if ( decimalUnits > 2 )
                    {
                        tableColumn.setPrefWidth( 60.0 );
                    }
                }
                /*
                for ( int i = 0; i < getItems().size(); i++ )
                {
                    Composition rowComp = getItems().get( i );
                    if ( rowComp == null )
                    {
                        continue;
                    }
                    boolean onlyAnalysis = AnalysisCalculator.isAnalyse( rowComp.getName() );
                    if ( onlyAnalysis )
                    {
                        continue;
                    }
                    Object cellData = tableColumn.getCellData( i );
                    if ( cellData instanceof Number )
                    {
                        final String formattedNumber = CompositionElement.ELEMENT_FORMATTER.format( ( (Number) cellData ).doubleValue() );
                        if ( formattedNumber.length() < 10 )
                        {
                            @SuppressWarnings( "restriction" )
                            final FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics( Font.getDefault() );
                            final char[] stringChars = new char[formattedNumber.length()];
                            formattedNumber.getChars( 0, formattedNumber.length(), stringChars, 0 );
                            double stringWidth = 0;
                            for ( char stringChar : stringChars )
                            {
                                stringWidth = stringWidth + fontMetrics.getCharWidth( stringChar );
                            }

                            if ( ( stringWidth + 10 ) > tableColumn.getWidth() )
                            {
                                tableColumn.setPrefWidth( stringWidth + 10 );
                            }
                        }
                    }
                }

                 */
            }
        }
        else
        {
            for ( TableColumn<Composition, ?> tableColumn : getColumns() )
            {
                if ( tableColumn.getPrefWidth() < 81 )
                {
                    tableColumn.setPrefWidth( 81 );
                }
            }
        }
    }

    private void displayCalculatedAnalysis( Transfer transfer, boolean singleFurnace, boolean compressAnalysis )
    {
        if ( transfer == null || transfer.getBatch() == null )
        {
            return;
        }

        final List<Composition> compositions = new ArrayList<>();

        Specification specification = transfer.getBatch().getSpecification();

        /*
        if ( transfer.isUseStandardSpec() )
        {
            specification = MeltingPlan.getInstance().getStandardSpecMainData().getSpecification();
        }
         */

        List<TransferMaterial> transferMaterials = transfer.getTransferMaterials();

        elementFTCF.setSpecification( specification );
        elementFTCF.setCheckedAnalysis( null );

        HashSet<String> allElementNames = new HashSet<String>();
        List<CompositionElement> allElements = new ArrayList<CompositionElement>();
        if ( specification != null )
        {
            Composition oneSpecComp = specification.getMin();
            for ( CompositionElement compositionElement : oneSpecComp.getCompositionElements() )
            {
                allElementNames.add( compositionElement.getName() );
                allElements.add( compositionElement );
            }
        }
        for ( TransferMaterial transferMaterial : transferMaterials )
        {
            List<CompositionElement> matElements = transferMaterial.getAnalysis().getCompositionElements();
            for ( CompositionElement matElement : matElements )
            {
                if ( allElementNames.contains( matElement.getName() ) )
                {
                    continue;
                }
                allElements.add( matElement );
                allElementNames.add( matElement.getName() );
            }
        }
        if ( transfer.getFillmentTransferMaterial() != null )
        {
            List<CompositionElement> matElements = transfer.getFillmentTransferMaterial().getAnalysis().getCompositionElements();
            for ( CompositionElement matElement : matElements )
            {
                if ( allElementNames.contains( matElement.getName() ) )
                {
                    continue;
                }
                allElements.add( matElement );
                allElementNames.add( matElement.getName() );
            }
        }
        if ( singleFurnace )
        {
            if ( transfer.getBatch() != null && transfer.getBatch().getBottomAnalysis() != null )
            {
                for ( CompositionElement compositionElement : transfer.getBatch().getBottomAnalysis().getCompositionElements() )
                {
                    if ( allElementNames.contains( compositionElement.getName() ) )
                    {
                        continue;
                    }
                    allElementNames.add( compositionElement.getName() );
                    allElements.add( compositionElement );
                }
            }
        }
        else
        {
            if ( transfer.getBottomAnalysis() != null )
            {
                for ( CompositionElement compositionElement : transfer.getBottomAnalysis().getCompositionElements() )
                {
                    if ( allElementNames.contains( compositionElement.getName() ) )
                    {
                        continue;
                    }
                    allElementNames.add( compositionElement.getName() );
                    allElements.add( compositionElement );
                }
            }
        }
        // berechne mittelwert
        final Analysis middledAnalysis = AnalysisCalculator.createAverage( "Ø", transfer, singleFurnace );
        elementFTCF.setCheckedAnalysis( middledAnalysis );

        averageAnalysis = middledAnalysis;
        restructureTable( allElements, specification, compressAnalysis, middledAnalysis );

        if ( specification != null )
        {
            compositions.add( specification.getMin() );
        }

        compositions.add( middledAnalysis );

        if ( specification != null )
        {
            compositions.add( specification.getMax() );
        }

        if ( singleFurnace )
        {
            if ( transfer.getBatch() != null && transfer.getBatch().getBottomWeight() > 0 )
            {
                Analysis copy = AnalysisCalculator.computeCalculatedElements( transfer.getBatch(), false, transfer.getBatch().getBottomAnalysis(), transfer.getBatch().getBottomWeight() );
                compositions.add( copy );
            }

            if ( transfer.getBatch() != null && transfer.getBatch().getBottomWeight() >= transfer.getTargetWeight() )
            {
                return;
            }
        }
        else
        {
            if ( transfer.getBottomWeight() > 0 )
            {
                Analysis copy = AnalysisCalculator.computeCalculatedElements( transfer.getBatch(), true, transfer.getBottomAnalysis(), transfer.getBottomWeight() );
                compositions.add( copy );
            }

            // if (transfer.getBottomWeight() >= transfer.getTargetWeight())
            // {
            // return;
            // }
        }

        if ( transfer.isNoInput() )
        {
            setLineIndex( 2 );
        }
        else
        {
            // Füllmaterial
            if ( transfer.getFillmentTransferMaterial() != null )
            {
                Analysis copy = AnalysisCalculator.computeCalculatedElements( transfer.getBatch(), !singleFurnace, transfer.getFillmentTransferMaterial().getAnalysis(),
                        transfer.getFillmentTransferMaterial().getWeight() );
                copy.setType( transfer.getFillmentTransferMaterial().getType() );
                compositions.add( copy );
            }
            // Dann die Einsätze
            for ( TransferMaterial transferMaterial : transferMaterials )
            {
                if ( ETransferMaterialType.findType( transferMaterial.getType() ) == ETransferMaterialType.VARIABLE )
                {
                    continue;
                }
                Analysis copy = AnalysisCalculator.computeCalculatedElements( transfer.getBatch(), !singleFurnace, transferMaterial.getAnalysis(), transferMaterial.getWeight() );
                copy.setType( transferMaterial.getType() );
                compositions.add( copy );
            }
            // Zeichne 2 dicke Linien zwischen Header und unterschiedlichen
            // Materialtypen
            setLineIndex( 2, compositions.size() - 1 );
            // Jetzt die Auflegierungen
            for ( TransferMaterial transferMaterial : transferMaterials )
            {
                if ( ETransferMaterialType.findType( transferMaterial.getType() ) != ETransferMaterialType.VARIABLE )
                {
                    continue;
                }
                Analysis copy = AnalysisCalculator.computeCalculatedElements( transfer.getBatch(), !singleFurnace, transferMaterial.getAnalysis(), transferMaterial.getWeight() );
                copy.setType( transferMaterial.getType() );
                compositions.add( copy );
            }
        }

        this.compositions.addAll( compositions );

        computeSizes( specification );
    }

    private void displayCalculatedAnalysis( Batch batch, List<Analysis> analysis, String averageLineName, int[] lineIndex, boolean showSpecification, boolean compressAnalysis )
    {
        if ( analysis == null || batch == null )
        {
            return;
        }

        Specification specification = batch.getSpecification();

        final List<Composition> compositions = new ArrayList<>();

        elementFTCF.setSpecification( specification );
        elementFTCF.setCheckedAnalysis( null );

        HashSet<String> allElementNames = new HashSet<String>();
        List<CompositionElement> allElements = new ArrayList<CompositionElement>();
        if ( specification != null )
        {
            Composition oneSpecComp = specification.getMin();
            for ( CompositionElement compositionElement : oneSpecComp.getCompositionElements() )
            {
                allElementNames.add( compositionElement.getName() );
                allElements.add( compositionElement );
            }
        }
        for ( Analysis analyse : analysis )
        {
            List<CompositionElement> matElements = analyse.getCompositionElements();
            for ( CompositionElement matElement : matElements )
            {
                if ( allElementNames.contains( matElement.getName() ) )
                {
                    continue;
                }
                allElements.add( matElement );
                allElementNames.add( matElement.getName() );
            }
        }

        if ( showSpecification )
        {
            // berechne mittelwert
            final Analysis middledAnalysis = AnalysisCalculator.createAverageFromAnalysis( averageLineName, analysis, specification );
            elementFTCF.setCheckedAnalysis( middledAnalysis );

            averageAnalysis = middledAnalysis;
            restructureTable( allElements, specification, compressAnalysis, middledAnalysis );

            if ( specification != null )
            {
                compositions.add( specification.getMin() );
            }

            compositions.add( middledAnalysis );

            if ( specification != null )
            {
                compositions.add( specification.getMax() );
            }
        }
        else
        {
            averageAnalysis = null;
            restructureTable( allElements, specification, compressAnalysis, null );
        }

        for ( Analysis analyse : analysis )
        {
            Analysis copy = AnalysisCalculator.computeCalculatedElements( batch, false, analyse, analyse.getWeight() );
            compositions.add( copy );
        }

        setLineIndex( lineIndex );

        this.compositions.addAll( compositions );

        computeSizes( specification );
    }

    private void displayAnalysis( Analysis analysis, Specification specification, boolean compressAnalysis )
    {
        if ( analysis == null )
        {
            return;
        }

        final List<Composition> compositions = new ArrayList<>();

        if ( specification == null )
        {
            elementFTCF.setFormat( StringTools.N05F );
        }
        else
        {
            elementFTCF.setSpecification( specification );
        }
        elementFTCF.setCheckedAnalysis( null );

        HashSet<String> allElementNames = new HashSet<>();
        List<CompositionElement> allElements = new ArrayList<>();
        if ( specification != null )
        {
            Composition oneSpecComp = specification.getMin();
            for ( CompositionElement compositionElement : oneSpecComp.getCompositionElements() )
            {
                allElementNames.add( compositionElement.getName() );
                allElements.add( compositionElement );
            }
        }
        List<CompositionElement> matElements = analysis.getCompositionElements();
        for ( CompositionElement matElement : matElements )
        {
            if ( allElementNames.contains( matElement.getName() ) )
            {
                continue;
            }
            allElements.add( matElement );
            allElementNames.add( matElement.getName() );
        }
        averageAnalysis = analysis;
        restructureTable( allElements, specification, compressAnalysis, analysis );

        if ( specification != null )
        {
            compositions.add( specification.getMin() );
        }

        // berechne mittelwert
        elementFTCF.setCheckedAnalysis( analysis );
        compositions.add( analysis );

        if ( specification != null )
        {
            compositions.add( specification.getMax() );
        }

        if ( specification != null )
        {
            setLineIndex( 2 );
        }

        this.compositions.addAll( compositions );

        if ( specification != null )
        {
            AnalysisCalculator.addCalculatedElementsToAnalysis( analysis, specification );
        }

        computeSizes( specification );
    }

    private void displayCalculatedAnalysis( Batch batch, boolean compressAnalysis )
    {
        final List<Composition> compositions = new ArrayList<>();

        elementFTCF.setSpecification( batch.getSpecification() );
        elementFTCF.setCheckedAnalysis( null );
        HashSet<String> allElementNames = new HashSet<String>();
        List<CompositionElement> allElements = new ArrayList<CompositionElement>();
        if ( batch.getSpecification() != null )
        {
            Composition oneSpecComp = batch.getSpecification().getMin();
            for ( CompositionElement compositionElement : oneSpecComp.getCompositionElements() )
            {
                allElementNames.add( compositionElement.getName() );
                allElements.add( compositionElement );
            }
        }
        // Sumpf-Analyse hinzufügen
        if ( batch.getBottomAnalysis() != null && batch.getBottomWeight() > 0 )
        {
            List<CompositionElement> sumpElements = batch.getBottomAnalysis().getCompositionElements();
            for ( CompositionElement sumpElement : sumpElements )
            {
                if ( allElementNames.contains( sumpElement.getName() ) )
                {
                    continue;
                }
                allElements.add( sumpElement );
                allElementNames.add( sumpElement.getName() );
            }
        }

        // Alle Materialien des Batches hinzufügen
        List<TransferMaterial> batchTransferMaterials = new ArrayList<>( batch.getTransferMaterials() );
        Collections.reverse( batchTransferMaterials );
        for ( TransferMaterial batchTransferMaterial : batchTransferMaterials )
        {
            List<CompositionElement> matElements = batchTransferMaterial.getAnalysis().getCompositionElements();
            for ( CompositionElement matElement : matElements )
            {
                if ( allElementNames.contains( matElement.getName() ) )
                {
                    continue;
                }
                allElements.add( matElement );
                allElementNames.add( matElement.getName() );
            }
        }

        // Alle Materialien der Transfers hinzufügen
        List<Transfer> transfers = batch.getTransfers();
        for ( Transfer transfer : transfers )
        {
            if ( transfer.getFurnaceTransferMaterial() != null )
            {
                continue;
            }
            List<TransferMaterial> transferMaterials = transfer.getTransferMaterials();
            for ( TransferMaterial transferMaterial : transferMaterials )
            {
                List<CompositionElement> matElements = transferMaterial.getAnalysis().getCompositionElements();
                for ( CompositionElement matElement : matElements )
                {
                    if ( allElementNames.contains( matElement.getName() ) )
                    {
                        continue;
                    }
                    allElements.add( matElement );
                    allElementNames.add( matElement.getName() );
                }
            }
            if ( transfer.getFillmentTransferMaterial() != null )
            {
                List<CompositionElement> matElements = transfer.getFillmentTransferMaterial().getAnalysis().getCompositionElements();
                for ( CompositionElement matElement : matElements )
                {
                    if ( allElementNames.contains( matElement.getName() ) )
                    {
                        continue;
                    }
                    allElements.add( matElement );
                    allElementNames.add( matElement.getName() );
                }
            }
        }

        List<Analysis> analysis = new ArrayList<>();
        analysis.add( AnalysisCalculator.computeCalculatedElements( batch, false, batch.getBottomAnalysis(), batch.getBottomWeight() ) );
        for ( TransferMaterial batchTransferMaterial : batchTransferMaterials )
        {
            analysis.add( AnalysisCalculator.computeCalculatedElements( batch, false, batchTransferMaterial.getAnalysis(), batchTransferMaterial.getWeight() ) );
        }
        for ( Transfer transfer : transfers )
        {
            if ( transfer.getWeight() > 0 && transfer.getFurnaceTransferMaterial() == null )
            {
                Analysis transferAnalysis = AnalysisCalculator.createAverage( "Ø " + transfer.getName(), transfer, false );
                transferAnalysis.setWeight( transfer.getWeight() );
                analysis.add( transferAnalysis );
            }
        }
        // Gibt es in den letzten Batches ein 'leer ziehen', dann sollen unten
        // darunter die Mengen angezeigt werden
        if ( batch.getNumberOfTransferProcesses() > 0 )
        {
            List<Transfer> lastTransfers = batch.getTransfers( batch.getNumberOfTransferProcesses() - 1 );
            if ( lastTransfers != null )
            {
                for ( Transfer lastTransfer : lastTransfers )
                {
                    if ( lastTransfer.isMakeEmpty() && lastTransfer.getTargetWeight() > lastTransfer.getWeight() )
                    {
                        String lastTransferName = lastTransfer.getName();
                        lastTransferName = lastTransferName.substring( 0, lastTransferName.length() - 2 ) + "LG";
                        Analysis transferAnalysis = AnalysisCalculator.createAverage( "Ø " + lastTransferName, lastTransfer, false );
                        transferAnalysis.setWeight( lastTransfer.getTargetWeight() - lastTransfer.getWeight() );
                        analysis.add( transferAnalysis );
                    }
                }
            }
        }

        final Analysis middledAnalyse = AnalysisCalculator.createAverageFromAnalysis( "Ø", analysis, batch.getSpecification() );
        elementFTCF.setCheckedAnalysis( middledAnalyse );

        averageAnalysis = middledAnalyse;
        restructureTable( allElements, batch.getSpecification(), compressAnalysis, middledAnalyse );

        setLineIndex( 2 );

        if ( batch.getSpecification() != null )
        {
            compositions.add( batch.getSpecification().getMin() );
        }

        compositions.add( middledAnalyse );

        if ( batch.getSpecification() != null )
        {
            compositions.add( batch.getSpecification().getMax() );
        }

        compositions.addAll( analysis );

        this.compositions.addAll( compositions );

        computeSizes( batch.getSpecification() );
    }

    private TransferMaterial getTransferMaterialAtRow( int row )
    {
        boolean searchVariableMaterials = false;
        // int[] currentLineIndex = weightFTCF.getLineIndex();
        int lastLineIndex = 0;
        if ( currentLineIndex != null )
        {
            for ( int currentLine : currentLineIndex )
            {
                if ( currentLine > lastLineIndex )
                {
                    lastLineIndex = currentLine;
                }
            }
        }
        if ( row > lastLineIndex )
        {
            searchVariableMaterials = true;
        }

        Composition rowComposition = compositions.get( row );
        String rowName = rowComposition.getName();
        if ( currentTransfer != null )
        {
            TransferMaterial rowTransferMaterial = null;
            final List<TransferMaterial> allTransferMaterials = new ArrayList<>( currentTransfer.getTransferMaterials() );
            if ( currentTransfer.getFillmentTransferMaterial() != null )
            {
                allTransferMaterials.add( currentTransfer.getFillmentTransferMaterial() );
            }
            for ( TransferMaterial transferMaterial : allTransferMaterials )
            {
                if ( searchVariableMaterials && ETransferMaterialType.findType( transferMaterial.getType() ) != ETransferMaterialType.VARIABLE )
                {
                    continue;
                }
                else if ( searchVariableMaterials == false && ETransferMaterialType.findType( transferMaterial.getType() ) == ETransferMaterialType.VARIABLE )
                {
                    continue;
                }
                if ( transferMaterial.getAnalysis().getName().equals( rowName ) )
                {
                    rowTransferMaterial = transferMaterial;
                    break;
                }
            }
            return rowTransferMaterial;
        }
        else if ( currentBatch != null )
        {
            TransferMaterial rowTransferMaterial = null;
            ObservableList<TransferMaterial> transferMaterials = currentBatch.getTransferMaterials();
            for ( TransferMaterial transferMaterial : transferMaterials )
            {
                if ( transferMaterial.getAnalysis().getName().equals( rowName ) )
                {
                    rowTransferMaterial = transferMaterial;
                    break;
                }
            }
            return rowTransferMaterial;
        }
        return null;
    }

    public List<TransferMaterial> getSelectedTransferMaterials()
    {
        List<Integer> selectedIndices = getSelectionModel().getSelectedIndices();
        List<TransferMaterial> selectedTransferMaterials = new ArrayList<>();
        for ( Integer selectedIndex : selectedIndices )
        {
            TransferMaterial selectedTransferMaterial = getTransferMaterialAtRow( selectedIndex );
            if ( selectedTransferMaterial != null )
            {
                selectedTransferMaterials.add( selectedTransferMaterial );
            }
        }
        return selectedTransferMaterials;
    }

    //    public Window getOwner()
    //    {
    //        return owner;
    //    }
    //
    //    public void setOwner( Window owner )
    //    {
    //        this.owner = owner;
    //    }
    //
    //    public void setWindowSelectionModel( SingleSelectionModel<NamedModelElement> windowSelectionModel )
    //    {
    //        this.windowSelectionModel = windowSelectionModel;
    //        if ( windowSelectionModel != null )
    //        {
    //            windowSelectionModel.selectedItemProperty().addListener( ( p, o, n ) -> {
    //                Platform.runLater( () -> {
    //                    if ( isFocused() )
    //                    {
    //                        return;
    //                    }
    //                    getSelectionModel().clearSelection();
    //                    if ( n != null )
    //                    {
    //                        if ( n.equals( currentBatch ) )
    //                        {
    //                            getSelectionModel().select( 3 );
    //                            scrollTo( 3 );
    //                        }
    //                        if ( n.equals( currentTransfer ) )
    //                        {
    //                            getSelectionModel().select( 3 );
    //                            scrollTo( 3 );
    //                        }
    //                        else
    //                        {
    //                            for ( int i = 3; i < getItems().size(); i++ )
    //                            {
    //                                TransferMaterial selectedTransferMaterial = getTransferMaterialAtRow( i );
    //                                if ( n.equals( selectedTransferMaterial ) )
    //                                {
    //                                    getSelectionModel().select( i );
    //                                    scrollTo( i );
    //                                    break;
    //                                }
    //                            }
    //                        }
    //                    }
    //                } );
    //            } );
    //        }
    //    }

    //    public void setSelectedTransfer( Transfer newValue )
    //    {
    //        this.selectedTransfer = newValue;
    //    }

    //    private PopOver createAverageAnalysisPopOver( Batch batch, List<Analysis> analysis )
    //    {
    //        PopOver popOver = new PopOver();
    //
    //        popOver.setTitle( "Berechnete Analyse" );
    //
    //        AnalysisTable analysisTable = new AnalysisTable();
    //
    //        analysisTable.setOwner( owner );
    //
    //        analysisTable.setBatch( batch, analysis );
    //
    //        popOver.setContentNode( analysisTable );
    //
    //        popOver.setDetachable( true );
    //
    //        popOver.setDetached( true );
    //
    //        return popOver;
    //    }

    public boolean isDisableAverageLineColor()
    {
        return disableAverageLineColor;
    }

    public void setDisableAverageLineColor( boolean disableAverageLineColor )
    {
        this.disableAverageLineColor = disableAverageLineColor;
    }

    public Analysis getAverageAnalyse()
    {
        return averageAnalysis;
    }
}
