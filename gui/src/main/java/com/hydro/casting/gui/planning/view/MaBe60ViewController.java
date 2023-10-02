package com.hydro.casting.gui.planning.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.planning.view.content.MaBeController;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.fxml.FXML;

@ViewDeclaration( id = MaBe60ViewController.ID, fxmlFile = "/com/hydro/casting/gui/planning/view/MaBe60View.fxml", type = ViewType.MAIN )
public class MaBe60ViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.MABE.CASTER_60.VIEW;

    @Inject
    private Injector injector;

    @FXML
    private MaBeController maBeController;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        maBeController.initialize( Casting.MACHINE.CASTER_60, injector );
    }

    @Override
    public void activateView( View view )
    {
        super.activateView( view );
        maBeController.activateView( view );
    }

    @Override
    public void deactivateView( View view )
    {
        super.deactivateView( view );
        maBeController.deactivateView( view );
    }
}
