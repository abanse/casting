package com.hydro.casting.gui.locking.workflow.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.common.locking.workflow.LockingWorkflowConstants;
import com.hydro.casting.gui.locking.workflow.dialog.LockingWokflowCommitDialogCommon;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowBusiness;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.common.exception.BusinessObjectChangedException;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.ViewManager;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

public class LWAddMessageTask extends LWAbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private NotifyManager notifiyManager;

    @Inject
    private ViewManager viewManager;

    private LockingWorkflowDTO lockingWorkflowDTO;

    public void setData( LockingWorkflowDTO lockingWorkflowDTO )
    {
        this.lockingWorkflowDTO = lockingWorkflowDTO;
    }

    @Override
    public void doWork() throws Exception
    {
        LockingWokflowCommitDialogCommon commitDialog = new LockingWokflowCommitDialogCommon( LockingWorkflowBusiness.Function.Com, lockingWorkflowDTO, false, notifiyManager, securityManager, "" );
        if ( commitDialog.isEmptyInput() )
        {
            return;
        }
        try
        {
            List<LockingWorkflowDTO> lockingWorkflowDTOs = commitDialog.getLockingWorkflowDTOs();
            final String userDisplayName;
            if ( securityManager.getUserInfo() != null )
            {
                userDisplayName = securityManager.getUserInfo().getUserShortName();
            }
            else
            {
                userDisplayName = securityManager.getCurrentUser();
            }

            LockingWorkflowBusiness lockingWorkflowBusiness = businessManager.getSession( LockingWorkflowBusiness.class );

            String actMessage = StringEscapeUtils.escapeJava( commitDialog.getMessage() );
            boolean done = false;

            actMessage = editMessage( actMessage );

            while ( done == false && StringTools.isNullOrEmpty( actMessage ) == false )
            {
                for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
                {
                    try
                    {
                        boolean isRemarkAV = securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.AV ) && lockingWorkflowDTO.getOwner().equals( "AV" ) == false;
                        lockingWorkflowBusiness.addMessage( lockingWorkflowDTO, userDisplayName, actMessage, isRemarkAV, true );
                        done = true;
                    }
                    catch ( BusinessObjectChangedException be )
                    {
                        actMessage = getFinalInputMessage( actMessage, lockingWorkflowDTOs, false, lockingWorkflowBusiness );
                        break;
                    }
                }
            }

        }
        finally
        {
            viewManager.reloadCurrentView( getId() );
        }
        // work( LockingWorkflowBusiness.COM );
    }

    @Override
    public String getId()
    {
        return LockingWorkflowConstants.ACTION_ID.ADD_MESSAGE;
    }
}
