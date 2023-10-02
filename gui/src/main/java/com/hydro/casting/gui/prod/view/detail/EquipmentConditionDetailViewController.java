package com.hydro.casting.gui.prod.view.detail;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.control.EquipmentConditionDetailController;
import com.hydro.casting.gui.prod.task.SaveInspectionTask;
import com.hydro.casting.server.contract.dto.EquipmentConditionDTO;
import com.hydro.casting.server.contract.dto.InspectionDTO;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ViewDeclaration( id = EquipmentConditionDetailViewController.ID, fxmlFile = "/com/hydro/casting/gui/prod/view/detail/EquipmentConditionDetailView.fxml", type = ViewType.DETAIL )
public class EquipmentConditionDetailViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.DETAILS.EQUIPMENT_CONDITION.VIEW;

    @Inject
    private TaskManager taskManager;

    @Inject
    private Injector injector;

    @Inject
    private ViewManager viewManager;

    @Inject
    private SecurityManager securityManager;

    @FXML
    private SaveInspectionTask saveInspectionTask;

    @FXML
    private EquipmentConditionDetailController detailController;

    private EquipmentConditionDTO equipmentConditionDTO;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        detailController.setEditable( true );

        injector.injectMembers( saveInspectionTask );
        saveInspectionTask.setLocked( false );
        saveInspectionTask.setDisabled( false );
    }

    @FXML
    void cancel( ActionEvent event )
    {
        viewManager.backward();
    }

    @FXML
    void save( ActionEvent event )
    {
        final InspectionDTO inspectionDTO = detailController.getInspectionDTO();
        saveInspectionTask.setData( inspectionDTO );
        taskManager.executeTask( saveInspectionTask );
    }

    @Override
    public void setStartObject( Object startObject )
    {
        if ( startObject instanceof EquipmentConditionDTO )
        {
            equipmentConditionDTO = (EquipmentConditionDTO) startObject;
        }
        else
        {
            equipmentConditionDTO = null;
        }
        detailController.loadDetail( equipmentConditionDTO );
    }

    @Override
    public void beforeShown( View view )
    {
        if ( equipmentConditionDTO != null )
        {
            view.setTitle( "Anlagenzustand " + equipmentConditionDTO.getCasterSchedule().getChargeWithoutYear() );
        }
    }

    @Override
    public void activateView( View view )
    {
    }

    @Override
    public void deactivateView( View view )
    {
    }

    @Override
    public void reloadView( Object source, View view )
    {
    }
}
