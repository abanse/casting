package com.hydro.casting.gui.prod.view;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.view.content.FurnaceController;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;

@ViewDeclaration( id = Furnace51ViewController.ID, fxmlFile = "/com/hydro/casting/gui/prod/view/Furnace51View.fxml", type = ViewType.MAIN )
public class Furnace51ViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.FURNACE_51.VIEW;

    @FXML
    private FurnaceController furnaceController;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        furnaceController.initialize( Casting.MACHINE.MELTING_FURNACE_51 );
    }

    @Override
    public void beforeShown( View view )
    {
        super.beforeShown( view );
        furnaceController.beforeShown( view );
    }

    @Override
    public void activateView( View view )
    {
        super.activateView( view );
        furnaceController.activateView( view );
    }

    @Override
    public void deactivateView( View view )
    {
        super.deactivateView( view );
        furnaceController.deactivateView( view );
    }

//    @Override
//    public boolean handleFunctionKey( KeyCode keyCode, boolean isShiftDown )
//    {
//        return furnaceController.handleFunctionKey( keyCode, isShiftDown );
//    }
}
