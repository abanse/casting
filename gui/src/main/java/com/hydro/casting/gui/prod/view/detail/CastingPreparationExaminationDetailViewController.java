package com.hydro.casting.gui.prod.view.detail;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.control.CastingPreparationExaminationDetailController;
import com.hydro.casting.gui.prod.task.SaveInspectionTask;
import com.hydro.casting.server.contract.dto.CastingPreparationExaminationDTO;
import com.hydro.casting.server.contract.dto.InspectionDTO;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewManager;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
@ViewDeclaration( id = CastingPreparationExaminationDetailViewController.ID, fxmlFile = "/com/hydro/casting/gui/prod/view/detail/CastingPreparationExaminationDetailView.fxml", type = ViewType.DETAIL )
public class CastingPreparationExaminationDetailViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.DETAILS.CASTING_PREPARATION_EXAMINATION.VIEW;

    @Inject
    private TaskManager taskManager;

    @Inject
    private Injector injector;

    @Inject
    private ViewManager viewManager;

    @FXML
    private SaveInspectionTask saveInspectionTask;

    @FXML
    private CastingPreparationExaminationDetailController detailController;

    private CastingPreparationExaminationDTO castingPreparationExaminationDTO;

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
        if ( startObject instanceof CastingPreparationExaminationDTO )
        {
            castingPreparationExaminationDTO = (CastingPreparationExaminationDTO) startObject;
        }
        else
        {
            castingPreparationExaminationDTO = null;
        }
        detailController.loadDetail( castingPreparationExaminationDTO );
    }

    @Override
    public void beforeShown( View view )
    {
        if ( castingPreparationExaminationDTO != null )
        {
            view.setTitle( "Prüfung Angussvorbereitung " + castingPreparationExaminationDTO.getCasterSchedule().getChargeWithoutYear() );
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
