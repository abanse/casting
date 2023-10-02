package com.hydro.casting.gui.locking.workflow.control;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.gui.binding.BeanPathAdapter;
import com.hydro.core.gui.comp.DoubleTextField;
import com.hydro.core.gui.comp.IntegerTextField;
import com.hydro.core.gui.comp.StringTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.List;

public class LockingWorkflowDetailControlController extends LockingWorkflowDetailController<LockingWorkflowDTO>
{
    private static LockingWorkflowDTO EMPTY_BEAN = new LockingWorkflowDTO();

    @Inject
    private Injector injector;

//    @FXML
//    private StringTextField scheduleNbr;

    @FXML
    private StringTextField kst;

    @FXML
    private StringTextField lot;

//    @FXML
//    private StringTextField sublotInvSuffix;

//    @FXML
//    private StringTextField opSeq;

    @FXML
    private DoubleTextField gaugeOut;

    @FXML
    private DoubleTextField widthOut;

    @FXML
    private DoubleTextField lengthOut;

//    @FXML
//    private DoubleTextField outputLength;

//    @FXML
//    private DoubleTextField outputGauge;

//    @FXML
//    private DoubleTextField outputWidth;

//    @FXML
//    private DoubleTextField buildup;

//    @FXML
//    private IntegerTextField buildupMin;

//    @FXML
//    private IntegerTextField buildupMax;

//    @FXML
//    private IntegerTextField exitArbor;

//    @FXML
//    private DoubleTextField outerDiameter;

    @FXML
    private StringTextField castingId;

//    @FXML
//    private StringTextField spool;

//    @FXML
//    private StringTextField nextCostCenter;

//    @FXML
//    private StringTextField endCostCenter;

//    @FXML
//    private StringTextField openOperations;

    @FXML
    private BorderPane subMaterialsArea;

    @FXML
    private GridPane singleSubMaterial;

    @FXML
    private LockingWorkflowSubMaterialDetailControlController singleSubMaterialController;

    private TabPane multipleSubMaterials;

    private BeanPathAdapter<LockingWorkflowDTO> beanPathAdapter;

    public LockingWorkflowDetailControlController()
    {
        super( LockingWorkflowDTO.class );
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize()
    {
        beanPathAdapter = new BeanPathAdapter<>( EMPTY_BEAN );

//        beanPathAdapter.bindBidirectional( "scheduleNbr", scheduleNbr.textProperty() );
        beanPathAdapter.bindBidirectional( "kst", kst.textProperty() );
        beanPathAdapter.bindBidirectional( "material", lot.textProperty() );
//        beanPathAdapter.bindBidirectional( "sublotInvSuffix", sublotInvSuffix.textProperty() );
//        beanPathAdapter.bindBidirectional( "opSeq", opSeq.textProperty() );
        beanPathAdapter.bindBidirectional( "gaugeOut", gaugeOut.doubleValueProperty() );
        beanPathAdapter.bindBidirectional( "widthOut", widthOut.doubleValueProperty() );
        beanPathAdapter.bindBidirectional( "lengthOut", lengthOut.doubleValueProperty() );
//        beanPathAdapter.bindBidirectional( "outputLength", outputLength.doubleValueProperty() );
//        beanPathAdapter.bindBidirectional( "outputGauge", outputGauge.doubleValueProperty() );
//        beanPathAdapter.bindBidirectional( "outputWidth", outputWidth.doubleValueProperty() );
//        beanPathAdapter.bindBidirectional( "buildup", buildup.doubleValueProperty() );
//        beanPathAdapter.bindBidirectional( "buildupMin", buildupMin.intValueProperty() );
//        beanPathAdapter.bindBidirectional( "buildupMax", buildupMax.intValueProperty() );
//        beanPathAdapter.bindBidirectional( "exitArbor", exitArbor.intValueProperty() );
//        beanPathAdapter.bindBidirectional( "outerDiameter", outerDiameter.doubleValueProperty() );
        beanPathAdapter.bindBidirectional( "castSampleNbr", castingId.textProperty() );
//        beanPathAdapter.bindBidirectional( "spool", spool.textProperty() );
//        beanPathAdapter.bindBidirectional( "nextCostCenter", nextCostCenter.textProperty() );
//        beanPathAdapter.bindBidirectional( "endCostCenter", endCostCenter.textProperty() );
//        beanPathAdapter.bindBidirectional( "openOperations", openOperations.textProperty() );

        multipleSubMaterials = new TabPane();
        multipleSubMaterials.setTabClosingPolicy( TabClosingPolicy.UNAVAILABLE );
    }

    @Override
    public void loadData( LockingWorkflowDTO data )
    {
        if ( data != null )
        {
            if ( data.getChilds() != null || data.getParent() != null )
            {
                List<LockingWorkflowDTO> childs = data.getChilds();
                if ( childs == null )
                {
                    childs = data.getParent().getChilds();
                }
                beanPathAdapter.setBean( childs.get( 0 ) );
                subMaterialsArea.setCenter( multipleSubMaterials );
                synchronizeTabSize( childs.size() );
                int index = 0;
                int selIndex = 0;
                for ( LockingWorkflowDTO child : childs )
                {
                    Tab tab = multipleSubMaterials.getTabs().get( index );

                    if ( child.getFreeDate() == null )
                    {
                        if ( child.getOwner().equals( LockingWorkflowDTO.OWNER_AV ) )
                        {
                            tab.setStyle( "-fx-background-color: #c8e4ff;" );

                        }
                        else if ( child.getOwner().equals( LockingWorkflowDTO.OWNER_TCS ) )
                        {
                            tab.setStyle( "-fx-background-color: #ffff99;" );
                        }
                        else if ( child.getOwner().equals( LockingWorkflowDTO.OWNER_PROD ) )
                        {
                            tab.setStyle( "-fx-background-color: #ffffff;" );
                        }
                        else if ( child.getOwner().equals( LockingWorkflowDTO.OWNER_QS ) )
                        {
                            tab.setStyle( "-fx-background-color: #ff4040;" );
                        }
                        else if ( child.getOwner().equals( LockingWorkflowDTO.OWNER_SAP ) )
                        {
                            tab.setStyle( "-fx-background-color: #d2b48c;" );
                        }
                        else
                        {
                            tab.setStyle( "-fx-background-color: #ff0000;" ); // alarm
                        }
                    }
                    else
                    {
                        if ( Casting.LOCKING_WORKFLOW.CONTAINER_MARK.equals( child.getMaterialStatus() ) )
                        {
                            tab.setStyle( "-fx-background-color: #9870d6;" );
                        }
                        else if ( Casting.LOCKING_WORKFLOW.SCRAP_MARK.equals( child.getMaterialStatus() ) )
                        {
                            tab.setStyle( "-fx-background-color: #aabbcc;" );
                        }
                        else if ( Casting.LOCKING_WORKFLOW.FREE_MARK.equals( child.getMaterialStatus() ) )
                        {
                            tab.setStyle( "-fx-background-color: #98fb98;" );
                        }
                        else
                        {
                            tab.setStyle( "-fx-background-color: #ff0000;" ); // alarm
                        }
                    }

                    // if ( StringTools.isFilled( child.getDropId() ) )
                    // {
                    // if (child.getCutId() != null && child.getCutId() > 0)
                    // {
                    // tab.setText( child.getDropId() + child.getCutId() );
                    // }
                    // else
                    // {
                    // tab.setText( child.getDropId() );
                    // }
                    // }
                    // else if ( child.getCutId() != null && child.getCutId() > 0 )
                    // {
                    // tab.setText( "" + child.getCutId() );
                    // }
                    // else if ( child.getPaletteId() != null && child.getPaletteId() > 0 )
                    // {
                    // tab.setText( "P" + child.getPaletteId() );
                    // }
                    // else
                    // {
                    // tab.setText( "?" );
                    // }
                    tab.setText( child.getMaterial() );
                    Object userData = tab.getUserData();
                    if ( userData instanceof LockingWorkflowSubMaterialDetailControlController )
                    {
                        ( (LockingWorkflowSubMaterialDetailControlController) userData ).setLockingWorkflow( child );
                    }
                    if ( data.equals( child ) )
                    {
                        selIndex = index;
//                        buildupStyling( child );
                    }
                    index++;
                }
//                if ( selIndex == 0 ) // directory
//                {
//                    buildupStyling( childs.get( 0 ) );// zeigt inhalt vom ersten streifen
//                }

                multipleSubMaterials.getSelectionModel().select( selIndex );
                if ( childs.size() > 1 )
                {
                    multipleSubMaterials.getTabs().get( selIndex ).setStyle( "-fx-background-color: #cccccc;" );
                }
            }
            else
            {
                beanPathAdapter.setBean( data );
                subMaterialsArea.setCenter( singleSubMaterial );
                singleSubMaterialController.setLockingWorkflow( data );

//                buildupStyling( data );

            }
        }
        else
        {
            beanPathAdapter.setBean( EMPTY_BEAN );
            subMaterialsArea.setCenter( singleSubMaterial );
            singleSubMaterialController.setLockingWorkflow( EMPTY_BEAN );
        }
    }

    private void synchronizeTabSize( int size )
    {
        if ( multipleSubMaterials.getTabs().size() == size )
        {
            return;
        }
        if ( multipleSubMaterials.getTabs().size() > size )
        {
            multipleSubMaterials.getTabs().remove( size, multipleSubMaterials.getTabs().size() );
            return;
        }
        for ( int i = multipleSubMaterials.getTabs().size(); i < size; i++ )
        {
            Pair<Node, LockingWorkflowSubMaterialDetailControlController> subMaterialDetail = createSubMaterialDetail();
            Tab tab = new Tab( "?", subMaterialDetail.getKey() );
            tab.setUserData( subMaterialDetail.getValue() );
            multipleSubMaterials.getTabs().add( tab );
        }
    }

    private Pair<Node, LockingWorkflowSubMaterialDetailControlController> createSubMaterialDetail()
    {
        Node node = null;
        LockingWorkflowSubMaterialDetailControlController controller = null;
        try
        {
            FXMLLoader loader = new FXMLLoader( getClass().getResource( "/com/hydro/pdc/gui/locking/workflow/control/LockingWorkflowSubMaterialDetailControl.fxml" ) );
            loader.setControllerFactory( injector::getInstance );
            node = loader.load();
            controller = loader.getController();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return new Pair<>( node, controller );
    }

//    private void buildupStyling( LockingWorkflowDTO data )
//    {
//
//        buildup.setStyle( data.getBuildup() == 0.0 ? "-fx-text-fill: black;" : "-fx-text-fill: red;" );
//
//    }

}
