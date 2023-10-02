package com.hydro.casting.server.ejb.planning.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.CasterStep;
import com.hydro.casting.server.contract.dto.CastingKTDTO;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.CastingKT;
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
import java.time.Duration;
import java.util.*;

@Stateless
public class CastingKTService extends BaseService<CastingKT, CastingKTDTO>
{
    private final static Logger log = LoggerFactory.getLogger( CastingKTService.class );

    //@formatter:off
    private final static String QUERY =
            "   SELECT ckt.objid as id, "
          + "          ckt.caster as caster, "
          + "          ckt.alloy as alloy, "
          + "          ckt.prio as prio, "
          + "          ckt.castingSpeed as castingSpeed, "
          + "          ckt.handlingTime as handlingTime, "
          + "          ckt.standingTime as standingTime, "
          + "          ckt.castingTM as castingTM, "
          + "          ckt.unloadingTM as unloadingTM "
          + "     FROM CastingKT ckt ";

    private final static String SELECT_WHERE =
            "    WHERE ckt in (:knowledges)";
    //@formatter:on

    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public CastingKTService()
    {
        super( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.CASTING_KT_DATA_PATH );
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
    public List<CastingKTDTO> load()
    {
        final String sql = QUERY;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<CastingKTDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setResultTransformer( new AliasToBeanResultTransformer( CastingKTDTO.class ) );

        return query.list();
    }

    @Override
    public List<CastingKTDTO> load( Collection<CastingKT> entities )
    {
        final String sql = QUERY + SELECT_WHERE;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<CastingKTDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setParameter( "knowledges", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( CastingKTDTO.class ) );

        return query.list();
    }

    public Optional<CastingKTDTO> findCastingKnowledge( final List<CastingKTDTO> castingKTList, final CastingBatch castingBatch )
    {
        Optional<String> alloy = castingBatch.getMembers().stream().findFirst().map( schedulable -> {
            if ( schedulable.getDemand() != null && schedulable.getDemand().getMaterialType() != null )
            {
                return schedulable.getDemand().getMaterialType().getApk();
            }
            return null;
        } );

        return castingKTList.stream().filter( castingKTDTO -> {
            if ( !( Objects.equals( castingKTDTO.getCaster(), "*" ) || Objects.equals( castingKTDTO.getCaster(), castingBatch.getExecutingMachine().getApk() ) ) )
            {
                return false;
            }
            if ( !( Objects.equals( castingKTDTO.getAlloy(), "*" ) || Objects.equals( castingKTDTO.getAlloy(), alloy ) ) )
            {
                return false;
            }
            return true;
        } ).sorted( Comparator.comparingInt( CastingKTDTO::getPrio ) ).findFirst();
    }

    public Optional<CastingKTDTO> findCastingKnowledge( final List<CastingKTDTO> castingKTList, final String caster, final String alloy )
    {
        return castingKTList.stream().filter( castingKTDTO -> {
            if ( !( Objects.equals( castingKTDTO.getCaster(), "*" ) || Objects.equals( castingKTDTO.getCaster(), caster ) ) )
            {
                return false;
            }
            if ( !( Objects.equals( castingKTDTO.getAlloy(), "*" ) || Objects.equals( castingKTDTO.getAlloy(), alloy ) ) )
            {
                return false;
            }
            return true;
        } ).sorted( Comparator.comparingInt( CastingKTDTO::getPrio ) ).findFirst();
    }

    /**
     * Returns the planned duration for the passed caster step based on the passed casting knowledge table entry.
     *
     * @param casterStep   caster step for which the planned duration is requested
     * @param castingKTDTO knowledge table entry used to determine the planned duration
     * @return The planned duration, or null if none was found or the knowledge table entry was null
     */
    public Duration getPlannedDuration( CasterStep casterStep, CastingKTDTO castingKTDTO )
    {
        if ( castingKTDTO == null )
        {
            return null;
        }

        Duration duration;

        switch ( casterStep )
        {
        case Casting:
            duration = Duration.ofMinutes( castingKTDTO.getCastingTM() );
            break;
        case Unloading:
            duration = Duration.ofMinutes( castingKTDTO.getUnloadingTM() );
            break;
        default:
            duration = Duration.ofMinutes( 60 );
            break;
        }

        return duration;
    }
}