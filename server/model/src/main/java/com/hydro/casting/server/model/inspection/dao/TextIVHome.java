package com.hydro.casting.server.model.inspection.dao;

import com.hydro.casting.server.model.inspection.TextIV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TextIVHome
{
    private final static Logger log = LoggerFactory.getLogger( TextIVHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( TextIV transientInstance )
    {
        log.trace( "persisting TextIV instance" );
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

    public void remove( TextIV persistentInstance )
    {
        log.trace( "removing TextIV instance" );
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

    public TextIV merge( TextIV detachedInstance )
    {
        log.trace( "merging TextIV instance" );
        try
        {
            TextIV result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public TextIV findById( long objid )
    {
        log.trace( "getting TextIV instance with name: " + objid );
        try
        {
            TextIV instance = entityManager.find( TextIV.class, objid );
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