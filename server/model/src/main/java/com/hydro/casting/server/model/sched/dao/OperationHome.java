package com.hydro.casting.server.model.sched.dao;

import com.hydro.casting.server.model.sched.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class OperationHome
{
    private final static Logger log = LoggerFactory.getLogger( OperationHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( Operation transientInstance )
    {
        log.trace( "persisting Operation instance" );
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

    public void remove( Operation persistentInstance )
    {
        log.trace( "removing Operation instance" );
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

    public Operation merge( Operation detachedInstance )
    {
        log.trace( "merging Operation instance" );
        try
        {
            Operation result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public Operation findById( long objid )
    {
        log.trace( "getting Operation instance with name: " + objid );
        try
        {
            Operation instance = entityManager.find( Operation.class, objid );
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