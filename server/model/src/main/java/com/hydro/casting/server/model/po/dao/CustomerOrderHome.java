package com.hydro.casting.server.model.po.dao;

import com.hydro.casting.server.model.po.CustomerOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class CustomerOrderHome
{
    private final static Logger log = LoggerFactory.getLogger( CustomerOrderHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( CustomerOrder transientInstance )
    {
        log.trace( "persisting CustomerOrder instance" );
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

    public void remove( CustomerOrder persistentInstance )
    {
        log.trace( "removing CustomerOrder instance" );
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

    public CustomerOrder merge( CustomerOrder detachedInstance )
    {
        log.trace( "merging CustomerOrder instance" );
        try
        {
            CustomerOrder result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public CustomerOrder findById( long objid )
    {
        log.trace( "getting CustomerOrder instance with name: " + objid );
        try
        {
            CustomerOrder instance = entityManager.find( CustomerOrder.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public CustomerOrder findByApk( String apk )
    {
        final TypedQuery<CustomerOrder> query = entityManager.createNamedQuery( "customerOrder.apk", CustomerOrder.class );
        query.setParameter( "apk", apk );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nrex )
        {
            return null;
        }
    }

}