package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.common.constant.CasterStep;
import com.hydro.casting.common.constant.FurnaceStep;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.prod.dialog.StepTimestampDialog;
import com.hydro.casting.gui.prod.dialog.result.StepTimestampResult;
import com.hydro.casting.server.contract.dto.CasterInstructionDTO;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.casting.server.contract.dto.MachineDTO;
import com.hydro.casting.server.contract.prod.ProductionBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.task.AbstractMultiSelectTask;

import java.time.LocalDateTime;

public class StartCastingTask extends AbstractMultiSelectTask<CasterScheduleDTO>
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private ClientModelManager clientModelManager;

    private String machine;
    private CasterInstructionDTO casterInstructionDTO;

    private StepTimestampResult stepTimestampResult;

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
    }

    public CasterInstructionDTO getCasterInstructionDTO()
    {
        return casterInstructionDTO;
    }

    public void setCasterInstructionDTO( CasterInstructionDTO casterInstructionDTO )
    {
        this.casterInstructionDTO = casterInstructionDTO;
    }

    @Override
    public String getId()
    {
        return SecurityCasting.PROD.ACTION.START_CASTING;
    }

    @Override
    public boolean beforeStart() throws Exception
    {
        MachineDTO machineDTO = null;
        final ClientModel clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
        if ( clientModel != null )
        {
            machineDTO = clientModel.getEntity( MachineDTO.class, machine );
        }
        LocalDateTime lastStepStartTS = null;
        if ( machineDTO != null )
        {
            lastStepStartTS = machineDTO.getCurrentStepStartTS();
        }

        stepTimestampResult = StepTimestampDialog.showDialog( "Gießanlage " + CasterStep.Casting.getDescription(), lastStepStartTS );
        if ( stepTimestampResult == null )
        {
            return false;
        }
        return true;
    }

    @Override
    public void doWork() throws Exception
    {
        final ProductionBusiness productionBusiness = businessManager.getSession( ProductionBusiness.class );
        productionBusiness.startCasting( securityManager.getCurrentUser(), machine, casterInstructionDTO.getCastingBatchOID(), stepTimestampResult.getStepTime() );

        notifyManager.showSplashMessage( "Start " + CasterStep.Casting.getDescription() + " wurde gebucht" );
    }
}
