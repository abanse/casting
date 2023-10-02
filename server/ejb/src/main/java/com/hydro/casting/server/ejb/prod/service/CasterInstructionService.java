package com.hydro.casting.server.ejb.prod.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.CasterInstructionDTO;
import com.hydro.casting.server.model.res.Caster;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.server.common.service.BaseService;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionSynchronizationRegistry;
import java.util.Collection;
import java.util.List;

@Stateless
public class CasterInstructionService extends BaseService<Caster, CasterInstructionDTO>
{
    private final static Logger log = LoggerFactory.getLogger( CasterInstructionService.class );

    /*
    private String caster;
    private String furnace;
    private String charge;
    private String alloy;
    private String castingProgram;
    private double castingLength;
    private double plannedWeight;
    private double netWeight;
     */

    //@formatter:off
    private final static String QUERY =
            "   SELECT c.apk as caster, "
          + "          (select mf.apk from MeltingFurnace mf where mf = cb.meltingFurnace) as furnace, "
          + "          cb.objid as castingBatchOID, "
          + "          cb.charge as charge, "
          + "          (select max(op.demand.materialType.alloy.name) from Operation op where op.batch = cb) as alloy, "
          + "          cb.castingLength as castingLength, "
          + "          cb.plannedWeight as plannedWeight "
          //+ "          cb. as utilization, "
//          + "          cb.charge as bottomWeight, "
//          + "          cb.charge as solidMetal, "
//          + "          cb.charge as liquidMetal "
          //+ "     FROM Caster c left join CastingBatch cb on cb.executingMachine = c and (cb.executionState = 310 or cb.executionState = 320)";
                    // Nur AUTO_IN_PROGRESS (290) und AUTO_UNLOADING_IN_PROGRESS (330) anzeigen
          + "     FROM Caster c left join CastingBatch cb on cb.executingMachine = c and ( cb.executionState = 310 or cb.executionState = 320 )";
    private final static String SELECT_WHERE =
            "    WHERE c in (:machines)";
    //@formatter:on
    @Inject
    private ServerCacheManager serverCacheManager;
    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public CasterInstructionService()
    {
        super( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.PROD_VERSION_CACHE_NAME, Casting.CACHE.CASTER_INSTRUCTION_DATA_PATH );
    }

    @Override
    protected ServerCacheManager getServerCacheManager()
    {
        return serverCacheManager;
    }

    @Override
    protected TransactionSynchronizationRegistry getTransactionSynchronizationRegistry()
    {
        return transactionSynchronizationRegistry;
    }

    @Override
    public List<CasterInstructionDTO> load()
    {
        final String sql = QUERY;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<CasterInstructionDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setResultTransformer( new AliasToBeanResultTransformer( CasterInstructionDTO.class ) );

        return query.list();
    }

    @Override
    public List<CasterInstructionDTO> load( Collection<Caster> entities )
    {
        final String sql = QUERY + SELECT_WHERE;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<CasterInstructionDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setParameter( "machines", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( CasterInstructionDTO.class ) );

        return query.list();
    }
}