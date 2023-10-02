package com.hydro.casting.server.model.sched.dao;

import com.hydro.casting.server.model.sched.MeltingKT;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MeltingKTHome
{
    private final static Logger log = LoggerFactory.getLogger( MeltingKTHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( MeltingKT transientInstance )
    {
        log.trace( "persisting MeltingKT instance" );
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

    public void remove( MeltingKT persistentInstance )
    {
        log.trace( "removing MeltingKT instance" );
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

    public MeltingKT merge( MeltingKT detachedInstance )
    {
        log.trace( "merging MeltingKT instance" );
        try
        {
            MeltingKT result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public MeltingKT findById( long objid )
    {
        log.trace( "getting MeltingKT instance with name: " + objid );
        try
        {
            MeltingKT instance = entityManager.find( MeltingKT.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public MeltingKT detach( MeltingKT persistentInstance )
    {
        log.debug( "detach MeltingKT instance" );
        final MeltingKT instance = (MeltingKT) Hibernate.unproxy( persistentInstance );
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