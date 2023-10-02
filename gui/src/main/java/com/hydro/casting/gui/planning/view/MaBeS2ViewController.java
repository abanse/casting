package com.hydro.casting.gui.planning.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.planning.view.content.MaBeMeltingController;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.fxml.FXML;

@ViewDeclaration( id = MaBeS2ViewController.ID, fxmlFile = "/com/hydro/casting/gui/planning/view/MaBeS2View.fxml", type = ViewType.MAIN )
public class MaBeS2ViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.MABE.MELTING_S2.VIEW;

    @Inject
    private Injector injector;

    @FXML
    private MaBeMeltingController maBeController;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        maBeController.initialize( Casting.MACHINE.MELTING_FURNACE_S2, injector );
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
