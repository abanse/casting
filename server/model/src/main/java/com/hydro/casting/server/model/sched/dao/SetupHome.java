package com.hydro.casting.server.model.sched.dao;

import com.hydro.casting.server.model.sched.Setup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class SetupHome
{
    private final static Logger log = LoggerFactory.getLogger( SetupHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( Setup transientInstance )
    {
        log.trace( "persisting Setup instance" );
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

    public void remove( Setup persistentInstance )
    {
        log.trace( "removing Setup instance" );
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

    public Setup merge( Setup detachedInstance )
    {
        log.trace( "merging Setup instance" );
        try
        {
            Setup result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public Setup findById( long objid )
    {
        log.trace( "getting Setup instance with name: " + objid );
        try
        {
            Setup instance = entityManager.find( Setup.class, objid );
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