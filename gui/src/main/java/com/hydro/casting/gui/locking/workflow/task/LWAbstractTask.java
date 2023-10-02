package com.hydro.casting.gui.locking.workflow.task;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.locking.workflow.dialog.LockingWokflowCommitDialogCommon;
import com.hydro.casting.gui.locking.workflow.report.LockingWorkflowPrinter;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowBusiness;
import com.hydro.casting.server.contract.locking.workflow.dto.LWAddMessageDTO;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.common.exception.*;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.ViewManager;
import com.hydro.core.gui.comp.SelectionProvider;
import com.hydro.core.gui.task.AbstractTask;
import com.hydro.core.gui.util.WindowsRegistry;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class LWAbstractTask extends AbstractTask
{
    private final static Logger log = LoggerFactory.getLogger( LWAbstractTask.class );

    public final static String TITLE_MASSNAHME = "Eingabe Massnahme";

    public final static String QUESTION_TITLE = "Sicherheitsabfrage";

    public final static String QUESTUIN_BAND = "Wollen Sie wirklich den Barren";

    public final static String QUESTUIN_STRIP = "Wollen Sie wirklich die Streifen";

    public final static String QUESTION_FREE = " freigeben?";

    public final static String QUESTION_SCRAP = " verschrotten?";

    public final static String QUESTION_CONTAINER = " an Container buchen?";

    public final static String QUESTION_PRINT = "Wollen Sie drucken?";

    public final static String TITLE_PRINT_SCRAP = "Drucken beim Verschrotten";

    public final static String TITLE_PRINT_FREI = "Drucken bei der Freigabe";

    public final static String QUESTION_TABG = "Wollen Sie den Fertigungsauftrag technisch abschliessen? ";

    public final static String TITLE_TABG = "TABG setzen";

    public final static String ERROR = "Fehler";

    public final static String ERROR_OCCURED = "Ein Fehler ist aufgetreten." + "\n" + "Error: ";

    public final static String REGISTRY_OUTLOOK = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Clients\\Mail\\Microsoft Outlook\\shell\\open\\command";

    public final static int NOT_AUTO_PRINT = -1;

    public final static int AFTER_SCRAPPING = 0;

    public final static int AFTER_FREEING = 1;

    public final static String BAND_FREE_DONE = "Der Barren wurde freigegeben";
    public final static String BAND_SCRAP_DONE = "Der Barren wurde verschrottet";
    public final static String BAND_CONTAINER_DONE = "Der Barren wurde an Container gebucht";
    public final static String NOT_BAND_FREE_DONE = "Der Barren wurde nicht freigegeben";
    public final static String NOT_BAND_SCRAP_DONE = "Der Barren wurde nicht verschrottet";
    public final static String NOT_BAND_CONTAINER_DONE = "Der Barren wurde nicht an Container gebucht";

    public final static String DATA_CHANGED = "Die Daten sind nicht aktuell!";
    public final static String STRIP_NOT_LOCKED = "Sie können die ausgewählten Sperren nicht verarbeiten, da mindestens eine der Sperren nicht offen ist. Inzwischen wurde mindestens eine Freigabe, Verschrottung oder Containerbuchung gemacht.";
    public final static String BAND_NOT_LOCKED = "Sie können die ausgewählte Sperre nicht verarbeiten, da diese Sperre nicht offen ist. Inzwischen wurde eine Freigabe, Verschrottung oder Containerbuchung gemacht.";
    public final static String STRIP_EXTERNAL = "Sie können die ausgewählten Sperren nicht verarbeiten, da mindestens eine der Sperren an eine andere Gruppe weitergeleitet wurde.";
    public final static String BAND_EXTERNAL = "Sie können die ausgewählte Sperre nicht verarbeiten, da sie an eine andere Gruppe weitergeleitet wurde.";
    public final static String STRIP_CHANGED = "Die Sperrmeldungen einiger der Streifen wurden von einem andren Benutzer geändert.";
    public final static String BAND_CHANGED = "Die Sperrmeldung wurde von einem andren Benutzer geändert.";

    @Inject
    private LockingWorkflowPrinter lockingWorkflowPrinter;

    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private NotifyManager notifiyManager;

    @Inject
    private ViewManager viewManager;

    private SelectionProvider<LockingWorkflowDTO> table;

    public SelectionProvider<LockingWorkflowDTO> getTable()
    {
        return table;
    }

    public void setTable( SelectionProvider<LockingWorkflowDTO> table )
    {
        this.table = table;
    }

    public void work( LockingWorkflowBusiness.Function function ) throws BusinessException
    {
        final String userDisplayName;
        if ( securityManager.getUserInfo() != null )
        {
            userDisplayName = securityManager.getUserInfo().getUserShortName();
        }
        else
        {
            userDisplayName = securityManager.getCurrentUser();
        }
        String actMessage = "A"; // something
        boolean done = false;
        LockingWorkflowDTO dtoSelected = null;
        boolean repeat = false;
        LockingWokflowCommitDialogCommon commitDialog = null;
        //boolean isStrip = true;
        HashMap<LockingWorkflowDTO, String> ownModifiedMessages = new HashMap<LockingWorkflowDTO, String>();
        while ( done == false && StringTools.isNullOrEmpty( actMessage ) == false )
        {
            List<LockingWorkflowDTO> lockingWorkflowDTOs = null;
            LockingWorkflowBusiness lockingWorkflowBusiness = null;
            try
            {
                List<LockingWorkflowDTO> returnDTOs = new ArrayList<>();
                if ( table == null )
                {
                    throw new BusinessException( "Konfigurationsproblem: Tabelle nicht besetzt" );
                }

                final String outlookPath = WindowsRegistry.readRegistry( REGISTRY_OUTLOOK );
                final boolean mailPossible = outlookPath != null;

                boolean print = false; // Druck bei Verschrottung

                boolean tabg = false;

                if ( repeat == false )
                {
                    dtoSelected = table.getSelectedValue();
                }

                lockingWorkflowBusiness = businessManager.getSession( LockingWorkflowBusiness.class );

                //isStrip = dtoSelected.createStrip();

                if ( repeat == false )
                {
                    commitDialog = new LockingWokflowCommitDialogCommon( function, dtoSelected, false, notifiyManager, securityManager, "" );
                    if ( commitDialog.isEmptyInput() )
                    {
                        return;
                    }
                }
                lockingWorkflowDTOs = commitDialog.getLockingWorkflowDTOs();

                if ( function == LockingWorkflowBusiness.Function.FreeMaterial || function == LockingWorkflowBusiness.Function.ScrapMaterial
                        || function == LockingWorkflowBusiness.Function.MoveToContainer )
                {
                    //String question = isStrip ? QUESTUIN_STRIP : QUESTUIN_BAND;
                    String question = QUESTUIN_BAND;
                    ButtonType result = notifiyManager.showQuestionMessage( QUESTION_TITLE,
                            switchMessage( function, question + QUESTION_FREE, question + QUESTION_SCRAP, question + QUESTION_CONTAINER ), ButtonType.YES, ButtonType.NO );
                    if ( result != ButtonType.YES )
                    {
                        return;
                    }
                }

                // Bei Verschrottung wird gefragt, ob man drucken und TABG setzen möchte (print und tabg
                // beinhalten die Antworten)
                if ( function == LockingWorkflowBusiness.Function.ScrapMaterial || function == LockingWorkflowBusiness.Function.MoveToContainer )
                {
                    if ( function == LockingWorkflowBusiness.Function.ScrapMaterial )
                    {
                        ButtonType resultPrint = notifiyManager.showQuestionMessage( TITLE_PRINT_SCRAP, QUESTION_PRINT, ButtonType.YES, ButtonType.NO );
                        print = resultPrint == ButtonType.YES;
                    }

                    //                    if ( isStrip == false || ( isStrip == true && lockingWorkflowBusiness.isLastNoPackedStrip( lockingWorkflowDTOs ) == true ) )
                    //                    {
                    //
                    //                        ButtonType resultTabg = notifiyManager.showQuestionMessage( TITLE_TABG, QUESTION_TABG, ButtonType.YES, ButtonType.NO );
                    //                        tabg = resultTabg == ButtonType.YES;
                    //                    }
                }
                // Messages mit user-Kommentaren und mit den ausgewählten Anweisungen

                String messageOriginal = StringEscapeUtils.escapeJava( commitDialog.getMessage() );

                if ( repeat == false )
                {
                    actMessage = messageOriginal;
                }
                actMessage = editMessage( actMessage );
                log.trace( "vor totalHandlingAll...." );
                List<LWAddMessageDTO> addMessages = new ArrayList<>();
                for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
                {
                    addMessages.add( new LWAddMessageDTO( lockingWorkflowDTO, actMessage ) );
                }

                boolean adoption = false;
                if ( securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.QS ) )
                {
                    for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
                    {
                        if ( Objects.equals( lockingWorkflowDTO.getOwner(), LockingWorkflowDTO.OWNER_PROD ) )
                        {
                            adoption = true;
                            break;
                        }
                    }
                }
                if ( securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.QS ) && adoption )
                {
                    returnDTOs = lockingWorkflowBusiness.totalHandlingAll( function, addMessages, true, false, securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.AV ),
                            securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.TCS ), StringEscapeUtils.escapeJava( commitDialog.getMessage() ), userDisplayName, tabg, print,
                            mailPossible, adoption );
                }
                else
                {
                    returnDTOs = lockingWorkflowBusiness.totalHandlingAll( function, addMessages, securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.PROD ),
                            securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.QS ), securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.AV ),
                            securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.TCS ), StringEscapeUtils.escapeJava( commitDialog.getMessage() ), userDisplayName, tabg, print,
                            mailPossible, adoption );
                }
                String packMessage = "";
                if ( function == LockingWorkflowBusiness.Function.MoveToContainer || function == LockingWorkflowBusiness.Function.ScrapMaterial )
                {
                    for ( LockingWorkflowDTO lockingWorkflowDTO : returnDTOs )
                    {
                        String ident = "";
                        //                        if ( isStrip )
                        //                        {
                        //                            ident = "Für den Streifen ";
                        //                        }
                        //                        else
                        //                        {
                        //String invSuffix = lockingWorkflowDTO.getInvSuffix();
                        ident = "Für den Barren ";
                        //                        }
                        switch ( lockingWorkflowDTO.getPacking() )
                        {
                        case Casting.LOCKING_WORKFLOW.FG_NOT_EXISTS:
                            break;
                        case Casting.LOCKING_WORKFLOW.FG_PACKING_LOT_IS_EMPTY:
                            //lockingWorkflowBusiness.updateFinishedGoods( lockingWorkflowDTO.getFinishedGoodsId() );
                            packMessage += ident + createVollName( lockingWorkflowDTO ) + " wurde Packauswahl entfernt " + "\n";
                            break;
                        case Casting.LOCKING_WORKFLOW.FG_PACKING_LOT_IS_NOT_EMPTY:
                            packMessage += ident + createVollName( lockingWorkflowDTO ) + " gibt es bereits eine Packauswahl " + "\n";
                            break;
                        default:
                            break;
                        }

                    }
                    if ( StringTools.isNullOrEmpty( packMessage ) == false )
                    {
                        String packTitle = "Packauswahlkontrolle ";
                        switch ( function )
                        {
                        case MoveToContainer:
                            packTitle += "bei der Containerbuchung";
                            break;
                        case ScrapMaterial:
                            packTitle += "bei der Verschrottung";
                            break;
                        default:
                            break;

                        }
                        notifiyManager.showInfoMessage( packTitle, packMessage );
                    }
                }

                done = true;

                //                if ( ( isStrip && lockingWorkflowDTOs != null && lockingWorkflowDTOs.size() > 0 ) || !isStrip )
                //                {
                //                    String freeDone = isStrip ? STRIP_FREE_DONE : BAND_FREE_DONE;
                //                    String scrapDone = isStrip ? STRIP_SCRAP_DONE : BAND_SCRAP_DONE;
                //                    String containerDone = isStrip ? STRIP_CONTAINER_DONE : BAND_CONTAINER_DONE;
                String freeDone = BAND_FREE_DONE;
                String scrapDone = BAND_SCRAP_DONE;
                String containerDone = BAND_CONTAINER_DONE;
                Platform.runLater( () -> {
                    notifiyManager.showSplashMessage( switchMessage( function, freeDone, scrapDone, containerDone ) );

                } );
                //                }

                if ( function == LockingWorkflowBusiness.Function.ScrapMaterial ) // Nach Verschrottung wird
                // gemailt
                // und gedruckt (wenn
                // erwünscht)
                {
                    String messageScrapMailPrint = actMessage;

                    // messageScrapMailPrint += createMessageScrap( print, mailPossible, tabg );
                    messageScrapMailPrint += lockingWorkflowBusiness.createMessageScrap( print, mailPossible, tabg );

                    messageScrapMailPrint += "\n";

                    if ( tabg )
                    {
                        messageScrapMailPrint += Casting.LOCKING_WORKFLOW.SCRAP_TABG_OK;
                    }
                    else
                    {
                        messageScrapMailPrint += Casting.LOCKING_WORKFLOW.SCRAP_OK;
                    }

                    lockingWorkflowDTOs = updateDTO( lockingWorkflowDTOs, userDisplayName, messageScrapMailPrint );

                    lockingWorkflowPrinter.print( lockingWorkflowDTOs, dtoSelected, userDisplayName, AFTER_SCRAPPING, print, mailPossible );

                    String costCenter = lockingWorkflowDTOs.get( 0 ).getKst();

                    if ( Objects.equals( costCenter, "52" ) || Objects.equals( costCenter, "62" ) || Objects.equals( costCenter, "64" ) || Objects.equals( costCenter, "84" ) )
                    {
                        List<LockingWorkflowDTO> dtoPrints = new ArrayList<>();

                        for ( LockingWorkflowDTO dto : lockingWorkflowDTOs )
                        {
                            LockingWorkflowDTO dtoPrint = dto;
                            dtoPrint.setWeight( dto.getWeightOut() );
                            dtoPrints.add( dtoPrint );
                        }

                        lockingWorkflowBusiness.print( dtoPrints );

                    }
                    else
                    {
                        lockingWorkflowBusiness.print( lockingWorkflowDTOs );
                    }

                }

                if ( function == LockingWorkflowBusiness.Function.FreeMaterial )
                {
                    List<LockingWorkflowDTO> printLockingWorkflowDTOs = createPrintLockingWorkflowDTOs( lockingWorkflowDTOs );

                    //boolean beforePack = printLockingWorkflowDTOs.size() > 0;

                    String messageFree = actMessage;

                    //                    messageFree += lockingWorkflowBusiness.createMessageFree( beforePack, mailPossible );
                    messageFree += lockingWorkflowBusiness.createMessageFree( mailPossible );

                    printLockingWorkflowDTOs = updateDTOFree( printLockingWorkflowDTOs, userDisplayName, messageFree, returnDTOs );

                    //                    if ( beforePack )
                    //                    {
                    //                        lockingWorkflowPrinter.print( printLockingWorkflowDTOs, dtoSelected, userDisplayName, AFTER_FREEING, !mailPossible, mailPossible );
                    //                    }

                    if ( printLockingWorkflowDTOs != null && printLockingWorkflowDTOs.isEmpty() == false )
                    {
                        lockingWorkflowBusiness.print( printLockingWorkflowDTOs );
                    }
                }

            }
            catch ( BusinessNotLockedException nle )
            {
                log.trace( "Not locked" );
                //                String breakMessage = isStrip ? STRIP_NOT_LOCKED : BAND_NOT_LOCKED;
                String breakMessage = BAND_NOT_LOCKED;
                notifiyManager.showInfoMessage( DATA_CHANGED, breakMessage );
                viewManager.reloadCurrentView( getId() );
                return; // die ganze Aktion wird abgebrochen
            }
            catch ( BusinessExternalException ee )
            {
                //                String breakMessage = isStrip ? STRIP_EXTERNAL : BAND_EXTERNAL;
                String breakMessage = BAND_EXTERNAL;
                notifiyManager.showInfoMessage( DATA_CHANGED, breakMessage );
                viewManager.reloadCurrentView( getId() );
                return; // die ganze Aktion wird abgebrochen
            }
            catch ( BusinessCommentModifiedException cme )
            {
                ButtonType result = notifiyManager.showQuestionMessage( "Sperrverwaltung", "Die Daten haben sich bereits geändert. Wollen Sie trotzdem die Aktion fortführen?", ButtonType.YES,
                        ButtonType.NO );
                if ( result != ButtonType.YES )
                {
                    viewManager.reloadCurrentView( getId() );
                    return;
                }
                Iterator<Long> iterator = cme.getModifiedMessages().keySet().iterator();
                while ( iterator.hasNext() )
                {
                    Long lockRecId = iterator.next();

                    for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
                    {
                        log.trace( "recID:" + lockingWorkflowDTO.getLockRecId() + ">>" );
                        if ( Objects.equals( lockingWorkflowDTO.getLockRecId(), lockRecId ) )
                        {
                            // log.trace(">>" + ee.getMessage() + ">>");
                            // lockingWorkflowDTO.setLockComment( ownModifiedMessages.get( dtoModified ) );
                            lockingWorkflowDTO.setLockComment( cme.getModifiedMessages().get( lockRecId ) );
                        }
                    }
                }
                repeat = true;
            }
            catch ( BusinessObjectChangedException ee )
            {
                boolean ownerOperation = function == LockingWorkflowBusiness.Function.MoveToContainer || function == LockingWorkflowBusiness.Function.FreeMaterial
                        || function == LockingWorkflowBusiness.Function.ScrapMaterial || function == LockingWorkflowBusiness.Function.AssignProd
                        || function == LockingWorkflowBusiness.Function.AssignQs || function == LockingWorkflowBusiness.Function.AssignTcs
                        || function == LockingWorkflowBusiness.Function.AssignAv; // Nur für eigene Sperren anwendbar
                boolean external =
                        ee.getMatState().equals( Casting.LOCKING_WORKFLOW.BLOCK_MARK ) == false || ( securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.AV ) && ee.getAvEnd() != null )
                                || ( securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.TCS ) && ee.getTcsEnd() != null ) || (
                                securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.PROD ) && ee.getProdEnd() != null ) || (
                                securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.QS ) && ee.getQsEnd() != null );
                if ( ownerOperation && ownModifiedMessages.size() == 0 ) // eine der Streifen ist nicht mehr
                // gesperrt oder gehört einer anderen
                // Gruppe
                {
                    String breakMessage = "";
                    if ( ee.getMatState().equals( Casting.LOCKING_WORKFLOW.BLOCK_MARK ) == false )
                    {
                        //                        breakMessage = isStrip ? STRIP_NOT_LOCKED : BAND_NOT_LOCKED;
                        breakMessage = BAND_NOT_LOCKED;
                        log.trace( "Not locked" );
                    }
                    else
                    {
                        if ( external )
                        {
                            //                            breakMessage = isStrip ? STRIP_EXTERNAL : BAND_EXTERNAL;
                            breakMessage = BAND_EXTERNAL;
                            log.trace( "External" );
                        }
                        else
                        {
                            //                            breakMessage = isStrip ? STRIP_CHANGED : BAND_CHANGED;
                            breakMessage = BAND_CHANGED;
                            log.trace( "Changed" );
                        }
                    }

                    notifiyManager.showInfoMessage( DATA_CHANGED, breakMessage );
                    viewManager.reloadCurrentView( getId() );
                    return; // die ganze Aktion wird abgebrochen
                }
                // 1. mailen oder drucken
                // 2. ownerOperation, alle Streifen noch gesperrt und bei einigen wurden Kommentare geändert
                else
                {
                    viewManager.reloadCurrentView( getId() );
                    // Platform.runLater( () -> {
                    //                    notifiyManager.showInfoMessage( DATA_CHANGED, isStrip ? STRIP_CHANGED : BAND_CHANGED );
                    notifiyManager.showInfoMessage( DATA_CHANGED, BAND_CHANGED );
                    // } );
                    LockingWokflowCommitDialogCommon commitDialogRepeat = new LockingWokflowCommitDialogCommon( function, dtoSelected, false, notifiyManager, securityManager, actMessage );

                    actMessage = commitDialogRepeat.getMessage();
                    repeat = true;
                    // lockingWorkflowDTOs mit Werten den Werten aus der DB füllen
                    Iterator<LockingWorkflowDTO> iterator = ownModifiedMessages.keySet().iterator();
                    while ( iterator.hasNext() )
                    {
                        LockingWorkflowDTO dtoModified = iterator.next();

                        for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
                        {
                            if ( Objects.equals( lockingWorkflowDTO.getLockRecId(), dtoModified.getLockRecId() ) )
                            {
                                lockingWorkflowDTO.setLockComment( ownModifiedMessages.get( dtoModified ) );
                            }
                        }

                    }
                }
            }
            catch ( Exception e )
            {
                done = true;
                log.error( "error locking workflow", e );
                //                String freeDoneNot = isStrip ? NOT_STRIP_FREE_DONE : NOT_BAND_FREE_DONE;
                //                String scrapDoneNot = isStrip ? NOT_STRIP_SCRAP_DONE : NOT_BAND_SCRAP_DONE;
                //                String containerDoneNot = isStrip ? NOT_STRIP_CONTAINER_DONE : NOT_BAND_CONTAINER_DONE;
                String freeDoneNot = NOT_BAND_FREE_DONE;
                String scrapDoneNot = NOT_BAND_SCRAP_DONE;
                String containerDoneNot = NOT_BAND_CONTAINER_DONE;
                Platform.runLater( () -> {
                    notifiyManager.showErrorMessage( "", switchMessage( function, freeDoneNot, scrapDoneNot, containerDoneNot ) + "\n" + e.getMessage() );

                } );
            }
            finally
            {
                viewManager.reloadCurrentView( getId() );
            }
        } // while
    }

    public List<LockingWorkflowDTO> workOutlook( LockingWorkflowDTO dtoSelected ) throws BusinessException
    {
        String pfad = WindowsRegistry.readRegistry( REGISTRY_OUTLOOK );
        return workJasper( pfad == null ? LockingWorkflowBusiness.NOT_OUTLOOK : LockingWorkflowBusiness.MAIL_MESSAGE, dtoSelected, false, LockingWorkflowBusiness.Function.Mail );
    }

    /**
     * In der Metode wird der Kommentar (der Parameter msg) mit einem neuen Kommentar verkettet und durch
     * Aufruf von der Methode print der Klasse LockingWorkflowPrinter wird das PDF-File erzeugt.
     */

    public List<LockingWorkflowDTO> workJasper( String msg, LockingWorkflowDTO dtoSelected, boolean afterScrap, LockingWorkflowBusiness.Function function ) throws BusinessException
    {
        final String userDisplayName;
        if ( securityManager.getUserInfo() != null )
        {
            userDisplayName = securityManager.getUserInfo().getUserShortName();
        }
        else
        {
            userDisplayName = securityManager.getCurrentUser();
        }

        LockingWorkflowBusiness lockingWorkflowBusiness = businessManager.getSession( LockingWorkflowBusiness.class );
        List<LWAddMessageDTO> addMessages = new ArrayList<>();

        LockingWokflowCommitDialogCommon commitDialog = new LockingWokflowCommitDialogCommon( function, dtoSelected, afterScrap, notifiyManager, securityManager, "" );
        if ( commitDialog.isEmptyInput() )
        {
            return null;
        }
        List<LockingWorkflowDTO> lockingWorkflowDTOs = commitDialog.getLockingWorkflowDTOs();
        //boolean isStrip = dtoSelected.createStrip();
        switch ( function )
        {
        case PrintJasper:

            String actMessage = getFinalInputMessage( StringEscapeUtils.escapeJava( commitDialog.getMessage() ), lockingWorkflowDTOs, true, lockingWorkflowBusiness );

            for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
            {
                addMessages.add( new LWAddMessageDTO( lockingWorkflowDTO, actMessage + " " + LockingWorkflowBusiness.PRINT_MESSAGE ) );
            }

            String messagePrint = StringTools.isNullOrEmpty( actMessage ) ? "" : StringEscapeUtils.escapeJava( actMessage );

            lockingWorkflowDTOs = updateDTO( lockingWorkflowDTOs, userDisplayName, messagePrint + " " + LockingWorkflowBusiness.PRINT_MESSAGE );

            lockingWorkflowPrinter.print( lockingWorkflowDTOs, dtoSelected, userDisplayName, NOT_AUTO_PRINT, true, false );

            break;

        case Mail:

            actMessage = getFinalInputMessage( StringEscapeUtils.escapeJava( commitDialog.getMessage() ), lockingWorkflowDTOs, true, lockingWorkflowBusiness );
            boolean doPrint = false;
            boolean doMail = false;
            if ( msg.equals( LockingWorkflowBusiness.NOT_OUTLOOK ) ) // msg kommt von workOutlook
            {
                doPrint = true; // Outlook ist nicht istalliert - drucken, nicht mailen
                doMail = false;
            }
            if ( msg.equals( LockingWorkflowBusiness.MAIL_MESSAGE ) )
            {
                doPrint = false; // Outlook ist istalliert - mailen, nicht drucken
                doMail = true;
            }

            for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
            {
                addMessages.add( new LWAddMessageDTO( lockingWorkflowDTO, actMessage + " " + msg ) );
            }

            String messageMail = StringTools.isNullOrEmpty( actMessage ) ? "" : StringEscapeUtils.escapeJava( actMessage );

            String pfad = WindowsRegistry.readRegistry( REGISTRY_OUTLOOK );

            String messageAction = pfad == null ? LockingWorkflowBusiness.NOT_OUTLOOK : LockingWorkflowBusiness.MAIL_MESSAGE;

            lockingWorkflowDTOs = updateDTO( lockingWorkflowDTOs, userDisplayName, messageMail + " " + messageAction );
            lockingWorkflowPrinter.print( lockingWorkflowDTOs, dtoSelected, userDisplayName, NOT_AUTO_PRINT, doPrint, doMail );
            break;

        default:
            break;
        }

        if ( addMessages.isEmpty() == false )
        {
            lockingWorkflowBusiness.addMessages( addMessages, userDisplayName, false, true );
        }
        return lockingWorkflowDTOs;
    }

    private String switchMessage( LockingWorkflowBusiness.Function function, String freeMessage, String scrapMessage, String contMessage )
    {
        switch ( function )
        {
        case FreeMaterial:
            return freeMessage;
        case ScrapMaterial:
            return scrapMessage;
        case MoveToContainer:
            return contMessage;
        default:
            return "";
        }
    }

    public List<LockingWorkflowDTO> updateDTO( List<LockingWorkflowDTO> lockingWorkflowDTOs, String user, String message )
    {
        List<LockingWorkflowDTO> modifiedDTOs = new ArrayList<>();
        for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
        {
            LockingWorkflowDTO copy = (LockingWorkflowDTO) lockingWorkflowDTO.clone();

            String oldComment = lockingWorkflowDTO.getLockComment() == null ? "" : lockingWorkflowDTO.getLockComment();

            copy.setLockComment( oldComment + user + " " + Casting.LOCKING_WORKFLOW.DF.format( Calendar.getInstance().getTime() ) + " " + message + "|" );

            modifiedDTOs.add( copy );
        }
        return modifiedDTOs;
    }

    public List<LockingWorkflowDTO> updateDTOFree( List<LockingWorkflowDTO> lockingWorkflowDTOs, String user, String message, List<LockingWorkflowDTO> notSAPFree )
    {
        List<LockingWorkflowDTO> modifiedDTOs = new ArrayList<>();
        for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
        {
            LockingWorkflowDTO copy = (LockingWorkflowDTO) lockingWorkflowDTO.clone();

            String oldComment = lockingWorkflowDTO.getLockComment() == null ? "" : lockingWorkflowDTO.getLockComment();

            String newComment = oldComment + user + " " + Casting.LOCKING_WORKFLOW.DF.format( Calendar.getInstance().getTime() ) + " " + message;

            if ( notSAPFree.contains( lockingWorkflowDTO ) == false )
            {
                newComment += "\n";
                newComment += Casting.LOCKING_WORKFLOW.FREE_OK;
            }
            newComment += "|";
            copy.setLockComment( newComment );
            modifiedDTOs.add( copy );
        }
        return modifiedDTOs;
    }

    public List<LockingWorkflowDTO> createPrintLockingWorkflowDTOs( List<LockingWorkflowDTO> lockingWorkflowDTOs )
    {
        List<LockingWorkflowDTO> printLockingWorkflowDTOs = new ArrayList<>();

        List<LockingWorkflowDTO> checkBeforePackDTOs = lockingWorkflowDTOs;

        for ( LockingWorkflowDTO loWorkflowDTO : checkBeforePackDTOs )
        {
            if ( nextIsPacking( loWorkflowDTO ) )
            {
                printLockingWorkflowDTOs.add( loWorkflowDTO );
            }
        }
        return printLockingWorkflowDTOs;
    }

    private boolean nextIsPacking( LockingWorkflowDTO lockingWorkflowDTO )
    {
        //        if ( lockingWorkflowDTO == null || lockingWorkflowDTO.getNextCostCenter() == null )
        //        {
        //            return false;
        //        }
        //        if ( lockingWorkflowDTO.getNextCostCenter().equals( Casting.MACHINE.PACKING ) )
        //        {
        //            return true;
        //        }
        return false;
    }

    public String infoCommentIsChanged( BusinessObjectChangedException be, LockingWorkflowDTO lockingWorkflowDTO, String message, boolean print )
    {
        if ( print )
        {
            notifiyManager.showInfoMessage( DATA_CHANGED, BAND_CHANGED );
            LockingWokflowCommitDialogCommon commitDialogRepeat = new LockingWokflowCommitDialogCommon( LockingWorkflowBusiness.Function.Com, lockingWorkflowDTO, false, notifiyManager,
                    securityManager, message );
            return StringEscapeUtils.escapeJava( commitDialogRepeat.getMessage() );

        }

        if ( be.getMatState().equals( Casting.LOCKING_WORKFLOW.BLOCK_MARK ) == false )
        {
            notifiyManager.showInfoMessage( DATA_CHANGED, BAND_NOT_LOCKED + "\n" + "Ihr Kommentar: " + message );
            return null;
        }
        else if ( ( securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.AV ) && be.getAvEnd() != null ) || ( securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.TCS )
                && be.getAvEnd() != null ) || ( securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.PROD ) && be.getProdEnd() != null ) || (
                securityManager.hasRole( SecurityCasting.LOCKING.LOCKING_WORKFLOW.QS ) && be.getQsEnd() != null ) )
        {
            notifiyManager.showInfoMessage( DATA_CHANGED, BAND_EXTERNAL );

            return null;
        }
        else
        {
            notifiyManager.showInfoMessage( DATA_CHANGED, BAND_CHANGED + lockingWorkflowDTO.getMaterial() );
            LockingWokflowCommitDialogCommon commitDialogRepeat = new LockingWokflowCommitDialogCommon( LockingWorkflowBusiness.Function.Com, lockingWorkflowDTO, false, notifiyManager,
                    securityManager, message );
            return StringEscapeUtils.escapeJava( commitDialogRepeat.getMessage() );
        }
    }

    public String getFinalInputMessage( String inputMessage, List<LockingWorkflowDTO> lockingWorkflowDTOs, boolean print, LockingWorkflowBusiness lockingWorkflowBusiness )
            throws BusinessException
    {
        String messageRepeat = "";
        String message = "A"; // egal was, nicht leer
        boolean repeat = false;
        for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
        {
            boolean changed = true;
            while ( changed == true && StringTools.isNullOrEmpty( message ) == false )
            {
                try
                {
                    if ( repeat == true )
                    {
                        message = messageRepeat;
                    }
                    lockingWorkflowBusiness.addMessage( lockingWorkflowDTO, "", "", false, false );
                    changed = false;
                }
                catch ( BusinessObjectChangedException be )
                {
                    viewManager.reloadCurrentView( getId() );
                    if ( repeat == false )
                    {
                        message = infoCommentIsChanged( be, lockingWorkflowDTO, inputMessage, print );
                    }
                    lockingWorkflowDTO.setLockComment( be.getMessage() );
                    repeat = true;
                    messageRepeat = message;
                }
                finally
                {
                    viewManager.reloadCurrentView( getId() );
                }
            }
        }
        if ( StringTools.isNullOrEmpty( message ) == true )// Abbrechen
        {
            return null;
        }
        String finalMessage = StringTools.isNullOrEmpty( messageRepeat ) == true ? inputMessage : messageRepeat;
        return finalMessage;
    }

    public String editMessage( String actMessage )
    {
        if ( StringTools.isNullOrEmpty( actMessage ) == true )
        {
            return actMessage;
        }
        String messageBuf = actMessage;

        // messageBuf = actMessage.replace("$1", "$ 1");
        String dopBlank = "  ";
        String blank = " ";
        String replaceCommentMark = Casting.LOCKING_WORKFLOW.COMMENT_MARK.substring( 0, 1 ) + " " + Casting.LOCKING_WORKFLOW.COMMENT_MARK.substring( 1 ); // im
        // kommentar
        // '$1'
        // durch
        // '$
        // 1'
        // ersetzen
        messageBuf = actMessage.replace( Casting.LOCKING_WORKFLOW.COMMENT_MARK, replaceCommentMark );
        actMessage = messageBuf;

        int nDopBlank = 0;
        messageBuf = actMessage;

        while ( nDopBlank != -1 )
        {
            messageBuf = actMessage.replaceAll( dopBlank, blank );
            nDopBlank = messageBuf.indexOf( dopBlank );
            actMessage = messageBuf;

        }
        if ( actMessage.startsWith( blank ) )
        {
            actMessage = actMessage.substring( 1 );
        }
        if ( actMessage.endsWith( blank ) )
        {
            actMessage = actMessage.substring( 0, actMessage.length() - 1 );
        }

        return actMessage;

    }

    private String createVollName( LockingWorkflowDTO lockingWorkflowDTO )
    {
        return lockingWorkflowDTO.getMaterial();
    }

}
