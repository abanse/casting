package com.hydro.casting.gui.melting.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.casting.server.contract.melting.MeltingBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.task.AbstractTask;
import javafx.scene.control.ButtonType;

public class DeactivateChargeTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private NotifyManager notifyManager;

    private String machine;
    private MeltingInstructionDTO meltingInstructionDTO;

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
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
    public String getId()
    {
        return SecurityCasting.MELTING.ACTION.DEACTIVATE_CHARGE;
    }

    @Override
    public boolean beforeStart() throws Exception
    {
        if ( meltingInstructionDTO == null || meltingInstructionDTO.getMeltingBatchOID() == null )
        {
            notifyManager.showErrorMessage( "Charge deaktivieren", "Es ist keine Charge aktiv" );
            return false;
        }
        final ButtonType result = notifyManager.showQuestionMessage( "Charge deaktivieren", "Wollen Sie wirklich die Charge '" + meltingInstructionDTO.getChargeWithoutYear() + "' deaktivieren?",
                ButtonType.YES, ButtonType.NO );
        return result == ButtonType.YES;
    }

    @Override
    public void doWork() throws Exception
    {
        final MeltingBusiness meltingBusiness = businessManager.getSession( MeltingBusiness.class );
        meltingBusiness.deactivateCharge( securityManager.getCurrentUser(), machine, meltingInstructionDTO.getMeltingBatchOID() );

        notifyManager.showSplashMessage( "Die Charge '" + meltingInstructionDTO.getChargeWithoutYear() + "' wurde erfolgreich abgemeldet" );
    }

}
