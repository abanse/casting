package com.hydro.casting.gui.melting.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.MeltingScheduleDTO;
import com.hydro.casting.server.contract.melting.MeltingBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.ViewManager;
import com.hydro.core.gui.task.AbstractMultiSelectTask;
import javafx.application.Platform;

public class ActivateChargeTask extends AbstractMultiSelectTask<MeltingScheduleDTO>
{
    @Inject
    private ViewManager viewManager;
    @Inject
    private BusinessManager businessManager;
    @Inject
    private SecurityManager securityManager;

    private String machine;

    @Override
    public String getId()
    {
        return SecurityCasting.MELTING.ACTION.ACTIVATE_CHARGE;
    }

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
    }

    @Override
    public void doWork() throws Exception
    {
        final MeltingBusiness meltingBusiness = businessManager.getSession( MeltingBusiness.class );
        meltingBusiness.activateCharge( securityManager.getCurrentUser(), machine, getSelectionProvider().getSelectedValue().getId() );

        Platform.runLater( () -> viewManager.backward() );
    }
}
