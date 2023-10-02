package com.hydro.casting.gui.locking.workflow.control;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.locking.workflow.task.LWCauserTask;
import com.hydro.casting.gui.locking.workflow.task.LWChangeMaterialLockLocationTask;
import com.hydro.casting.gui.locking.workflow.task.LWChangeMaterialLockTypeTask;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.binding.BeanPathAdapter;
import com.hydro.core.gui.comp.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;

public class LockingWorkflowSubMaterialDetailControlController extends LockingWorkflowDetailController<LockingWorkflowDTO>
{
    private static LockingWorkflowDTO EMPTY_BEAN = new LockingWorkflowDTO();

    @FXML
    private StringTextField defectTypeCat;

//    @FXML
//    private StringTextField kst;

    @FXML
    private LocalDateTimeTextField lockDate;

    @FXML
    private LocalDateTimeTextField freeDate;

    @FXML
    private DoubleTextField weightOut;

//    @FXML
//    private IntegerTextField pdWeight;

    @FXML
    private Label weightOutLabel;

//    @FXML
//    private Label pdWeightLabel;

    @FXML
    private StringTextField defectTypeLoc;

    @FXML
    private StringTextField ocDescription;

    @FXML
    private StringTextField userId;

    @FXML
    private StringTextField scrapCodeDescription;

    @FXML
    private StringTextField scrapAreaCodeDescription;

    @FXML
    private TextArea defectTypeRea;

    @FXML
    private Label freeDateLabel;

    @FXML
    private TaskButton changeCouser;

    @FXML
    private TaskButton changeScrapCode;

    @FXML
    private TaskButton changeScrapAreaCodeDescription;

    @Inject
    private LWCauserTask causerTask;

    @Inject
    private LWChangeMaterialLockTypeTask defectTypeCatTask;

    @Inject
    private LWChangeMaterialLockLocationTask changeMaterialLockLocationTask;

    @Inject
    private TaskManager taskManager;

    @Inject
    private SecurityManager securityManager;

    private BeanPathAdapter<LockingWorkflowDTO> beanPathAdapter;

    public LockingWorkflowSubMaterialDetailControlController()
    {
        super( LockingWorkflowDTO.class );
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize()
    {
        beanPathAdapter = new BeanPathAdapter<LockingWorkflowDTO>( EMPTY_BEAN );
        beanPathAdapter.bindBidirectional( "lockDate", lockDate.localDateTimeValueProperty(), LocalDateTime.class );
        beanPathAdapter.bindBidirectional( "freeDate", freeDate.localDateTimeValueProperty(), LocalDateTime.class );
        beanPathAdapter.bindBidirectional( "defectTypeCat", defectTypeCat.textProperty() );
        //beanPathAdapter.bindBidirectional( "kst", kst.textProperty() );
        beanPathAdapter.bindBidirectional( "weight", weightOut.doubleValueProperty() );
//        beanPathAdapter.bindBidirectional( "pdWeight", pdWeight.intValueProperty() );
        beanPathAdapter.bindBidirectional( "defectTypeLoc", defectTypeLoc.textProperty() );
        beanPathAdapter.bindBidirectional( "kst", ocDescription.textProperty() );
        beanPathAdapter.bindBidirectional( "userId", userId.textProperty() );
        beanPathAdapter.bindBidirectional( "scrapCodeDescription", scrapCodeDescription.textProperty() );
        beanPathAdapter.bindBidirectional( "scrapAreaCodeDescription", scrapAreaCodeDescription.textProperty() );
        beanPathAdapter.bindBidirectional( "defectTypeRea", defectTypeRea.textProperty() );

    }

    @Override
    public void loadData( LockingWorkflowDTO data )
    {
        if ( !securityManager.hasRole( SecurityCasting.LOCKING.ACTION.CHANGE_INITIATOR ) )
        {
            changeCouser.setLocked( true, "Sie haben keine Berechtigung für diese Funktion" );
        }
        else
        {
            changeCouser.setLocked( false, "" );
        }
        if ( !securityManager.hasRole( SecurityCasting.LOCKING.ACTION.CHANGE_SCRAP_CODE ) )
        {
            changeScrapCode.setLocked( true, "Sie haben keine Berechtigung für diese Funktion" );
        }
        else
        {
            changeScrapCode.setLocked( false, "" );
        }
        if ( !securityManager.hasRole( SecurityCasting.LOCKING.ACTION.CHANGE_MATERIAL_LOCK_LOCATION ) )
        {
            changeScrapAreaCodeDescription.setLocked( true, "Sie haben keine Berechtigung für diese Funktion" );
        }
        else
        {
            changeScrapAreaCodeDescription.setLocked( false, "" );
        }

        if ( data != null )
        {
            beanPathAdapter.setBean( data );
        }
        else
        {
            beanPathAdapter.setBean( EMPTY_BEAN );
        }
        if ( data.getMaterialStatus() != null && data.getMaterialStatus().length() == 1 && ( data.getMaterialStatus().startsWith( Casting.LOCKING_WORKFLOW.FREE_MARK ) || data.getMaterialStatus()
                .startsWith( Casting.LOCKING_WORKFLOW.SCRAP_MARK ) || data.getMaterialStatus().startsWith( Casting.LOCKING_WORKFLOW.CONTAINER_MARK ) ) )
        {
            freeDate.setVisible( true );
            freeDateLabel.setVisible( true );
        }
        else
        {
            freeDate.setVisible( false );
            freeDateLabel.setVisible( false );
        }
        defectTypeRea.setStyle( "-fx-font: 16 arial;" );
        //boolean isQT = Objects.equal( kst.getText(), Casting.COST_CENTER.CUTLENGTH_1 ) || Objects.equal( kst.getText(), Casting.COST_CENTER.CUTLENGTH_2 );
//        boolean isQT = false;
//        weightOut.setVisible( isQT == false );
//        weightOutLabel.setVisible( isQT == false );
//        pdWeight.setVisible( isQT == true );
//        pdWeightLabel.setVisible( isQT == true );
        //kst.setVisible( false );
    }


    @FXML
    void changeCouser( ActionEvent event )
    {
        causerTask.setTargetNode( ocDescription );
        causerTask.setLockingWorkflowDTO( beanPathAdapter.getBean() );
        taskManager.executeTask( causerTask );
    }

    @FXML
    void changeScrapCode( ActionEvent event )
    {
        defectTypeCatTask.setTargetNode( defectTypeCat );
        defectTypeCatTask.setLockingWorkflowDTO( beanPathAdapter.getBean() );
        taskManager.executeTask( defectTypeCatTask );
    }

    @FXML
    void changeScrapAreaCodeDescription( ActionEvent actionEvent )
    {
        changeMaterialLockLocationTask.setTargetNode( scrapAreaCodeDescription );
        changeMaterialLockLocationTask.setLockingWorkflowDTO( beanPathAdapter.getBean() );
        taskManager.executeTask( changeMaterialLockLocationTask );
    }
}
