package com.hydro.casting.server.model.po.dao;

import com.hydro.casting.server.model.po.CustomerOrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class CustomerOrderItemHome
{
    private final static Logger log = LoggerFactory.getLogger( CustomerOrderItemHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( CustomerOrderItem transientInstance )
    {
        log.trace( "persisting CustomerOrderItem instance" );
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

    public void remove( CustomerOrderItem persistentInstance )
    {
        log.trace( "removing CustomerOrderItem instance" );
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

    public CustomerOrderItem merge( CustomerOrderItem detachedInstance )
    {
        log.trace( "merging CustomerOrderItem instance" );
        try
        {
            CustomerOrderItem result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public CustomerOrderItem findById( long objid )
    {
        log.trace( "getting CustomerOrderItem instance with name: " + objid );
        try
        {
            CustomerOrderItem instance = entityManager.find( CustomerOrderItem.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public CustomerOrderItem findByApk( String apk )
    {
        final TypedQuery<CustomerOrderItem> query = entityManager.createNamedQuery( "customerOrderItem.apk", CustomerOrderItem.class );
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