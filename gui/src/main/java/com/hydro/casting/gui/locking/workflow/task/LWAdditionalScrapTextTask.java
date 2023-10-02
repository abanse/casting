package com.hydro.casting.gui.locking.workflow.task;

import com.google.inject.Inject;
import com.hydro.casting.common.locking.workflow.LockingWorkflowConstants;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LWAdditionalScrapTextTask extends LWAbstractTask
{
    @Inject
    private NotifyManager notifyManager;

    @Inject
    private SecurityManager securityManager;

    @Override
    public void doWork() throws Exception
    {
        final String userDisplayName;
        if ( securityManager.getUserInfo() != null )
        {
            userDisplayName = securityManager.getUserInfo().getUserShortName();
        }
        else
        {
            userDisplayName = securityManager.getCurrentUser();
        }
        String scrapText = notifyManager.showTextInputMessage( "Zusätzlichen Verschrottungstext erfassen", true );
        if ( scrapText != null )
        {
            notifyManager.showSplashMessage( "Verschrottungstext erfasst:\n" + scrapText + " - " + LocalDate.now().format( DateTimeFormatter.ofPattern( "dd.MM.yyyy" ) ) + " " + userDisplayName );
        }
    }

    @Override
    public String getId()
    {
        return LockingWorkflowConstants.ACTION_ID.SCRAP;
    }
}
