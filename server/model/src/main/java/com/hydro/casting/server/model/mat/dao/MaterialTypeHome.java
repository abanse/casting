package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.MaterialType;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class MaterialTypeHome
{
    private final static Logger log = LoggerFactory.getLogger( MaterialTypeHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( MaterialType transientInstance )
    {
        log.trace( "persisting MaterialType instance" );
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

    public void remove( MaterialType persistentInstance )
    {
        log.trace( "removing MaterialType instance" );
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

    public MaterialType merge( MaterialType detachedInstance )
    {
        log.trace( "merging MaterialType instance" );
        try
        {
            MaterialType result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public MaterialType detach( MaterialType persistentInstance )
    {
        log.debug( "detach MaterialType instance" );
        final MaterialType instance = (MaterialType) Hibernate.unproxy( persistentInstance );
        try
        {
            entityManager.detach( instance );
            log.debug( "detach successful" );
        }
        catch ( RuntimeException re )
        {
            log.error( "detach failed", re );
            throw re;
        }
        instance.setObjid( 0 );
        return instance;
    }

    public MaterialType findById( long objid )
    {
        log.trace( "getting MaterialType instance with name: " + objid );
        try
        {
            MaterialType instance = entityManager.find( MaterialType.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public MaterialType findByApk( String apk )
    {
        final TypedQuery<MaterialType> query = entityManager.createNamedQuery( "materialType.apk", MaterialType.class );
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

    public List<MaterialType> findByCategoryAttributes( String category, String quality, String qualityCode, double height, double width, double length, String alloy )
    {
        final TypedQuery<MaterialType> query = entityManager.createNamedQuery( "materialType.categoryAttributes", MaterialType.class );
        query.setParameter( "category", category );
        query.setParameter( "quality", quality );
        query.setParameter( "qualityCode", qualityCode );
        query.setParameter( "height", height );
        query.setParameter( "width", width );
        query.setParameter( "length", length );
        query.setParameter( "alloy", alloy );
        return query.getResultList();
    }
}