package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.dialog.SelectSchedulableDialog;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.prod.ProductionBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.task.AbstractMultiSelectTask;

public class ChargingActivateChargeTask extends AbstractMultiSelectTask<CasterScheduleDTO>
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private Injector injector;

    private String machine;

    private CasterScheduleDTO casterScheduleDTO = null;

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
    public boolean beforeStart() throws Exception
    {
        casterScheduleDTO = SelectSchedulableDialog.showDialog( machine, injector );
        if ( casterScheduleDTO == null )
        {
            return false;
        }
        return true;
    }

    @Override
    public void doWork() throws Exception
    {
        if ( casterScheduleDTO == null )
        {
            return;
        }
        final ProductionBusiness productionBusiness = businessManager.getSession( ProductionBusiness.class );
        productionBusiness.activateCharge( securityManager.getCurrentUser(), machine, casterScheduleDTO.getId() );

        notifyManager.showSplashMessage( "Die Charge " + casterScheduleDTO.getChargeWithoutYear() + " wurde erfolgreich aktiviert" );
    }
}
