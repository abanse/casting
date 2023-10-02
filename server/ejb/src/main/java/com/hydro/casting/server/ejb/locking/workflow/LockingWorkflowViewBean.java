package com.hydro.casting.server.ejb.locking.workflow;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.locking.workflow.LockingWorkflowConstants;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowView;
import com.hydro.casting.server.contract.locking.workflow.dto.LockHistoryDTO;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.casting.server.ejb.locking.workflow.detail.LockingWorkflowDetailProvider;
import com.hydro.core.common.cache.ServerCache;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.server.common.model.AliasToBeanTrimCharResultTransformer;
import com.hydro.core.server.contract.workplace.TaskValidation;
import com.hydro.core.server.contract.workplace.ViewModel;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Stateless
public class LockingWorkflowViewBean implements LockingWorkflowView
{
    private final static Logger log = LoggerFactory.getLogger( LockingWorkflowViewBean.class );
    @Inject
    private ServerCacheManager serverCacheManager;

    //@formatter:off
    private final static String LOCKING_WORKFLOW_QUERY =
              "select   ml.objid as lockRecId, "
            + "         ml.imposationMachine as kst, "
            + "         ml.material.name as material, "
            + "         ml.materialStatus as materialStatus, "
            + "         ml.prodStartTs as prodStartTs, "
            + "         ml.prodEndTs as prodEndTs, "
            + "         ml.avStartTs as avStartTs, "
            + "         ml.avEndTs as avEndTs, "
            + "         ml.tcsStartTs as tcsStartTs, "
            + "         ml.tcsEndTs as tcsEndTs, "
            + "         ml.qsStartTs as qsStartTs, "
            + "         ml.qsEndTs as qsEndTs, "
            + "         ml.lockComment as lockComment, "
            + "         ml.imposation as lockDate, "
            + "         ml.material.alloy.name as alloy, "
            + "         ml.material.height as gaugeOut, "
            + "         ml.material.width as widthOut, "
            + "         ml.material.length as lengthOut, "
            + "         ml.imposationComment as defectTypeRea, "
            + "         ml.material.weight as weight, "
            + "         ml.materialLockType.apk as defectTypeCat, "
            + "         ml.materialLockType.description as scrapCodeDescription, "
            + "         ml.materialLockLocation.apk as defectTypeLoc, "
            + "         ml.materialLockLocation.description as scrapAreaCodeDescription, "
            + "         ml.imposator as userId, "
            + "         ml.revocation as freeDate, "
            + "         substr(ml.material.name, 0, 5) as castSampleNbr "
            + "from     MaterialLock ml "
            + "where    ${filter} "; //ml.active = true
//            "SELECT   ml.LR.KST, " +
//            "         LR.LOT, " +
//            "         LR.SUBLOT, " +
//            "         LR.INV_SUFFIX as invSuffix, " +
//            "         O.SCHEDULE_NBR as scheduleNbr, " +
//            "         O.ALLOY, " +
//            "         '-' as temper, " +
//            "         '-' as qualityCode, " +
//            "         O.GAUGE_OUT as gaugeOut, " +
//            "         O.WIDTH_OUT as widthOut, " +
//            "         O.LENGTH_OUT as lengthOut, " +
//            "         CASE " +
//            "            WHEN LR.DEFECT_TYPE_CAT IS NULL THEN '-' " +
//            "            ELSE LR.DEFECT_TYPE_CAT " +
//            "         END as defectTypeCat, " +
//            "         CASE " +
//            "            WHEN LR.DEFECT_TYPE_LOC IS NULL THEN '-' " +
//            "            ELSE LR.DEFECT_TYPE_LOC " +
//            "         END as defectTypeLoc, " +
//            "         CASE " +
//            "            WHEN LR.DEFECT_TYPE_REA IS NULL THEN '-' " +
//            "            ELSE LR.DEFECT_TYPE_REA " +
//            "         END as defectTypeRea, " +
//            "         SC.DESCRIPTION as scrapCodeDescription, " +
//            "         SAC.DESCRIPTION as scrapAreaCodeDescription, " +
//            "         '-' as customerName, " +
//            "         '-' as customerOrderNr, " +
//            "         '-' as orderDescription, " +
//            "         0 as finishGauge, " +
//            "         0 as orderedWidth, " +
//            "         CASE WHEN O.MATERIAL_NO IS NULL THEN '-' ELSE O.MATERIAL_NO END as materialNo, " +
//            "         CASE " +
//            "            WHEN O.NEXT_COST_CENTER IS NULL THEN '-' " +
//            "            ELSE O.NEXT_COST_CENTER " +
//            "         END as nextCostCenter, " +
//            "         LR.LOCK_DATE as lockDate, " +
//            "         CA.CAST_DROP_NO as castDropNo, " +
//            "         CA.CAST_HOUSE_NO as castHouseNo, " +
//            "         CA.YEAR_CAST_DROP as yearCastDrop, " +
//            "         LR.CAST_SAMPLE_NBR as castSampleNbr, " +
//            "         LR.DROP_ID as dropId, " +
//            "         LR.CUT_ID as cutId, " +
//            "         LR.PALETTE_ID as paletteId, " +
//            "         LR.FREE_DATE as freeDate, " +
//            "         LR.OP_SEQ as opSeq, " +
//            "         LR.USER_ID as userId, " +
//            "         LR.MATERIAL_STATUS as materialStatus, " +
//            "         LR.PROD_START_TS as prodStartTs, " +
//            "         LR.PROD_END_TS as prodEndTs, " +
//            "         LR.AV_START_TS as avStartTs, " +
//            "         LR.AV_END_TS as avEndTs, " +
//            "         LR.TCS_START_TS as tcsStartTs, " +
//            "         LR.TCS_END_TS as tcsEndTs, " +
//            "         LR.QS_START_TS as qsStartTs, " +
//            "         LR.QS_END_TS as qsEndTs, " +
//            "         LOCK_REC_ID as lockRecId, " +
//            "         LR.LOCK_COMMENT as lockComment, " +
//            "         LR.OP_MESSAGE as opMessage, " +
//            "         O.SCHEDULED_ORDER as scheduledOrder, " +
//            "         CASE " +
//            "            WHEN ST.OPERATION_TEXT IS NULL THEN '-' " +
//            "            ELSE ST.OPERATION_TEXT " +
//            "         END as operationText, " +
//            "         CASE " +
//            "            WHEN ST.OPEN_OPERATIONS IS NULL THEN '-' " +
//            "            ELSE ST.OPEN_OPERATIONS " +
//            "         END as openOperations, " +
//            "         '-' as cbuCode, " +
//            "         '-' as kdServiceName, " +
//            "         '-' as kdServiceTel, " +
//            "         OC.CODE, " +
//            "         OC.DESCRIPTION as ocDescription, " +
//            "         CASE WHEN O.WEIGHT_OUT IS NULL THEN 0 ELSE O.WEIGHT_OUT END as weightOut, " +
//            "         CASE WHEN ST.OUTPUT_GAUGE IS NULL THEN 0 ELSE ST.OUTPUT_GAUGE END as outputGauge, " +
//            "         CASE WHEN ST.OUTPUT_WIDTH IS NULL THEN 0 ELSE ST.OUTPUT_WIDTH END as outputWidth, " +
//            "         CASE WHEN ST.OUTPUT_LENGTH IS NULL THEN 0 ELSE ST.OUTPUT_LENGTH END as outputLength, " +
//            "         '-' as partNrCustomer, " +
//            "         '-' as purchaseOrderNr, " +
//            "         CASE WHEN BD.BUILDUP IS NULL THEN 0 ELSE BD.BUILDUP END as buildup, " +
//            "         0 as buildupMin, " +
//            "         0 as buildupMax, " +
//            "         CASE WHEN BD.WEIGHT IS NULL THEN 0 ELSE BD.WEIGHT END as weight, " +
//            "         CASE WHEN PD.WEIGHT IS NULL THEN 0 ELSE PD.WEIGHT END as pdWeight, " +
//            "         CASE WHEN BD.EXIT_ARBOR IS NULL THEN 0 ELSE BD.EXIT_ARBOR END as exitArbor, " +
//            "         '-' as spool, " +
//            "         0 as delWeekDemanded, " +
//            "         0 as delYearDemanded, " +
//            "         O.OP_HIST_ID as opHistId, " +
//            "         '-' as endCostCenter, " +
//            "		  CASE " +
//            "            WHEN O.PROD_ORDER_NO IS NULL THEN '-' " +
//            "            ELSE O.PROD_ORDER_NO " +
//            "         END as prodOrderNo " +
//            "  FROM   MaterialLock ml " +
//            " WHERE       SC.CODE = LR.DEFECT_TYPE_CAT " +
//            "         AND SAC.CODE = LR.DEFECT_TYPE_LOC " +
//            "         AND LR.OP_HIST_ID = O.OP_HIST_ID " +
//            "         AND O.OP_HIST_ID = ST.OP_HIST_ID " +
//            "         AND LR.CAST_SAMPLE_NBR = CA.CAST_SAMPLE_NBR " +
//            "         AND LR.DEFECT_TYPE_ORI = OC.CODE " +
//            "         AND (   O.COST_CENTER = '52' " +
//            "              OR O.COST_CENTER = '67' " +
//            "              OR O.COST_CENTER = '84') " +
//            "         AND O.LOT = BD.LOT " +
//            "         AND O.SUBLOT = BD.SUBLOT " +
//            "         AND ( (O.INV_SUFFIX = BD.INV_SUFFIX) " +
//            "              OR (O.INV_SUFFIX IS NULL AND BD.INV_SUFFIX IS NULL)) " +
//            "         AND ( (LR.CUT_ID = BD.CUT_ID) " +
//            "              OR (LR.CUT_ID IS NULL AND BD.CUT_ID IS NULL)) " +
//            "         AND ( (LR.DROP_ID = BD.DROP_ID) " +
//            "              OR (LR.DROP_ID IS NULL AND BD.DROP_ID IS NULL)) " +
//            "         AND O.LOT = PD.LOT " +
//            "         AND ( (O.INV_SUFFIX = PD.INV_SUFFIX) " +
//            "              OR (O.INV_SUFFIX IS NULL AND PD.INV_SUFFIX IS NULL)) " +
//            "         AND ( (LR.PALETTE_ID = PD.PALETTE_ID) " +
//            "              OR (LR.PALETTE_ID IS NULL AND PD.PALETTE_ID IS NULL)) " +
//            "         AND LR.OP_HIST_ID = BD.OP_HIST_ID " +
//            "         AND LR.OP_HIST_ID = PD.OP_HIST_ID " +
//            "         ${filter} "; //AND LR.FREE_DATE IS NULL
    //@formatter:on

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    @EJB( beanName = "LWLockHistoryDetailProvider" )
    private LockingWorkflowDetailProvider<LockHistoryDTO> lWLockHistoryDetailProvider;

    private Map<Class<?>, LockingWorkflowDetailProvider<?>> lockingWorkflowDetailProviders = new HashMap<>();

    @PostConstruct
    private void postConstruct()
    {
        lockingWorkflowDetailProviders.put( LockHistoryDTO.class, lWLockHistoryDetailProvider );
    }

    @Override
    public List<LockingWorkflowDTO> loadLockingWorkflows( String filter, Map<String, Object> parameters )
    {
        final Map<String, String> filterMap = new HashMap<>();
        filterMap.put( "filter", filter );

        return loadLockingWorkflows( filterMap, parameters );
    }

    private List<LockingWorkflowDTO> loadActiveLockingWorkflows()
    {
        final Map<String, String> filterMap = new HashMap<>();
        filterMap.put( "filter", "ml.active = true" );

        return loadLockingWorkflows( filterMap, null );
    }

    private List<LockingWorkflowDTO> loadLockingWorkflows( List<LockingWorkflowDTO> lockingWorkflowDTOs )
    {
        final Map<String, String> filterMap = new HashMap<>();
        StringBuilder lockRecIds = new StringBuilder();
        for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
        {
            if ( lockRecIds.length() > 0 )
            {
                lockRecIds.append( "," );
            }
            lockRecIds.append( lockingWorkflowDTO.getId() );
        }

        filterMap.put( "filter", "ml.objid IN (" + lockRecIds.toString() + ")" );

        return loadLockingWorkflows( filterMap, null );
    }

    private List<LockingWorkflowDTO> loadLockingWorkflows( Map<String, String> filterMap, Map<String, Object> parameters )
    {
        final long replicTime = System.currentTimeMillis();

        entityManager.flush();

        final String query = StringTools.getKeyValueSubstituted( filterMap, LOCKING_WORKFLOW_QUERY );
        Query<LockingWorkflowDTO> lockingWorkflowQuery = entityManager.unwrap( Session.class ).createQuery( query );

        if ( parameters != null )
        {
            for ( String parameterKey : parameters.keySet() )
            {
                lockingWorkflowQuery.setParameter( parameterKey, parameters.get( parameterKey ) );
            }
        }

        //        lockingWorkflowQuery.addScalar( "kst", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "lot", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "sublot", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "invSuffix", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "scheduleNbr", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "alloy", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "temper", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "qualityCode", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "gaugeOut", StandardBasicTypes.DOUBLE );
        //        lockingWorkflowQuery.addScalar( "widthOut", StandardBasicTypes.DOUBLE );
        //        lockingWorkflowQuery.addScalar( "lengthOut", StandardBasicTypes.DOUBLE );
        //        lockingWorkflowQuery.addScalar( "defectTypeCat", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "defectTypeLoc", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "defectTypeRea", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "scrapCodeDescription", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "scrapAreaCodeDescription", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "customerName", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "customerOrderNr", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "orderDescription", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "finishGauge", StandardBasicTypes.DOUBLE );
        //        lockingWorkflowQuery.addScalar( "orderedWidth", StandardBasicTypes.DOUBLE );
        //        lockingWorkflowQuery.addScalar( "materialNo", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "nextCostCenter", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "openOperations", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "lockDate", StandardBasicTypes.DATE );
        //        lockingWorkflowQuery.addScalar( "castDropNo", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "castHouseNo", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "yearCastDrop", StandardBasicTypes.INTEGER );
        //        lockingWorkflowQuery.addScalar( "castSampleNbr", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "dropId", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "cutId", StandardBasicTypes.INTEGER );
        //        lockingWorkflowQuery.addScalar( "paletteId", StandardBasicTypes.INTEGER );
        //        lockingWorkflowQuery.addScalar( "freeDate", StandardBasicTypes.DATE );
        //        lockingWorkflowQuery.addScalar( "opSeq", StandardBasicTypes.INTEGER );
        //        lockingWorkflowQuery.addScalar( "userId", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "materialStatus", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "prodStartTs", StandardBasicTypes.DATE );
        //        lockingWorkflowQuery.addScalar( "prodEndTs", StandardBasicTypes.DATE );
        //        lockingWorkflowQuery.addScalar( "avStartTs", StandardBasicTypes.DATE );
        //        lockingWorkflowQuery.addScalar( "avEndTs", StandardBasicTypes.DATE );
        //        lockingWorkflowQuery.addScalar( "tcsStartTs", StandardBasicTypes.DATE );
        //        lockingWorkflowQuery.addScalar( "tcsEndTs", StandardBasicTypes.DATE );
        //        lockingWorkflowQuery.addScalar( "qsStartTs", StandardBasicTypes.DATE );
        //        lockingWorkflowQuery.addScalar( "qsEndTs", StandardBasicTypes.DATE );
        //        lockingWorkflowQuery.addScalar( "lockRecId", StandardBasicTypes.LONG );
        //        lockingWorkflowQuery.addScalar( "lockComment", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "opMessage", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "scheduledOrder", StandardBasicTypes.INTEGER );
        //        lockingWorkflowQuery.addScalar( "operationText", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "cbuCode", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "kdServiceName", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "kdServiceTel", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "code", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "ocDescription", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "weightOut", StandardBasicTypes.LONG );
        //        lockingWorkflowQuery.addScalar( "outputGauge", StandardBasicTypes.DOUBLE );
        //        lockingWorkflowQuery.addScalar( "outputWidth", StandardBasicTypes.DOUBLE );
        //        lockingWorkflowQuery.addScalar( "outputLength", StandardBasicTypes.DOUBLE );
        //        lockingWorkflowQuery.addScalar( "partNrCustomer", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "purchaseOrderNr", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "buildup", StandardBasicTypes.DOUBLE );
        //        lockingWorkflowQuery.addScalar( "buildupMin", StandardBasicTypes.INTEGER );
        //        lockingWorkflowQuery.addScalar( "buildupMax", StandardBasicTypes.INTEGER );
        //        lockingWorkflowQuery.addScalar( "weight", StandardBasicTypes.LONG );
        //        lockingWorkflowQuery.addScalar( "pdWeight", StandardBasicTypes.LONG );
        //        lockingWorkflowQuery.addScalar( "exitArbor", StandardBasicTypes.INTEGER );
        //        lockingWorkflowQuery.addScalar( "spool", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "delWeekDemanded", StandardBasicTypes.INTEGER );
        //        lockingWorkflowQuery.addScalar( "delYearDemanded", StandardBasicTypes.INTEGER );
        //        lockingWorkflowQuery.addScalar( "opHistId", StandardBasicTypes.LONG );
        //        lockingWorkflowQuery.addScalar( "endCostCenter", StandardBasicTypes.STRING );
        //        lockingWorkflowQuery.addScalar( "prodOrderNo", StandardBasicTypes.STRING );
        lockingWorkflowQuery.setResultTransformer( new AliasToBeanTrimCharResultTransformer( LockingWorkflowDTO.class ) );

        @SuppressWarnings( "unchecked" )
        final List<LockingWorkflowDTO> lockingWorkflowDTOs = lockingWorkflowQuery.getResultList();

        log.debug( "lockingWorkflow size " + lockingWorkflowDTOs.size() );

        // Group Entries and filter corrupt data
        // Build tree
        Set<Long> existingKeys = new HashSet<>();

        for ( final LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflowDTOs )
        {
            lockingWorkflowDTO.setReplicTime( replicTime );

            // Check corrupted data
            if ( existingKeys.contains( lockingWorkflowDTO.getId() ) )
            {
                log.warn( "Corrupted entry exist in locking workflow id: " + lockingWorkflowDTO.getId() );
                continue;
            }
            existingKeys.add( lockingWorkflowDTO.getId() );

            // set special values

            // owner
            if ( lockingWorkflowDTO.getProdStartTs() != null && lockingWorkflowDTO.getProdEndTs() == null && lockingWorkflowDTO.getMaterialStatus() != null && lockingWorkflowDTO.getMaterialStatus().equals(
                    Casting.LOCKING_WORKFLOW.BLOCK_MARK ) )
            {
                lockingWorkflowDTO.setOwner( LockingWorkflowDTO.OWNER_PROD );
            }
            else if ( lockingWorkflowDTO.getAvStartTs() != null && lockingWorkflowDTO.getAvEndTs() == null && lockingWorkflowDTO.getMaterialStatus() != null && lockingWorkflowDTO.getMaterialStatus().equals(
                    Casting.LOCKING_WORKFLOW.BLOCK_MARK ) )
            {
                lockingWorkflowDTO.setOwner( LockingWorkflowDTO.OWNER_AV );
            }
            else if ( lockingWorkflowDTO.getTcsStartTs() != null && lockingWorkflowDTO.getTcsEndTs() == null && lockingWorkflowDTO.getMaterialStatus() != null && lockingWorkflowDTO.getMaterialStatus().equals(
                    Casting.LOCKING_WORKFLOW.BLOCK_MARK ) )
            {
                lockingWorkflowDTO.setOwner( LockingWorkflowDTO.OWNER_TCS );
            }
            else if ( lockingWorkflowDTO.getQsStartTs() != null && lockingWorkflowDTO.getQsEndTs() == null && lockingWorkflowDTO.getMaterialStatus() != null && lockingWorkflowDTO.getMaterialStatus().equals(
                    Casting.LOCKING_WORKFLOW.BLOCK_MARK ) )
            {
                lockingWorkflowDTO.setOwner( LockingWorkflowDTO.OWNER_QS );
            }
            else
            {
                lockingWorkflowDTO.setOwner( "NN" );
            }
        }

        return lockingWorkflowDTOs;
    }

    @Override
    public ViewModel<LockingWorkflowDTO> validateLockingWorkflow( LockingWorkflowDTO currentLockingWorkflow, boolean hasProdRole, boolean hasAVRole, boolean hasTCSRole, boolean hasQSRole )
    {
        ViewModel<LockingWorkflowDTO> viewModel = new ViewModel<>();
        viewModel.setCurrentDTOs( currentLockingWorkflow );

        if ( currentLockingWorkflow == null )
        {
            return viewModel;
        }

        final String owner = currentLockingWorkflow.getOwner();

        boolean ownRecord = false;
        // SAP Eintrag ist nur für die AV editierbar
        if ( ( LockingWorkflowDTO.OWNER_PROD.equals( owner ) && hasProdRole ) || ( LockingWorkflowDTO.OWNER_QS.equals( owner ) && hasQSRole ) || ( LockingWorkflowDTO.OWNER_AV.equals( owner )
                && hasAVRole ) || ( LockingWorkflowDTO.OWNER_TCS.equals( owner ) && hasTCSRole ) || ( LockingWorkflowDTO.OWNER_SAP.equals( owner ) && hasAVRole ) )
        {
            ownRecord = true;
        }

        final TaskValidation mailTaskValidation = new TaskValidation();
        mailTaskValidation.setId( LockingWorkflowConstants.ACTION_ID.SEND_MAIL );
        if ( currentLockingWorkflow.getChilds() != null && currentLockingWorkflow.getChilds().size() > 0 )
        {
            mailTaskValidation.setDisabled( true );
            mailTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
        }
        else
        {
            mailTaskValidation.setDisabled( false );
            mailTaskValidation.setRemark( null );
        }
        viewModel.addTaskValidations( mailTaskValidation );

        final TaskValidation printTaskValidation = new TaskValidation();
        printTaskValidation.setId( LockingWorkflowConstants.ACTION_ID.PRINT );
        if ( currentLockingWorkflow.getChilds() != null && currentLockingWorkflow.getChilds().size() > 0 )
        {
            printTaskValidation.setDisabled( true );
            printTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
        }
        else
        {
            printTaskValidation.setDisabled( false );
            printTaskValidation.setRemark( null );
        }
        viewModel.addTaskValidations( printTaskValidation );

        final TaskValidation unlockTaskValidation = new TaskValidation();
        unlockTaskValidation.setId( LockingWorkflowConstants.ACTION_ID.UNLOCK );
        if ( ownRecord == false || ( currentLockingWorkflow.getMaterialStatus() != null && currentLockingWorkflow.getMaterialStatus().equals( "B" ) == false ) )

        {
            unlockTaskValidation.setDisabled( true );
            unlockTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
        }
        else
        {
            unlockTaskValidation.setDisabled( false );
            unlockTaskValidation.setRemark( null );
        }
        viewModel.addTaskValidations( unlockTaskValidation );

        final TaskValidation scrapTaskValidation = new TaskValidation();
        scrapTaskValidation.setId( LockingWorkflowConstants.ACTION_ID.SCRAP );
        if ( ownRecord == false || ( currentLockingWorkflow.getMaterialStatus() != null && currentLockingWorkflow.getMaterialStatus().equals( "B" ) == false ) )

        {
            scrapTaskValidation.setDisabled( true );
            scrapTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
        }
        else
        {
            scrapTaskValidation.setDisabled( false );
            scrapTaskValidation.setRemark( null );
        }
        viewModel.addTaskValidations( scrapTaskValidation );

        final TaskValidation moveToContainerTaskValidation = new TaskValidation();
        moveToContainerTaskValidation.setId( LockingWorkflowConstants.ACTION_ID.MOVE_TO_CONTAINER );
        if ( ownRecord == false || ( currentLockingWorkflow.getMaterialStatus() != null && currentLockingWorkflow.getMaterialStatus().equals( "B" ) == false ) )

        {
            moveToContainerTaskValidation.setDisabled( true );
            moveToContainerTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
        }
        else
        {
            moveToContainerTaskValidation.setDisabled( false );
            moveToContainerTaskValidation.setRemark( null );
        }
        viewModel.addTaskValidations( moveToContainerTaskValidation );

        final TaskValidation assignProductionTaskValidation = new TaskValidation();
        assignProductionTaskValidation.setId( LockingWorkflowConstants.ACTION_ID.ASSIGN_PRODUCTION );
        if ( ownRecord == false || ( currentLockingWorkflow.getMaterialStatus() != null && currentLockingWorkflow.getMaterialStatus().equals( "B" ) == false ) )

        {
            assignProductionTaskValidation.setDisabled( true );
            assignProductionTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
        }
        else if ( LockingWorkflowDTO.OWNER_PROD.equals( owner ) )
        {
            assignProductionTaskValidation.setDisabled( true );
            assignProductionTaskValidation.setRemark( "Der Sperreintrag ist Ihnen bereits zugeordnet" );
        }
        else
        {
            assignProductionTaskValidation.setDisabled( false );
            assignProductionTaskValidation.setRemark( null );
        }
        viewModel.addTaskValidations( assignProductionTaskValidation );

        final TaskValidation assignAVTaskValidation = new TaskValidation();
        assignAVTaskValidation.setId( LockingWorkflowConstants.ACTION_ID.ASSIGN_AV );
        if ( ownRecord == false || ( currentLockingWorkflow.getMaterialStatus() != null && currentLockingWorkflow.getMaterialStatus().equals( "B" ) == false ) )

        {
            assignAVTaskValidation.setDisabled( true );
            assignAVTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
        }
        else if ( LockingWorkflowDTO.OWNER_AV.equals( owner ) )
        {
            assignAVTaskValidation.setDisabled( true );
            assignAVTaskValidation.setRemark( "Der Sperreintrag ist Ihnen bereits zugeordnet" );
        }
        else
        {
            assignAVTaskValidation.setDisabled( false );
            assignAVTaskValidation.setRemark( null );
        }
        viewModel.addTaskValidations( assignAVTaskValidation );

        final TaskValidation assignTCSTaskValidation = new TaskValidation();
        assignTCSTaskValidation.setId( LockingWorkflowConstants.ACTION_ID.ASSIGN_TCS );
        if ( ownRecord == false || ( currentLockingWorkflow.getMaterialStatus() != null && currentLockingWorkflow.getMaterialStatus().equals( "B" ) == false ) )

        {
            assignTCSTaskValidation.setDisabled( true );
            assignTCSTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
        }
        else if ( LockingWorkflowDTO.OWNER_TCS.equals( owner ) )
        {
            assignTCSTaskValidation.setDisabled( true );
            assignTCSTaskValidation.setRemark( "Der Sperreintrag ist Ihnen bereits zugeordnet" );
        }
        else
        {
            assignTCSTaskValidation.setDisabled( false );
            assignTCSTaskValidation.setRemark( null );
        }
        viewModel.addTaskValidations( assignTCSTaskValidation );

        final TaskValidation assignQSTaskValidation = new TaskValidation();
        assignQSTaskValidation.setId( LockingWorkflowConstants.ACTION_ID.ASSIGN_QS );
        if ( ownRecord == false || ( currentLockingWorkflow.getMaterialStatus() != null && currentLockingWorkflow.getMaterialStatus().equals( "B" ) == false ) )

        {
            if ( LockingWorkflowDTO.OWNER_PROD.equals( owner ) && hasQSRole )
            {
                assignQSTaskValidation.setDisabled( false );
                assignQSTaskValidation.setRemark( null );
            }
            else
            {
                assignQSTaskValidation.setDisabled( true );
                assignQSTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich!" );
            }
        }
        else if ( LockingWorkflowDTO.OWNER_QS.equals( owner ) )
        {
            assignQSTaskValidation.setDisabled( true );
            assignQSTaskValidation.setRemark( "Der Sperreintrag ist Ihnen bereits zugeordnet" );
        }
        else
        {
            assignQSTaskValidation.setDisabled( false );
            assignQSTaskValidation.setRemark( null );
        }
        viewModel.addTaskValidations( assignQSTaskValidation );

        final TaskValidation addMessageTaskValidation = new TaskValidation();
        addMessageTaskValidation.setId( LockingWorkflowConstants.ACTION_ID.ADD_MESSAGE );
        if ( hasAVRole == false )
        {
            if ( ownRecord == false || ( currentLockingWorkflow.getMaterialStatus() != null && currentLockingWorkflow.getMaterialStatus().equals( "B" ) == false ) )
            {
                addMessageTaskValidation.setDisabled( true );
                addMessageTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
            }
            else
            {
                addMessageTaskValidation.setDisabled( false );
                addMessageTaskValidation.setRemark( null );
            }
        }
        else
        {
            if ( currentLockingWorkflow.getMaterialStatus() != null && currentLockingWorkflow.getMaterialStatus().equals( "B" ) )
            {
                // die AV kann für offene Sperren Kommentare (Erinnerungen) schreben
                addMessageTaskValidation.setDisabled( false );
                addMessageTaskValidation.setRemark( null );
            }
            else
            {
                addMessageTaskValidation.setDisabled( true );
                addMessageTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
            }
        }
        viewModel.addTaskValidations( addMessageTaskValidation );

        final TaskValidation changeInitiatorTaskValidation = new TaskValidation();
        changeInitiatorTaskValidation.setId( LockingWorkflowConstants.ACTION_ID.CHANGE_INITIATOR );
        if ( ownRecord == false )
        {
            changeInitiatorTaskValidation.setDisabled( true );
            changeInitiatorTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
        }
        else
        {
            changeInitiatorTaskValidation.setDisabled( false );
            changeInitiatorTaskValidation.setRemark( null );
        }
        // SAP-Sperren kann die AV nur verschrotten
        if ( ownRecord == true && LockingWorkflowDTO.OWNER_SAP.equals( owner ) && hasAVRole )
        {

            assignTCSTaskValidation.setDisabled( true );
            assignTCSTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
            assignQSTaskValidation.setDisabled( true );
            assignQSTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
            assignProductionTaskValidation.setDisabled( true );
            assignProductionTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
            moveToContainerTaskValidation.setDisabled( true );
            moveToContainerTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
            unlockTaskValidation.setDisabled( true );
            unlockTaskValidation.setRemark( "Der Sperreintrag liegt nicht in Ihrem Verantwortungsbereich" );
        }
        viewModel.addTaskValidations( changeInitiatorTaskValidation );

        return viewModel;
    }

    @Override
    public <S> S loadDetail( Class<S> dto, LockingWorkflowDTO currentLockingWorkflow )
    {
        Session session = entityManager.unwrap( Session.class );
        session.setDefaultReadOnly( true );

        @SuppressWarnings( "unchecked" )
        LockingWorkflowDetailProvider<S> provider = (LockingWorkflowDetailProvider<S>) lockingWorkflowDetailProviders.get( dto );
        if ( provider != null )
        {
            return provider.loadData( currentLockingWorkflow );
        }
        return null;
    }

    @Override
    public void refreshCache( List<LockingWorkflowDTO> lockingWorkflows )
    {
        long start = System.currentTimeMillis();
        log.debug( "replication lw start" );

        ServerCache<List<Long>> lockingWorkflowSequenceCache = serverCacheManager.getCache( Casting.CACHE.LOCKING_CACHE_NAME );
        ServerCache<LockingWorkflowDTO> lockingWorkflowDataCache = serverCacheManager.getCache( Casting.CACHE.LOCKING_CACHE_NAME );

        List<Long> cacheLockingWorkflowSequence = lockingWorkflowSequenceCache.get( Casting.CACHE.LOCKING_WORKFLOW_KEY );

        // single replication
        if ( lockingWorkflows != null )
        {
            List<LockingWorkflowDTO> currentLockingWorkflows = loadLockingWorkflows( lockingWorkflows );

            for ( LockingWorkflowDTO lockingWorkflow : currentLockingWorkflows )
            {
                // remove
                if ( lockingWorkflow.getFreeDate() != null )
                {
                    if ( cacheLockingWorkflowSequence.contains( lockingWorkflow.getId() ) )
                    {
                        cacheLockingWorkflowSequence.remove( lockingWorkflow.getId() );
                        log.debug( "change sequence" );
                        lockingWorkflowSequenceCache.put( Casting.CACHE.LOCKING_WORKFLOW_KEY, cacheLockingWorkflowSequence );
                    }
                    // else nothing to do
                }
                else
                {
                    final String cacheLockingWorkflowKey = Casting.CACHE.LOCKING_WORKFLOW_KEY + "/data/" + lockingWorkflow.getId();
                    // add
                    if ( cacheLockingWorkflowSequence.contains( lockingWorkflow.getId() ) == false )
                    {
                        cacheLockingWorkflowSequence.add( lockingWorkflow.getId() );
                        log.debug( "change sequence" );
                        lockingWorkflowSequenceCache.put( Casting.CACHE.LOCKING_WORKFLOW_KEY, cacheLockingWorkflowSequence );

                        log.debug( "add lockingWorkflow " + lockingWorkflow.getId() );
                        lockingWorkflowDataCache.put( cacheLockingWorkflowKey, lockingWorkflow );
                    }
                    // update
                    else
                    {
                        LockingWorkflowDTO cacheLockingWorkflow = lockingWorkflowDataCache.get( cacheLockingWorkflowKey );
                        if ( Objects.equals( cacheLockingWorkflow, lockingWorkflow ) == false )
                        {
                            log.debug( "update lockingWorkflow " + lockingWorkflow.getId() );
                            lockingWorkflowDataCache.put( cacheLockingWorkflowKey, lockingWorkflow );
                        }
                        // else nothing to do
                    }
                }
            }
        }
        // replicate all
        else
        {
            final List<Long> currentCacheLockingWorkflowSequence;
            if ( cacheLockingWorkflowSequence != null )
            {
                currentCacheLockingWorkflowSequence = new ArrayList<>( cacheLockingWorkflowSequence );
            }
            else
            {
                currentCacheLockingWorkflowSequence = new ArrayList<>();
            }
            List<LockingWorkflowDTO> currentLockingWorkflows = loadActiveLockingWorkflows();
            List<Long> lockingWorkflowSequence = createLockingWorkflowSequence( currentLockingWorkflows );
            if ( currentCacheLockingWorkflowSequence == null || Arrays.equals( currentCacheLockingWorkflowSequence.toArray(), lockingWorkflowSequence.toArray() ) == false )
            {
                log.debug( "put new sequence" );
                lockingWorkflowSequenceCache.put( Casting.CACHE.LOCKING_WORKFLOW_KEY, lockingWorkflowSequence );
            }

            for ( LockingWorkflowDTO currentLockingWorkflow : currentLockingWorkflows )
            {
                final String cacheLockingWorkflowKey = Casting.CACHE.LOCKING_WORKFLOW_KEY + "/data/" + currentLockingWorkflow.getId();
                if ( lockingWorkflowDataCache.containsKey( cacheLockingWorkflowKey ) )
                {
                    LockingWorkflowDTO cacheLockingWorkflow = lockingWorkflowDataCache.get( cacheLockingWorkflowKey );
                    if ( Objects.equals( cacheLockingWorkflow, currentLockingWorkflow ) == false )
                    {
                        log.debug( "update lockingWorkflow " + currentLockingWorkflow.getId() );
                        lockingWorkflowDataCache.put( cacheLockingWorkflowKey, currentLockingWorkflow );
                    }
                }
                else
                {
                    log.debug( "put new lockingWorkflow " + currentLockingWorkflow.getId() );

                    lockingWorkflowDataCache.put( cacheLockingWorkflowKey, currentLockingWorkflow );
                }
                if ( currentCacheLockingWorkflowSequence != null )
                {
                    currentCacheLockingWorkflowSequence.remove( currentLockingWorkflow.getId() );
                }
            }

            // Delete old ones
            if ( currentCacheLockingWorkflowSequence != null )
            {
                for ( Long oldCacheLockingWorkflowId : currentCacheLockingWorkflowSequence )
                {
                    log.debug( "remove new lockingWorkflow " + oldCacheLockingWorkflowId );
                    final String cacheLockingWorkflowKey = Casting.CACHE.LOCKING_WORKFLOW_KEY + "/data/" + oldCacheLockingWorkflowId;
                    lockingWorkflowDataCache.remove( cacheLockingWorkflowKey );
                }
            }
        }

        log.debug( "replication lw end duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }

    private List<Long> createLockingWorkflowSequence( List<LockingWorkflowDTO> lockingWorkflow )
    {
        if ( lockingWorkflow == null || lockingWorkflow.isEmpty() )
        {
            return Collections.emptyList();
        }

        List<Long> lockingWorkflowSequence = new ArrayList<Long>();
        for ( LockingWorkflowDTO lockingWorkflowDTO : lockingWorkflow )
        {
            lockingWorkflowSequence.add( lockingWorkflowDTO.getId() );
        }

        return lockingWorkflowSequence;
    }
}
