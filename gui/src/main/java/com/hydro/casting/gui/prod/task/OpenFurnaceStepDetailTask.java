package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.hydro.casting.common.constant.FurnaceStep;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.prod.dialog.StepTimestampDialog;
import com.hydro.casting.gui.prod.dialog.result.StepTimestampResult;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.casting.server.contract.dto.MachineDTO;
import com.hydro.casting.server.contract.prod.ProductionBusiness;
import com.hydro.core.common.SecurityCore;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;

import java.util.Objects;

public class OpenFurnaceStepDetailTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private ViewManager viewManager;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private ClientModelManager clientModelManager;

    private FurnaceStep furnaceStep;
    private String viewIdToOpen;
    private String securityId;

    private String machine;
    private FurnaceInstructionDTO furnaceInstructionDTO;

    private StepTimestampResult stepTimestampResult;

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

    public FurnaceStep getFurnaceStep()
    {
        return furnaceStep;
    }

    public void setFurnaceStep( FurnaceStep furnaceStep )
    {
        this.furnaceStep = furnaceStep;
    }

    public String getViewIdToOpen()
    {
        return viewIdToOpen;
    }

    public void setViewIdToOpen( String viewIdToOpen )
    {
        this.viewIdToOpen = viewIdToOpen;
    }

    public String getSecurityId()
    {
        return securityId;
    }

    public void setSecurityId( String securityId )
    {
        this.securityId = securityId;
    }

    @Override
    public String getId()
    {
        if ( securityId == null )
        {
            return SecurityCore.LOGON;
        }
        return securityId;
    }

    @Override
    public boolean beforeStart() throws Exception
    {
        final ClientModel clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
        final MachineDTO furnaceMachineDTO = clientModel.getEntity( MachineDTO.class, machine );
        if ( furnaceMachineDTO != null && !Objects.equals( furnaceMachineDTO.getCurrentStep(), furnaceStep.getShortName() ) )
        {
            stepTimestampResult = StepTimestampDialog.showDialog( "Ofen " + furnaceStep.getDescription(), furnaceMachineDTO.getCurrentStepStartTS() );
            if ( stepTimestampResult == null )
            {
                return false;
            }
        }
        else
        {
            stepTimestampResult = null;
        }
        return true;
    }

    @Override
    public void doWork() throws Exception
    {
        if ( stepTimestampResult != null )
        {
            final ProductionBusiness productionBusiness = businessManager.getSession( ProductionBusiness.class );
            productionBusiness.startFurnaceStep( securityManager.getCurrentUser(), machine, furnaceInstructionDTO.getCastingBatchOID(), furnaceStep, stepTimestampResult.getStepTime() );

            notifyManager.showSplashMessage( "Start " + furnaceStep.getDescription() + " wurde gebucht" );
        }

        if ( viewIdToOpen != null )
        {
            if ( stepTimestampResult != null )
            {
                Thread.sleep( 1000 );
            }
            Platform.runLater( () -> viewManager.openView( viewIdToOpen, furnaceInstructionDTO ) );
        }
    }
}
