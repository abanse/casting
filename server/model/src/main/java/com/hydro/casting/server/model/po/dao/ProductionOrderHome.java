package com.hydro.casting.server.model.po.dao;

import com.hydro.casting.server.model.po.ProductionOrder;
import com.hydro.casting.server.model.sched.Batch;
import com.hydro.casting.server.model.sched.CastingBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class ProductionOrderHome
{
    private final static Logger log = LoggerFactory.getLogger( ProductionOrderHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( ProductionOrder transientInstance )
    {
        log.trace( "persisting ProductionOrder instance" );
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

    public void remove( ProductionOrder persistentInstance )
    {
        log.trace( "removing ProductionOrder instance" );
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

    public ProductionOrder merge( ProductionOrder detachedInstance )
    {
        log.trace( "merging ProductionOrder instance" );
        try
        {
            ProductionOrder result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public ProductionOrder findById( long objid )
    {
        log.trace( "getting ProductionOrder instance with name: " + objid );
        try
        {
            ProductionOrder instance = entityManager.find( ProductionOrder.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public ProductionOrder findByApk( String apk )
    {
        final TypedQuery<ProductionOrder> query = entityManager.createNamedQuery( "productionOrder.apk", ProductionOrder.class );
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

    public List<ProductionOrder> findByBatch( Batch batch )
    {
        final TypedQuery<ProductionOrder> query = entityManager.createNamedQuery( "productionOrder.findByBatch", ProductionOrder.class );
        query.setParameter( "batch", batch );
        return query.getResultList();
    }
}