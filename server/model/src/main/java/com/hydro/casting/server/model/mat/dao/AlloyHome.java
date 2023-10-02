package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.Alloy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class AlloyHome
{
    private final static Logger log = LoggerFactory.getLogger( AlloyHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( Alloy transientInstance )
    {
        log.trace( "persisting Alloy instance" );
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

    public void remove( Alloy persistentInstance )
    {
        log.trace( "removing Alloy instance" );
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

    public Alloy merge( Alloy detachedInstance )
    {
        log.trace( "merging Alloy instance" );
        try
        {
            Alloy result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public Alloy findById( long objid )
    {
        log.trace( "getting Alloy instance with name: " + objid );
        try
        {
            Alloy instance = entityManager.find( Alloy.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public Alloy findByAlloyId( Long alloyId )
    {
        log.trace( "getting alloy instance with alloy id: " + alloyId );
        final TypedQuery<Alloy> query = entityManager.createNamedQuery( "alloy.byAlloyId", Alloy.class );
        query.setParameter( "alloyId", alloyId );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nrex )
        {
            return null;
        }
    }

    public Alloy findActiveByName( String name )
    {
        log.trace( "getting alloy instance with alloy name: " + name );
        final TypedQuery<Alloy> query = entityManager.createNamedQuery( "alloy.activeByName", Alloy.class );
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

    public Alloy findAlloyByNameAndVersion( String name, int version )
    {
        log.trace( "getting alloy instance with alloy name: " + name + " and version: " + version );
        final TypedQuery<Alloy> query = entityManager.createNamedQuery( "alloy.byNameAndVersion", Alloy.class );
        query.setParameter( "name", name );
        query.setParameter( "version", version );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nrex )
        {
            return null;
        }
    }

    public List<Alloy> findAll()
    {
        log.trace( "getting all alloys" );
        final TypedQuery<Alloy> query = entityManager.createQuery( "select a from Alloy a", Alloy.class );
        try
        {
            return query.getResultList();
        }
        catch ( NoResultException nrex )
        {
            return null;
        }
    }
}