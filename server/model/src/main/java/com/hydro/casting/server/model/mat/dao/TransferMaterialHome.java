package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.TransferMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TransferMaterialHome
{
    private final static Logger log = LoggerFactory.getLogger( TransferMaterialHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( TransferMaterial transientInstance )
    {
        log.trace( "persisting TransferMaterial instance" );
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

    public void remove( TransferMaterial persistentInstance )
    {
        log.trace( "removing TransferMaterial instance" );
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

    public TransferMaterial merge( TransferMaterial detachedInstance )
    {
        log.trace( "merging TransferMaterial instance" );
        try
        {
            TransferMaterial result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public TransferMaterial findById( long objid )
    {
        log.trace( "getting TransferMaterial instance with name: " + objid );
        try
        {
            TransferMaterial instance = entityManager.find( TransferMaterial.class, objid );
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