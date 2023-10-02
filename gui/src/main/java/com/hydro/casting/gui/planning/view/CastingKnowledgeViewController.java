package com.hydro.casting.gui.planning.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CastingKTDTO;
import com.hydro.casting.server.contract.dto.MeltingKTDTO;
import com.hydro.casting.server.contract.planning.CastingKTTMP;
import com.hydro.casting.server.contract.planning.MeltingKTTMP;
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

@ViewDeclaration( id = CastingKnowledgeViewController.ID, fxmlFile = "/com/hydro/casting/gui/planning/view/CastingKnowledgeView.fxml", type = ViewType.MAIN )
public class CastingKnowledgeViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.KNOWLEDGE.VIEW;

    @Inject
    private Injector injector;

    @Inject
    private BusinessManager businessManager;

    @FXML
    private SearchBox searchBoxCM;

    @FXML
    private HBox summaryCM;

    @FXML
    private MaintenanceTableView<MeltingKTDTO> tableCM;

    @FXML
    private TaskButton deleteCM;

    @FXML
    private TaskButton copyCM;

    @FXML
    private SearchBox searchBoxC;

    @FXML
    private HBox summaryC;

    @FXML
    private MaintenanceTableView<CastingKTDTO> tableC;

    @FXML
    private TaskButton deleteC;

    @FXML
    private TaskButton copyC;

    @FXML
    public void initialize()
    {
        MeltingKTTMP meltingKTTMP = businessManager.getSession( MeltingKTTMP.class );
        tableCM.connect( injector, meltingKTTMP );
        tableCM.loadData();
        tableCM.filterProperty().bindBidirectional( searchBoxCM.textProperty() );

        deleteCM.disableProperty().bind( tableCM.deleteAllowedProperty().not() );
        copyCM.disableProperty().bind( tableCM.copyAllowedProperty().not() );

        CastingKTTMP castingKTTMP = businessManager.getSession( CastingKTTMP.class );
        tableC.connect( injector, castingKTTMP );
        tableC.loadData();
        tableC.filterProperty().bindBidirectional( searchBoxC.textProperty() );

        deleteC.disableProperty().bind( tableC.deleteAllowedProperty().not() );
        copyC.disableProperty().bind( tableC.copyAllowedProperty().not() );
    }

    @FXML
    void reloadCM( ActionEvent event )
    {
        tableCM.loadData();
    }

    @FXML
    void addCM( ActionEvent event )
    {
        tableCM.insert();
    }

    @FXML
    void copyCM( ActionEvent event )
    {
        tableCM.copy();
    }

    @FXML
    void deleteCM( ActionEvent event )
    {
        tableCM.delete();
    }

    @FXML
    void reloadC( ActionEvent event )
    {
        tableC.loadData();
    }

    @FXML
    void addC( ActionEvent event )
    {
        tableC.insert();
    }

    @FXML
    void copyC( ActionEvent event )
    {
        tableC.copy();
    }

    @FXML
    void deleteC( ActionEvent event )
    {
        tableC.delete();
    }
}
