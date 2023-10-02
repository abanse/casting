package com.hydro.casting.server.ejb.melting.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.MelterStep;
import com.hydro.casting.server.contract.dto.MeltingFurnaceKTDTO;
import com.hydro.casting.server.model.sched.MeltingBatch;
import com.hydro.casting.server.model.sched.MeltingFurnaceKT;
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
public class MeltingFurnaceKTService extends BaseService<MeltingFurnaceKT, MeltingFurnaceKTDTO>
{
    private final static Logger log = LoggerFactory.getLogger( MeltingFurnaceKTService.class );

    //@formatter:off
    private final static String QUERY =
            "   SELECT mfkt.objid as id, "
                    + "          mfkt.melter as melter, "
                    + "          mfkt.alloy as alloy, "
                    + "          mfkt.prio as prio, "
                    + "          mfkt.chargingTM as chargingTM, "
                    + "          mfkt.meltingTM as meltingTM, "
                    + "          mfkt.skimmingTM as skimmingTM, "
                    + "          mfkt.treatingTM as treatingTM, "
                    + "          mfkt.heatingTM as heatingTM, "
                    + "          mfkt.pouringTM as pouringTM, "
                    + "          mfkt.dredgingTM as dredgingTM "
                    + "     FROM MeltingFurnaceKT mfkt ";

    private final static String SELECT_WHERE =
            "    WHERE mfkt in (:knowledges)";
    //@formatter:on

    @Inject
    ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public MeltingFurnaceKTService()
    {
        super( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.PROD_VERSION_CACHE_NAME, Casting.CACHE.MELTING_FURNACE_KT_DATA_PATH );
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
    public List<MeltingFurnaceKTDTO> load()
    {
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<MeltingFurnaceKTDTO> query = entityManager.unwrap( Session.class ).createQuery( QUERY );
        query.setResultTransformer( new AliasToBeanResultTransformer( MeltingFurnaceKTDTO.class ) );

        return query.list();
    }

    @Override
    public List<MeltingFurnaceKTDTO> load( Collection<MeltingFurnaceKT> entities )
    {
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<MeltingFurnaceKTDTO> query = entityManager.unwrap( Session.class ).createQuery( QUERY + SELECT_WHERE );
        query.setParameter( "knowledges", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( MeltingFurnaceKTDTO.class ) );

        return query.list();
    }

    public Optional<MeltingFurnaceKTDTO> findMeltingFurnaceKnowledge( final List<MeltingFurnaceKTDTO> meltingFurnaceKTDTOList, final MeltingBatch meltingBatch )
    {
        return meltingFurnaceKTDTOList.stream().filter( meltingFurnaceKTDTO -> {
            if ( !( Objects.equals( meltingFurnaceKTDTO.getMelter(), "*" ) || Objects.equals( meltingFurnaceKTDTO.getMelter(), meltingBatch.getExecutingMachine().getApk() ) ) )
            {
                return false;
            }
            return Objects.equals( meltingFurnaceKTDTO.getAlloy(), "*" ) || Objects.equals( meltingFurnaceKTDTO.getAlloy(), meltingBatch.getAlloyName() );
        } ).min( Comparator.comparingInt( MeltingFurnaceKTDTO::getPrio ) );
    }

    /**
     * Returns the planned duration for the passed melting step based on the passed melting furnace knowledge table entry.
     *
     * @param melterStep          Melting step for which the planned duration is requested
     * @param meltingFurnaceKTDTO Knowledge table entry used to determine the planned duration
     * @return The planned duration, or null if none was found or the knowledge table entry was null
     */
    public Duration getPlannedDuration( MelterStep melterStep, MeltingFurnaceKTDTO meltingFurnaceKTDTO )
    {
        if ( meltingFurnaceKTDTO == null )
        {
            return null;
        }

        Duration duration;

        switch ( melterStep )
        {
        case Dredging:
            duration = Duration.ofMinutes( meltingFurnaceKTDTO.getDredgingTM() );
            break;
        case Pouring:
            duration = Duration.ofMinutes( meltingFurnaceKTDTO.getPouringTM() );
            break;
        case Heating:
            duration = Duration.ofMinutes( meltingFurnaceKTDTO.getHeatingTM() );
            break;
        case Treating:
            duration = Duration.ofMinutes( meltingFurnaceKTDTO.getTreatingTM() );
            break;
        case Skimming:
            duration = Duration.ofMinutes( meltingFurnaceKTDTO.getSkimmingTM() );
            break;
        case Melting:
            duration = Duration.ofMinutes( meltingFurnaceKTDTO.getMeltingTM() );
            break;
        case Charging:
            duration = Duration.ofMinutes( meltingFurnaceKTDTO.getChargingTM() );
            break;
        default:
            duration = Duration.ofMinutes( 60 );
            break;
        }

        return duration;
    }
}
