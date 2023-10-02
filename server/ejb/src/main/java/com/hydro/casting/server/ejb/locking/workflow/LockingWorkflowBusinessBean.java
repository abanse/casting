package com.hydro.casting.server.ejb.locking.workflow;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowBusiness;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowView;
import com.hydro.casting.server.contract.locking.workflow.dto.LWAddMessageDTO;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.casting.server.model.mat.MaterialLock;
import com.hydro.casting.server.model.mat.MaterialLockLocation;
import com.hydro.casting.server.model.mat.MaterialLockType;
import com.hydro.casting.server.model.mat.dao.MaterialLockHome;
import com.hydro.casting.server.model.mat.dao.MaterialLockLocationHome;
import com.hydro.casting.server.model.mat.dao.MaterialLockTypeHome;
import com.hydro.core.common.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.*;

@Stateless
public class LockingWorkflowBusinessBean implements LockingWorkflowBusiness
{
    private final static Logger log = LoggerFactory.getLogger( LockingWorkflowBusinessBean.class );
    private final static String CPMU = "CPMU";
    private final static String TABG = "TABG";

    @EJB
    private LockingWorkflowService lockingWorkflowService;

    @EJB
    private LockingWorkflowView lockingWorkflowView;

    @EJB
    private MaterialLockHome lockedRecordsHome;

    @EJB
    private MaterialLockTypeHome materialLockTypeHome;

    @EJB
    private MaterialLockLocationHome materialLockLocationHome;

    //    @EJB
    //    private OperationsHistHome operationsHistHome;
    //
    //    @EJB
    //    private FinishedGoodsHome finishedGoodsHome;

    @Override
    public void addMessage( LockingWorkflowDTO lockingWorkflowDTO, String user, String message, boolean isRemarkAV, boolean save ) throws BusinessObjectChangedException, BusinessException
    {
        log.info( "addMessage " + lockingWorkflowDTO.getId() + " " + user + " " + message );

        final MaterialLock lockedRecord = lockedRecordsHome.findById( lockingWorkflowDTO.getLockRecId() );

        if ( Objects.equals( lockedRecord.getLockComment(), lockingWorkflowDTO.getLockComment() ) == false )
        {

            throw new BusinessObjectChangedException( "LockComment", (Object) lockingWorkflowDTO.getLockRecId(), lockedRecord.getLockComment(), lockedRecord.getMaterialStatus(),
                    lockedRecord.getProdEndTs(), lockedRecord.getQsEndTs(), lockedRecord.getTcsEndTs(), lockedRecord.getAvEndTs() );
        }

        if ( save == true )
        {
            lockingWorkflowService.addMessage( lockedRecord, user, message, isRemarkAV );
        }

        lockingWorkflowView.refreshCache( Collections.singletonList( lockingWorkflowDTO ) );
    }

    @Override
    public void addMessages( List<LWAddMessageDTO> addMessages, String user, boolean isRemarkAV, boolean save ) throws BusinessObjectChangedException, BusinessException
    {
        lockingWorkflowService.addMessages( addMessages, user, isRemarkAV, save );
    }

    @Override
    public Map<LockingWorkflowDTO, String> checkOwnModifiedMessages( List<LWAddMessageDTO> addMessages, int role ) throws BusinessObjectChangedException, BusinessException
    {
        log.info( "checkMessages role " + role );
        Map<LockingWorkflowDTO, String> ret = new HashMap<>();
        for ( LWAddMessageDTO addMessage : addMessages )
        {
            final LockingWorkflowDTO lockingWorkflowDTO = addMessage.getDto();
            final MaterialLock lockedRecord = lockedRecordsHome.findById( lockingWorkflowDTO.getLockRecId() );
            if ( Objects.equals( lockedRecord.getLockComment(), lockingWorkflowDTO.getLockComment() ) == false )
            {
                if ( lockedRecord.getMaterialStatus().equals( Casting.LOCKING_WORKFLOW.BLOCK_MARK ) == false )
                {
                    throw new BusinessObjectChangedException( "LockComment", lockingWorkflowDTO.getLockRecId(), lockedRecord.getLockComment(), lockedRecord.getMaterialStatus(),
                            lockedRecord.getProdEndTs(), lockedRecord.getQsEndTs(), lockedRecord.getTcsEndTs(), lockedRecord.getAvEndTs() );
                }
                switch ( role )
                {
                case ROLE_AV:
                    if ( lockedRecord.getAvEndTs() != null )
                    {
                        throw new BusinessObjectChangedException( "LockComment", lockingWorkflowDTO.getLockRecId(), lockedRecord.getLockComment(), lockedRecord.getMaterialStatus(),
                                lockedRecord.getProdEndTs(), lockedRecord.getQsEndTs(), lockedRecord.getTcsEndTs(), lockedRecord.getAvEndTs() );
                    }

                case ROLE_PROD:
                    if ( lockedRecord.getProdEndTs() != null )
                    {
                        throw new BusinessObjectChangedException( "LockComment", lockingWorkflowDTO.getLockRecId(), lockedRecord.getLockComment(), lockedRecord.getMaterialStatus(),
                                lockedRecord.getProdEndTs(), lockedRecord.getQsEndTs(), lockedRecord.getTcsEndTs(), lockedRecord.getAvEndTs() );
                    }
                case ROLE_QS:
                {
                    if ( lockedRecord.getQsEndTs() != null )
                    {
                        throw new BusinessObjectChangedException( "LockComment", lockingWorkflowDTO.getLockRecId(), lockedRecord.getLockComment(), lockedRecord.getMaterialStatus(),
                                lockedRecord.getProdEndTs(), lockedRecord.getQsEndTs(), lockedRecord.getTcsEndTs(), lockedRecord.getAvEndTs() );
                    }
                }
                case ROLE_TCS:
                {
                    if ( lockedRecord.getTcsEndTs() != null )
                    {
                        throw new BusinessObjectChangedException( "LockComment", lockingWorkflowDTO.getLockRecId(), lockedRecord.getLockComment(), lockedRecord.getMaterialStatus(),
                                lockedRecord.getProdEndTs(), lockedRecord.getQsEndTs(), lockedRecord.getTcsEndTs(), lockedRecord.getAvEndTs() );
                    }
                }
                }
                ret.put( lockingWorkflowDTO, lockedRecord.getLockComment() );
            }
        }
        return ret;
    }
    //

    @Override
    public List<LockingWorkflowDTO> totalHandling( Function function, List<LockingWorkflowDTO> lockingWorkflowDTOs, boolean hasRoleProd, boolean hasRoleQS, boolean hasRoleAV, boolean hasRoleTCS,
            String message, String user, boolean tabg, boolean print, boolean mailPossible, boolean adoption ) throws BusinessException
    {
        List<LockingWorkflowDTO> returnDTOs = new ArrayList<>();
        int size = lockingWorkflowDTOs.size();
        int index = 0;
        for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
        {
            log.info( "totalHandling " + lockingWorkflowDTO.getId() );

            MaterialLock lockedRecord = lockedRecordsHome.findById( lockingWorkflowDTO.getLockRecId() );
            //OperationsHist operationsHistRecord = operationsHistHome.findById( lockingWorkflowDTO.getOpHistId() );

            switch ( function )
            {
            case FreeMaterial:
                //                if ( lockingWorkflowService.isMultiLock( lockingWorkflowDTO ) == false )
                //                {
                lockingWorkflowService.handle( lockedRecord, lockingWorkflowDTO, user, message, hasRoleProd, hasRoleQS, hasRoleAV, hasRoleTCS, Casting.LOCKING_WORKFLOW.FREE_MARK, true, FREE_MESSAGE );
                //lockingWorkflowService.sapTelegram( operationsHistRecord, lockingWorkflowDTO, CPMU, user, new String( matStatusFree ) );
                //                }
                //                else
                //                {
                //                    lockingWorkflowService.handle( lockedRecord, lockingWorkflowDTO, user, message, hasRoleProd, hasRoleQS, hasRoleAV, hasRoleTCS, "" + FREE_MATERIAL_STATUS, true, FREE_MESSAGE );
                //                    returnDTOs.add( lockingWorkflowDTO );
                //                }

                break;

            case ScrapMaterial:
                //                if ( lockingWorkflowService.isMultiLock( lockingWorkflowDTO ) )
                //                {
                //                    throw new BusinessException( MULTI_LOCK_MESSAGE );
                //                }

                lockingWorkflowService.handle( lockedRecord, lockingWorkflowDTO, user, message, hasRoleProd, hasRoleQS, hasRoleAV, hasRoleTCS, Casting.LOCKING_WORKFLOW.SCRAP_MARK, true,
                        SCRAP_MESSAGE );
                //                lockingWorkflowService.sapTelegram( operationsHistRecord, lockingWorkflowDTO, CPMU, user, new String( matStatusFree ) );
                //                lockingWorkflowService.sapTelegram( operationsHistRecord, lockingWorkflowDTO, CPMU, user, new String( matStatus ) );
                //                if ( tabg && index == size - 1 )
                //                {
                //                    lockingWorkflowService.tabgTelegram( operationsHistRecord, lockingWorkflowDTO, TABG, "" + LockingWorkflowBusiness.SCRAP_MATERIAL_STATUS, user, "S", "S" );
                //                }
                returnDTOs.add( lockingWorkflowDTO );
                //                lockingWorkflowService.scrapHandling( operationsHistRecord, lockingWorkflowDTO );
                break;

            case MoveToContainer:

                //                if ( lockingWorkflowService.isMultiLock( lockingWorkflowDTO ) )
                //                {
                //                    throw new BusinessException( MULTI_LOCK_MESSAGE );
                //                }

                lockingWorkflowService.handle( lockedRecord, lockingWorkflowDTO, user, message, hasRoleProd, hasRoleQS, hasRoleAV, hasRoleTCS, Casting.LOCKING_WORKFLOW.CONTAINER_MARK, true,
                        CONTAINER_MESSAGE );
                //                lockingWorkflowService.sapTelegram( operationsHistRecord, lockingWorkflowDTO, CPMU, user, new String( matStatusFree ) );
                //                lockingWorkflowService.sapTelegram( operationsHistRecord, lockingWorkflowDTO, CPMU, user, new String( matStatus ) );
                //                if ( Objects.equals( lockingWorkflowDTO.getNextCostCenter(), BDE.COST_CENTER.PACKING ) )
                //                {
                //                    returnDTOs.add( lockingWorkflowDTO );
                //                }

                //                if ( tabg && index == size - 1 )
                //                {
                //                    lockingWorkflowService.tabgTelegram( operationsHistRecord, lockingWorkflowDTO, TABG, "" + LockingWorkflowBusiness.CONTAINER_MATERIAL_STATUS, user, "S", "S" );
                //                }
                //                OperationsHist operationsHistRecordCont = lockingWorkflowService.contHandling( operationsHistRecord, lockingWorkflowDTO );
                //                lockingWorkflowService.insertBuilupDetail( operationsHistRecordCont, lockingWorkflowDTO );
                break;

            case AssignProd:
                lockingWorkflowService.handle( lockedRecord, lockingWorkflowDTO, user, message, hasRoleProd, hasRoleQS, hasRoleAV, hasRoleTCS, null, false, TRANSFER_PROD );
                lockedRecord.setProdStartTs( LocalDateTime.now() );
                lockedRecord.setProdEndTs( null );

                break;

            case AssignAv:
                lockingWorkflowService.handle( lockedRecord, lockingWorkflowDTO, user, message, hasRoleProd, hasRoleQS, hasRoleAV, hasRoleTCS, null, false, TRANSFER_AV );

                lockedRecord.setAvStartTs( LocalDateTime.now() );
                lockedRecord.setAvEndTs( null );

                break;

            case AssignTcs:
                lockingWorkflowService.handle( lockedRecord, lockingWorkflowDTO, user, message, hasRoleProd, hasRoleQS, hasRoleAV, hasRoleTCS, null, false, TRANSFER_TCS );

                lockedRecord.setTcsStartTs( LocalDateTime.now() );
                lockedRecord.setTcsEndTs( null );

                break;

            case AssignQs:

                lockingWorkflowService.handle( lockedRecord, lockingWorkflowDTO, user, message, hasRoleProd, hasRoleQS, hasRoleAV, hasRoleTCS, null, false, TRANSFER_QS );

                lockedRecord.setQsStartTs( LocalDateTime.now() );
                lockedRecord.setQsEndTs( null );

                break;

            default:
                throw new BusinessException( "Funktion '" + function + "' nicht vorgesehen" );
            }
            index++;
        }
        lockingWorkflowView.refreshCache( lockingWorkflowDTOs );
        return returnDTOs;
    }

    @Override
    public void print( List<LockingWorkflowDTO> lockingWorkflowDTOs )
    {
        lockingWorkflowView.refreshCache( lockingWorkflowDTOs );
    }

    @Override
    public Map<String, String> createCauserCodeHashTable()
    {
        return lockingWorkflowService.createCauserHashTable();
    }

    @Override
    public void updateCauser( LockingWorkflowDTO lockingWorkflowDTO, String newCauser ) throws BusinessException
    {
        final MaterialLock lockedRecords = lockedRecordsHome.findById( lockingWorkflowDTO.getLockRecId() );
        lockedRecords.setImposationMachine( newCauser );

        lockingWorkflowView.refreshCache( Collections.singletonList( lockingWorkflowDTO ) );
    }

    @Override
    public Map<String, String> createMaterialLockLocationHashTable()
    {
        return lockingWorkflowService.createMaterialLockLocationHashTable();
    }

    @Override
    public Map<String, String> createMaterialLockTypeHashTable()
    {
        return lockingWorkflowService.createMaterialLockTypeHashTable();
    }

    @Override
    public void updateDefectTypeCat( LockingWorkflowDTO lockingWorkflowDTO, String newDefectTypeCat ) throws BusinessException
    {
        final MaterialLock lockedRecords = lockedRecordsHome.findById( lockingWorkflowDTO.getLockRecId() );
        final MaterialLockType materialLockType = materialLockTypeHome.findByApk( newDefectTypeCat );
        lockedRecords.setMaterialLockType( materialLockType );

        lockingWorkflowView.refreshCache( Collections.singletonList( lockingWorkflowDTO ) );
    }

    @Override
    public void updateMaterialLockLocation( LockingWorkflowDTO lockingWorkflowDTO, String newMaterialLockLocation ) throws BusinessException
    {
        final MaterialLock lockedRecords = lockedRecordsHome.findById( lockingWorkflowDTO.getLockRecId() );
        final MaterialLockLocation materialLockLocation = materialLockLocationHome.findByApk( newMaterialLockLocation );
        lockedRecords.setMaterialLockLocation( materialLockLocation );

        lockingWorkflowView.refreshCache( Collections.singletonList( lockingWorkflowDTO ) );
    }

    @Override
    public String getLockComment( LockingWorkflowDTO lockingWorkflowDTO )
    {
        final MaterialLock lockedRecord = lockedRecordsHome.findById( lockingWorkflowDTO.getLockRecId() );
        return lockedRecord.getLockComment();
    }

    @Override
    public List<LockingWorkflowDTO> totalHandlingAll( Function function, List<LWAddMessageDTO> addMessages, boolean hasRoleProd, boolean hasRoleQS, boolean hasRoleAV, boolean hasRoleTCS,
            String message, String user, boolean tabg, boolean print, boolean mailPossible, boolean adoption ) throws BusinessException
    {
        log.info( "totalHandlingAll " );
        List<LockingWorkflowDTO> notSAPFree = new ArrayList<>();
        HashMap<Long, String> ownModified = new HashMap<Long, String>();
        for ( LWAddMessageDTO addMessage : addMessages )
        {
            final LockingWorkflowDTO lockingWorkflowDTO = addMessage.getDto();
            final MaterialLock lockedRecord = lockedRecordsHome.findById( lockingWorkflowDTO.getLockRecId() );
            if ( Objects.equals( lockedRecord.getLockComment(), lockingWorkflowDTO.getLockComment() ) == false )
            {
                if ( lockedRecord.getMaterialStatus().equals( Casting.LOCKING_WORKFLOW.BLOCK_MARK ) == false )
                {
                    throw new BusinessNotLockedException( "NotLocked", (Object) lockingWorkflowDTO.getLockRecId(), lockingWorkflowDTO.getMaterial() );
                }
                if ( ( hasRoleAV && lockedRecord.getAvEndTs() != null ) || ( hasRoleTCS && lockedRecord.getTcsEndTs() != null ) || ( hasRoleProd && lockedRecord.getProdEndTs() != null ) || ( hasRoleQS
                        && lockedRecord.getQsEndTs() != null ) )
                {
                    throw new BusinessExternalException( "External", (Object) lockingWorkflowDTO.getLockRecId(), lockingWorkflowDTO.getMaterial() );
                }
                ownModified.put( lockingWorkflowDTO.getLockRecId(), lockedRecord.getLockComment() );
            }
        }
        if ( ownModified.size() > 0 )
        {
            throw new BusinessCommentModifiedException( "CommentModified", ownModified );
        }
        List<LockingWorkflowDTO> lockingWorkflowDTOs = new ArrayList<>();
        for ( LWAddMessageDTO addMessage : addMessages )
        {
            final LockingWorkflowDTO lockingWorkflowDTO = addMessage.getDto();
            lockingWorkflowDTOs.add( lockingWorkflowDTO );
        }
        try
        {
            notSAPFree = totalHandling( function, lockingWorkflowDTOs, hasRoleProd, hasRoleQS, hasRoleAV, hasRoleTCS, message, user, tabg, print, mailPossible, adoption );
            addMessages = new ArrayList<>();
            for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
            {
                addMessages.add( new LWAddMessageDTO( lockingWorkflowDTO, createMessage( function, lockingWorkflowDTO, message, tabg, print, mailPossible, adoption ) ) );
            }

            addMessages = addFeedback( function, addMessages, tabg, notSAPFree );

            addMessages( addMessages, user, false, true );
        }
        catch ( Exception e )
        {
            for ( LWAddMessageDTO lwAddMessageDTO1 : addMessages )
            {
                String messageError = lwAddMessageDTO1.getMessage();
                messageError += Casting.LOCKING_WORKFLOW.COMMENT_MARK;
                messageError += Casting.LOCKING_WORKFLOW.ERROR_MARK;
                messageError += e.getMessage();
                lwAddMessageDTO1.setMessage( messageError );
            }
            try
            {
                lockingWorkflowService.addMessagesWithNewTransaction( addMessages, user, false, true );
            }
            catch ( Exception ee )
            {
                log.warn( "error in add messages", ee );
                // ignore
            }
            throw e;
        }

        return notSAPFree;
    }

    @Override
    public String createMessage( Function function, LockingWorkflowDTO lockingWorkflowDTO, String message, boolean tabg, boolean print, boolean mailPossible, boolean adoption )
    {
        switch ( function )
        {
        case Com:
            return message;
        case FreeMaterial:
            return message + createMessageFree( mailPossible );
        case ScrapMaterial:
            String retScrapMessage = message + createMessageScrap( print, mailPossible, tabg );
            return retScrapMessage;
        case MoveToContainer:
            String retContMessage = message + CONTAINER_MESSAGE;
            if ( tabg )
            {
                retContMessage = retContMessage + ", " + TABG_MESSAGE;
            }
            return retContMessage;
        case AssignProd:
            return message + " " + TRANSFER_PROD;
        case AssignAv:
            return message + " " + TRANSFER_AV;
        case AssignTcs:
            return message + " " + TRANSFER_TCS;
        case AssignQs:
            if ( adoption )
            {
                return message + " " + ADOPTION_QS;
            }
            else
            {
                return message + " " + TRANSFER_QS;
            }
        case PrintJasper:
            return message + " " + PRINT_MESSAGE;
        default:
            return message;

        }
    }

    //    @Override
    //    public boolean nextIsPacking( LockingWorkflowDTO lockingWorkflowDTO )
    //    {
    //        if ( lockingWorkflowDTO == null || lockingWorkflowDTO.getNextCostCenter() == null )
    //        {
    //            return false;
    //        }
    //        if ( lockingWorkflowDTO.getNextCostCenter().equals( BDE.COST_CENTER.PACKING ) )
    //        {
    //            return true;
    //        }
    //        return false;
    //    }

    @Override
    public String createMessageScrap( boolean print, boolean mailPossible, boolean tabg )
    {
        String messageScrap = SCRAP_MESSAGE + ", ";
        if ( mailPossible )
        {
            if ( print )
            {
                messageScrap += PRINT_MESSAGE;
            }
            else
            {
                messageScrap += NOT_PRINT_MESSAGE;
            }
        }
        else
        {
            if ( print )
            {
                messageScrap += PRINT_MESSAGE + ", ";
                messageScrap += NOT_OUTLOOK;
            }
            else
            {
                messageScrap += NOT_PRINT_MESSAGE + ", ";
                messageScrap += WARNING_NOT_OUTLOOK;
            }
        }
        if ( tabg )
        {
            messageScrap += ", ";
            messageScrap += TABG_MESSAGE;

        }
        return messageScrap;
    }

    public String createMessageFree( boolean mailPossible )
    {
        String messageFree = LockingWorkflowBusiness.FREE_MESSAGE;
        //        if ( beforePack == false )
        //        {
        //            return messageFree;
        //        }
        if ( !mailPossible )// outlook installiert
        {
            messageFree = messageFree + ", " + NOT_OUTLOOK;
        }
        else
        {
            messageFree = messageFree + ", " + MAIL_MESSAGE;
        }
        return messageFree;
    }

    @Override
    public List<LWAddMessageDTO> addFeedback( Function function, List<LWAddMessageDTO> addMessages, boolean tabg, List<LockingWorkflowDTO> notSAPFree )
    {
        for ( LWAddMessageDTO lwAddMessageDTO : addMessages )
        {
            if ( function == Function.FreeMaterial || function == Function.MoveToContainer || function == Function.ScrapMaterial )
            {

                String message = lwAddMessageDTO.getMessage();
                if ( notSAPFree.contains( lwAddMessageDTO.getDto() ) == false )
                {
                    message += Casting.LOCKING_WORKFLOW.COMMENT_MARK;
                }
                switch ( function )
                {
                case FreeMaterial:
                    if ( notSAPFree.contains( lwAddMessageDTO.getDto() ) == false )
                    {
                        message += Casting.LOCKING_WORKFLOW.FREE_MARK;
                    }
                    break;
                case MoveToContainer:
                    message += Casting.LOCKING_WORKFLOW.CONTAINER_MARK;
                    if ( tabg )
                    {
                        message += Casting.LOCKING_WORKFLOW.TABG_MARK;
                    }
                    break;
                case ScrapMaterial:
                    message += Casting.LOCKING_WORKFLOW.SCRAP_MARK;
                    if ( tabg )
                    {
                        message += Casting.LOCKING_WORKFLOW.TABG_MARK;
                    }
                    break;
                default:
                    break;
                }
                if ( notSAPFree.contains( lwAddMessageDTO.getDto() ) == false )
                {
                    message += Casting.LOCKING_WORKFLOW.OK;
                }
                lwAddMessageDTO.setMessage( message );
            }
        }
        return addMessages;
    }

    //    @Override
    //    public boolean updateFinishedGoods( long id ) throws RuntimeException
    //    {
    //        // TODO Auto-generated method stub
    //        FinishedGoods finishedGoods = finishedGoodsHome.findById( id );
    //        if ( finishedGoods == null )
    //        {
    //            return false;
    //        }
    //        finishedGoodsHome.remove( finishedGoods );
    //        return true;
    //    }
    //
    //    @Override
    //    public boolean isLastNoPackedStrip( List<LockingWorkflowDTO> lockingWorkflowDTOs )
    //    {
    //        // TODO Auto-generated method stub
    //        return finishedGoodsHome.isLastNoPackedStrip( lockingWorkflowDTOs );
    //    }
}
