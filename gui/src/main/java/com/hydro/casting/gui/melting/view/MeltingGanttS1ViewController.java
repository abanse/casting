package com.hydro.casting.gui.melting.view;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.gantt.content.GanttChartController;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

@ViewDeclaration( id = MeltingGanttS1ViewController.ID, fxmlFile = "/com/hydro/casting/gui/melting/view/MeltingGanttS1View.fxml", type = ViewType.MAIN )
public class MeltingGanttS1ViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.MELTING.GANTT_S1.VIEW;

    @FXML
    private GanttChartController ganttChartController;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        final List<String> machines = new ArrayList<>();
        machines.add( Casting.MACHINE.MELTING_FURNACE_S1 );
        ganttChartController.initialize( machines );
    }

    @Override
    public void beforeShown( View view )
    {
        super.beforeShown( view );
        ganttChartController.beforeShown( view );
    }

    @Override
    public void activateView( View view )
    {
        super.activateView( view );
        ganttChartController.activateView( view );
    }

    @Override
    public void deactivateView( View view )
    {
        super.deactivateView( view );
        ganttChartController.deactivateView( view );
    }

    @Override
    public boolean handleFunctionKey( KeyCode keyCode, boolean isShiftDown )
    {
        return ganttChartController.handleFunctionKey( keyCode, isShiftDown );
    }
}
