package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.prod.ProductionBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.ViewManager;
import com.hydro.core.gui.task.AbstractMultiSelectTask;
import javafx.application.Platform;

public class ActivateChargeTask extends AbstractMultiSelectTask<CasterScheduleDTO>
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private ViewManager viewManager;

    private String machine;

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
    }

    @Override
    public String getId()
    {
        return SecurityCasting.PROD.ACTION.ACTIVATE_CHARGE;
    }

    @Override
    public void doWork() throws Exception
    {
        final ProductionBusiness productionBusiness = businessManager.getSession( ProductionBusiness.class );
        productionBusiness.activateCharge( securityManager.getCurrentUser(), machine, getSelectionProvider().getSelectedValue().getId() );

        Platform.runLater( () -> viewManager.backward() );
    }
}
