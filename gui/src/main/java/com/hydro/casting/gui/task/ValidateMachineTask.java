package com.hydro.casting.gui.task;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterInstructionDTO;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.casting.server.contract.melting.MeltingView;
import com.hydro.casting.server.contract.prod.ProductionView;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.Task;
import com.hydro.core.gui.ViewContext;
import com.hydro.core.gui.task.AbstractTask;
import com.hydro.core.server.contract.workplace.TaskValidation;
import com.hydro.core.server.contract.workplace.ViewModel;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;
import javafx.application.Platform;

import javax.ejb.EJBTransactionRolledbackException;
import java.util.ArrayList;
import java.util.List;
public class ValidateMachineTask<T extends ViewDTO> extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;
    @Inject
    private SecurityManager securityManager;

    private ViewContext viewContext;
    private T instructionDTO;

    private final ListMultimap<String, Task> tasks = ArrayListMultimap.create();

    public void addTask( Task task )
    {
        tasks.put( task.getId(), task );
    }

    public ViewContext getViewContext()
    {
        return viewContext;
    }

    public void setViewContext( ViewContext viewContext )
    {
        this.viewContext = viewContext;
    }

    public void setData( T instructionDTO )
    {
        this.instructionDTO = instructionDTO;
        for ( Task task : tasks.values() )
        {
            task.clear();
        }
    }

    @Override
    public void doWork() throws Exception
    {
        ViewModel<?> viewModel;
        try
        {
            if ( instructionDTO instanceof FurnaceInstructionDTO )
            {
                ProductionView productionView = businessManager.getSession( ProductionView.class );
                viewModel = productionView.validate( securityManager.getCurrentUser(), (FurnaceInstructionDTO) instructionDTO );
            }
            else if ( instructionDTO instanceof CasterInstructionDTO )
            {
                ProductionView productionView = businessManager.getSession( ProductionView.class );
                viewModel = productionView.validate( securityManager.getCurrentUser(), (CasterInstructionDTO) instructionDTO );
            }
            else if ( instructionDTO instanceof MeltingInstructionDTO )
            {
                MeltingView meltingView = businessManager.getSession( MeltingView.class );
                viewModel = meltingView.validate( securityManager.getCurrentUser(), (MeltingInstructionDTO) instructionDTO );
            } else
            {
                throw new Exception( "Type of instructionDTO has to be FurnaceInstructionDTO, CasterInstructionDTO or MeltingInstructionDTO." );
            }
        }
        catch ( EJBTransactionRolledbackException rollback )
        {
            // ignore
            return;
        }

        Platform.runLater( () -> {
            final List<Task> allTasks = new ArrayList<>( tasks.values() );
            List<TaskValidation> taskValidations = viewModel.getTaskValidations();
            for ( TaskValidation taskValidation : taskValidations )
            {
                List<Task> taskEntries = tasks.get( taskValidation.getId() );
                for ( Task task : taskEntries )
                {
                    if ( task != null )
                    {
                        if ( !task.isSupportMultiSelect() && viewModel.getCurrentDTOs() != null && viewModel.getCurrentDTOs().length > 1 )
                        {
                            task.setDisabled( true );
                            task.setRemark( "Mehrfachauswahl nicht unterstützt" );
                        }
                        else
                        {
                            task.setDisabled( taskValidation.isDisabled() );
                            task.setRemark( taskValidation.getRemark() );
                        }
                        task.setLocked( !securityManager.hasRole( task.getSecurityId( viewContext ) ) );
                        allTasks.remove( task );
                    }
                }
            }
            // Check only local tasks
            for ( Task task : allTasks )
            {
                if ( !task.isSupportMultiSelect() && viewModel.getCurrentDTOs() != null && viewModel.getCurrentDTOs().length > 1 )
                {
                    task.setDisabled( true );
                    task.setRemark( "Mehrfachauswahl nicht unterstützt" );
                }
                else
                {
                    task.setDisabled( false );
                    task.setRemark( null );
                }
                task.setLocked( !securityManager.hasRole( task.getSecurityId( viewContext ) ) );
            }
        } );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.PROD.ACTION.VALIDATE_TASK;
    }
}
