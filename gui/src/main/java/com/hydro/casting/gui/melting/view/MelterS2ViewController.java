package com.hydro.casting.gui.melting.view;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.melting.content.MeltingFurnaceController;
import com.hydro.casting.gui.melting.content.TwoChamberMeltingFurnaceController;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.fxml.FXML;

@ViewDeclaration( id = MelterS2ViewController.ID, fxmlFile = "/com/hydro/casting/gui/melting/view/MelterS2View.fxml", type = ViewType.MAIN )
public class MelterS2ViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.MELTER_S2.VIEW;

    @FXML
    private TwoChamberMeltingFurnaceController twoChamberMeltingFurnaceController;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        twoChamberMeltingFurnaceController.initialize( Casting.MACHINE.MELTING_FURNACE_S2 );
    }

    @Override
    public void beforeShown( View view )
    {
        super.beforeShown( view );
        twoChamberMeltingFurnaceController.beforeShown( view );
    }

    @Override
    public void activateView( View view )
    {
        super.activateView( view );
        twoChamberMeltingFurnaceController.activateView( view );
    }

    @Override
    public void deactivateView( View view )
    {
        super.deactivateView( view );
        twoChamberMeltingFurnaceController.deactivateView( view );
    }
}
