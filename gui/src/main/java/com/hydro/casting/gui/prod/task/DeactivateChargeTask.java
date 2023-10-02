package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.casting.server.contract.prod.ProductionBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.task.AbstractMultiSelectTask;
import javafx.scene.control.ButtonType;

public class DeactivateChargeTask extends AbstractMultiSelectTask<CasterScheduleDTO>
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private NotifyManager notifyManager;

    private String machine;
    private FurnaceInstructionDTO furnaceInstructionDTO;

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
    }

    public FurnaceInstructionDTO getFurnaceInstructionDTO()
    {
        return furnaceInstructionDTO;
    }

    public void setFurnaceInstructionDTO( FurnaceInstructionDTO furnaceInstructionDTO )
    {
        this.furnaceInstructionDTO = furnaceInstructionDTO;
    }

    @Override
    public String getId()
    {
        return SecurityCasting.PROD.ACTION.DEACTIVATE_CHARGE;
    }

    @Override
    public boolean beforeStart() throws Exception
    {
        if ( furnaceInstructionDTO == null || furnaceInstructionDTO.getCastingBatchOID() == null )
        {
            notifyManager.showErrorMessage( "Charge deaktivieren", "Es ist keine Charge aktiv" );
            return false;
        }
        final ButtonType result = notifyManager.showQuestionMessage( "Charge deaktivieren", "Wollen Sie wirklich die Charge '" + furnaceInstructionDTO.getChargeWithoutYear() + "' deaktivieren?",
                ButtonType.YES, ButtonType.NO );
        return result == ButtonType.YES;
    }

    @Override
    public void doWork() throws Exception
    {
        final ProductionBusiness productionBusiness = businessManager.getSession( ProductionBusiness.class );
        productionBusiness.deactivateCharge( securityManager.getCurrentUser(), machine, furnaceInstructionDTO.getCastingBatchOID() );

        notifyManager.showSplashMessage( "Die Charge '" + furnaceInstructionDTO.getChargeWithoutYear() + "' wurde erfolgreich abgemeldet" );
    }
}
