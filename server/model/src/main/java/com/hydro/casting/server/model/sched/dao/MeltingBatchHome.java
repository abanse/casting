package com.hydro.casting.server.model.sched.dao;

import com.hydro.casting.server.model.sched.MeltingBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Stateless
public class MeltingBatchHome
{
    private final static Logger log = LoggerFactory.getLogger( MeltingBatchHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( MeltingBatch transientInstance )
    {
        log.trace( "persisting MeltingBatch instance" );
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

    public void remove( MeltingBatch persistentInstance )
    {
        log.trace( "removing MeltingBatch instance" );
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

    public MeltingBatch merge( MeltingBatch detachedInstance )
    {
        log.trace( "merging MeltingBatch instance" );
        try
        {
            MeltingBatch result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public MeltingBatch findById( long objid )
    {
        log.trace( "getting MeltingBatch instance with name: " + objid );
        try
        {
            MeltingBatch instance = entityManager.find( MeltingBatch.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public MeltingBatch findActiveForMelter( String melterApk )
    {
        final TypedQuery<MeltingBatch> query = entityManager.createNamedQuery( "meltingBatch.findActiveForMelter", MeltingBatch.class );
        query.setParameter( "melterApk", melterApk );
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

    public List<MeltingBatch> findBatchesForPeriod( Collection<String> machines, LocalDateTime fromTS, LocalDateTime toTS )
    {
        final TypedQuery<MeltingBatch> query = entityManager.createNamedQuery( "meltingBatch.findForPeriod", MeltingBatch.class );
        query.setParameter( "machines", machines );
        query.setParameter( "fromTS", fromTS );
        query.setParameter( "toTS", toTS );
        try
        {
            return query.getResultList();
        }
        catch ( NoResultException nre )
        {
            return null;
        }
    }
}
