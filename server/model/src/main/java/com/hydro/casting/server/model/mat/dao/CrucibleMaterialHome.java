package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.CrucibleMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class CrucibleMaterialHome
{
    private final static Logger log = LoggerFactory.getLogger( CrucibleMaterialHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( CrucibleMaterial transientInstance )
    {
        log.trace( "persisting CrucibleMaterial instance" );
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

    public void remove( CrucibleMaterial persistentInstance )
    {
        log.trace( "removing CrucibleMaterial instance" );
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

    public CrucibleMaterial merge( CrucibleMaterial detachedInstance )
    {
        log.trace( "merging CrucibleMaterial instance" );
        try
        {
            CrucibleMaterial result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public CrucibleMaterial findById( long objid )
    {
        log.trace( "getting CrucibleMaterial instance with name: " + objid );
        try
        {
            CrucibleMaterial instance = entityManager.find( CrucibleMaterial.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public CrucibleMaterial findByWeightSystemAndReference( String weightSystem, long weightReference )
    {
        final TypedQuery<CrucibleMaterial> query = entityManager.createNamedQuery( "crucibleMaterial.weightSystemAndReference", CrucibleMaterial.class );
        query.setParameter( "weightSystem", weightSystem );
        query.setParameter( "weightReference", weightReference );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException noResultException )
        {
            return null;
        }
    }

    public List<CrucibleMaterial> findActive()
    {
        final TypedQuery<CrucibleMaterial> query = entityManager.createNamedQuery( "crucibleMaterial.findActive", CrucibleMaterial.class );
        return query.getResultList();
    }

    public CrucibleMaterial findByCrucibleOrder( String crucibleOrder)
    {
        final TypedQuery<CrucibleMaterial> query = entityManager.createNamedQuery( "crucibleMaterial.findByCrucibleOrder", CrucibleMaterial.class );
        query.setParameter( "crucibleOrder", crucibleOrder );
        query.setMaxResults( 1 );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException noResultException )
        {
            return null;
        }
    }
}