package com.hydro.casting.gui.melting.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.casting.server.contract.melting.MeltingBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.task.AbstractTask;
public class ActivateNextMeltingChargeTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;
    @Inject
    private NotifyManager notifyManager;
    @Inject
    private SecurityManager securityManager;

    private String machineApk;
    private MeltingInstructionDTO meltingInstructionDTO;

    @Override
    public String getId()
    {
        return SecurityCasting.MELTING.ACTION.ACTIVATE_NEXT_MELTING_CHARGE;
    }

    public String getMachineApk()
    {
        return machineApk;
    }

    public void setMachineApk( String machineApk )
    {
        this.machineApk = machineApk;
    }

    public MeltingInstructionDTO getMeltingInstructionDTO()
    {
        return meltingInstructionDTO;
    }

    public void setMeltingInstructionDTO( MeltingInstructionDTO meltingInstructionDTO )
    {
        this.meltingInstructionDTO = meltingInstructionDTO;
    }

    @Override
    public void doWork() throws Exception
    {
        final MeltingBusiness meltingBusiness = businessManager.getSession( MeltingBusiness.class );

        if ( meltingInstructionDTO != null && meltingInstructionDTO.getMeltingBatchOID() != null )
        {
            meltingBusiness.finishCharge( securityManager.getCurrentUser(), getMachineApk(), meltingInstructionDTO.getMeltingBatchOID() );
        }

        boolean successful = meltingBusiness.activateNextCharge( securityManager.getCurrentUser(), getMachineApk() );

        if ( successful )
        {
            notifyManager.showSplashMessage( "Nächste Schmelzcharge wurde aktiviert!" );
        }
        else
        {
            notifyManager.showSplashMessage( "Keine nächste Schmelzcharge vorhanden!" );
        }
    }
}
