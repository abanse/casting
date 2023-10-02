package com.hydro.casting.server.model.inspection.dao;

import com.hydro.casting.server.model.inspection.CasterPositionsIV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CasterPositionsIVHome
{
    private final static Logger log = LoggerFactory.getLogger( CasterPositionsIVHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( CasterPositionsIV transientInstance )
    {
        log.trace( "persisting CasterPositionsIV instance" );
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

    public void remove( CasterPositionsIV persistentInstance )
    {
        log.trace( "removing CasterPositionsIV instance" );
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

    public CasterPositionsIV merge( CasterPositionsIV detachedInstance )
    {
        log.trace( "merging CasterPositionsIV instance" );
        try
        {
            CasterPositionsIV result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public CasterPositionsIV findById( long objid )
    {
        log.trace( "getting CasterPositionsIV instance with name: " + objid );
        try
        {
            CasterPositionsIV instance = entityManager.find( CasterPositionsIV.class, objid );
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