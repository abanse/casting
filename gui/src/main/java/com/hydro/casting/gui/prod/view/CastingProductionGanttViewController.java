package com.hydro.casting.gui.prod.view;

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

@ViewDeclaration( id = CastingProductionGanttViewController.ID, fxmlFile = "/com/hydro/casting/gui/prod/view/CastingProductionGanttView.fxml", type = ViewType.MAIN )
public class CastingProductionGanttViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.CASTING_PRODUCTION_GANTT.VIEW;

    @FXML
    private GanttChartController ganttChartController;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        final List<String> machines = new ArrayList<>();
        //machines.add( Casting.MACHINE.MELTING_FURNACE_82 );
        machines.add( Casting.MACHINE.CASTER_80 );
        //machines.add( Casting.MACHINE.MELTING_FURNACE_81 );
        //machines.add( Casting.MACHINE.MELTING_FURNACE_72 );
        machines.add( Casting.MACHINE.CASTER_70 );
        //machines.add( Casting.MACHINE.MELTING_FURNACE_71 );
        //machines.add( Casting.MACHINE.MELTING_FURNACE_62 );
        machines.add( Casting.MACHINE.CASTER_60 );
        //machines.add( Casting.MACHINE.MELTING_FURNACE_61 );
        //machines.add( Casting.MACHINE.MELTING_FURNACE_52 );
        machines.add( Casting.MACHINE.CASTER_50 );
        //machines.add( Casting.MACHINE.MELTING_FURNACE_51 );
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
