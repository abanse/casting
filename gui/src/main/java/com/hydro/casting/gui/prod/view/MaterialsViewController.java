package com.hydro.casting.gui.prod.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.prod.control.MaterialBrowser;
import com.hydro.casting.gui.prod.control.MaterialsProvider;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

@ViewDeclaration( id = MaterialsViewController.ID, fxmlFile = "/com/hydro/casting/gui/prod/view/MaterialsView.fxml", type = ViewType.DIALOG )
public class MaterialsViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.MATERIALS.VIEW;

    @FXML
    private TabPane sourceTab;

    @Inject
    private TaskManager taskManager;

    @Inject
    private Injector injector;

    @FXML
    private MaterialBrowser materialBrowser;

    public MaterialsViewController()
    {
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        sourceTab.setOnDragEntered( dragEvent -> {
            String dragboardString = dragEvent.getDragboard().getString();
            if ( dragboardString.startsWith( Transfer.DND_IDENT ) )
            {
                sourceTab.getSelectionModel().select( 1 );
            }
        } );

        injector.injectMembers( materialBrowser );
    }

    @Override
    public void beforeShown( View view )
    {
        super.beforeShown( view );

        Platform.runLater( () -> materialBrowser.loadMaterials() );
    }

    public MaterialsProvider getMaterialsProvider()
    {
        return materialBrowser;
    }
}
