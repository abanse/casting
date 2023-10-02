package com.hydro.casting.server.model.sched.dao;

import com.hydro.casting.server.model.sched.CastingOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CastingOperationHome
{
    private final static Logger log = LoggerFactory.getLogger( CastingOperationHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( CastingOperation transientInstance )
    {
        log.trace( "persisting CastingOperation instance" );
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

    public void remove( CastingOperation persistentInstance )
    {
        log.trace( "removing CastingOperation instance" );
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

    public CastingOperation merge( CastingOperation detachedInstance )
    {
        log.trace( "merging CastingOperation instance" );
        try
        {
            CastingOperation result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public CastingOperation findById( long objid )
    {
        log.trace( "getting CastingOperation instance with name: " + objid );
        try
        {
            CastingOperation instance = entityManager.find( CastingOperation.class, objid );
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