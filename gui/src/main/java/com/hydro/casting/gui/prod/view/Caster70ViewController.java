package com.hydro.casting.gui.prod.view;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.view.content.CasterController;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;

@ViewDeclaration( id = Caster70ViewController.ID, fxmlFile = "/com/hydro/casting/gui/prod/view/Caster70View.fxml", type = ViewType.MAIN )
public class Caster70ViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.CASTER_70.VIEW;

    @FXML
    private CasterController casterController;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        casterController.initialize( Casting.MACHINE.CASTER_70 );
    }

    @Override
    public void beforeShown( View view )
    {
        super.beforeShown( view );
        casterController.beforeShown( view );
    }

    @Override
    public void activateView( View view )
    {
        super.activateView( view );
        casterController.activateView( view );
    }

    @Override
    public void deactivateView( View view )
    {
        super.deactivateView( view );
        casterController.deactivateView( view );
    }
//
//    @Override
//    public boolean handleFunctionKey( KeyCode keyCode, boolean isShiftDown )
//    {
//        return casterController.handleFunctionKey( keyCode, isShiftDown );
//    }

}
