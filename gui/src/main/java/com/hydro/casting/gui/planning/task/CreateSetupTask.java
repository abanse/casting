package com.hydro.casting.gui.planning.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.planning.dialog.SetupCommentDialog;
import com.hydro.casting.gui.planning.dialog.SetupDialog;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.SetupTypeDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.core.gui.ApplicationManager;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.comp.grid.GridView;
import com.hydro.core.gui.task.AbstractTask;

import java.util.List;

public class CreateSetupTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private ApplicationManager applicationManager;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private SecurityManager securityManager;

    private String costCenter;
    private GridView gridView;

    private String dragboardString;
    private CasterScheduleDTO beforeEntry;

    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter( String costCenter )
    {
        this.costCenter = costCenter;
    }

    public GridView getGridView()
    {
        return gridView;
    }

    public void setGridView( GridView gridView )
    {
        this.gridView = gridView;
    }

    public void setData( String dragboardString, CasterScheduleDTO beforeEntry )
    {
        this.dragboardString = dragboardString;
        this.beforeEntry = beforeEntry;
    }

    @Override
    public void doWork() throws Exception
    {
        SetupTypeDTO setupType = null;
        if ( "NEW REMARK".equals( dragboardString ) )
        {
            setupType = SetupCommentDialog.showDialog( applicationManager, "Neuen Kommentareintrag erzeugen" );
        }
        else if ( "NEW SETUP".equals( dragboardString ) )
        {
            setupType = SetupDialog.showDialog( applicationManager, "Neuen Spülguss erzeugen" );
        }
        /* Templates momentan nich integriert
        else
        {
            if ( SetupTypeDTO.isSetupTypeContent( dragboardString ) == false )
            {
                return;
            }

            try
            {
                setupType = SetupTypeDTO.fromDragboardString( dragboardString );
            }
            catch ( ParseException e )
            {
                throw new BusinessException( "Fehler beim Einfügen", e );
            }
        }

         */
        if ( setupType == null )
        {
            return;
        }

        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );
        //final List<Long> setupIDs = casterScheduleBusiness.insertSetup( securityManager.getCurrentUser(), costCenter, setupType, beforeEntry );
        casterScheduleBusiness.insertSetup( securityManager.getCurrentUser(), costCenter, setupType, beforeEntry );

        notifyManager.showSplashMessage( "Der Eintrag wurde erfolgreich erstellt" );

        //gridView.setNewSelection( setupIDs );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.MABE.ACTION.CREATE_ENTRY;
    }
}