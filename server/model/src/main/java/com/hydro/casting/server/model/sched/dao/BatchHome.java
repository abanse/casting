package com.hydro.casting.server.model.sched.dao;

import com.hydro.casting.server.model.sched.Batch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class BatchHome
{
    private final static Logger log = LoggerFactory.getLogger( BatchHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( Batch transientInstance )
    {
        log.trace( "persisting Batch instance" );
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

    public void remove( Batch persistentInstance )
    {
        log.trace( "removing Batch instance" );
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

    public Batch merge( Batch detachedInstance )
    {
        log.trace( "merging Batch instance" );
        try
        {
            Batch result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public Batch findById( long objid )
    {
        log.trace( "getting Batch instance with name: " + objid );
        try
        {
            Batch instance = entityManager.find( Batch.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }
}