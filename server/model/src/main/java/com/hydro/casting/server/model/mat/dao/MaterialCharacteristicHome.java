package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.MaterialCharacteristic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MaterialCharacteristicHome
{
    private final static Logger log = LoggerFactory.getLogger( MaterialCharacteristicHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( MaterialCharacteristic transientInstance )
    {
        log.trace( "persisting MaterialCharacteristic instance" );
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

    public void remove( MaterialCharacteristic persistentInstance )
    {
        log.trace( "removing MaterialCharacteristic instance" );
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

    public MaterialCharacteristic merge( MaterialCharacteristic detachedInstance )
    {
        log.trace( "merging MaterialCharacteristic instance" );
        try
        {
            MaterialCharacteristic result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public MaterialCharacteristic findById( long objid )
    {
        log.trace( "getting MaterialCharacteristic instance with name: " + objid );
        try
        {
            MaterialCharacteristic instance = entityManager.find( MaterialCharacteristic.class, objid );
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