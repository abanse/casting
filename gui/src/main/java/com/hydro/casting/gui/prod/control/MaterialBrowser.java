package com.hydro.casting.gui.prod.control;

import com.google.inject.Inject;
import com.hydro.casting.common.constant.EMaterialCalcMode;
import com.hydro.casting.gui.ImagesCasting;
import com.hydro.casting.gui.model.*;
import com.hydro.casting.gui.model.common.EMaterialGroup;
import com.hydro.casting.gui.model.dnd.MaterialDND;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.Task;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.comp.SearchBox;
import com.hydro.core.gui.comp.TaskProgressPane;
import com.hydro.core.gui.comp.cell.TreeTableDateCellFactory;
import com.hydro.core.gui.comp.cell.TreeTableStringCellFactory;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.util.FXUtilities;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MaterialBrowser extends BorderPane implements MaterialsProvider
{
    private static Predicate<MaterialGroup> DEFAULT_MATERIAL_GROUP_PREDICATE = new Predicate<MaterialGroup>()
    {
        @Override
        public boolean test( MaterialGroup materialGroup )
        {
            if ( materialGroup.getName().equals( EMaterialGroup.SPEC_MATERIAL.getApk() ) )
            {
                return false;
            }
            return true;
        }
    };

    @Inject
    private TaskManager taskManager;

    @Inject
    private ClientModelManager clientModelManager;

    @FXML
    private Button wizard;

    @FXML
    private SearchBox searchBox;

    private TaskProgressPane progressPane = new TaskProgressPane();

    private TreeTableView<NamedModelElement> materialTreeTable;

    private EMaterialCalcMode materialCalcMode = null;

    private Predicate<MaterialGroup> materialGroupPredicate = DEFAULT_MATERIAL_GROUP_PREDICATE;

    private SimpleObjectProperty<Material> selectedMaterial = new SimpleObjectProperty<Material>();

    private List<TreeTableColumn<NamedModelElement, Number>> analyseColumns = new ArrayList<>();

    private TreeTableColumn<NamedModelElement, Number> calcWeightCol;

    private Transfer lastCalcedTransfer;

    private Batch batch;

    private Window owner;

    public MaterialBrowser()
    {
        FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource( "MaterialBrowser.fxml" ) );
        fxmlLoader.setRoot( this );
        fxmlLoader.setController( this );

        try
        {
            fxmlLoader.load();
        }
        catch ( IOException exception )
        {
            throw new RuntimeException( exception );
        }
    }

    public void registerTask( Task task )
    {
        progressPane.addTask( task );
    }

    @FXML
    void initialize()
    {
        searchBox.setPromptText( "Suche z.B. * (ohne Gruppierung) oder *AL (alle mit AL) oder *FE>0.5 (alle mit FE > 0.5)" );
        searchBox.textProperty().addListener( new ChangeListener<String>()
        {
            @Override
            public void changed( ObservableValue<? extends String> ov, String t, String t1 )
            {
                filterResults();
            }
        } );

        initializePresentation();
    }

    @FXML
    void wizard( ActionEvent event )
    {
        /*
        CalculateSpecMaterialWeight calculateSpecMaterialWeight = new CalculateSpecMaterialWeight( owner, batch, getCurrentMaterials(), this );
        CommandManager.getInstance().executeCommand( calculateSpecMaterialWeight );
         */
    }

    @FXML
    void reload( ActionEvent event )
    {
        //taskManager.executeTask( reloadModelMaterialTask );
    }

    public List<Material> getCurrentMaterials()
    {
        List<Material> currentMaterials = new ArrayList<>();
        addRecursiveMaterials( currentMaterials, materialTreeTable.getRoot() );
        return currentMaterials;
    }

    private void addRecursiveMaterials( List<Material> materials, TreeItem<NamedModelElement> element )
    {
        if ( element.getValue() instanceof Material )
        {
            materials.add( (Material) element.getValue() );
        }
        List<TreeItem<NamedModelElement>> childs = element.getChildren();
        for ( TreeItem<NamedModelElement> child : childs )
        {
            addRecursiveMaterials( materials, child );
        }
    }

    public void enableWizardMode()
    {
        wizard.setVisible( true );
    }

    public void enableSearchMode()
    {
        wizard.setVisible( false );

        GridPane.setColumnSpan( searchBox, 2 );
    }

    private void initializePresentation()
    {
        if ( materialTreeTable == null )
        {
            materialTreeTable = createMaterialTreeTable();
        }
        materialTreeTable.setRoot( null );

        progressPane.setMainNode( materialTreeTable );

        setCenter( progressPane );
    }

    private TreeTableView<NamedModelElement> createMaterialTreeTable()
    {
        TreeTableView<NamedModelElement> materialTreeTable = new TreeTableView<>();

        materialTreeTable.setOnDragDetected( new EventHandler<MouseEvent>()
        {
            @Override
            public void handle( MouseEvent event )
            {
                if ( event.getY() < 30 )
                {
                    return;
                }
                TreeItem<NamedModelElement> selectedTreeItem = materialTreeTable.getSelectionModel().getSelectedItem();
                if ( selectedTreeItem != null && selectedTreeItem.getValue() instanceof Material )
                {
                    final Dragboard dragBoard;
                    if ( event.isShiftDown() )
                    {
                        dragBoard = materialTreeTable.startDragAndDrop( TransferMode.COPY );
                    }
                    else
                    {
                        dragBoard = materialTreeTable.startDragAndDrop( TransferMode.MOVE );
                    }

                    ClipboardContent content = new ClipboardContent();

                    final Material selectedMaterial = (Material) selectedTreeItem.getValue();
                    final MaterialDND materialDND;
                    if ( calcWeightCol != null && calcWeightCol.isVisible() )
                    {
                        if ( materialCalcMode == EMaterialCalcMode.WITH_VAR_MATERIALS )
                        {
                            materialDND = new MaterialDND( selectedMaterial.getObjid(), selectedMaterial.getCalcWeight(), true );
                        }
                        else
                        {
                            materialDND = new MaterialDND( selectedMaterial.getObjid(), selectedMaterial.getCalcWeight(), false );
                        }
                    }
                    else if ( selectedMaterial.getUnitWeight() > 0 )
                    {
                        materialDND = new MaterialDND( selectedMaterial.getObjid(), selectedMaterial.getUnitWeight(), false );
                    }
                    else
                    {
                        materialDND = new MaterialDND( selectedMaterial.getObjid(), 1000, false );
                    }
                    content.putString( materialDND.toString() );

                    dragBoard.setDragView( ImagesCasting.MATERIAL.load() );
                    dragBoard.setContent( content );
                }
            }
        } );

        TreeTableColumn<NamedModelElement, String> nameCol = new TreeTableColumn<>( "Name" );
        nameCol.setCellValueFactory( new TreeItemPropertyValueFactory<>( "name" ) );
        nameCol.setPrefWidth( 200 );

        final TreeTableColumn<NamedModelElement, String> materialNrCol = new TreeTableColumn<>( "Material-Nr." );
        materialNrCol.setCellValueFactory( new TreeItemPropertyValueFactory<>( "apk" ) );
        final TreeTableStringCellFactory materialNrCellFactory = new TreeTableStringCellFactory();
        materialNrCellFactory.setBeginIndex( 10 );
        materialNrCellFactory.setEndIndex( 18 );
        materialNrCol.setCellFactory( materialNrCellFactory );
        materialNrCol.setPrefWidth( 85 );

        final TreeTableColumn<NamedModelElement, String> deliveryChargeCol = new TreeTableColumn<>( "Liefer-Charge" );
        deliveryChargeCol.setCellValueFactory( new TreeItemPropertyValueFactory<>( "deliveryCharge" ) );
        deliveryChargeCol.setPrefWidth( 85 );

        final TreeTableColumn<NamedModelElement, LocalDateTime> generationSuccessTSCol = new TreeTableColumn<>( "Einlagerung" );
        generationSuccessTSCol.setCellValueFactory( new TreeItemPropertyValueFactory<>( "generationSuccessTS" ) );
        generationSuccessTSCol.setPrefWidth( 85 );
        final TreeTableDateCellFactory<NamedModelElement, LocalDateTime> generationSuccessDF = new TreeTableDateCellFactory<>();
        generationSuccessDF.setDateFormat( "dd.MM.yy" );
        generationSuccessTSCol.setCellFactory( generationSuccessDF );

        TreeTableColumn<NamedModelElement, Number> weightCol = new TreeTableColumn<>( "Menge[t]" );
        weightCol.setStyle( "-fx-alignment: CENTER-RIGHT;" );
        weightCol.setCellValueFactory( new Callback<CellDataFeatures<NamedModelElement, Number>, ObservableValue<Number>>()
        {
            @Override
            public ObservableValue<Number> call( CellDataFeatures<NamedModelElement, Number> param )
            {
                if ( param.getValue() != null && param.getValue().getValue() != null && param.getValue().getValue() instanceof Material )
                {
                    return ( (Material) param.getValue().getValue() ).weightProperty().divide( 1000.0 );
                }
                return null;
            }
        } );
        weightCol.setCellFactory( new Callback<TreeTableColumn<NamedModelElement, Number>, TreeTableCell<NamedModelElement, Number>>()
        {
            @Override
            public TreeTableCell<NamedModelElement, Number> call( TreeTableColumn<NamedModelElement, Number> param )
            {
                return new TreeTableCell<NamedModelElement, Number>()
                {
                    @Override
                    protected void updateItem( Number item, boolean empty )
                    {
                        super.updateItem( item, empty );

                        if ( empty || item == null )
                        {
                            setText( null );
                            setGraphic( null );
                        }
                        else
                        {
                            setText( Material.WEIGHT_T_FORMATTER.format( item ) );
                        }
                    }
                };
            }
        } );

        TreeTableColumn<NamedModelElement, Number> unitWeightCol = new TreeTableColumn<>( "Stückgew.[kg]" );
        unitWeightCol.setStyle( "-fx-alignment: CENTER-RIGHT;" );
        unitWeightCol.setCellValueFactory( new Callback<CellDataFeatures<NamedModelElement, Number>, ObservableValue<Number>>()
        {
            @Override
            public ObservableValue<Number> call( CellDataFeatures<NamedModelElement, Number> param )
            {
                if ( param.getValue() != null && param.getValue().getValue() != null && param.getValue().getValue() instanceof Material )
                {
                    return ( (Material) param.getValue().getValue() ).unitWeightProperty();
                }
                return null;
            }
        } );
        unitWeightCol.setCellFactory( new Callback<TreeTableColumn<NamedModelElement, Number>, TreeTableCell<NamedModelElement, Number>>()
        {
            @Override
            public TreeTableCell<NamedModelElement, Number> call( TreeTableColumn<NamedModelElement, Number> param )
            {
                return new TreeTableCell<NamedModelElement, Number>()
                {
                    @Override
                    protected void updateItem( Number item, boolean empty )
                    {
                        super.updateItem( item, empty );

                        if ( empty || item == null || item.doubleValue() < 0 )
                        {
                            setText( null );
                            setGraphic( null );
                        }
                        else
                        {
                            setText( Material.WEIGHT_KG_FORMATTER.format( item ) );
                        }
                    }
                };
            }
        } );

        calcWeightCol = new TreeTableColumn<>( "Einzusetzen[kg]" );
        calcWeightCol.setStyle( "-fx-alignment: CENTER-RIGHT" );
        calcWeightCol.setCellValueFactory( new Callback<CellDataFeatures<NamedModelElement, Number>, ObservableValue<Number>>()
        {
            @Override
            public ObservableValue<Number> call( CellDataFeatures<NamedModelElement, Number> param )
            {
                if ( param.getValue() != null && param.getValue().getValue() != null && param.getValue().getValue() instanceof Material )
                {
                    return ( (Material) param.getValue().getValue() ).calcWeightProperty();
                }
                return null;
            }
        } );
        calcWeightCol.setCellFactory( new Callback<TreeTableColumn<NamedModelElement, Number>, TreeTableCell<NamedModelElement, Number>>()
        {
            @Override
            public TreeTableCell<NamedModelElement, Number> call( TreeTableColumn<NamedModelElement, Number> param )
            {
                return new TreeTableCell<NamedModelElement, Number>()
                {
                    @Override
                    protected void updateItem( Number item, boolean empty )
                    {
                        super.updateItem( item, empty );

                        if ( empty || item == null || item.doubleValue() <= 0 )
                        {
                            setText( null );
                            setGraphic( null );
                        }
                        else
                        {
                            setText( Material.WEIGHT_T_FORMATTER.format( item ) );
                        }
                        if ( empty || item == null )
                        {
                            setStyle( null );
                        }
                        else
                        {
                            setStyle( "-fx-background-color: #d4cda655;" );
                        }
                    }
                };
            }
        } );
        calcWeightCol.setVisible( false );

        //        TreeTableColumn<NamedModelElement, String> colorCol = new TreeTableColumn<>( "" );
        //        colorCol.setCellFactory( new Callback<TreeTableColumn<NamedModelElement, String>, TreeTableCell<NamedModelElement, String>>()
        //        {
        //            @Override
        //            public TreeTableCell<NamedModelElement, String> call( TreeTableColumn<NamedModelElement, String> param )
        //            {
        //                return new TreeTableCell<NamedModelElement, String>()
        //                {
        //
        //                    @Override
        //                    protected void updateItem( String item, boolean empty )
        //                    {
        //                        super.updateItem( item, empty );
        //
        //                        if ( getTreeTableRow().getItem() instanceof Material && ( (Material) getTreeTableRow().getItem() ).getPresentationColor() != null )
        //                        {
        //                            String colorValues = Integer.toHexString( ( (Material) getTreeTableRow().getItem() ).getPresentationColor().hashCode() );
        //                            setStyle( "-fx-background-color: #" + colorValues + ";" );
        //                        }
        //                        else
        //                        {
        //                            setStyle( "" );
        //                        }
        //                    }
        //                };
        //            }
        //        } );
        //        colorCol.setMinWidth( 20 );
        //        colorCol.setMaxWidth( 20 );

        materialTreeTable.getColumns().add( nameCol );
        materialTreeTable.getColumns().add( materialNrCol );
        materialTreeTable.getColumns().add( deliveryChargeCol );
        materialTreeTable.getColumns().add( generationSuccessTSCol );
        //        materialTreeTable.getColumns().add( colorCol );
        materialTreeTable.getColumns().add( weightCol );
        materialTreeTable.getColumns().add( unitWeightCol );
        materialTreeTable.getColumns().add( calcWeightCol );

        materialTreeTable.setShowRoot( false );

        materialTreeTable.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<TreeItem<NamedModelElement>>()
        {
            @Override
            public void changed( ObservableValue<? extends TreeItem<NamedModelElement>> observable, TreeItem<NamedModelElement> oldValue, TreeItem<NamedModelElement> newValue )
            {
                if ( newValue != null && newValue.getValue() instanceof Material )
                {
                    selectedMaterial.set( ( (Material) newValue.getValue() ) );
                }
                else
                {
                    selectedMaterial.set( null );
                }
            }
        } );

        return materialTreeTable;
    }

    private void addAnalyseColumns( List<NamedModelElement> targetElements )
    {
        if ( analyseColumns.isEmpty() == false )
        {
            for ( TreeTableColumn<NamedModelElement, Number> treeTableColumn : analyseColumns )
            {
                materialTreeTable.getColumns().remove( treeTableColumn );
            }
        }

        List<Material> materials = new ArrayList<>();
        for ( NamedModelElement targetElement : targetElements )
        {
            if ( targetElement instanceof Material )
            {
                materials.add( (Material) targetElement );
            }
            if ( targetElement instanceof MaterialGroup )
            {
                materials.addAll( ( (MaterialGroup) targetElement ).getMaterials() );
            }
        }

        HashSet<String> allElementNames = new HashSet<String>();
        List<CompositionElement> allElements = new ArrayList<CompositionElement>();
        for ( Material material : materials )
        {
            if ( material.getAnalysis() == null || material.getAnalysis().getCompositionElements() == null )
            {
                continue;
            }
            List<CompositionElement> matElements = material.getAnalysis().getCompositionElements();
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

        for ( CompositionElement compositionElement : allElements )
        {
            TreeTableColumn<NamedModelElement, Number> elementCol = new TreeTableColumn<>( compositionElement.getName() );
            elementCol.setStyle( "-fx-alignment: CENTER-RIGHT;" );
            elementCol.setCellValueFactory( new Callback<CellDataFeatures<NamedModelElement, Number>, ObservableValue<Number>>()
            {
                @Override
                public ObservableValue<Number> call( CellDataFeatures<NamedModelElement, Number> param )
                {
                    if ( param.getValue() != null && param.getValue().getValue() != null && param.getValue().getValue() instanceof Material )
                    {
                        final Material material = ( (Material) param.getValue().getValue() );
                        if ( material.getAnalysis() == null || material.getAnalysis().compositionElementValueProperty( compositionElement.getName() ) == null )
                        {
                            return null;
                        }
                        return material.getAnalysis().compositionElementValueProperty( compositionElement.getName() );
                    }
                    return null;
                }
            } );
            elementCol.setCellFactory( new Callback<TreeTableColumn<NamedModelElement, Number>, TreeTableCell<NamedModelElement, Number>>()
            {
                @Override
                public TreeTableCell<NamedModelElement, Number> call( TreeTableColumn<NamedModelElement, Number> param )
                {
                    return new TreeTableCell<NamedModelElement, Number>()
                    {
                        @Override
                        protected void updateItem( Number item, boolean empty )
                        {
                            super.updateItem( item, empty );

                            if ( empty || item == null )
                            {
                                setText( null );
                                setGraphic( null );
                            }
                            else
                            {
                                String formatedNumber = CompositionElement.ELEMENT_FORMATTER.format( item );

                                setText( formatedNumber );
                            }
                        }
                    };
                }
            } );
            elementCol.setPrefWidth( 55.0 );
            analyseColumns.add( elementCol );
            materialTreeTable.getColumns().add( elementCol );
        }
    }

    private void filterResults()
    {
        filterMaterials( searchBox.getText() );
    }

    public void loadMaterials()
    {
        filterMaterials( null );
    }

    public void loadMaterials( Predicate<MaterialGroup> materialGroupPredicate )
    {
        this.materialGroupPredicate = materialGroupPredicate;
        filterMaterials( null );
    }

    private void filterMaterials( String filteredValue )
    {
        //MeltingPlan meltingPlan = MeltingPlan.getInstance();

        final ClientModel casterClientModel = clientModelManager.getClientModel( CastingClientModel.ID );

        List<MaterialGroup> materialGroups = casterClientModel.getView( CastingClientModel.CHARGING_MATERIAL_GROUP_VIEW_ID );
        if ( materialGroupPredicate != null )
        {
            materialGroups = materialGroups.stream().filter( materialGroupPredicate ).collect( Collectors.toList() );
        }

        initializePresentation();

        selectedMaterial.set( null );

        Predicate<Material> materialFilter = null;

        List<NamedModelElement> currentElements = new ArrayList<>();

        if ( filteredValue == null || filteredValue.length() <= 0 )
        {
            for ( MaterialGroup materialGroup : materialGroups )
            {
                currentElements.add( materialGroup );
            }
        }
        else if ( filteredValue.equals( "*" ) )
        {
            for ( MaterialGroup materialGroup : materialGroups )
            {
                List<Material> materials = materialGroup.getMaterials();
                for ( Material material : materials )
                {
                    currentElements.add( material );
                }
            }
        }
        else
        {
            if ( filteredValue.startsWith( "*" ) )
            {
                for ( MaterialGroup materialGroup : materialGroups )
                {
                    List<Material> materials = materialGroup.getMaterials();
                    currentElements.addAll( materials );
                }
            }
            else
            {
                String textFilter = filteredValue.toLowerCase();
                if ( textFilter.indexOf( '*' ) > 0 )
                {
                    textFilter = textFilter.substring( 0, textFilter.indexOf( '*' ) );
                }
                for ( MaterialGroup materialGroup : materialGroups )
                {
                    if ( materialGroup.getName().toLowerCase().indexOf( textFilter ) >= 0 )
                    {
                        currentElements.add( materialGroup );
                        continue;
                    }
                    ObservableList<Material> materials = materialGroup.getMaterials();
                    for ( Material material : materials )
                    {
                        if ( material.getName().toLowerCase().indexOf( textFilter ) >= 0 )
                        {
                            currentElements.add( material );
                        }
                        if ( material.getApk() != null && material.getApk().toLowerCase().indexOf( textFilter ) >= 0 )
                        {
                            currentElements.add( material );
                        }
                    }
                }
            }

            if ( filteredValue.indexOf( '*' ) >= 0 && filteredValue.length() > ( filteredValue.indexOf( '*' ) + 1 ) )
            {
                String expression = filteredValue.substring( filteredValue.indexOf( '*' ) + 1 );
                String[] expressionParts = expression.split( " " );
                for ( String expressionPart : expressionParts )
                {
                    if ( materialFilter == null )
                    {
                        materialFilter = new MaterialFilter( expressionPart );
                    }
                    else
                    {
                        materialFilter = materialFilter.and( new MaterialFilter( expressionPart ) );
                    }
                }
            }
        }

        if ( calcWeightCol != null && calcWeightCol.isVisible() )
        {
            Collections.sort( currentElements, new Comparator<NamedModelElement>()
            {
                @Override
                public int compare( NamedModelElement o1, NamedModelElement o2 )
                {
                    if ( o1 instanceof Material && o2 instanceof Material )
                    {
                        return (int) Math.round( ( (Material) o2 ).getCalcWeight() - ( (Material) o1 ).getCalcWeight() );
                    }
                    return o1.getName().compareTo( o2.getName() );
                }
            } );
        }

        addAnalyseColumns( currentElements );
        TreeItem<NamedModelElement> rootTreeItem = new TreeItem<>();
        for ( NamedModelElement currentElement : currentElements )
        {
            if ( currentElement instanceof Material )
            {
                Material material = (Material) currentElement;
                if ( materialFilter == null || materialFilter.test( material ) )
                {
                    TreeItem<NamedModelElement> materialTreeItem = new TreeItem<NamedModelElement>( (Material) currentElement );
                    rootTreeItem.getChildren().add( materialTreeItem );
                }
            }
            if ( currentElement instanceof MaterialGroup )
            {
                ObservableList<Material> groupMaterials = ( (MaterialGroup) currentElement ).getMaterials();
                if ( materialFilter != null )
                {
                    groupMaterials = groupMaterials.filtered( materialFilter );
                }
                if ( groupMaterials.isEmpty() )
                {
                    continue;
                }
                if ( calcWeightCol.isVisible() )
                {
                    groupMaterials = groupMaterials.sorted( new Comparator<Material>()
                    {
                        @Override
                        public int compare( Material o1, Material o2 )
                        {
                            return (int) Math.round( o2.getCalcWeight() - o1.getCalcWeight() );
                        }
                    } );
                }
                TreeItem<NamedModelElement> groupItem = new TreeItem<NamedModelElement>( (MaterialGroup) currentElement );
                for ( Material groupMaterial : groupMaterials )
                {
                    TreeItem<NamedModelElement> materialTreeItem = new TreeItem<NamedModelElement>( groupMaterial );
                    groupItem.getChildren().add( materialTreeItem );
                }
                groupItem.setExpanded( true );
                rootTreeItem.getChildren().add( groupItem );
            }
        }
        materialTreeTable.setRoot( rootTreeItem );
    }

    public final SimpleObjectProperty<Material> selectedMaterialProperty()
    {
        return this.selectedMaterial;
    }

    public final Material getSelectedMaterial()
    {
        return this.selectedMaterialProperty().get();
    }

    public Batch getBatch()
    {
        return batch;
    }

    public void setBatch( Batch batch )
    {
        this.batch = batch;
    }

    public Window getOwner()
    {
        return owner;
    }

    public void setOwner( Window owner )
    {
        this.owner = owner;
    }

    private class MaterialFilter implements Predicate<Material>
    {
        private String filterExpression;
        private String searchTitle;

        public MaterialFilter( String filterExpression )
        {
            this.filterExpression = filterExpression;
        }

        @Override
        public boolean test( Material material )
        {
            if ( material.getAnalysis() == null )
            {
                return false;
            }
            if ( filterExpression.contains( ">" ) )
            {
                String valueName = filterExpression.substring( 0, filterExpression.indexOf( ">" ) ).toUpperCase();
                String searchValueS = filterExpression.substring( filterExpression.indexOf( ">" ) + 1 );
                searchTitle = "mit " + valueName + " größer " + searchValueS;
                double searchValue;
                try
                {
                    searchValue = Double.parseDouble( searchValueS.replace( ',', '.' ) );
                }
                catch ( NumberFormatException nfex )
                {
                    return false;
                }
                if ( valueName != null && valueName.length() > 0 )
                {
                    if ( "Einsetzbar".toUpperCase().equals( valueName ) )
                    {
                        if ( material.getCalcWeight() > searchValue )
                        {
                            return true;
                        }
                    }
                    else
                    {
                        CompositionElement compositionElement = material.getAnalysis().getCompositionElement( valueName );
                        if ( compositionElement == null )
                        {
                            return false;
                        }
                        if ( compositionElement.getElementValue() > searchValue )
                        {
                            return true;
                        }
                    }
                }
            }
            else if ( filterExpression.contains( "<" ) )
            {
                String valueName = filterExpression.substring( 0, filterExpression.indexOf( "<" ) ).toUpperCase();
                String searchValueS = filterExpression.substring( filterExpression.indexOf( "<" ) + 1 );
                searchTitle = "mit " + valueName + " kleiner " + searchValueS;
                double searchValue;
                try
                {
                    searchValue = Double.parseDouble( searchValueS.replace( ',', '.' ) );
                }
                catch ( NumberFormatException nfex )
                {
                    return false;
                }
                if ( valueName != null && valueName.length() > 0 )
                {
                    if ( "Einsetzbar".toUpperCase().equals( valueName ) )
                    {
                        if ( material.getCalcWeight() < searchValue )
                        {
                            return true;
                        }
                    }
                    else
                    {
                        CompositionElement compositionElement = material.getAnalysis().getCompositionElement( valueName );
                        if ( compositionElement == null )
                        {
                            return true;
                        }
                        if ( compositionElement.getElementValue() < searchValue )
                        {
                            return true;
                        }
                    }
                }
            }
            else
            {
                String valueName = filterExpression.toUpperCase();
                if ( "Einsetzbar".toUpperCase().equals( valueName ) )
                {
                    searchTitle = "mit zudienbarer Menge";
                    if ( material.getCalcWeight() > 0.1 )
                    {
                        return true;
                    }
                }
                else
                {
                    searchTitle = "mit " + valueName;
                    if ( material.getAnalysis().getCompositionElement( valueName ) != null )
                    {
                        return true;
                    }
                }
            }

            return false;
        }

        @Override
        public String toString()
        {
            return searchTitle;
        }
    }

    @Override
    public void setMaterialCalcMode( EMaterialCalcMode materialCalcMode, Transfer transfer )
    {
        this.materialCalcMode = materialCalcMode;
        this.lastCalcedTransfer = transfer;
        if ( calcWeightCol != null )
        {
            if ( materialCalcMode == null )
            {
                calcWeightCol.setVisible( false );
                return;
            }
            calcWeightCol.setVisible( true );
            calcWeightCol.getTreeTableView().sort();
        }
        if ( searchBox.getText() == null || searchBox.getText().length() <= 0 )
        {
            if ( materialCalcMode == EMaterialCalcMode.SPEC_MATERIAL )
            {
                int specMaterialRatio = (int) ( 0.9 * 100.0 );
                searchBox.setText( "*Einsetzbar>" + specMaterialRatio );
            }
            //            else
            //            {
            //searchBox.setText( "*" );
            //            }
        }
        else
        {
            filterResults();
        }

        FXUtilities.flash( materialTreeTable, "Die Daten wurden berechnet" );
    }

    @Override
    public void clearMaterialCalculation()
    {
        setMaterialCalcMode( null, null );
    }

    public Transfer getLastCalcedTransfer()
    {
        return this.lastCalcedTransfer;
    }
}
