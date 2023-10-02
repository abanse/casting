package com.hydro.casting.server.ejb.locking.workflow.detail;

import com.hydro.core.server.common.model.AliasToBeanTrimCharResultTransformer;
import com.hydro.casting.server.contract.locking.workflow.dto.LockHistoryDTO;
import com.hydro.casting.server.contract.locking.workflow.dto.LockHistoryElementDTO;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless( name = "LWLockHistoryDetailProvider" )
public class LWLockHistoryDetailProvider implements LockingWorkflowDetailProvider<LockHistoryDTO>
{
    private static int HISTORY_CHARGE_INTERVAL = 500;

    @PersistenceContext
    private EntityManager entityManager;

    //@formatter:off
    private final static String LOCK_HISTORY_QUERY = 
            "SELECT   LR.MATERIAL_STATUS as materialStatus, " +
            "         ZM.TEXT, " +
            "         LR.LOT, " +
            "         LR.SUBLOT, " +
            "         LR.INV_SUFFIX as invSuffix, " +
            "         LR.OP_SEQ as opSeq, " +
            "         LR.KST, " +
            "         LR.USER_ID as userId, " +
            "         LR.DEFECT_TYPE_CAT as defectTypeCat, " +
            "         LR.DEFECT_TYPE_REA as defectTypeRea, " +
            "         LR.FREE_DATE as freeDate, " +
            "         ZMU.NAME, " +
            "         ZMU.MASSNAHME_TS as massnahmeTs, " +
            "         LR.DROP_ID as dropId, " +
            "         LR.CUT_ID as cutId, " +
            "         LR.PALETTE_ID as PaletteId, " +
            "         LR.LOCK_REC_ID as lockRecId " +
            "  FROM   LOCKED_RECORDS LR, " +
            "         OUTER ZUSATZMASSNAHME ZM, " +
            "         OUTER ZUSATZMASSNAHME_USER ZMU " +
            " WHERE       LR.CAST_SAMPLE_NBR = :castSampleNbr " +
            "         AND LR.OP_HIST_ID = ZM.OP_HIST_ID " +
            "         AND LR.OP_HIST_ID = ZMU.OP_HIST_ID " +
            "         AND LR.LOCK_DATE > current-" + HISTORY_CHARGE_INTERVAL + " UNITS DAY "
            ;
    //@formatter:on

    private List<LockHistoryElementDTO> loadElements( String castSampleNbr )
    {
        if ( castSampleNbr == null )
        {
            return null;
        }

        SQLQuery lockHistoryQuery = ( (Session) entityManager.getDelegate() ).createSQLQuery( LOCK_HISTORY_QUERY );
        lockHistoryQuery.setParameter( "castSampleNbr", castSampleNbr );
        lockHistoryQuery.addScalar( "materialStatus", StandardBasicTypes.STRING );
        lockHistoryQuery.addScalar( "text", StandardBasicTypes.STRING );
        lockHistoryQuery.addScalar( "lot", StandardBasicTypes.STRING );
        lockHistoryQuery.addScalar( "sublot", StandardBasicTypes.STRING );
        lockHistoryQuery.addScalar( "invSuffix", StandardBasicTypes.STRING );
        lockHistoryQuery.addScalar( "opSeq", StandardBasicTypes.INTEGER );
        lockHistoryQuery.addScalar( "kst", StandardBasicTypes.STRING );
        lockHistoryQuery.addScalar( "userId", StandardBasicTypes.STRING );
        lockHistoryQuery.addScalar( "defectTypeCat", StandardBasicTypes.STRING );
        lockHistoryQuery.addScalar( "defectTypeRea", StandardBasicTypes.STRING );
        lockHistoryQuery.addScalar( "freeDate", StandardBasicTypes.DATE );
        lockHistoryQuery.addScalar( "name", StandardBasicTypes.STRING );
        lockHistoryQuery.addScalar( "massnahmeTs", StandardBasicTypes.DATE );
        lockHistoryQuery.addScalar( "dropId", StandardBasicTypes.STRING );
        lockHistoryQuery.addScalar( "cutId", StandardBasicTypes.INTEGER );
        lockHistoryQuery.addScalar( "paletteId", StandardBasicTypes.INTEGER );
        lockHistoryQuery.addScalar( "lockRecId", StandardBasicTypes.LONG );
        lockHistoryQuery.setResultTransformer( new AliasToBeanTrimCharResultTransformer( LockHistoryElementDTO.class ) );

        @SuppressWarnings( "unchecked" )
        List<LockHistoryElementDTO> lockHistoryElements = lockHistoryQuery.list();

        return lockHistoryElements;
    }

    @Override
    public LockHistoryDTO loadData( LockingWorkflowDTO lockingWorkflowDTO )
    {
        if ( lockingWorkflowDTO == null )
        {
            return null;
        }
        String castSampleNbr = lockingWorkflowDTO.getCastSampleNbr();
        if (castSampleNbr == null &&
                lockingWorkflowDTO.getChilds() != null)
        {
            castSampleNbr = lockingWorkflowDTO.getChilds().get( 0 ).getCastSampleNbr();
        }

        LockHistoryDTO lockHistoryDTO = new LockHistoryDTO();
        lockHistoryDTO.setLockHistoryElements( loadElements( castSampleNbr ) );
        return lockHistoryDTO;
    }
}
