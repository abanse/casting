package com.hydro.casting.gui.locking.workflow.table;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.gui.comp.ServerTreeTableView;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;

public class LockingWorkflowHistoryTableController
{
    @FXML
    private ServerTreeTableView<LockingWorkflowDTO> table;

    @FXML

    private TreeTableColumn<LockingWorkflowDTO, String> materialStatusColumn;

    @FXML

    private TreeTableColumn<LockingWorkflowDTO, String> ownerColumn;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        table.setRowFactory( tv -> new LockingWorkflowTreeTableRow() );

        materialStatusColumn.setCellFactory( materialStatus -> new TreeTableCell<LockingWorkflowDTO, String>()
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
                    if ( Casting.LOCKING_WORKFLOW.SCRAP_MARK.equals( item ) )
                    {
                        setText( "Schrott" );
                    }
                    else if ( Casting.LOCKING_WORKFLOW.CONTAINER_MARK.equals( item ) )
                    {
                        setText( "Cont." );
                    }
                    else if ( Casting.LOCKING_WORKFLOW.FREE_MARK.equals( item ) )
                    {
                        setText( "Frei" );
                    }
                    else if ( Casting.LOCKING_WORKFLOW.BLOCK_MARK.equals( item ) )
                    {
                        setText( "Gesp." );
                    }
                    else
                    {
                        setText( item );
                    }
                }
            }
        } );

        ownerColumn.setCellFactory( owner -> new TreeTableCell<LockingWorkflowDTO, String>()
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
                    else if ( LockingWorkflowDTO.OWNER_SAP.equals( item ) )
                    {
                        setText( "Lab" );
                    }
                    else if ( LockingWorkflowDTO.OWNER_DATA_ERROR.equals( item ) )
                    {
                        setText( "Fehlerhafte Daten" );
                    }
                    else
                    {
                        setText( null );
                    }
                }
            }
        } );

    }

    private class LockingWorkflowTreeTableRow extends TreeTableRow<LockingWorkflowDTO>
    {
        private StringProperty materialStatus = new SimpleStringProperty();

        private StringProperty owner = new SimpleStringProperty();

        private final InvalidationListener materialStatusInvalidationListener = o -> {
            final String materialStatusValue = ( (StringProperty) o ).get();

            pseudoClassStateChanged( FREE_PSEUDOCLASS_STATE, Casting.LOCKING_WORKFLOW.FREE_MARK.equals( materialStatusValue ) );
            pseudoClassStateChanged( SCRAP_PSEUDOCLASS_STATE, Casting.LOCKING_WORKFLOW.SCRAP_MARK.equals( materialStatusValue ) );
            pseudoClassStateChanged( CONTAINER_PSEUDOCLASS_STATE, Casting.LOCKING_WORKFLOW.CONTAINER_MARK.equals( materialStatusValue ) );
        };

        private final InvalidationListener ownerInvalidationListener = o -> {
            final String ownerValue = ( (StringProperty) o ).get();
            pseudoClassStateChanged( OWNER_PROD_PSEUDOCLASS_STATE, LockingWorkflowDTO.OWNER_PROD.equals( ownerValue ) );
            pseudoClassStateChanged( OWNER_SAP_PSEUDOCLASS_STATE, LockingWorkflowDTO.OWNER_SAP.equals( ownerValue ) );
            pseudoClassStateChanged( OWNER_AV_PSEUDOCLASS_STATE, LockingWorkflowDTO.OWNER_AV.equals( ownerValue ) );
            pseudoClassStateChanged( OWNER_TCS_PSEUDOCLASS_STATE, LockingWorkflowDTO.OWNER_TCS.equals( ownerValue ) );
            pseudoClassStateChanged( OWNER_QS_PSEUDOCLASS_STATE, LockingWorkflowDTO.OWNER_QS.equals( ownerValue ) );
            pseudoClassStateChanged( DATA_ERROR_PSEUDOCLASS_STATE, LockingWorkflowDTO.OWNER_DATA_ERROR.equals( ownerValue ) );

        };

        public LockingWorkflowTreeTableRow()
        {
            getStyleClass().add( "locking-workflow-table-row-cell" );

            materialStatus.addListener( materialStatusInvalidationListener );

            owner.addListener( ownerInvalidationListener );
        }

        @Override
        public void updateItem( LockingWorkflowDTO item, boolean empty )
        {
            super.updateItem( item, empty );
            if ( item == null || empty )
            {
                materialStatus.set( null );
                owner.set( null );
            }
            else if ( item.getChilds() != null )
            {
                materialStatus.set( item.getChilds().get( 0 ).getMaterialStatus() );
                owner.set( item.getChilds().get( 0 ).getOwner() );
            }
            else
            {
                materialStatus.set( item.getMaterialStatus() );
                owner.set( item.getOwner() );
            }
        }
    }

    private static final PseudoClass DATA_ERROR_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "data_error" );
    private static final PseudoClass FREE_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "free" );
    private static final PseudoClass SCRAP_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "scrap" );
    private static final PseudoClass CONTAINER_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "container" );

    private static final PseudoClass OWNER_PROD_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "prod" );
    private static final PseudoClass OWNER_SAP_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "sap" );
    private static final PseudoClass OWNER_AV_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "av" );
    private static final PseudoClass OWNER_TCS_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "tcs" );
    private static final PseudoClass OWNER_QS_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass( "qs" );

}
