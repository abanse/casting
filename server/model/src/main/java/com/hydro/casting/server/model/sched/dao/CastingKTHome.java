package com.hydro.casting.server.model.sched.dao;

import com.hydro.casting.server.model.sched.CastingKT;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CastingKTHome
{
    private final static Logger log = LoggerFactory.getLogger( CastingKTHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( CastingKT transientInstance )
    {
        log.trace( "persisting CastingKT instance" );
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

    public void remove( CastingKT persistentInstance )
    {
        log.trace( "removing CastingKT instance" );
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

    public CastingKT merge( CastingKT detachedInstance )
    {
        log.trace( "merging CastingKT instance" );
        try
        {
            CastingKT result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public CastingKT findById( long objid )
    {
        log.trace( "getting CastingKT instance with name: " + objid );
        try
        {
            CastingKT instance = entityManager.find( CastingKT.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public CastingKT detach( CastingKT persistentInstance )
    {
        log.debug( "detach CastingKT instance" );
        final CastingKT instance = (CastingKT) Hibernate.unproxy( persistentInstance );
        try
        {
            entityManager.detach( instance );
            log.debug( "detach successful" );
        }
        catch ( RuntimeException re )
        {
            log.error( "detach failed", re );
            throw re;
        }
        instance.setObjid( 0 );
        return instance;
    }
}