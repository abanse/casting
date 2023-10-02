package com.hydro.casting.gui.melting.view;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.gantt.content.S2GanttChartController;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

@ViewDeclaration( id = MeltingGanttS2ViewController.ID, fxmlFile = "/com/hydro/casting/gui/melting/view/MeltingGanttS2View.fxml", type = ViewType.MAIN )
public class MeltingGanttS2ViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.MELTING.GANTT_S2.VIEW;

    @FXML
    private S2GanttChartController s2GanttChartController;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        final List<String> machines = new ArrayList<>();
        machines.add( Casting.MACHINE.MELTING_FURNACE_S2 );
        s2GanttChartController.initialize( machines );
    }

    @Override
    public void beforeShown( View view )
    {
        super.beforeShown( view );
        s2GanttChartController.beforeShown( view );
    }

    @Override
    public void activateView( View view )
    {
        super.activateView( view );
        s2GanttChartController.activateView( view );
    }

    @Override
    public void deactivateView( View view )
    {
        super.deactivateView( view );
        s2GanttChartController.deactivateView( view );
    }

    @Override
    public boolean handleFunctionKey( KeyCode keyCode, boolean isShiftDown )
    {
        return s2GanttChartController.handleFunctionKey( keyCode, isShiftDown );
    }
}
