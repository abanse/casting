package com.hydro.casting.gui.prod.table;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.server.contract.dto.ProductionLogDTO;
import com.hydro.casting.server.contract.prod.ProductionView;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.comp.ServerTableView;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class ProductionLogTableController
{
    @Inject
    private Injector injector;
    @Inject
    private BusinessManager businessManager;

    @FXML
    private ServerTableView<ProductionLogDTO> table;
    @FXML
    private TableColumn eventTS;

    private String machineApk;

    @FXML
    private void initialize()
    {
        eventTS.setSortType( TableColumn.SortType.DESCENDING );
        table.getSortOrder().add( eventTS );

        table.connect( injector, () -> {
            final ProductionView productionView = businessManager.getSession( ProductionView.class );
            return productionView.loadCurrentProductionLog( machineApk );
        } );
    }

    public void reload()
    {
        table.loadData();
    }

    public String getMachineApk()
    {
        return machineApk;
    }

    public void setMachineApk( String machineApk )
    {
        this.machineApk = machineApk;
    }
}
