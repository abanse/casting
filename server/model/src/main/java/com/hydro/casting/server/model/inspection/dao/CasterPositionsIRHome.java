package com.hydro.casting.server.model.inspection.dao;

import com.hydro.casting.server.model.inspection.CasterPositionsIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CasterPositionsIRHome
{
    private final static Logger log = LoggerFactory.getLogger( CasterPositionsIRHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( CasterPositionsIR transientInstance )
    {
        log.trace( "persisting CasterPositionsIR instance" );
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

    public void remove( CasterPositionsIR persistentInstance )
    {
        log.trace( "removing CasterPositionsIR instance" );
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

    public CasterPositionsIR merge( CasterPositionsIR detachedInstance )
    {
        log.trace( "merging CasterPositionsIR instance" );
        try
        {
            CasterPositionsIR result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public CasterPositionsIR findById( long objid )
    {
        log.trace( "getting CasterPositionsIR instance with name: " + objid );
        try
        {
            CasterPositionsIR instance = entityManager.find( CasterPositionsIR.class, objid );
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