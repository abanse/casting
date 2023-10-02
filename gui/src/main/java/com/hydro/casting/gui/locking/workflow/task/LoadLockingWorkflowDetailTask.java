package com.hydro.casting.gui.locking.workflow.task;

import com.google.inject.Inject;
import com.hydro.casting.common.locking.workflow.LockingWorkflowConstants;
import com.hydro.casting.gui.locking.workflow.control.LockingWorkflowDetailController;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowView;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;

public class LoadLockingWorkflowDetailTask<S> extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    private LockingWorkflowDTO lockingWorkflowDTO;
    private LockingWorkflowDetailController<S> lockingWorkflowDetailController;
    private Class<S> lockingWorkflowDetailDTOClass;

    public void setData( LockingWorkflowDTO lockingWorkflowDTO, LockingWorkflowDetailController<S> lockingWorkflowDetailController, Class<S> lockingWorkflowDetailDTOClass )
    {
        this.lockingWorkflowDTO = lockingWorkflowDTO;
        this.lockingWorkflowDetailController = lockingWorkflowDetailController;
        this.lockingWorkflowDetailDTOClass = lockingWorkflowDetailDTOClass;
    }

    @Override
    public void doWork() throws Exception
    {
        if ( lockingWorkflowDetailController.getDtoClass().equals( lockingWorkflowDTO.getClass() ) )
        {
            Platform.runLater( new Runnable()
            {
                @SuppressWarnings( "unchecked" )
                @Override
                public void run()
                {
                    lockingWorkflowDetailController.loadData( (S) lockingWorkflowDTO );
                }
            } );
            return;
        }

        Platform.runLater( new Runnable()
        {
            @Override
            public void run()
            {
                lockingWorkflowDetailController.loadData( null );
            }
        } );

        LockingWorkflowView lockingWorkflowView = businessManager.getSession( LockingWorkflowView.class );

        S data = lockingWorkflowView.loadDetail( lockingWorkflowDetailDTOClass, lockingWorkflowDTO );

        Platform.runLater( new Runnable()
        {
            @Override
            public void run()
            {
                lockingWorkflowDetailController.loadData( data );
            }
        } );
    }

    @Override
    public String getId()
    {
        return LockingWorkflowConstants.ACTION_ID.LOAD_DETAIL;
    }
}
