package com.hydro.casting.gui.planning.table;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.planning.table.cell.DemandCounterValueFactory;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.comp.CacheTreeTableView;
import com.hydro.core.gui.model.ClientModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableColumn;

public class CasterDemandTableController implements InvalidationListener
{
    @FXML
    private DemandCounterValueFactory currentPlanned;

    @Inject
    private ClientModelManager clientModelManager;

    @FXML
    private CacheTreeTableView demandTable;

    @FXML
    private TreeTableColumn doubleLengthColumn;

    public void initialize( final String costCenter )
    {
        currentPlanned.setCostCenter( costCenter );

        final ClientModel clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
        clientModel.addRelationListener( CastingClientModel.SCHEDULE_ASSIGNMENT, this );

        if ( Casting.MACHINE.CASTER_80.equals( costCenter ) )
        {
            doubleLengthColumn.setVisible( true );
        }
        else
        {
            doubleLengthColumn.setVisible( false );
        }
    }

    @Override
    public void invalidated( Observable observable )
    {
        demandTable.refresh();
    }
}
