package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.Material;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class MaterialHome
{
    private final static Logger log = LoggerFactory.getLogger( MaterialHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( Material transientInstance )
    {
        log.trace( "persisting Material instance" );
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

    public void remove( Material persistentInstance )
    {
        log.trace( "removing Material instance" );
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

    public Material merge( Material detachedInstance )
    {
        log.trace( "merging Material instance" );
        try
        {
            Material result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public Material findById( long objid )
    {
        log.trace( "getting Material instance with name: " + objid );
        try
        {
            Material instance = entityManager.find( Material.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public List<Material> findByName( String name )
    {
        final TypedQuery<Material> query = entityManager.createNamedQuery( "material.name", Material.class );
        query.setParameter( "name", name );
        return query.getResultList();
    }

    public List<Material> findByConsumingOperation( long consumingOperationOID )
    {
        final TypedQuery<Material> query = entityManager.createNamedQuery( "material.findByConsumingOperation", Material.class );
        query.setParameter( "consumingOperationOID", consumingOperationOID );
        return query.getResultList();
    }
}