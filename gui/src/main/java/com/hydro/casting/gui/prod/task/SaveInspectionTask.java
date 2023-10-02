package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.InspectionDTO;
import com.hydro.casting.server.contract.prod.ProcessDocuBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.ViewManager;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;

public class SaveInspectionTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private ViewManager viewManager;

    private InspectionDTO inspectionDTO;

    @Override
    public String getId()
    {
        return SecurityCasting.PROD.ACTION.EDIT_ENTRY;
    }

    public void setData( InspectionDTO inspectionDTO )
    {
        this.inspectionDTO = inspectionDTO;
    }

    @Override
    public void doWork() throws Exception
    {
        final ProcessDocuBusiness processDocuBusiness = businessManager.getSession( ProcessDocuBusiness.class );
        processDocuBusiness.saveInspection( securityManager.getCurrentUser(), inspectionDTO );

        Platform.runLater( () -> viewManager.backward("REFRESH") );
    }
}
