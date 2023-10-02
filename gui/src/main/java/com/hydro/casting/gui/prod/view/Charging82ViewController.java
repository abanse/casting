package com.hydro.casting.gui.prod.view;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.view.content.ChargingController;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;

@ViewDeclaration( id = Charging82ViewController.ID, fxmlFile = "/com/hydro/casting/gui/prod/view/Charging82View.fxml", type = ViewType.MAIN )
public class Charging82ViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.CHARGING_82.VIEW;

    @FXML
    private ChargingController chargingController;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        chargingController.initialize( Casting.MACHINE.MELTING_FURNACE_82 );
    }

    @Override
    public void setStartObject( Object startObject )
    {
        super.setStartObject( startObject );
        chargingController.setStartObject( startObject );
    }

    @Override
    public void activateView( View view )
    {
        super.activateView( view );
        chargingController.activateView( view );
    }

    @Override
    public void deactivateView( View view )
    {
        super.deactivateView( view );
        chargingController.deactivateView( view );
    }
}
