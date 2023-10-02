package com.hydro.casting.gui.locking.workflow.dialog;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.locking.workflow.dialog.result.LockingWorkflowCommitResult;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowBusiness;
import com.hydro.casting.server.contract.locking.workflow.dto.LWStripHandleEntryDTO;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.gui.SecurityManager;
import io.netty.util.internal.StringUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class LockingWorkflowCommitDialog extends Dialog<LockingWorkflowCommitResult>
{
    public final static String TITLE_FREE = "Freigeben Auftrag ";
    public final static String TITLE_SCRAP = "Verschrotten Auftrag ";
    public final static String TITLE_CONTAINER = "An Container buchen Auftrag ";
    public final static String TITLE_PRINT = "Drucken/Mailen Auftrag ";
    public final static String TITLE_COM = "Kommentar hinzufügen Auftrag ";

    public final static String TITLE_STRIP_END = ". Bitte Streifen auswählen";

    public final static String TITLE_PROD_ASSIGN = "Weiter an die Produktion Auftrag ";
    public final static String TITLE_AV_ASSIGN = "Weiter an die AV Auftrag ";
    public final static String TITLE_TCS_ASSIGN = "Weiter an die TCS Auftrag ";
    public final static String TITLE_QS_ASSIGN = "Weiter an die QS Auftrag ";

    public final static String TITLE_COLUMN1 = "Alle Streifen";

    public final static String TITLE_COLUMN2 = "Sperrcode";

    public final static String TITLE_COLUMN3 = "Packstück";

    private String startText;

    public LockingWorkflowCommitDialog( LockingWorkflowDTO current, LockingWorkflowBusiness.Function function, SecurityManager securityManager, boolean withMessage, String startText )
    {
        boolean ownStripExists = false;
        this.startText = startText;
        if ( function == LockingWorkflowBusiness.Function.PrintJasper || function == LockingWorkflowBusiness.Function.Mail ) // beim
        // Drucken/Mailen
        // muss
        // Benutzer
        // kommentieren,
        { // wenn mindestens ein Streifen ihm gehört
            for ( LockingWorkflowDTO stripDTO : current.getParent().getChilds() )
            {
                ownStripExists = createIsOwner( stripDTO, securityManager, function );
                if ( ownStripExists )
                {
                    withMessage = true;
                    break;
                }
            }
        }
        final DialogPane dialogPane = getDialogPane();

        setTitle( "Sperrabwicklung" );
        dialogPane.setHeaderText( createTitleStripDialog( function, current ) );
        dialogPane.getButtonTypes().addAll( ButtonType.CANCEL, ButtonType.OK );

        LWStripHandleEntryDTO title = new LWStripHandleEntryDTO( true, TITLE_COLUMN1, TITLE_COLUMN2, TITLE_COLUMN3 );

        CheckBoxTreeItem<LWStripHandleEntryDTO> rootStripEntry = new CheckBoxTreeItem<>( title );
        rootStripEntry.setExpanded( true );

        long lockRecId = current.getLockRecId();
        for ( LockingWorkflowDTO stripDTO : current.getParent().getChilds() )
        {
            LWStripHandleEntryDTO dto = new LWStripHandleEntryDTO( stripDTO, true, "-", TITLE_COLUMN1, TITLE_COLUMN2 );
            boolean isOwner = createIsOwner( stripDTO, securityManager, function );
            if ( isOwner || function == LockingWorkflowBusiness.Function.PrintJasper || function == LockingWorkflowBusiness.Function.Mail )// eigene
            // Sperre
            // oder
            // man
            // druckt/mailt
            {
                CheckBoxTreeItem<LWStripHandleEntryDTO> stripCheckBox = new CheckBoxTreeItem<>( dto );
                rootStripEntry.getChildren().add( stripCheckBox );
                if ( StringUtil.isNullOrEmpty( startText ) == false )
                {
                    dto.setDisabled( true );
                }

                if ( stripDTO.getLockRecId() == lockRecId )
                {
                    dto.setDisabled( true );
                }
                if ( stripDTO.getDefectTypeCat().equals( current.getDefectTypeCat() ) == false )
                {
                    dto.setDisabled( true );
                }

                if ( stripDTO.getMaterialStatus().startsWith( Casting.LOCKING_WORKFLOW.SCRAP_MARK ) || stripDTO.getMaterialStatus().startsWith( Casting.LOCKING_WORKFLOW.CONTAINER_MARK )
                        || stripDTO.getMaterialStatus().startsWith( Casting.LOCKING_WORKFLOW.FREE_MARK ) )
                {
                    dto.setDisabled( true );
                }

                stripCheckBox.setSelected( dto.getDefectTypeCat().equals( current.getDefectTypeCat() ) );

                // boolean scrapOrMoveToContainer = handling == LockingWorkflowBusiness.SCRAP_MATERIAL_STATUS
                // || handling == LockingWorkflowBusiness.CONTAINER_MATERIAL_STATUS;

                // if ( scrapOrMoveToContainer )
                // {
                // stripCheckBox.setSelected( false );
                // }

                stripCheckBox.selectedProperty().addListener( ( p, o, n ) -> {
                    if ( n == false && stripDTO.getLockRecId() == lockRecId )
                    {
                        Platform.runLater( () -> stripCheckBox.setSelected( true ) );
                    }
                    if ( n == true && ( !dto.getDefectTypeCat().equals( current.getDefectTypeCat() )/* || scrapOrMoveToContainer */ ) )

                    {
                        Platform.runLater( () -> stripCheckBox.setSelected( false ) );
                    }
                } );
            }
        }

        final TreeView<LWStripHandleEntryDTO> treeView = new TreeView<LWStripHandleEntryDTO>();

        treeView.setRoot( rootStripEntry );

        Callback<TreeItem<LWStripHandleEntryDTO>, ObservableValue<Boolean>> getSelectedProperty = item -> {
            if ( item instanceof CheckBoxTreeItem<?> )
            {
                return ( (CheckBoxTreeItem<?>) item ).selectedProperty();
            }
            return null;
        };

        treeView.setCellFactory( new Callback<TreeView<LWStripHandleEntryDTO>, TreeCell<LWStripHandleEntryDTO>>()
        {
            @Override
            public TreeCell<LWStripHandleEntryDTO> call( TreeView<LWStripHandleEntryDTO> param )
            {
                return new CheckBoxTreeCell<LWStripHandleEntryDTO>( getSelectedProperty, new StringConverter<TreeItem<LWStripHandleEntryDTO>>()
                {
                    @Override
                    public String toString( TreeItem<LWStripHandleEntryDTO> treeItem )
                    {
                        if ( treeItem == null )
                        {
                            return "";
                        }

                        return treeItem.getValue().getStripLabel();
                    }

                    @Override
                    public TreeItem<LWStripHandleEntryDTO> fromString( String string )
                    {
                        return null;
                    }
                } )
                {

                    @Override
                    public void updateItem( LWStripHandleEntryDTO item, boolean empty )
                    {
                        super.updateItem( item, empty );

                        if ( empty == false )
                        {
                            CheckBox checkBox = (CheckBox) getGraphic();
                            if ( item != null && item.isDisabled() )
                            {
                                checkBox.setDisable( true );
                            }
                            else
                            {
                                checkBox.setDisable( false );
                            }
                        }
                    }

                };
            }
        } );

        treeView.setPrefWidth( 600 );
        treeView.setPrefHeight( 400 );

        final TextArea messageTA = new TextArea();
        messageTA.setText( startText );
        messageTA.setPrefWidth( 600 );
        messageTA.setPrefHeight( 100 );
        messageTA.setWrapText( true );

        VBox mainBox = new VBox( 5.0 );
        mainBox.getChildren().add( treeView );
        if ( withMessage )
        {
            dialogPane.lookupButton( ButtonType.OK ).disableProperty().bind( messageTA.textProperty().isEmpty() );

            mainBox.getChildren().add( new Label( "Maßnahme hinzufügen" ) );
            mainBox.getChildren().add( messageTA );

            Platform.runLater( () -> messageTA.requestFocus() );
        }
        mainBox.setPadding( new Insets( 5 ) );

        dialogPane.setContent( mainBox );

        setResultConverter( buttonType -> {

            if ( buttonType != ButtonType.OK )
            {
                return null;
            }
            LockingWorkflowCommitResult result = new LockingWorkflowCommitResult();

            List<LWStripHandleEntryDTO> selectedEntries = new ArrayList<>();
            addSelectedEntries( selectedEntries, rootStripEntry );

            List<LockingWorkflowDTO> selectedLockingWorkflowDTOs = new ArrayList<>();
            for ( LWStripHandleEntryDTO selectedEntry : selectedEntries )
            {
                selectedLockingWorkflowDTOs.add( selectedEntry.getDto() );
            }
            result.setSelectedLockingWorkflows( selectedLockingWorkflowDTOs );
            result.setMessage( messageTA.getText() );

            return result;
        } );
    }

    private boolean createIsOwner( LockingWorkflowDTO lockingWorkflowDTO, SecurityManager securityManager, LockingWorkflowBusiness.Function function )
    {
        return ( lockingWorkflowDTO.getOwner().equals( LockingWorkflowDTO.OWNER_AV ) && securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.AV ) ) || (
                lockingWorkflowDTO.getOwner().equals( LockingWorkflowDTO.OWNER_PROD ) && securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.PROD ) ) || (
                lockingWorkflowDTO.getOwner().equals( LockingWorkflowDTO.OWNER_TCS ) && securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.TCS ) ) || (
                lockingWorkflowDTO.getOwner().equals( LockingWorkflowDTO.OWNER_QS ) && securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.QS )
                        && function != LockingWorkflowBusiness.Function.AssignQs )
                // damit
                // eigene
                // Streifen
                // bei Überhahme nicht ersheinen
                || ( function == LockingWorkflowBusiness.Function.Com && securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.AV ) // die
                // AV
                // kann
                // allen
                // Kommentare
                // schreiben
                || ( lockingWorkflowDTO.getOwner().equals( LockingWorkflowDTO.OWNER_SAP ) && function == LockingWorkflowBusiness.Function.ScrapMaterial && securityManager.hasRole(
                SecurityCasting.LOCKING.LOCKING_WORKFLOW.AV ) ) // die AV kann
                // Material mit
                // SAP-Sperren
                // verschrotten
                || ( lockingWorkflowDTO.getOwner().equals( LockingWorkflowDTO.OWNER_PROD ) && securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.QS )
                && function == LockingWorkflowBusiness.Function.AssignQs ) );// Übernahme
    }

    private String createTitleStripDialog( LockingWorkflowBusiness.Function function, LockingWorkflowDTO lockingWorkflowDTO )
    {
        String title = "";
        switch ( function )
        {
        case FreeMaterial:
            title = TITLE_FREE;
            break;
        case ScrapMaterial:
            title = TITLE_SCRAP;
            break;
        case MoveToContainer:
            title = TITLE_CONTAINER;
            break;
        case AssignProd:
            title = TITLE_PROD_ASSIGN;
            break;
        case AssignAv:
            title = TITLE_AV_ASSIGN;
            break;
        case AssignTcs:
            title = TITLE_TCS_ASSIGN;
            break;
        case AssignQs:
            title = TITLE_TCS_ASSIGN;
            break;
        case PrintJasper:
            title = TITLE_PRINT;
            break;
        case Com:
            if ( StringUtil.isNullOrEmpty( startText ) )
            {
                title = TITLE_COM;
            }
            break;
        default:
            title = "";
            break;

        }
        if ( StringUtil.isNullOrEmpty( startText ) == true )
        {
            title = title + lockingWorkflowDTO.getMaterial() + TITLE_STRIP_END;
        }
        else
        {
            title = "Auftrag  " + lockingWorkflowDTO.getMaterial() + "\n";
            title += "Sie können Ihren Kommentar noch ändern und danach hinzufügen.";
            title += "\n";
            title += "Auswahl von Streifen ist nicht mehr möglich. Sie bleiben bei den von Ihnen vorher ausgewählten Streifen.";
        }
        return title;
    }

    private void addSelectedEntries( List<LWStripHandleEntryDTO> selectedEntries, CheckBoxTreeItem<LWStripHandleEntryDTO> treeItem )
    {

        if ( treeItem.getValue().getDto() != null && treeItem.getValue().getDto().getOpHistId() > 0 && treeItem.isSelected() )
        {
            selectedEntries.add( treeItem.getValue() );
        }
        if ( treeItem.getChildren() != null && treeItem.getChildren().isEmpty() == false )
        {
            for ( TreeItem<LWStripHandleEntryDTO> child : treeItem.getChildren() )
            {
                if ( child instanceof CheckBoxTreeItem )
                {
                    addSelectedEntries( selectedEntries, (CheckBoxTreeItem<LWStripHandleEntryDTO>) child );
                }
            }
        }
    }

}
