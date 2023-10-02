package com.hydro.casting.server.model.av.dao;

import com.hydro.casting.server.model.av.ActualValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

@Stateless
public class ActualValuesHome
{
    private final static Logger log = LoggerFactory.getLogger( ActualValuesHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( ActualValues transientInstance )
    {
        log.trace( "persisting ActualValues instance" );
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

    public void remove( ActualValues persistentInstance )
    {
        log.trace( "removing ActualValues instance" );
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

    public ActualValues merge( ActualValues detachedInstance )
    {
        log.trace( "merging ActualValues instance" );
        try
        {
            ActualValues result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public void lock( ActualValues actualValues )
    {
        log.trace( "lock ActualValues instance" );
        try
        {
            entityManager.lock( actualValues, LockModeType.PESSIMISTIC_WRITE );
        }
        catch ( RuntimeException re )
        {
            log.error( "lock failed", re );
            throw re;
        }
    }

    public ActualValues findById( long objid )
    {
        log.trace( "getting ActualValues instance with name: " + objid );
        try
        {
            ActualValues instance = entityManager.find( ActualValues.class, objid );
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