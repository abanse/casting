package com.hydro.casting.gui.main.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.MachineCalendarDTO;
import com.hydro.casting.server.contract.main.MachineCalendarTMP;
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

@ViewDeclaration( id = MachineCalendarViewController.ID, fxmlFile = "/com/hydro/casting/gui/main/view/MachineCalendarView.fxml", type = ViewType.MAIN )
public class MachineCalendarViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.MAIN.MACHINE_CALENDAR.VIEW;

    @Inject
    private Injector injector;

    @Inject
    private BusinessManager businessManager;

    @FXML
    private SearchBox searchBox;

    @FXML
    private HBox summary;

    @FXML
    private MaintenanceTableView<MachineCalendarDTO> table;

    @FXML
    private TaskButton delete;

    @FXML
    private TaskButton copy;

    @FXML
    public void initialize()
    {
        MachineCalendarTMP machineCalendarTMP = businessManager.getSession( MachineCalendarTMP.class );
        table.connect( injector, machineCalendarTMP );
        table.loadData();
        table.filterProperty().bindBidirectional( searchBox.textProperty() );

        delete.disableProperty().bind( table.deleteAllowedProperty().not() );
        copy.disableProperty().bind( table.copyAllowedProperty().not() );
    }

    @FXML
    void reload( ActionEvent event )
    {
        table.loadData();
    }

    @FXML
    void add( ActionEvent event )
    {
        table.insert();
    }

    @FXML
    void copy( ActionEvent event )
    {
        table.copy();
    }

    @FXML
    void delete( ActionEvent event )
    {
        table.delete();
    }

}