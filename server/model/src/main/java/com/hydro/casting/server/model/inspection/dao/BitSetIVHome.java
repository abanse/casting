package com.hydro.casting.server.model.inspection.dao;

import com.hydro.casting.server.model.inspection.BitSetIV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class BitSetIVHome
{
    private final static Logger log = LoggerFactory.getLogger( BitSetIVHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( BitSetIV transientInstance )
    {
        log.trace( "persisting BitSetIV instance" );
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

    public void remove( BitSetIV persistentInstance )
    {
        log.trace( "removing BitSetIV instance" );
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

    public BitSetIV merge( BitSetIV detachedInstance )
    {
        log.trace( "merging BitSetIV instance" );
        try
        {
            BitSetIV result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public BitSetIV findById( long objid )
    {
        log.trace( "getting BitSetIV instance with name: " + objid );
        try
        {
            BitSetIV instance = entityManager.find( BitSetIV.class, objid );
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