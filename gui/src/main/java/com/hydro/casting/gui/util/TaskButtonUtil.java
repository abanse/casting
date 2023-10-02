package com.hydro.casting.gui.util;

import com.google.inject.Injector;
import com.hydro.casting.gui.task.ValidateMachineTask;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.View;
import com.hydro.core.gui.comp.TaskButton;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import org.controlsfx.control.MasterDetailPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class related to TaskButtons. Gathers shared code that is used among multiple classes.
 */
public abstract class TaskButtonUtil
{
    /**
     * Register one task for a task button specifically by adding the task to the validation task and setting the onAction attribute of the TaskButton to execute its task.
     *
     * @param taskButton          The TaskButton to register a task on
     * @param injector            The injector that should be used to inject the tasks on the found TaskButtons
     * @param taskManager         The TaskManager that should be used to execute found tasks when the respective TaskButton is clicked
     * @param validateMachineTask A ValidateMachineTask that should have the tasks on the TaskButtons added for later validation - can be null
     */
    public static void registerTask( TaskButton taskButton, Injector injector, TaskManager taskManager, ValidateMachineTask<?> validateMachineTask )
    {
        if ( taskButton.getTask() != null )
        {
            injector.injectMembers( taskButton.getTask() );

            if ( validateMachineTask != null )
            {
                validateMachineTask.addTask( taskButton.getTask() );
            }

            taskButton.setOnAction( event -> taskManager.executeTask( taskButton.getTask() ) );
        }
    }

    /**
     * Registers all tasks on a given view by finding all TaskButtons on the view and its children and setting up the buttons and tasks.
     * Steps in the process:
     * - Find all TaskButtons on the provided view and its children
     * - Inject the tasks of the TaskButtons
     * - Add the tasks of the TaskButtons to the ValidateTask
     * - Set the onAction attribute of the TaskButtons to execute their tasks
     *
     * @param view                The view, should be of type Node, to register all tasks on
     * @param injector            The injector that should be used to inject the tasks on the found TaskButtons
     * @param taskManager         The TaskManager that should be used to execute found tasks when the respective TaskButton is clicked
     * @param validateMachineTask A ValidateMachineTask that should have the tasks on the TaskButtons added for later validation - can be null
     */
    public static void registerTasks( View view, Injector injector, TaskManager taskManager, ValidateMachineTask<?> validateMachineTask )
    {
        final List<TaskButton> taskButtons = new ArrayList<>();
        if ( view instanceof Node )
        {
            recursiveSearchTasksOnNode( (Node) view, taskButtons );
        }

        taskButtons.forEach( taskButton -> registerTask( taskButton, injector, taskManager, validateMachineTask ) );
    }

    /**
     * Recursively finds all TaskButtons on the provided node and its children. Stores
     * the results in the provided list.
     *
     * @param node        The node that should be recursively searched for TaskButtons.
     * @param taskButtons The list that stores the result (all found TaskButtons).
     */
    private static void recursiveSearchTasksOnNode( Node node, List<TaskButton> taskButtons )
    {
        if ( node instanceof TaskButton )
        {
            TaskButton taskButton = (TaskButton) node;
            taskButtons.add( taskButton );
        }

        if ( node instanceof Parent )
        {
            Parent parent = (Parent) node;
            for ( Node child : parent.getChildrenUnmodifiable() )
            {
                recursiveSearchTasksOnNode( child, taskButtons );
            }
        }

        if ( node instanceof MasterDetailPane )
        {
            MasterDetailPane masterDetailPane = (MasterDetailPane) node;
            recursiveSearchTasksOnNode( masterDetailPane.getMasterNode(), taskButtons );
            recursiveSearchTasksOnNode( masterDetailPane.getDetailNode(), taskButtons );
        }

        if ( node instanceof Accordion )
        {
            Accordion accordion = (Accordion) node;
            for ( Node child : accordion.getPanes() )
            {
                recursiveSearchTasksOnNode( child, taskButtons );
            }
        }

        if ( node instanceof TitledPane )
        {
            TitledPane titledPane = (TitledPane) node;
            recursiveSearchTasksOnNode( titledPane.getContent(), taskButtons );
        }

        if ( node instanceof ScrollPane )
        {
            ScrollPane scrollPane = (ScrollPane) node;
            recursiveSearchTasksOnNode( scrollPane.getContent(), taskButtons );
        }
    }
}
