package com.hydro.casting.gui.downtime.task;

import com.google.inject.Inject;
import com.hydro.casting.common.downtime.DowntimeConstants;
import com.hydro.casting.gui.downtime.view.DowntimeHistoryViewController;
import com.hydro.casting.server.contract.downtime.DowntimeBusiness;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.comp.SelectionProvider;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;

public class DowntimeDeleteTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private NotifyManager notifyManager;

    private DowntimeBusiness downtimeBusiness;

    private SelectionProvider<DowntimeDTO> table;
    private DowntimeHistoryViewController historyController;

    public SelectionProvider<DowntimeDTO> getTable()
    {
        return table;
    }

    public void setTable( SelectionProvider<DowntimeDTO> table )
    {
        this.table = table;
    }

    public DowntimeHistoryViewController getHistoryController()
    {
        return historyController;
    }

    public void setHistoryController( DowntimeHistoryViewController historyController )
    {
        this.historyController = historyController;
    }

    @Override
    public void doWork() throws Exception
    {
        downtimeBusiness = businessManager.getSession( DowntimeBusiness.class );

        ButtonType choice = notifyManager.showQuestionMessage( "Störzeit löschen", "Möchten Sie die Störzeit wirklich löschen?", ButtonType.OK, ButtonType.CANCEL );
        if ( choice.equals( ButtonType.OK ) )
        {
            downtimeBusiness.deleteDowntime( table.getSelectedValue() );

            Platform.runLater( () -> {
                notifyManager.showSplashMessage( "Die Störzeit wurde gelöscht." );
            } );
        }
        else
        {
            Platform.runLater( () -> {
                notifyManager.showSplashMessage( "Störzeit nicht gelöscht." );
            } );
        }

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
        return DowntimeConstants.ACTION_ID.DELETE;
    }
}
