package com.hydro.casting.gui.planning.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.planning.dialog.ConfigureDialog;
import com.hydro.casting.gui.planning.dialog.result.ConfigureResult;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.MachineDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.core.gui.ApplicationManager;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.task.AbstractTask;

public class ConfigureTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private ApplicationManager applicationManager;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private SecurityManager securityManager;

    private MachineDTO machineDTO;
    private CasterScheduleDTO scheduleDTO;

    public void setData( MachineDTO machineDTO, CasterScheduleDTO scheduleDTO )
    {
        this.machineDTO = machineDTO;
        this.scheduleDTO = scheduleDTO;
    }

    @Override
    public void doWork() throws Exception
    {
        ConfigureResult configureResult = ConfigureDialog.showDialog( applicationManager, "Gießanlage/Charge konfigurieren", machineDTO, scheduleDTO );
        if ( configureResult == null )
        {
            return;
        }

        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );
        casterScheduleBusiness.saveLastCharge( machineDTO.getApk(), configureResult.getChargeCounter() );
        if ( configureResult.getCharge() != null && configureResult.getCastingSequence() != null && configureResult.getFurnace() != null )
        {
            casterScheduleBusiness.overwriteChargeValues( scheduleDTO, configureResult.getCastingSequence(), configureResult.getCharge(), configureResult.getFurnace() );
        }
        if ( configureResult.getSchedulableState() != null && configureResult.getSchedulableState().intValue() != scheduleDTO.getExecutionState() )
        {
            casterScheduleBusiness.changeExecutionState( scheduleDTO, configureResult.getSchedulableState().intValue(), null );
        }

        notifyManager.showSplashMessage( "Die Daten wurde erfolgreich gespeichert" );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.MABE.ACTION.EDIT_ENTRY;
    }
}