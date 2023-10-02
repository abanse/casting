package com.hydro.casting.server.ejb.main.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.MachineDTO;
import com.hydro.casting.server.model.res.Machine;
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
public class MachineService extends BaseService<Machine, MachineDTO>
{
    private final static Logger log = LoggerFactory.getLogger( MachineService.class );
    //@formatter:off
    private final static String QUERY =
            "   SELECT m.apk as apk, "
          + "          m.maxWeight as maxWeight, "
          + "          m.maxCastingLength as maxCastingLength, "
          + "          m.lastCharge as lastCharge, "
          + "          m.currentStep as currentStep, "
          + "          m.currentStepStartTS as currentStepStartTS, "
          + "          m.currentStepDuration as currentStepDuration, "
          + "          (select max(dt.downtimeKind.description) from Downtime dt where dt.costCenter = m.apk and dt.endTS is null) as activeDowntime, "
          + "          (select max(dt.downtimeKind.description) from Downtime dt where dt.costCenter = m.apk and dt.endTS > m.currentStepStartTS) as currentStepDowntime "
          + "     FROM Machine m ";
    private final static String SELECT_WHERE =
            "    WHERE m in (:machines)";
    //@formatter:on
    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public MachineService()
    {
        super( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.MACHINE_DATA_PATH );
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
    public List<MachineDTO> load()
    {
        final String sql = QUERY;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<MachineDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setResultTransformer( new AliasToBeanResultTransformer( MachineDTO.class ) );

        return query.list();
    }

    @Override
    public List<MachineDTO> load( Collection<Machine> entities )
    {
        final String sql = QUERY + SELECT_WHERE;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<MachineDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setParameter( "machines", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( MachineDTO.class ) );

        return query.list();
    }
}