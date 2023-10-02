package com.hydro.casting.server.ejb.locking.workflow;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowView;
import com.hydro.casting.server.contract.locking.workflow.dto.LWAddMessageDTO;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.casting.server.model.mat.MaterialLock;
import com.hydro.casting.server.model.mat.dao.MaterialLockHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.exception.BusinessMessageException;
import com.hydro.core.common.exception.BusinessObjectChangedException;
import com.hydro.core.common.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Stateless
public class LockingWorkflowService
{
    private final static DateFormat DF = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    private final static Logger log = LoggerFactory.getLogger( LockingWorkflowService.class );

    @EJB
    private MaterialLockHome lockedRecordsHome;

    //    @EJB
    //    private SapUploadHome sapUploadHome;
    //
    //    @EJB
    //    private SapLockrecUploadHome sapLockrecUploadHome;
    //
    //    @EJB
    //    private OperationsHistHome operationsHistHome;
    //
    //    @EJB
    //    private SchedulingTableHome schedulingTableHome;
    //
    //    @EJB
    //    private OriginCodeHome originCodeHome;

    @EJB
    private LockingWorkflowView lockingWorkflowView;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    //    @EJB
    //    private ScrapCodeHome scrapCodeHome;
    //
    //    @EJB
    //    private FinishedGoodsHome finishedGoodsHome;
    //
    //    @EJB
    //    private TransportService transportService;
    //
    //    @EJB
    //    private IngotInventoryHome ingotInventoryHome;
    //
    //    @EJB
    //    private BuildupDetailHome buildupDetailHome;

    public void addMessage( MaterialLock lockedRecord, String user, String message, boolean isRemarkAV ) throws BusinessException
    {
        String messageFormatted = message;
        if ( isRemarkAV )
        {
            messageFormatted += Casting.LOCKING_WORKFLOW.COMMENT_MARK;
            messageFormatted += Casting.LOCKING_WORKFLOW.AV_MARK;
        }
        final String newComment = user + " " + DF.format( Calendar.getInstance().getTime() ) + " " + messageFormatted + "|";
        final String comment = StringTools.isNullOrEmpty( lockedRecord.getLockComment() ) ? newComment : lockedRecord.getLockComment() + newComment;

        if ( comment.length() > Casting.LOCKING_WORKFLOW.COMMENT_LENGTH )
        {
            throw new BusinessMessageException( "Die Länge der Bemerkung überschreitet die maximale Länge des Systems" );
        }
        lockedRecord.setLockComment( comment );
    }

    @TransactionAttribute( TransactionAttributeType.REQUIRES_NEW )
    public void addMessagesWithNewTransaction( List<LWAddMessageDTO> addMessages, String user, boolean isRemarkAV, boolean save ) throws BusinessObjectChangedException, BusinessException
    {
        addMessages( addMessages, user, isRemarkAV, save );
    }

    public void addMessages( List<LWAddMessageDTO> addMessages, String user, boolean isRemarkAV, boolean save ) throws BusinessObjectChangedException, BusinessException
    {
        final List<LockingWorkflowDTO> lockingWorkflowDTOs = new ArrayList<>();
        for ( LWAddMessageDTO addMessage : addMessages )
        {
            final LockingWorkflowDTO lockingWorkflowDTO = addMessage.getDto();
            final MaterialLock lockedRecord = lockedRecordsHome.findById( lockingWorkflowDTO.getLockRecId() );
            if ( Objects.equals( lockedRecord.getLockComment(), lockingWorkflowDTO.getLockComment() ) == false )
            {
                throw new BusinessObjectChangedException( "LockComment", lockingWorkflowDTO.getLockRecId(), lockedRecord.getLockComment(), lockedRecord.getMaterialStatus(),
                        lockedRecord.getProdEndTs(), lockedRecord.getQsEndTs(), lockedRecord.getTcsEndTs(), lockedRecord.getAvEndTs() );
            }
            if ( save == true )
            {
                addMessage( lockedRecord, user, addMessage.getMessage(), isRemarkAV );
            }
            lockingWorkflowDTOs.add( lockingWorkflowDTO );
        }

        lockingWorkflowView.refreshCache( lockingWorkflowDTOs );
    }

    //    public boolean isMultiLock( LockingWorkflowDTO lockingWorkflowDTO )
    //    {
    //        final long countLocks = lockingWorkflowDTO.createStrip() ?
    //                lockedRecordsHome.countStripLocks( lockingWorkflowDTO.getLot(), lockingWorkflowDTO.getSublot(), lockingWorkflowDTO.getInvSuffix(), lockingWorkflowDTO.getDropId(),
    //                        lockingWorkflowDTO.getCutId(), lockingWorkflowDTO.getPaletteId() ) :
    //                lockedRecordsHome.countBandLocks( lockingWorkflowDTO.getLot(), lockingWorkflowDTO.getSublot(), lockingWorkflowDTO.getInvSuffix() );
    //        return countLocks > 1;
    //
    //    }

    public void handle( final MaterialLock lockedRecord, final LockingWorkflowDTO lockingWorkflowDTO, final String user, final String initialMessage, final boolean hasRoleProd,
            final boolean hasRoleQS, final boolean hasRoleAV, final boolean hasRoleTCS, final String matState, boolean unlock, final String doneMessage ) throws BusinessException
    {
        if ( matState != null )
        {
            lockedRecord.setMaterialStatus( matState );
        }
        final LocalDateTime unlockDate = LocalDateTime.now();
        if ( unlock )
        {
            lockedRecord.setRevocation( unlockDate );
            lockedRecord.setActive( false );
        }
        writeEndTime( lockedRecord, hasRoleProd, hasRoleQS, hasRoleAV, hasRoleTCS, unlockDate );
        //        if ( StringTools.isNullOrEmpty( matState ) == false && matState.charAt( 0 ) == LockingWorkflowBusinessBean.SCRAP_MATERIAL_STATUS && matState.length() == 1 )
        //        {
        //            String costCenter = lockedRecord.getKst();
        //            if ( Objects.deepEquals( costCenter, "52" ) || Objects.deepEquals( costCenter, "62" ) || Objects.deepEquals( costCenter, "63" ) || Objects.deepEquals( costCenter, "64" )
        //                    || Objects.deepEquals( costCenter, "84" ) )
        //            {
        //                // OperationsHist operationsHist = lockedRecord.getOperationsHist();
        //                IngotInventory ingotInventory = ingotInventoryHome.findById( lockedRecord.getCastSampleNbr() );
        //                // IngotInventory ingotInventory = ingotInventoryHome.findByOpHistId( operationsHist );
        //                ingotInventory.setLot( "SCHROTT" );
        //                ingotInventoryHome.persist( ingotInventory );
        //            }
        //        }
    }

    //    public void sapTelegram( OperationsHist operationsHistRecord, LockingWorkflowDTO lockingWorkflowDTO, String recordType, String userId, String materialStatus ) throws BusinessException
    //    {
    //        int recordStatus = operationsHistRecord.getRecordStatus().getCode();
    //
    //        if ( recordStatus != 0 && recordStatus != 70 )
    //        {
    //            throw new BusinessException( "BDE-Schichtsatz hat noch nicht den Sendestatus H!" );
    //        }
    //        SapUpload sapUploadrecord = new SapUpload();
    //        sapUploadrecord.setDatetimeStamp( null );
    //        sapUploadrecord.setRecordType( recordType );
    //        sapUploadrecord.setUserId( userId );
    //        sapUploadrecord.setScheduleNbr( lockingWorkflowDTO.getKst() );
    //        sapUploadrecord.setSchedCostCenter( lockingWorkflowDTO.getKst() );
    //        sapUploadrecord.setCostCenter( null );// ?
    //        sapUploadrecord.setLot( lockingWorkflowDTO.getLot() );
    //        sapUploadrecord.setProdOrderNo( lockingWorkflowDTO.getProdOrderNo() );      //Neufeld 22.04.21 prodOrderNo ist in LockingWorkflow eingefügt
    //        sapUploadrecord.setSublot( lockingWorkflowDTO.getSublot() );
    //        sapUploadrecord.setInvSuffix( lockingWorkflowDTO.getInvSuffix() );
    //        sapUploadrecord.setOperationSeq( lockingWorkflowDTO.getOpSeq() );
    //        sapUploadrecord.setDropId( lockingWorkflowDTO.getDropId() );
    //        sapUploadrecord.setCutId( lockingWorkflowDTO.getCutId() == null ? 0 : lockingWorkflowDTO.getCutId() );
    //        sapUploadrecord.setPaletteId( lockingWorkflowDTO.getPaletteId() == null ? 0 : lockingWorkflowDTO.getPaletteId() );
    //        sapUploadrecord.setScheduleNbr( "0000000" );
    //        sapUploadrecord.setScheduledOrder( 0 );
    //        sapUploadrecord.setUploadId( 0L );
    //        if ( materialStatus.equals( LockingWorkflowBusiness.CONTAINER_MATERIAL_STATUS_STRING ) )
    //        {
    //            sapUploadrecord.setCostCenter( lockingWorkflowDTO.getKst() );
    //        }
    //        sapUploadHome.persist( sapUploadrecord );
    //
    //        SapLockrecUpload sapLockrecUpload = new SapLockrecUpload();
    //        sapLockrecUpload.setSapUpload( sapUploadrecord );
    //        sapLockrecUpload.setMaterialStatus( materialStatus );
    //        sapLockrecUpload.setBlockedProd( "X" );
    //        sapLockrecUpload.setBlockedQa( null );
    //        sapLockrecUpload.setMaterialLockType( lockingWorkflowDTO.getMaterialLockType() );
    //        sapLockrecUpload.setReason( lockingWorkflowDTO.getReason() );
    //        sapLockrecUpload.setMaterialLockLocation( lockingWorkflowDTO.getMaterialLockLocation() );
    //        sapLockrecUploadHome.persist( sapLockrecUpload );
    //    }

    //    public void tabgTelegram( OperationsHist operationsHistRecord, LockingWorkflowDTO lockingWorkflowDTO, String recordType, String materialStatus, String userId, String correctionInd,
    //            String confirmationInd ) throws BusinessException
    //    {
    //        int recordStatus = operationsHistRecord.getRecordStatus().getCode();
    //
    //        if ( recordStatus != 0 && recordStatus != 70 )
    //        {
    //            throw new BusinessException( "Arbeitsgang wurde noch nicht an SAP versendet" );
    //        }
    //        SapUpload sapUploadrecord = new SapUpload();
    //        sapUploadrecord.setDatetimeStamp( null );
    //        sapUploadrecord.setRecordType( recordType );
    //        sapUploadrecord.setUserId( userId );
    //        sapUploadrecord.setScheduleNbr( lockingWorkflowDTO.getKst() );
    //        sapUploadrecord.setSchedCostCenter( lockingWorkflowDTO.getKst() );
    //        sapUploadrecord.setCostCenter( null );// ?
    //        sapUploadrecord.setLot( lockingWorkflowDTO.getLot() );
    //        sapUploadrecord.setProdOrderNo( lockingWorkflowDTO.getProdOrderNo() );      //Neufeld 22.04.21 prodOrderNo ist in LockingWorkflow eingefügt
    //        sapUploadrecord.setSublot( lockingWorkflowDTO.getSublot() );
    //        sapUploadrecord.setInvSuffix( lockingWorkflowDTO.getInvSuffix() );
    //        sapUploadrecord.setOperationSeq( lockingWorkflowDTO.getOpSeq() );
    //        sapUploadrecord.setDropId( lockingWorkflowDTO.getDropId() );
    //        sapUploadrecord.setCutId( lockingWorkflowDTO.getCutId() == null ? 0 : lockingWorkflowDTO.getCutId() );
    //        sapUploadrecord.setPaletteId( lockingWorkflowDTO.getPaletteId() == null ? 0 : lockingWorkflowDTO.getPaletteId() );
    //        sapUploadrecord.setScheduleNbr( "0000000" );
    //        sapUploadrecord.setScheduledOrder( 0 );
    //        sapUploadrecord.setCorrectionInd( correctionInd );
    //        sapUploadrecord.setConfirmationInd( confirmationInd );
    //        sapUploadrecord.setUploadId( 0L );
    //        sapUploadHome.persist( sapUploadrecord );
    //
    //        SapLockrecUpload sapLockrecUpload = new SapLockrecUpload();
    //        sapLockrecUpload.setSapUpload( sapUploadrecord );
    //        sapLockrecUpload.setBlockedProd( "X" );
    //        sapLockrecUpload.setBlockedQa( null );
    //        sapLockrecUpload.setMaterialStatus( materialStatus );
    //        sapLockrecUpload.setMaterialLockType( lockingWorkflowDTO.getMaterialLockType() );
    //        sapLockrecUpload.setReason( lockingWorkflowDTO.getReason() );
    //        sapLockrecUpload.setMaterialLockLocation( lockingWorkflowDTO.getMaterialLockLocation() );
    //        sapLockrecUploadHome.persist( sapLockrecUpload );
    //    }

    //    public OperationsHist contHandling( OperationsHist operationsHistRecord, LockingWorkflowDTO lockingWorkflowDTO ) throws BusinessException
    //    {
    //        boolean coilBreak = false;
    //
    //        if ( operationsHistRecord.getCostCenter().getCostCenter().equals( COST_CENTER.EXTERN ) )
    //        {
    //            operationsHistRecord.setCostCenter( new CostCenter( BDE.LOCKING_WORKFLOW.CONTAINER_CC ) );
    //            operationsHistRecord.setOutputLocation( BDE.LOCKING_WORKFLOW.CONTAINER_OUTPUT_LOC );
    //            operationsHistRecord.setUserId( BDE.LOCKING_WORKFLOW.TO_CONTAINER_USER + "_UPD" );
    //            operationsHistRecord.setDatetimeStamp( new Timestamp( System.currentTimeMillis() ) );
    //            return operationsHistRecord;
    //        }
    //
    //        List<OperationsHist> colilBreaks = operationsHistHome.coilBreak( operationsHistRecord );
    //
    //        if ( colilBreaks == null ) // Exception in operationsHistHome.coilBreak
    //        {
    //            return operationsHistRecord;
    //        }
    //        int quantCoilBreak = colilBreaks.size();
    //
    //        if ( quantCoilBreak >= 1 )
    //        {
    //            coilBreak = true;
    //        }
    //
    //        if ( coilBreak == false ) // Kein BANDRISS
    //        {
    //            String coilId = operationsHistHome.getFLSCoilID( operationsHistRecord );
    //            OperationsHist operationsHistRecordNew = (OperationsHist) SerializationUtils.clone( operationsHistRecord );
    //
    //            SchedulingTable schedulingTable = operationsHistRecord.getSchedulingTable();
    //            if ( schedulingTable == null )
    //            {
    //                throw new BusinessException( "contHandling schedulingTable is null Error" );
    //            }
    //            SchedulingTable schedulingTableRecordNew = (SchedulingTable) SerializationUtils.clone( schedulingTable );
    //
    //            operationsHistRecordNew.setOpHistId( 0 );
    //            operationsHistRecordNew.setCostCenter( new CostCenter( BDE.LOCKING_WORKFLOW.CONTAINER_CC ) );
    //            operationsHistRecordNew.setMaterialNo( BDE.LOCKING_WORKFLOW.CONTAINER_MAT_NO );
    //            operationsHistRecordNew.setOperationSeq( 0 );
    //            operationsHistRecordNew.setFlsSts( (long) BDE.FLS_STATUS.FLS_STOP );
    //            operationsHistRecordNew.setOutputLocation( BDE.LOCKING_WORKFLOW.CONTAINER_OUTPUT_LOC );
    //            operationsHistRecordNew.setRecordStatus( new CodeDescription( BDE.STATUS.CORRECTED_STATUS ) );
    //            operationsHistRecordNew.setPiecesIn( 1 );
    //            operationsHistRecordNew.setPiecesOut( 1 );
    //            operationsHistRecordNew.setUserId( BDE.LOCKING_WORKFLOW.TO_CONTAINER_USER );
    //            operationsHistRecordNew.setDatetimeStamp( new Timestamp( System.currentTimeMillis() ) );
    //
    //            schedulingTableRecordNew.setId( 0 );
    //
    //            schedulingTableRecordNew.setOperationSeq( 0 );
    //
    //            String newCoilId = operationsHistHome.getFLSCoilID( operationsHistRecordNew );
    //            operationsHistHome.persist( operationsHistRecordNew );
    //
    //            transportService.renameHandlingUnit( coilId, newCoilId );
    //
    //            boolean buildupsExistsforContainer = buildupDetailHome.existFor( operationsHistRecordNew );
    //            BuildupDetail buildupOld = buildupDetailHome.findLastBuildupDetail( operationsHistRecordNew );
    //            if ( buildupsExistsforContainer == true )
    //            {
    //                //throw new BusinessException( "BuildupDetail für Container existiert bereits!" );
    //            }
    //            else
    //            {
    //
    //                if ( buildupOld != null )
    //                {
    //                    BuildupDetail buildupDetailforContainer = new BuildupDetail();
    //
    //                    buildupDetailforContainer = buildupOld;
    //
    //                    buildupDetailforContainer.setDisplayOrder( 1 );
    //                    buildupDetailforContainer.setOperationsHist( operationsHistRecordNew );
    //                    buildupDetailforContainer.setLot( buildupOld.getLot() );
    //                    buildupDetailforContainer.setSublot( buildupOld.getSublot() );
    //                    buildupDetailforContainer.setInvSuffix( buildupOld.getInvSuffix() );
    //                    buildupDetailforContainer.setDropId( buildupOld.getDropId() );
    //                    buildupDetailforContainer.setCutId( buildupOld.getCutId() );
    //                    buildupDetailforContainer.setDateTimeStamp( new Date() );
    //
    //                    buildupDetailforContainer.setExitArbor( buildupOld.getExitArbor() );
    //                    buildupDetailforContainer.setWidth( buildupOld.getWidth() );
    //                    buildupDetailforContainer.setBuildup( buildupOld.getBuildup() );
    //                    buildupDetailforContainer.setWeight( buildupOld.getWeight() );
    //
    //                    buildupDetailHome.persist( buildupDetailforContainer );
    //                }
    //            }
    //
    //            schedulingTableRecordNew.setOpHistId( operationsHistRecordNew.getOpHistId() );
    //
    //            operationsHistRecordNew.setSchedulingTable( schedulingTableRecordNew );
    //
    //            schedulingTableRecordNew.setOperationsHist( operationsHistRecordNew );
    //
    //            schedulingTableHome.persist( schedulingTableRecordNew );
    //        }
    //        else
    //        {
    //            for ( int index = 0; index < quantCoilBreak; index++ )
    //            {
    //                colilBreaks.get( index ).setMaterialNo( BDE.LOCKING_WORKFLOW.CONTAINER_MAT_NO );
    //                colilBreaks.get( index ).setOperationSeq( 0 );
    //                colilBreaks.get( index ).setOutputLocation( BDE.LOCKING_WORKFLOW.CONTAINER_OUTPUT_LOC );
    //                colilBreaks.get( index ).setRecordStatus( new CodeDescription( BDE.STATUS.CORRECTED_STATUS ) );
    //                colilBreaks.get( index ).setPiecesIn( 1 );
    //                colilBreaks.get( index ).setPiecesOut( 1 );
    //                colilBreaks.get( index ).setUserId( BDE.LOCKING_WORKFLOW.TO_CONTAINER_USER + "_UPD" );
    //                colilBreaks.get( index ).setDatetimeStamp( new Timestamp( System.currentTimeMillis() ) );
    //            }
    //        }
    //
    //        scrapHandling( operationsHistRecord, lockingWorkflowDTO );
    //        return operationsHistRecord;
    //    }

    //    public void scrapHandling( OperationsHist operationsHistRecord, LockingWorkflowDTO lockingWorkflowDTO ) throws BusinessException
    //    {
    //        if ( Objects.equals( operationsHistRecord.getNextCostCenter(), BDE.COST_CENTER.PACKING ) )
    //        {
    //            FinishedGoods finishedGoodsPack = finishedGoodsHome.findBy( lockingWorkflowDTO.getLot(), lockingWorkflowDTO.getSublot(), lockingWorkflowDTO.getInvSuffix(), lockingWorkflowDTO.getDropId(),
    //                    lockingWorkflowDTO.getCutId(), lockingWorkflowDTO.getPaletteId() );
    //            if ( finishedGoodsPack == null )
    //            {
    //                lockingWorkflowDTO.setPacking( BDE.LOCKING_WORKFLOW.FG_NOT_EXISTS );
    //                lockingWorkflowDTO.setFinishedGoodsId( 0L ); // something
    //                return;
    //            }
    //            if ( StringTools.isNullOrEmpty( finishedGoodsPack.getPackingLot() ) )
    //            {
    //                lockingWorkflowDTO.setPacking( BDE.LOCKING_WORKFLOW.FG_PACKING_LOT_IS_EMPTY );
    //                lockingWorkflowDTO.setFinishedGoodsId( finishedGoodsPack.getId() );
    //            }
    //            else
    //            {
    //                lockingWorkflowDTO.setPacking( BDE.LOCKING_WORKFLOW.FG_PACKING_LOT_IS_NOT_EMPTY );
    //                lockingWorkflowDTO.setFinishedGoodsId( 0L ); // something
    //            }
    //        }
    //    }

    private void writeEndTime( MaterialLock lockedRecord, final boolean hasRoleProd, final boolean hasRoleQS, final boolean hasRoleAV, final boolean hasRoleTCS, LocalDateTime unlockDate )
    {
        if ( hasRoleProd && lockedRecord.getProdStartTs() != null )
        {
            lockedRecord.setProdEndTs( unlockDate );
        }
        if ( hasRoleQS && lockedRecord.getQsStartTs() != null )
        {
            lockedRecord.setQsEndTs( unlockDate );
        }

        if ( hasRoleAV && lockedRecord.getAvStartTs() != null )
        {
            lockedRecord.setAvEndTs( unlockDate );
        }

        if ( hasRoleTCS && lockedRecord.getTcsStartTs() != null )
        {
            lockedRecord.setTcsEndTs( unlockDate );
        }
    }

    public Map<String, String> createMaterialLockTypeHashTable()
    {
        final TypedQuery<Tuple> lockTypesQuery = entityManager.createQuery( "select mlt.apk as apk, mlt.description as description from MaterialLockType mlt order by mlt.apk", Tuple.class );
        final Map<String, String> lockTypes = new Hashtable<>();
        lockTypesQuery.getResultList().forEach( tuple -> lockTypes.put( tuple.get( "apk", String.class ), tuple.get( "description", String.class ) ) );
        return lockTypes;
    }

    public Map<String, String> createCauserHashTable()
    {
        final TypedQuery<Tuple> lockTypesQuery = entityManager.createQuery( "select m.apk as apk, m.description as description from Machine m order by m.apk", Tuple.class );
        final Map<String, String> lockTypes = new Hashtable<>();
        lockTypesQuery.getResultList().forEach( tuple -> lockTypes.put( tuple.get( "apk", String.class ), tuple.get( "description", String.class ) ) );
        return lockTypes;
    }

    public Map<String, String> createMaterialLockLocationHashTable()
    {
        final TypedQuery<Tuple> lockLocationsQuery = entityManager.createQuery( "select mll.apk as apk, mll.description as description from MaterialLockLocation mll order by mll.apk", Tuple.class );
        final Map<String, String> lockLocations = new Hashtable<>();
        lockLocationsQuery.getResultList().forEach( tuple -> lockLocations.put( tuple.get( "apk", String.class ), tuple.get( "description", String.class ) ) );
        return lockLocations;
    }

    //    public void insertBuilupDetail( OperationsHist opHist, LockingWorkflowDTO lockingWorkflowDTO ) throws BusinessException
    //    {
    //        try
    //        {
    //            if ( !buildupDetailHome.existFor( opHist ) )
    //            {
    //                BuildupDetail prevBuildup = buildupDetailHome.findLastBuildupDetail( opHist );
    //                if ( prevBuildup != null )
    //                {
    //                    buildupDetailHome.detach( prevBuildup );
    //                    BuildupDetail newBuildup = prevBuildup;
    //                    newBuildup.setId( 0 );
    //                    newBuildup.setOperationsHist( opHist );
    //                    newBuildup.setDateTimeStamp( new Date() );
    //
    //                    buildupDetailHome.persist( newBuildup );
    //
    //                }
    //            }
    //        }
    //        catch ( Exception e )
    //        {
    //            // do nothing
    //            return;
    //        }
    //    }
}