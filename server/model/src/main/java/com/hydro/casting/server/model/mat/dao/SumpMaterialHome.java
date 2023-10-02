package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.SumpMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class SumpMaterialHome
{
    private final static Logger log = LoggerFactory.getLogger( SumpMaterialHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( SumpMaterial transientInstance )
    {
        log.trace( "persisting SumpMaterial instance" );
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

    public void remove( SumpMaterial persistentInstance )
    {
        log.trace( "removing SumpMaterial instance" );
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

    public SumpMaterial merge( SumpMaterial detachedInstance )
    {
        log.trace( "merging SumpMaterial instance" );
        try
        {
            SumpMaterial result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public SumpMaterial findById( long objid )
    {
        log.trace( "getting SumpMaterial instance with name: " + objid );
        try
        {
            SumpMaterial instance = entityManager.find( SumpMaterial.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

}