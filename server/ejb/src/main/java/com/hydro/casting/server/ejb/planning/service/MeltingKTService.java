package com.hydro.casting.server.ejb.planning.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.FurnaceStep;
import com.hydro.casting.server.contract.dto.MeltingKTDTO;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.MeltingKT;
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
public class MeltingKTService extends BaseService<MeltingKT, MeltingKTDTO>
{
    private final static Logger log = LoggerFactory.getLogger( MeltingKTService.class );

    //@formatter:off
    private final static String QUERY =
            "   SELECT mkt.objid as id, "
          + "          mkt.caster as caster, "
          + "          mkt.furnace as furnace, "
          + "          mkt.alloy as alloy, "
          + "          mkt.prio as prio, "
          + "          mkt.percentageSolidMetal as percentageSolidMetal, "
          + "          mkt.meltingCapacity as meltingCapacity, "
          + "          mkt.handlingTime as handlingTime, "
          + "          mkt.percentageS1 as percentageS1, "
          + "          mkt.percentageS2 as percentageS2, "
          + "          mkt.percentageS3 as percentageS3, "
          + "          mkt.percentageEL as percentageEL, "
          + "          mkt.percentageRA as percentageRA, "
          + "          mkt.standingTime as standingTime, "
          + "          mkt.preparingTM as preparingTM, "
          + "          mkt.chargingTM as chargingTM, "
          + "          mkt.treatingTM as treatingTM, "
          + "          mkt.skimmingTM as skimmingTM, "
          + "          mkt.restingTM as restingTM, "
          + "          mkt.readyToCastTM as readyToCastTM, "
          + "          mkt.castingTM as castingTM "
          + "     FROM MeltingKT mkt ";

    private final static String SELECT_WHERE =
            "    WHERE mkt in (:knowledges)";
    //@formatter:on

    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public MeltingKTService()
    {
        super( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.MELTING_KT_DATA_PATH );
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
    public List<MeltingKTDTO> load()
    {
        final String sql = QUERY;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<MeltingKTDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setResultTransformer( new AliasToBeanResultTransformer( MeltingKTDTO.class ) );

        return query.list();
    }

    @Override
    public List<MeltingKTDTO> load( Collection<MeltingKT> entities )
    {
        final String sql = QUERY + SELECT_WHERE;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<MeltingKTDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setParameter( "knowledges", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( MeltingKTDTO.class ) );

        return query.list();
    }

    public Optional<MeltingKTDTO> findMeltingKnowledge( final List<MeltingKTDTO> meltingKTList, final CastingBatch castingBatch )
    {
        Optional<String> alloy = castingBatch.getMembers().stream().findFirst().map( schedulable -> {
            if ( schedulable.getDemand() != null && schedulable.getDemand().getMaterialType() != null )
            {
                return schedulable.getDemand().getMaterialType().getApk();
            }
            return null;
        } );

        return meltingKTList.stream().filter( meltingKTDTO -> {
            if ( !( Objects.equals( meltingKTDTO.getCaster(), "*" ) || Objects.equals( meltingKTDTO.getCaster(), castingBatch.getExecutingMachine().getApk() ) ) )
            {
                return false;
            }
            if ( !( Objects.equals( meltingKTDTO.getFurnace(), "*" ) || Objects.equals( meltingKTDTO.getFurnace(), castingBatch.getMeltingFurnace().getApk() ) ) )
            {
                return false;
            }
            if ( !( Objects.equals( meltingKTDTO.getAlloy(), "*" ) || Objects.equals( meltingKTDTO.getAlloy(), alloy ) ) )
            {
                return false;
            }
            return true;
        } ).sorted( Comparator.comparingInt( MeltingKTDTO::getPrio ) ).findFirst();
    }

    public Optional<MeltingKTDTO> findMeltingKnowledge( final List<MeltingKTDTO> meltingKTList, final String caster, final String furnace, final String alloy )
    {
        return meltingKTList.stream().filter( meltingKTDTO -> {
            if ( !( Objects.equals( meltingKTDTO.getCaster(), "*" ) || Objects.equals( meltingKTDTO.getCaster(), caster ) ) )
            {
                return false;
            }
            if ( !( Objects.equals( meltingKTDTO.getFurnace(), "*" ) || Objects.equals( meltingKTDTO.getFurnace(), furnace ) ) )
            {
                return false;
            }
            if ( !( Objects.equals( meltingKTDTO.getAlloy(), "*" ) || Objects.equals( meltingKTDTO.getAlloy(), alloy ) ) )
            {
                return false;
            }
            return true;
        } ).sorted( Comparator.comparingInt( MeltingKTDTO::getPrio ) ).findFirst();
    }

    /**
     * Returns the planned duration for the passed furnace step based on the passed melting knowledge table entry.
     *
     * @param furnaceStep  furnace step for which the planned duration is requested
     * @param meltingKTDTO knowledge table entry used to determine the planned duration
     * @return The planned duration, or null if none was found or the knowledge table entry was null
     */
    public Duration getPlannedDuration( FurnaceStep furnaceStep, MeltingKTDTO meltingKTDTO )
    {
        if ( meltingKTDTO == null )
        {
            return null;
        }

        Duration duration;

        switch ( furnaceStep )
        {
        case Preparing:
            duration = Duration.ofMinutes( meltingKTDTO.getPreparingTM() );
            break;
        case Charging:
            duration = Duration.ofMinutes( meltingKTDTO.getChargingTM() );
            break;
        case Treating:
            duration = Duration.ofMinutes( meltingKTDTO.getTreatingTM() );
            break;
        case Skimming:
            duration = Duration.ofMinutes( meltingKTDTO.getSkimmingTM() );
            break;
        case Resting:
            duration = Duration.ofMinutes( meltingKTDTO.getRestingTM() );
            break;
        case ReadyToCast:
            duration = Duration.ofMinutes( meltingKTDTO.getReadyToCastTM() );
            break;
        case Casting:
            duration = Duration.ofMinutes( meltingKTDTO.getCastingTM() );
            break;
        default:
            duration = Duration.ofMinutes( 60 );
            break;
        }

        return duration;
    }
}