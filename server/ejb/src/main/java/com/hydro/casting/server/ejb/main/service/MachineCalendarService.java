package com.hydro.casting.server.ejb.main.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.MachineCalendarDTO;
import com.hydro.casting.server.model.res.MachineCalendar;
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
public class MachineCalendarService extends BaseService<MachineCalendar, MachineCalendarDTO>
{
    private final static Logger log = LoggerFactory.getLogger( MachineCalendarService.class );
    //@formatter:off
    private final static String QUERY =
            "   SELECT mc.objid as id, "
          + "          mc.startTS as startTS, "
          + "          mc.duration as duration, "
          + "          mc.description as description, "
          + "          mc.machine.apk as machine "
          + "     FROM MachineCalendar mc ";
    private final static String SELECT_ALL =
            "    WHERE mc.endTS > current_timestamp()";
    private final static String SELECT_WHERE =
            "    WHERE mc in (:machineCalendars)";
    //@formatter:on
    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public MachineCalendarService()
    {
        super( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.MACHINE_CALENDAR_DATA_PATH );
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
    public List<MachineCalendarDTO> load()
    {
        final String sql = QUERY + SELECT_ALL;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<MachineCalendarDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setResultTransformer( new AliasToBeanResultTransformer( MachineCalendarDTO.class ) );

        return query.list();
    }

    @Override
    public List<MachineCalendarDTO> load( Collection<MachineCalendar> entities )
    {
        final String sql = QUERY + SELECT_WHERE;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<MachineCalendarDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setParameter( "machineCalendars", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( MachineCalendarDTO.class ) );

        return query.list();
    }
}