package com.hydro.casting.server.model.sched.dao;

import com.hydro.casting.server.model.sched.MeltingFurnaceKT;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MeltingFurnaceKTHome
{
    private final static Logger log = LoggerFactory.getLogger( MeltingFurnaceKTHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( MeltingFurnaceKT transientInstance )
    {
        log.trace( "persisting MeltingFurnaceKT instance" );
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

    public void remove( MeltingFurnaceKT persistentInstance )
    {
        log.trace( "removing MeltingFurnaceKT instance" );
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

    public MeltingFurnaceKT merge( MeltingFurnaceKT detachedInstance )
    {
        log.trace( "merging MeltingFurnaceKT instance" );
        try
        {
            MeltingFurnaceKT result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public MeltingFurnaceKT findById( long objid )
    {
        log.trace( "getting MeltingFurnaceKT instance with name: " + objid );
        try
        {
            MeltingFurnaceKT instance = entityManager.find( MeltingFurnaceKT.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public MeltingFurnaceKT detach( MeltingFurnaceKT persistentInstance )
    {
        log.debug( "detach MeltingFurnaceKT instance" );
        final MeltingFurnaceKT instance = (MeltingFurnaceKT) Hibernate.unproxy( persistentInstance );
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
