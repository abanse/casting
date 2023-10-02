package com.hydro.casting.gui.main.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.MaterialTypeDTO;
import com.hydro.casting.server.contract.main.MaterialTypeTMP;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.comp.MaintenanceTableView;
import com.hydro.core.gui.comp.SearchBox;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.util.Collections;

@ViewDeclaration( id = MaterialTypeMaintenanceViewController.ID, fxmlFile = "/com/hydro/casting/gui/main/view/MaterialTypeMaintenanceView.fxml", type = ViewType.MAIN )
public class MaterialTypeMaintenanceViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.MAIN.MATERIAL_TYPE_MAINTENANCE.VIEW;

    @Inject
    private Injector injector;

    @Inject
    private BusinessManager businessManager;

    @FXML
    private SearchBox searchBoxSM;

    @FXML
    private HBox summarySM;

    @FXML
    private MaintenanceTableView<MaterialTypeDTO> tableSM;

    @FXML
    private TaskButton deleteSM;

    @FXML
    private TaskButton copySM;

    //    @FXML
    //    private SearchBox searchBoxC;
    //
    //    @FXML
    //    private HBox summaryC;
    //
    //    @FXML
    //    private MaintenanceTableView<CastingKTDTO> tableC;
    //
    //    @FXML
    //    private TaskButton deleteC;
    //
    //    @FXML
    //    private TaskButton copyC;

    @FXML
    public void initialize()
    {
        MaterialTypeTMP materialTypeTMP = businessManager.getSession( MaterialTypeTMP.class );
        tableSM.connect( injector, materialTypeTMP, mtDTO -> Collections.singletonMap( "category", "SM" ) );
        tableSM.loadData();
        tableSM.filterProperty().bindBidirectional( searchBoxSM.textProperty() );

        deleteSM.disableProperty().bind( tableSM.deleteAllowedProperty().not() );
        copySM.disableProperty().bind( tableSM.copyAllowedProperty().not() );

        //        CastingKTTMP castingKTTMP = businessManager.getSession( CastingKTTMP.class );
        //        tableC.connect( injector, castingKTTMP );
        //        tableC.loadData();
        //        tableC.filterProperty().bindBidirectional( searchBoxC.textProperty() );
        //
        //        deleteC.disableProperty().bind( tableC.deleteAllowedProperty().not() );
        //        copyC.disableProperty().bind( tableC.copyAllowedProperty().not() );
    }

    @FXML
    void reloadSM( ActionEvent event )
    {
        tableSM.loadData();
    }

    @FXML
    void addSM( ActionEvent event )
    {
        tableSM.insert();
    }

    @FXML
    void copySM( ActionEvent event )
    {
        tableSM.copy();
    }

    @FXML
    void deleteSM( ActionEvent event )
    {
        tableSM.delete();
    }

    //    @FXML
    //    void reloadC( ActionEvent event )
    //    {
    //        tableC.loadData();
    //    }
    //
    //    @FXML
    //    void addC( ActionEvent event )
    //    {
    //        tableC.insert();
    //    }
    //
    //    @FXML
    //    void copyC( ActionEvent event )
    //    {
    //        tableC.copy();
    //    }
    //
    //    @FXML
    //    void deleteC( ActionEvent event )
    //    {
    //        tableC.delete();
    //    }
}
