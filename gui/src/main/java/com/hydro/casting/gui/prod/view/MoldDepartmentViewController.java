package com.hydro.casting.gui.prod.view;

import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.view.content.MoldDepartmentController;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.fxml.FXML;

@ViewDeclaration( id = MoldDepartmentViewController.ID, fxmlFile = "/com/hydro/casting/gui/prod/view/MoldDepartmentView.fxml", type = ViewType.MAIN )
public class MoldDepartmentViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.MOLD_DEPARTMENT.VIEW;

    @FXML
    private MoldDepartmentController moldDepartmentController;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        moldDepartmentController.initialize();
    }

    @Override
    public void activateView( View view )
    {
        super.activateView( view );
        moldDepartmentController.activateView( view );
    }

    @Override
    public void deactivateView( View view )
    {
        super.deactivateView( view );
        moldDepartmentController.deactivateView( view );
    }
}
