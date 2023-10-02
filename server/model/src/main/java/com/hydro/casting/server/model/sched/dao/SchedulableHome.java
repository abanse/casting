package com.hydro.casting.server.model.sched.dao;

import com.hydro.casting.server.model.res.Machine;
import com.hydro.casting.server.model.sched.Schedulable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class SchedulableHome
{
    private final static Logger log = LoggerFactory.getLogger( SchedulableHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void remove( Schedulable persistentInstance )
    {
        log.trace( "removing Schedulable instance" );
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

    public Schedulable findById( long objid )
    {
        log.trace( "getting Schedulable instance with name: " + objid );
        try
        {
            Schedulable instance = entityManager.find( Schedulable.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public long findLastExecutingSequenceIndex( Machine executingMachine )
    {
        final TypedQuery<Long> query = entityManager.createNamedQuery( "schedulable.lastExecutingSequenceIndex", Long.class );
        query.setParameter( "executingMachine", executingMachine );
        final Long result = query.getSingleResult();
        if ( result != null )
        {
            return result.longValue();
        }
        return 0;
    }

    public Schedulable findNextAvailableSchedulableForExecution( Machine executingMachine )
    {
        final TypedQuery<Schedulable> query = entityManager.createNamedQuery( "schedulable.nextAvailableBatch", Schedulable.class );
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

    public List<Schedulable> findEqualsOrGreaterExecutingSequenceIndex( Machine executingMachine, long executingSequenceIndex )
    {
        final TypedQuery<Schedulable> query = entityManager.createNamedQuery( "schedulable.equalsOrGreaterExecutingSequenceIndex", Schedulable.class );
        query.setParameter( "executingMachine", executingMachine );
        query.setParameter( "executingSequenceIndex", executingSequenceIndex );
        return query.getResultList();
    }
}