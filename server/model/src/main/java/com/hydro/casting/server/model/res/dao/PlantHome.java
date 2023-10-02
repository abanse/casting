package com.hydro.casting.server.model.res.dao;

import com.hydro.casting.server.model.res.Plant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class PlantHome
{
    private final static Logger log = LoggerFactory.getLogger( PlantHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( Plant transientInstance )
    {
        log.trace( "persisting Plant instance" );
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

    public void remove( Plant persistentInstance )
    {
        log.trace( "removing Plant instance" );
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

    public Plant merge( Plant detachedInstance )
    {
        log.trace( "merging Plant instance" );
        try
        {
            Plant result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public Plant findById( long objid )
    {
        log.trace( "getting Plant instance with name: " + objid );
        try
        {
            Plant instance = entityManager.find( Plant.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public Plant findByApk( String apk )
    {
        final TypedQuery<Plant> query = entityManager.createNamedQuery( "plant.apk", Plant.class );
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