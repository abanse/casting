package com.hydro.casting.gui.prod.view.detail;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.control.VisualInspectionDetailController;
import com.hydro.casting.gui.prod.task.SaveInspectionTask;
import com.hydro.casting.server.contract.dto.InspectionDTO;
import com.hydro.casting.server.contract.dto.VisualInspectionDTO;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
@ViewDeclaration( id = VisualInspectionDetailViewController.ID, fxmlFile = "/com/hydro/casting/gui/prod/view/detail/VisualInspectionDetailView.fxml", type = ViewType.DETAIL )
public class VisualInspectionDetailViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.DETAILS.VISUAL_INSPECTION.VIEW;

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
    private VisualInspectionDetailController detailController;

    private VisualInspectionDTO visualInspectionDTO;

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
        if ( startObject instanceof VisualInspectionDTO )
        {
            visualInspectionDTO = (VisualInspectionDTO) startObject;
        }
        else
        {
            visualInspectionDTO = null;
        }
        detailController.loadDetail( visualInspectionDTO );
    }

    @Override
    public void beforeShown( View view )
    {
        if ( visualInspectionDTO != null )
        {
            view.setTitle( "Arbeitsschritte " + visualInspectionDTO.getCasterSchedule().getChargeWithoutYear() );
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
