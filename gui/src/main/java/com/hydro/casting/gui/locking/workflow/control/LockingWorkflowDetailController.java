package com.hydro.casting.gui.locking.workflow.control;

import com.google.inject.Inject;
import com.hydro.core.gui.TaskManager;
import com.hydro.casting.gui.locking.workflow.task.LoadLockingWorkflowDetailTask;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import javafx.beans.property.SimpleObjectProperty;

public abstract class LockingWorkflowDetailController<S>
{
    @Inject
    private TaskManager taskManager;
    
    @Inject
    protected LoadLockingWorkflowDetailTask<S> loadLockingWorkflowDetailTask = new LoadLockingWorkflowDetailTask<S>();

    private SimpleObjectProperty<LockingWorkflowDTO> lockingWorkflow = new SimpleObjectProperty<>();

    private Class<S> dtoClass;

    public LockingWorkflowDetailController( Class<S> dtoClass )
    {
        this.dtoClass = dtoClass;
        lockingWorkflow.addListener( ( observableValue, oldValue, newValue ) -> {
            if ( newValue == null )
            {
                loadData( null );
            }
            else
            {
                loadLockingWorkflowDetailTask.setData( newValue, this, dtoClass );
                taskManager.executeTask( loadLockingWorkflowDetailTask );
            }
        } );
    }

    public abstract void loadData( S data );

    public final SimpleObjectProperty<LockingWorkflowDTO> lockingWorkflowProperty()
    {
        return this.lockingWorkflow;
    }

    public final LockingWorkflowDTO getLockingWorkflow()
    {
        return this.lockingWorkflowProperty().get();
    }

    public final void setLockingWorkflow( final LockingWorkflowDTO lockingWorkflow )
    {
        this.lockingWorkflowProperty().set( lockingWorkflow );
    }

    public Class<S> getDtoClass()
    {
        return dtoClass;
    }
}
