package com.hydro.casting.gui.locking.workflow.dialog;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.locking.workflow.dialog.result.LockingWorkflowCommitResult;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowBusiness;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import javafx.application.Platform;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.FutureTask;

public class LockingWokflowCommitDialogCommon
{
    //    private boolean isStrip;
    private String message = "";
    private List<LockingWorkflowDTO> lockingWorkflowDTOs = null;
    private boolean emptyInput = false;
    private boolean withMessage = true;

    public LockingWokflowCommitDialogCommon( LockingWorkflowBusiness.Function function, LockingWorkflowDTO dtoSelected, boolean afterScrap, NotifyManager notifiyManager,
            SecurityManager securityManager, String startText )
    {
        //        isStrip = dtoSelected.createStrip();

        boolean isOwner = createIsOwner( dtoSelected, securityManager );

        if ( ( ( function == LockingWorkflowBusiness.Function.PrintJasper || function == LockingWorkflowBusiness.Function.Mail ) && isOwner == false && afterScrap == false ) || // drucken
                // oder
                // mailen,
                // kein
                // kommentar
                // vom
                // besitzer
                ( function == LockingWorkflowBusiness.Function.PrintJasper && afterScrap == true ) || dtoSelected.getMaterialStatus().equals( Casting.LOCKING_WORKFLOW.FREE_MARK )
                || dtoSelected.getMaterialStatus().equals( Casting.LOCKING_WORKFLOW.SCRAP_MARK ) || dtoSelected.getMaterialStatus().equals( Casting.LOCKING_WORKFLOW.CONTAINER_MARK ) )// drucken
        // nach
        // verschrotten,
        // kein
        // extra-kommentar
        // für
        // drucken
        {
            withMessage = false;
        }

        LockingWorkflowCommitResult lockingWorkflowCommitResult;

        //        if ( isStrip )
        //        {
        //            lockingWorkflowCommitResult = showCommitDialog( dtoSelected, function, withMessage, securityManager, startText );
        //
        //            if ( lockingWorkflowCommitResult == null )
        //            {
        //                emptyInput = true;
        //            }
        //            else
        //            {
        //                lockingWorkflowDTOs = lockingWorkflowCommitResult.getSelectedLockingWorkflows();
        //                message = lockingWorkflowCommitResult.getMessage();
        //            }
        //        }
        //        else
        //        {
        if ( withMessage )
        {
            message = notifiyManager.showTextInputMessage( "Eingabe Massnahme", startText, true );

            if ( StringTools.isNullOrEmpty( message ) )
            {
                emptyInput = true;
            }
        }
        else
        {
            if ( afterScrap )
            {
                emptyInput = true;
            }
        }
        //        }
        //        if ( emptyInput == false && isStrip == false )
        if ( emptyInput == false )
        {
            lockingWorkflowDTOs = Collections.singletonList( dtoSelected );
        }
    }

    private LockingWorkflowCommitResult showCommitDialog( LockingWorkflowDTO dtoSelected, LockingWorkflowBusiness.Function function, boolean withMessage, SecurityManager securityManager,
            String startText )
    {
        FutureTask<Optional<LockingWorkflowCommitResult>> future = new FutureTask<>( () -> {
            LockingWorkflowCommitDialog commitDialog = new LockingWorkflowCommitDialog( dtoSelected, function, securityManager, withMessage, startText );
            return commitDialog.showAndWait();
        } );
        Platform.runLater( future );
        Optional<LockingWorkflowCommitResult> lockingWorkflowCommitResult = null;
        try
        {
            lockingWorkflowCommitResult = future.get();
        }
        catch ( Exception ex )
        {
            // do nothing
        }
        if ( lockingWorkflowCommitResult == null || lockingWorkflowCommitResult.isPresent() == false )
        {
            return null;
        }
        return lockingWorkflowCommitResult.get();
    }

    protected boolean createIsOwner( LockingWorkflowDTO lockingWorkflowDTO, SecurityManager securityManager )
    {
        return ( lockingWorkflowDTO.getOwner().equals( LockingWorkflowDTO.OWNER_AV ) ) && securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.AV )
                || ( lockingWorkflowDTO.getOwner().equals( LockingWorkflowDTO.OWNER_PROD ) ) && securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.PROD )
                || ( lockingWorkflowDTO.getOwner().equals( LockingWorkflowDTO.OWNER_TCS ) ) && securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.TCS )
                || ( lockingWorkflowDTO.getOwner().equals( LockingWorkflowDTO.OWNER_QS ) ) && securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.QS );

    }

    public String getMessage()
    {
        return message == null ? "" : message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public boolean isEmptyInput()
    {
        return emptyInput;
    }

    public void setEmptyInput( boolean emptyInput )
    {
        this.emptyInput = emptyInput;
    }

    public List<LockingWorkflowDTO> getLockingWorkflowDTOs()
    {
        return lockingWorkflowDTOs;
    }

    public void setLockingWorkflowDTOs( List<LockingWorkflowDTO> lockingWorkflowDTOs )
    {
        this.lockingWorkflowDTOs = lockingWorkflowDTOs;
    }

    //    public boolean isStrip()
    //    {
    //        return isStrip;
    //    }

    public boolean isWithMessage()
    {
        return withMessage;
    }

}
