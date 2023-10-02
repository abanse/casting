package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.StockMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class StockMaterialHome
{
    private final static Logger log = LoggerFactory.getLogger( StockMaterialHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( StockMaterial transientInstance )
    {
        log.trace( "persisting StockMaterial instance" );
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

    public void remove( StockMaterial persistentInstance )
    {
        log.trace( "removing StockMaterial instance" );
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

    public StockMaterial merge( StockMaterial detachedInstance )
    {
        log.trace( "merging StockMaterial instance" );
        try
        {
            StockMaterial result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public StockMaterial findById( long objid )
    {
        log.trace( "getting StockMaterial instance with name: " + objid );
        try
        {
            StockMaterial instance = entityManager.find( StockMaterial.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public List<StockMaterial> findByName( String name )
    {
        final TypedQuery<StockMaterial> query = entityManager.createNamedQuery( "stockMaterial.name", StockMaterial.class );
        query.setParameter( "name", name );
        return query.getResultList();
    }

    public LocalDateTime findNewestReplicationTS()
    {
        final TypedQuery<LocalDateTime> query = entityManager.createNamedQuery( "stockMaterial.findNewestReplicationTS", LocalDateTime.class );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nrex )
        {
            return null;
        }
    }

    public List<StockMaterial> findConsumedStockMaterials( LocalDateTime timeHorizont )
    {
        final TypedQuery<StockMaterial> query = entityManager.createNamedQuery( "stockMaterial.findConsumedStockMaterials", StockMaterial.class );
        query.setParameter( "timeHorizont", timeHorizont );
        return query.getResultList();
    }
}