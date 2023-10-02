package com.hydro.casting.server.ejb.downtime.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.downtime.dto.DowntimeRequestDTO;
import com.hydro.casting.server.model.downtime.DowntimeRequest;
import com.hydro.casting.server.model.downtime.dao.DowntimeRequestHome;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.server.common.service.BaseService;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionSynchronizationRegistry;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Stateless
public class DowntimeRequestService extends BaseService<DowntimeRequest, DowntimeRequestDTO>
{
    private final static Logger log = LoggerFactory.getLogger( DowntimeRequestService.class );

    //@formatter:off
    private final static String QUERY =
            "   SELECT dr.id as id, "
          + "          dr.costCenter as costCenter, "
          + "          dr.machine as machine, "
          + "          dr.fromTS as fromTS, "
          + "          dr.endTS as endTS, "
          + "          dr.description as description, "
          + "          dr.phase as phase, "
          + "          dr.createTS as createTS, "
          + "          dr.releaseUser as releaseUser, "
          + "          dr.releaseTS as releaseTS "
          + "     FROM DowntimeRequest dr ";

    private final static String SELECT_ALL =
            "    WHERE dr.releaseTS is null";
    private final static String SELECT_WHERE =
            "    WHERE dr.releaseTS is null "
          + "      AND dr in (:downtimeRequests)";
    //@formatter:on
    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    @EJB
    private DowntimeRequestHome downtimeRequestHome;

    public DowntimeRequestService()
    {
        super( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.PROD_VERSION_CACHE_NAME, Casting.CACHE.DOWNTIME_REQUEST_KEY );
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
    public List<DowntimeRequestDTO> load()
    {
        final String sql = QUERY + SELECT_ALL;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<DowntimeRequestDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setResultTransformer( new AliasToBeanResultTransformer( DowntimeRequestDTO.class ) );

        return query.list();
    }

    @Override
    public List<DowntimeRequestDTO> load( Collection<DowntimeRequest> entities )
    {
        final String sql = QUERY + SELECT_WHERE;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<DowntimeRequestDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setParameter( "downtimeRequests", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( DowntimeRequestDTO.class ) );

        return query.list();
    }

    public List<DowntimeRequest> split( DowntimeRequest downtimeRequest, LocalDateTime splitTime )
    {
        final DowntimeRequest copy = new DowntimeRequest();
        copy.setCostCenter( downtimeRequest.getCostCenter() );
        copy.setMachine( downtimeRequest.getMachine() );
        copy.setFromTS( splitTime );
        copy.setEndTS( downtimeRequest.getEndTS() );
        copy.setDescription( downtimeRequest.getDescription() );
        copy.setCreateTS( LocalDateTime.now() );
        copy.setReleaseUser( null );
        copy.setReleaseTS( null );
        downtimeRequestHome.persist( copy );

        downtimeRequest.setEndTS( splitTime );

        return Arrays.asList( downtimeRequest, copy );
    }

    public void release( DowntimeRequest downtimeRequest, String userId )
    {
        downtimeRequest.setReleaseTS( LocalDateTime.now() );
        downtimeRequest.setReleaseUser( userId );
    }

    public void createDowntimeRequest( String costCenter, String machine, LocalDateTime start, LocalDateTime end, String description, String phase )
    {
        DowntimeRequest downtimeRequest = createDowntimeRequest( costCenter, start, end, description, phase );
        downtimeRequest.setMachine( machine );
    }

    public DowntimeRequest createDowntimeRequest( String costCenter, LocalDateTime start, LocalDateTime end, String description, String phase )
    {
        final DowntimeRequest downtimeRequest = new DowntimeRequest();
        downtimeRequest.setCostCenter( costCenter );
        downtimeRequest.setFromTS( start );
        downtimeRequest.setEndTS( end );
        downtimeRequest.setDescription( description );
        downtimeRequest.setPhase( phase );
        downtimeRequest.setCreateTS( LocalDateTime.now() );
        downtimeRequestHome.persist( downtimeRequest );

        return downtimeRequest;
    }

    public void updateEndTSIfOpenDowntimeRequestExists( LocalDateTime endTS, String costCenter, String phase, String description )
    {
        DowntimeRequest downtimeRequest = downtimeRequestHome.findUniqueOpenDowntimeRequest( costCenter, phase, description );
        if ( downtimeRequest != null )
        {
            downtimeRequest.setEndTS( endTS );
        }
    }

    public void updateEndTSForAllOpenDowntimeRequests( LocalDateTime endTS, String costCenter )
    {
        List<DowntimeRequest> downtimeRequests = downtimeRequestHome.findAllOpenForCostCenterWithoutEnd( costCenter );
        if ( downtimeRequests != null && !downtimeRequests.isEmpty() )
        {
            for ( DowntimeRequest downtimeRequest : downtimeRequests )
            {
                downtimeRequest.setEndTS( endTS );
            }
        }
    }
}