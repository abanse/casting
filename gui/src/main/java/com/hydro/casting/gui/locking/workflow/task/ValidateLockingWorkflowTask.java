package com.hydro.casting.gui.locking.workflow.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.common.locking.workflow.LockingWorkflowConstants;
import com.hydro.casting.gui.locking.workflow.view.LockingWorkflowViewController;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowView;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.Task;
import com.hydro.core.gui.task.AbstractTask;
import com.hydro.core.server.contract.workplace.TaskValidation;
import com.hydro.core.server.contract.workplace.ViewModel;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateLockingWorkflowTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    private LockingWorkflowDTO lockingWorkflowDTO;

    private Map<String, Task> tasks = new HashMap<>();

    public void addTask( Task task )
    {
        tasks.put( task.getId(), task );
    }

    public void setData( LockingWorkflowDTO lockingWorkflowDTO )
    {
        this.lockingWorkflowDTO = lockingWorkflowDTO;
        for ( Task task : tasks.values() )
        {
            task.clear();
        }
    }

    @Override
    public void doWork() throws Exception
    {
        LockingWorkflowView lockingWorkflowView = businessManager.getSession( LockingWorkflowView.class );

        ViewModel<LockingWorkflowDTO> viewModel = lockingWorkflowView.validateLockingWorkflow( lockingWorkflowDTO, securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.PROD ),
                securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.AV ), securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.TCS ),
                securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.QS ) );

        Platform.runLater( new Runnable()
        {
            @Override
            public void run()
            {
                final List<Task> allTasks = new ArrayList<>( tasks.values() );
                List<TaskValidation> taskValidations = viewModel.getTaskValidations();
                for ( TaskValidation taskValidation : taskValidations )
                {
                    taskValidation.setLocked( !securityManager.hasRole( taskValidation.getId() ) );
                    Task task = tasks.get( taskValidation.getId() );
                    if ( task != null )
                    {
                        task.setDisabled( taskValidation.isDisabled() );
                        task.setRemark( taskValidation.getRemark() );
                        if ( !securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.QS ) )
                        {
                            task.setLocked( taskValidation.isLocked() );
                        }

                        allTasks.remove( task );
                    }
                }
                // Check only local tasks
                for ( Task task : allTasks )
                {
                    task.setDisabled( false );
                    task.setRemark( null );
                    task.setLocked( !securityManager.hasRole( task.getSecurityId( LockingWorkflowViewController.class ) ) );
                }
            }
        } );
    }

    @Override
    public String getId()
    {
        return LockingWorkflowConstants.ACTION_ID.VALIDATE;
    }
}
