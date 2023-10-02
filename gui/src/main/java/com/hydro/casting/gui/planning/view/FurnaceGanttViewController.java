package com.hydro.casting.gui.planning.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.planning.gantt.FurnaceGanttChart;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;

@ViewDeclaration( id = FurnaceGanttViewController.ID, fxmlFile = "/com/hydro/casting/gui/planning/view/FurnaceGanttView.fxml", type = ViewType.MAIN )
public class FurnaceGanttViewController extends ViewControllerBase implements InvalidationListener
{
    public final static String ID = SecurityCasting.GANTT.FURNACE.VIEW;

    @Inject
    private Injector injector;

    @FXML
    private FurnaceGanttChart gantt;

    @Inject
    private ClientModelManager clientModelManager;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        injector.injectMembers( gantt );
        gantt.rebuild();
    }

    @Inject
    private void initializeInjection()
    {
        final ClientModel casterModel = clientModelManager.getClientModel( CastingClientModel.ID );
        casterModel.addRelationListener( CastingClientModel.CASTER_REFRESH, this );
        casterModel.addRelationListener( CastingClientModel.MATERIAL, this );
    }

    @Override
    public void activateView( View view )
    {
        super.activateView( view );
        gantt.load();
    }

    @Override
    public void deactivateView( View view )
    {
        super.deactivateView( view );
    }

    @Override
    public void invalidated( Observable observable )
    {
        if ( isViewActive() )
        {
            gantt.load();
        }
    }
}
