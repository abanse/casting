package com.hydro.casting.server.model.wms.dao;

import com.hydro.casting.server.model.wms.HandlingUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class HandlingUnitHome
{
    private final static Logger log = LoggerFactory.getLogger( HandlingUnitHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( HandlingUnit transientInstance )
    {
        log.trace( "persisting HandlingUnit instance" );
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

    public void remove( HandlingUnit persistentInstance )
    {
        log.trace( "removing HandlingUnit instance" );
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

    public HandlingUnit merge( HandlingUnit detachedInstance )
    {
        log.trace( "merging HandlingUnit instance" );
        try
        {
            HandlingUnit result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public HandlingUnit findById( long objid )
    {
        log.trace( "getting HandlingUnit instance with name: " + objid );
        try
        {
            HandlingUnit instance = entityManager.find( HandlingUnit.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public HandlingUnit findByName( String name )
    {
        final TypedQuery<HandlingUnit> query = entityManager.createNamedQuery( "handlingUnit.name", HandlingUnit.class );
        query.setParameter( "name", name );
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