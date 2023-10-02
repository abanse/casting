package com.hydro.casting.server.model.inspection.dao;

import com.hydro.casting.server.model.inspection.TextIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TextIRHome
{
    private final static Logger log = LoggerFactory.getLogger( TextIRHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( TextIR transientInstance )
    {
        log.trace( "persisting TextIR instance" );
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

    public void remove( TextIR persistentInstance )
    {
        log.trace( "removing TextIR instance" );
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

    public TextIR merge( TextIR detachedInstance )
    {
        log.trace( "merging TextIR instance" );
        try
        {
            TextIR result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public TextIR findById( long objid )
    {
        log.trace( "getting TextIR instance with name: " + objid );
        try
        {
            TextIR instance = entityManager.find( TextIR.class, objid );
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