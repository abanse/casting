package com.hydro.casting.gui.locking.workflow.table;

import com.hydro.core.gui.comp.CacheTreeTableView;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;

public class LockingWorkflowTableController
{
    @FXML
    private CacheTreeTableView<LockingWorkflowDTO> table;

    @FXML
    private TreeTableColumn<LockingWorkflowDTO, String> ownerColumn;
    @FXML
    private TreeTableColumn<LockingWorkflowDTO, String> materialColumn;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        table.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        materialColumn.setSortType( TreeTableColumn.SortType.ASCENDING );
        table.getTable().getSortOrder().add( materialColumn );

        table.setRowFactory( tv -> new LockingWorkflowTreeTableRow() );
        table.setSelectNextLineIfMissed( true );

        ownerColumn.setCellFactory( materialStatus -> new TreeTableCell<LockingWorkflowDTO, String>()
        {
            @Override
            protected void updateItem( String item, boolean empty )
            {
                super.updateItem( item, empty );
                if ( item == null || empty )
                {
                    setText( null );
                }
                else
                {
                    if ( LockingWorkflowDTO.OWNER_PROD.equals( item ) )
                    {
                        setText( "Prod" );
                    }
                    else if ( LockingWorkflowDTO.OWNER_SAP.equals( item ) )
                    {
                        setText( "Lab" );
                    }
                    else if ( LockingWorkflowDTO.OWNER_AV.equals( item ) )
                    {
                        setText( "AV" );
                    }
                    else if ( LockingWorkflowDTO.OWNER_TCS.equals( item ) )
                    {
                        setText( "TCS" );
                    }
                    else if ( LockingWorkflowDTO.OWNER_QS.equals( item ) )
                    {
                        setText( "QS" );
                    }
                    else
                    {
                        setText( item );
                    }
                }
            }
        } );
    }

    private class LockingWorkflowTreeTableRow extends TreeTableRow<LockingWorkflowDTO>
    {
        private StringProperty owner = new SimpleStringProperty();

        private final InvalidationListener ownerInvalidationListener = o -> {
            final String ownerValue = ( (StringProperty) o ).get();
            pseudoClassStateChanged( OWNER_PROD_PSEUDOCLASS_STATE, LockingWorkflowDTO.OWNER_PROD.equals( ownerValue ) );
            pseudoClassStateChanged( OWNER_SAP_PSEUDOCLASS_STATE, LockingWorkflowDTO.OWNER_SAP.equals( ownerValue ) );
            pseudoClassStateChanged( OWNER_AV_PSEUDOCLASS_STATE, LockingWorkflowDTO.OWNER_AV.equals( ownerValue ) );
            pseudoClassStateChanged( OWNER_TCS_PSEUDOCLASS_STATE, LockingWorkflowDTO.OWNER_TCS.equals( ownerValue ) );
            pseudoClassStateChanged( OWNER_QS_PSEUDOCLASS_STATE, LockingWorkflowDTO.OWNER_QS.equals( ownerValue ) );
        };

        public LockingWorkflowTreeTableRow()
        {
            getStyleClass().add( "locking-workflow-table-row-cell" );

            owner.addListener( ownerInvalidationListener );
        }

        @Override
        public void updateItem( LockingWorkflowDTO item, boolean empty )
        {
            super.updateItem( item, empty );
            if ( item == null || empty )
            {
                owner.set( null );
            }
            else if (item.getChilds() != null)
            {
                owner.set( item.getChilds().get( 0 ).getOwner() );
            }
            else
            {
                owner.set( item.getOwner() );
            }
        }
    }

    private static final PseudoClass OWNER_PROD_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "prod" );
    private static final PseudoClass OWNER_SAP_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "sap" );
    private static final PseudoClass OWNER_AV_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "av" );
    private static final PseudoClass OWNER_TCS_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "tcs" );
    private static final PseudoClass OWNER_QS_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "qs" );
}
