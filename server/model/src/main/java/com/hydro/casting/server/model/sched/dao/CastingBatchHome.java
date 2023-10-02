package com.hydro.casting.server.model.sched.dao;

import com.hydro.casting.server.model.res.Caster;
import com.hydro.casting.server.model.res.Machine;
import com.hydro.casting.server.model.sched.CastingBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class CastingBatchHome
{
    private final static Logger log = LoggerFactory.getLogger( CastingBatchHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( CastingBatch transientInstance )
    {
        log.trace( "persisting CastingBatch instance" );
        try
        {
            entityManager.persist( transientInstance );
            log.trace( "persist successful" );
        }
        catch ( RuntimeException re )
        {
            log.error( "persist failed", re );
            throw re;
        }
    }

    public void remove( CastingBatch persistentInstance )
    {
        log.trace( "removing CastingBatch instance" );
        try
        {
            entityManager.remove( persistentInstance );
            log.trace( "remove successful" );
        }
        catch ( RuntimeException re )
        {
            log.error( "remove failed", re );
            throw re;
        }
    }

    public CastingBatch merge( CastingBatch detachedInstance )
    {
        log.trace( "merging CastingBatch instance" );
        try
        {
            CastingBatch result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public CastingBatch findById( long objid )
    {
        log.trace( "getting CastingBatch instance with name: " + objid );
        try
        {
            CastingBatch instance = entityManager.find( CastingBatch.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public CastingBatch findPrev( Machine executingMachine, long executingSequenceIndex )
    {
        final TypedQuery<CastingBatch> query = entityManager.createNamedQuery( "castingBatch.findPrev", CastingBatch.class );
        query.setParameter( "executingMachine", executingMachine );
        query.setParameter( "executingSequenceIndex", executingSequenceIndex );
        query.setMaxResults( 1 );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nre )
        {
            return null;
        }
    }

    public long exist( String charge, int executionState )
    {
        final TypedQuery<Long> query = entityManager.createNamedQuery( "castingBatch.exist", Long.class );
        query.setParameter( "charge", "%" + charge );
        query.setParameter( "executionState", executionState );
        query.setParameter( "plannedTS", LocalDateTime.now().minusYears( 1 ) );
        try
        {
            final Long objid = query.getSingleResult();
            if ( objid == null )
            {
                return -1;
            }
            return objid.longValue();
        }
        catch ( NoResultException nre )
        {
            //ignore
        }
        return -1;
    }

    public boolean existCharge( String charge )
    {
        final TypedQuery<Long> query = entityManager.createNamedQuery( "castingBatch.existCharge", Long.class );
        query.setParameter( "charge", charge );
        try
        {
            final Long objid = query.getSingleResult();
            if ( objid == null )
            {
                return false;
            }
            return true;
        }
        catch ( NoResultException nre )
        {
            //ignore
        }
        return false;
    }

    public List<Long> findByMachineAndState( String executingResource, int executionState )
    {
        final TypedQuery<Long> query = entityManager.createNamedQuery( "castingBatch.findByMachineAndState", Long.class );
        query.setParameter( "executingResource", executingResource );
        query.setParameter( "executionState", executionState );
        return query.getResultList();
    }

    public CastingBatch findActiveForMeltingFurnace( String meltingFurnaceApk )
    {
        final TypedQuery<CastingBatch> query = entityManager.createNamedQuery( "castingBatch.findActiveForMeltingFurnace", CastingBatch.class );
        query.setParameter( "meltingFurnaceApk", meltingFurnaceApk );
        query.setMaxResults( 1 );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nre )
        {
            return null;
        }
    }

    public CastingBatch findInProgressForMeltingFurnace( String meltingFurnaceApk )
    {
        final TypedQuery<CastingBatch> query = entityManager.createNamedQuery( "castingBatch.findInProgressForMeltingFurnace", CastingBatch.class );
        query.setParameter( "meltingFurnaceApk", meltingFurnaceApk );
        query.setMaxResults( 1 );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nre )
        {
            return null;
        }
    }

    public CastingBatch findActiveForCaster( String casterApk )
    {
        final TypedQuery<CastingBatch> query = entityManager.createNamedQuery( "castingBatch.findActiveForCaster", CastingBatch.class );
        query.setParameter( "casterApk", casterApk );
        query.setMaxResults( 1 );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nre )
        {
            return null;
        }
    }

    public List<CastingBatch> findLastCastingBatches( String casterApk, LocalDateTime from )
    {
        final TypedQuery<CastingBatch> query = entityManager.createNamedQuery( "castingBatch.findLastCastingBatches", CastingBatch.class );
        query.setParameter( "casterApk", casterApk );
        query.setParameter( "from", from );
        return query.getResultList();
    }

   public List<CastingBatch> findLastCastingBatchesForFurnace( String furnaceApk, LocalDateTime from )
    {
        final TypedQuery<CastingBatch> query = entityManager.createNamedQuery( "castingBatch.findLastCastingBatchesForFurnace", CastingBatch.class );
        query.setParameter( "furnaceApk", furnaceApk );
        query.setParameter( "from", from );
        return query.getResultList();
    }

    public Long findMaxCastingSequence( Machine executingMachine )
    {
        final TypedQuery<Long> query = entityManager.createNamedQuery( "castingBatch.findMaxCastingSequence", Long.class );
        query.setParameter( "executingMachine", executingMachine );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nre )
        {
            return null;
        }
    }

    public List<CastingBatch> findActiveForMeltingFurnaces()
    {
        final TypedQuery<CastingBatch> query = entityManager.createNamedQuery( "castingBatch.findActiveForMeltingFurnaces", CastingBatch.class );
        return query.getResultList();
    }

    public List<CastingBatch> findNextInSchedule( String casterApk, long refExecutingSequenceIndex, int executionState )
    {
        final TypedQuery<CastingBatch> query = entityManager.createNamedQuery( "castingBatch.findNextInSchedule", CastingBatch.class );
        query.setParameter( "casterApk", casterApk );
        query.setParameter( "refExecutingSequenceIndex", refExecutingSequenceIndex );
        query.setParameter( "executionState", executionState );
        return query.getResultList();
    }

    public CastingBatch findByChargeWithoutYear( String charge )
    {
        final TypedQuery<CastingBatch> query = entityManager.createNamedQuery( "castingBatch.findByChargeWithoutYear", CastingBatch.class );
        query.setParameter( "charge", "__" + charge );
        query.setParameter( "timeHorizont", LocalDateTime.now().minusYears( 1 ) );
        query.setMaxResults( 1 );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nre )
        {
            return null;
        }
    }

    public CastingBatch findByProductionOrder( String productionOrderApk )
    {
        final TypedQuery<CastingBatch> query = entityManager.createNamedQuery( "castingBatch.findByProductionOrder", CastingBatch.class );
        query.setParameter( "productionOrderApk", productionOrderApk );
        query.setMaxResults( 1 );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nre )
        {
            return null;
        }
    }

    public CastingBatch findByCharge( String charge )
    {
        final TypedQuery<CastingBatch> query = entityManager.createNamedQuery( "castingBatch.findByCharge", CastingBatch.class );
        query.setParameter( "charge", charge );
        query.setMaxResults( 1 );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nre )
        {
            return null;
        }
    }

    public Long findFirstCastingSequence( Machine machine, int executionState )
    {
        final TypedQuery<Long> query = entityManager.createNamedQuery( "castingBatch.findFirstCastingSequence", Long.class );
        query.setParameter( "machine", machine );
        query.setParameter( "executionState", executionState );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nre )
        {
            return null;
        }
    }
}