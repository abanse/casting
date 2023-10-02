package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.Slab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class SlabHome
{
    private final static Logger log = LoggerFactory.getLogger( SlabHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( Slab transientInstance )
    {
        log.trace( "persisting Slab instance" );
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

    public void remove( Slab persistentInstance )
    {
        log.trace( "removing Slab instance" );
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

    public Slab merge( Slab detachedInstance )
    {
        log.trace( "merging Slab instance" );
        try
        {
            Slab result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public Slab findById( long objid )
    {
        log.trace( "getting Slab instance with name: " + objid );
        try
        {
            Slab instance = entityManager.find( Slab.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public List<Slab> findActiveByName( String name )
    {
        final TypedQuery<Slab> query = entityManager.createNamedQuery( "slab.activeByName", Slab.class );
        query.setParameter( "name", name );
        return query.getResultList();
    }
}