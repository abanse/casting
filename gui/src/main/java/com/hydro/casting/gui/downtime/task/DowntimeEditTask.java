package com.hydro.casting.gui.downtime.task;

import com.google.inject.Inject;
import com.hydro.casting.common.downtime.DowntimeConstants;
import com.hydro.casting.gui.downtime.dialog.DowntimeDialog;
import com.hydro.casting.gui.downtime.view.DowntimeHistoryViewController;
import com.hydro.casting.server.contract.downtime.DowntimeBusiness;
import com.hydro.casting.server.contract.downtime.dto.DowntimeCreationDTO;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.comp.SelectionProvider;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;

public class DowntimeEditTask extends AbstractTask
{
    @Inject
    private DowntimeDialog downtimeDialog;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private BusinessManager businessManager;

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
        DowntimeDTO editDowntimeDTO = table.getSelectedValue();
        String costCenter = editDowntimeDTO.getCostCenter();
        String machine = editDowntimeDTO.getMachine();
        DowntimeBusiness downtimeBusiness = businessManager.getSession( DowntimeBusiness.class );
        DowntimeCreationDTO data = downtimeBusiness.loadDataDowntime( editDowntimeDTO );
        downtimeDialog.getDowntimeController().setCostCenters( costCenter, new String[] {costCenter} );
        downtimeDialog.getDowntimeController().setMachine( machine );
        downtimeDialog.getDowntimeController().loadData( data, null );
        notifyManager.showCompWindow( "Störzeit für Kostenstelle " + costCenter + " bearbeiten", downtimeDialog );
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
        return DowntimeConstants.ACTION_ID.EDIT;
    }
}
