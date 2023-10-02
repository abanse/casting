package com.hydro.casting.server.model.wms.dao;

import com.hydro.casting.server.model.wms.Place;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class PlaceHome
{
    private final static Logger log = LoggerFactory.getLogger( PlaceHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( Place transientInstance )
    {
        log.trace( "persisting Place instance" );
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

    public void remove( Place persistentInstance )
    {
        log.trace( "removing Place instance" );
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

    public Place merge( Place detachedInstance )
    {
        log.trace( "merging Place instance" );
        try
        {
            Place result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public Place findById( long objid )
    {
        log.trace( "getting Place instance with name: " + objid );
        try
        {
            Place instance = entityManager.find( Place.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public Place findByName( String name )
    {
        final TypedQuery<Place> query = entityManager.createNamedQuery( "place.name", Place.class );
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