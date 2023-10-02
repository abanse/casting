package com.hydro.casting.gui.downtime.task;

import com.google.inject.Inject;
import com.hydro.casting.gui.downtime.control.DowntimeDetailControlController;
import com.hydro.casting.server.contract.downtime.DowntimeBusiness;
import com.hydro.casting.server.contract.downtime.dto.DowntimeCreationDTO;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;

public class DowntimeLoaderTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    private DowntimeBusiness downtimeBusiness;
    private DowntimeDetailControlController downtimeController;
    private String[] costCenters;
    private String machine;
    private DowntimeDTO suggestionDowntimeDTO;
    private DowntimeDTO editDowntimeDTO;

    public void setController( DowntimeDetailControlController downtimeController )
    {
        this.downtimeController = downtimeController;
    }

    public void setCostCenters( String[] costCenters )
    {
        this.costCenters = costCenters;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
    }

    public DowntimeDTO getSuggestionDowntimeDTO()
    {
        return suggestionDowntimeDTO;
    }

    public void setSuggestionDowntimeDTO( DowntimeDTO suggestionDowntimeDTO )
    {
        this.suggestionDowntimeDTO = suggestionDowntimeDTO;
    }

    public DowntimeDTO getEditDowntimeDTO()
    {
        return editDowntimeDTO;
    }

    public void setEditDowntimeDTO( DowntimeDTO editDowntimeDTO )
    {
        this.editDowntimeDTO = editDowntimeDTO;
    }

    @Override
    public void doWork() throws Exception
    {
        downtimeController.setWorking( true );
        Platform.runLater( new Runnable()
        {
            @Override
            public void run()
            {
                downtimeController.loadData( null, null );
            }
        } );

        downtimeBusiness = businessManager.getSession( DowntimeBusiness.class );
        DowntimeCreationDTO data;
        if ( editDowntimeDTO != null )
        {
            data = downtimeBusiness.loadDataDowntime( editDowntimeDTO );
        }
        else
        {
            data = downtimeBusiness.loadEmptyDataDowntime( costCenters, machine );
        }

        Platform.runLater( new Runnable()
        {
            @Override
            public void run()
            {
                downtimeController.loadData( data, suggestionDowntimeDTO );
            }
        } );
        downtimeController.setWorking( false );
    }

    @Override
    public String getId()
    {
        return "downtime.action.loadDowntime";
    }
}
