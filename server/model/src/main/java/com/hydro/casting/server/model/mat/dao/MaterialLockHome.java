package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.MaterialLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MaterialLockHome
{
    private final static Logger log = LoggerFactory.getLogger( MaterialLockHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( MaterialLock transientInstance )
    {
        log.trace( "persisting MaterialLock instance" );
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

    public void remove( MaterialLock persistentInstance )
    {
        log.trace( "removing MaterialLock instance" );
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

    public MaterialLock merge( MaterialLock detachedInstance )
    {
        log.trace( "merging MaterialLock instance" );
        try
        {
            MaterialLock result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public MaterialLock findById( long objid )
    {
        log.trace( "getting MaterialLock instance with name: " + objid );
        try
        {
            MaterialLock instance = entityManager.find( MaterialLock.class, objid );
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