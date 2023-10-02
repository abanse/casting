package com.hydro.casting.gui.locking.workflow.control;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.core.gui.comp.ServerTableView;
import com.hydro.casting.server.contract.locking.workflow.dto.LockHistoryDTO;
import com.hydro.casting.server.contract.locking.workflow.dto.LockHistoryElementDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class LWLockHistoryControlController extends LockingWorkflowDetailController<LockHistoryDTO>
{
    @Inject
    private Injector injector;
    
    @FXML
    private ServerTableView<LockHistoryElementDTO> lockHistoryTable;

    public LWLockHistoryControlController()
    {
        super( LockHistoryDTO.class );
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize()
    {
        lockHistoryTable.registerLoaderTask( loadLockingWorkflowDetailTask );
        lockHistoryTable.connect( injector, null );
    }

    @Override
    public void loadData( LockHistoryDTO data )
    {
        ObservableList<LockHistoryElementDTO> items = FXCollections.observableArrayList();
        if ( data != null &&
                data.getLockHistoryElements() != null)
        {
            items.addAll( data.getLockHistoryElements() );
        }
        lockHistoryTable.setItems( items );
    }
}
