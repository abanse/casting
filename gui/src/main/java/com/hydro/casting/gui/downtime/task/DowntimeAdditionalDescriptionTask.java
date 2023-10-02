package com.hydro.casting.gui.downtime.task;

import com.google.inject.Inject;
import com.hydro.casting.common.downtime.DowntimeConstants;
import com.hydro.casting.gui.downtime.view.DowntimeHistoryViewController;
import com.hydro.casting.server.contract.downtime.DowntimeBusiness;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.comp.SelectionProvider;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;

public class DowntimeAdditionalDescriptionTask extends AbstractTask
{
    @Inject
    private NotifyManager notifyManager;

    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    private DowntimeHistoryViewController historyController;
    private SelectionProvider<DowntimeDTO> table;

    public SelectionProvider<DowntimeDTO> getTable()
    {
        return table;
    }

    public void setTable( SelectionProvider<DowntimeDTO> table )
    {
        this.table = table;
    }

    public void setHistoryController( DowntimeHistoryViewController historyController )
    {
        this.historyController = historyController;
    }

    @Override
    public void doWork() throws Exception
    {
        DowntimeDTO downtimeDTO = table.getSelectedValue();
        String input = notifyManager.showTextInputMessage( "Zusatzinformationen eingeben", downtimeDTO.getRemark(), false );
        downtimeDTO.setRemark( input );
        downtimeDTO.setUserId( securityManager.getCurrentUser() );
        DowntimeBusiness downtimeBusiness = businessManager.getSession( DowntimeBusiness.class );
        downtimeBusiness.addAdditionalDescription( downtimeDTO );
        notifyManager.showSplashMessage( "Information eingetragen" );
        if ( historyController != null )
        {
            Platform.runLater( new Runnable()
            {
                @Override
                public void run()
                {
                    historyController.reload( null );
                }
            } );
        }
    }

    @Override
    public String getId()
    {
        return DowntimeConstants.ACTION_ID.ADDITIONAL_DESCRIPTION;
    }
}
