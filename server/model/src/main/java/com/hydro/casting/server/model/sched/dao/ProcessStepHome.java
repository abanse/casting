package com.hydro.casting.server.model.sched.dao;

import com.hydro.casting.server.model.sched.ProcessStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ProcessStepHome
{
    private final static Logger log = LoggerFactory.getLogger( ProcessStepHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( ProcessStep transientInstance )
    {
        log.trace( "persisting ProcessStep instance" );
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

    public void remove( ProcessStep persistentInstance )
    {
        log.trace( "removing ProcessStep instance" );
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

    public ProcessStep merge( ProcessStep detachedInstance )
    {
        log.trace( "merging ProcessStep instance" );
        try
        {
            ProcessStep result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public ProcessStep findById( long objid )
    {
        log.trace( "getting ProcessStep instance with name: " + objid );
        try
        {
            ProcessStep instance = entityManager.find( ProcessStep.class, objid );
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
