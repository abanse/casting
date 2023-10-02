package com.hydro.casting.gui.melting.view;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.melting.content.MeltingFurnaceController;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.fxml.FXML;

@ViewDeclaration( id = MelterS1ViewController.ID, fxmlFile = "/com/hydro/casting/gui/melting/view/MelterS1View.fxml", type = ViewType.MAIN )
public class MelterS1ViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.MELTER_S1.VIEW;

    @FXML
    private MeltingFurnaceController meltingFurnaceController;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        meltingFurnaceController.initialize( Casting.MACHINE.MELTING_FURNACE_S1 );
    }

    @Override
    public void beforeShown( View view )
    {
        super.beforeShown( view );
        meltingFurnaceController.beforeShown( view );
    }

    @Override
    public void activateView( View view )
    {
        super.activateView( view );
        meltingFurnaceController.activateView( view );
    }

    @Override
    public void deactivateView( View view )
    {
        super.deactivateView( view );
        meltingFurnaceController.deactivateView( view );
    }
}
