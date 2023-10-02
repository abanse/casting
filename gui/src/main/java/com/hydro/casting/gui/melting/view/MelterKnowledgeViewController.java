package com.hydro.casting.gui.melting.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.AlloyDTO;
import com.hydro.casting.server.contract.dto.MeltingFurnaceKTDTO;
import com.hydro.casting.server.contract.melting.MeltingAlloyTMP;
import com.hydro.casting.server.contract.melting.MeltingFurnaceKTTMP;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.comp.MaintenanceTableView;
import com.hydro.core.gui.comp.SearchBox;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

@ViewDeclaration( id = MelterKnowledgeViewController.ID, fxmlFile = "/com/hydro/casting/gui/melting/view/MelterKnowledgeView.fxml", type = ViewType.MAIN )
public class MelterKnowledgeViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.MELTING.KNOWLEDGE.VIEW;

    @Inject
    private Injector injector;

    @Inject
    private BusinessManager businessManager;

    @FXML
    private SearchBox searchBoxTargetTime;

    @FXML
    private MaintenanceTableView<MeltingFurnaceKTDTO> tableTargetTime;

    @FXML
    private TaskButton deleteTargetTime;

    @FXML
    private TaskButton copyTargetTime;

    @FXML
    private SearchBox searchBoxAlloys;

    @FXML
    private MaintenanceTableView<AlloyDTO> tableAlloys;

    @FXML
    public void initialize()
    {
        MeltingFurnaceKTTMP meltingFurnaceKTTMP = businessManager.getSession( MeltingFurnaceKTTMP.class );
        tableTargetTime.connect( injector, meltingFurnaceKTTMP );
        tableTargetTime.loadData();
        tableTargetTime.filterProperty().bindBidirectional( searchBoxTargetTime.textProperty() );

        deleteTargetTime.disableProperty().bind( tableTargetTime.deleteAllowedProperty().not() );
        copyTargetTime.disableProperty().bind( tableTargetTime.copyAllowedProperty().not() );

        MeltingAlloyTMP meltingAlloyTMP = businessManager.getSession( MeltingAlloyTMP.class );
        tableAlloys.connect( injector, meltingAlloyTMP );
        tableAlloys.loadData();
        tableAlloys.filterProperty().bindBidirectional( searchBoxAlloys.textProperty() );
    }

    @FXML
    void reloadTargetTime( ActionEvent event )
    {
        tableTargetTime.loadData();
    }

    @FXML
    void addTargetTime( ActionEvent event )
    {
        tableTargetTime.insert();
    }

    @FXML
    void copyTargetTime( ActionEvent event )
    {
        tableTargetTime.copy();
    }

    @FXML
    void deleteTargetTime( ActionEvent event )
    {
        tableTargetTime.delete();
    }

    @FXML
    void reloadAlloys( ActionEvent event )
    {
        tableAlloys.loadData();
    }
}