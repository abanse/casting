package com.hydro.casting.server.model.po.dao;

import com.hydro.casting.server.model.po.WorkStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class WorkStepHome
{
    private final static Logger log = LoggerFactory.getLogger( WorkStepHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( WorkStep transientInstance )
    {
        log.trace( "persisting WorkStep instance" );
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

    public void remove( WorkStep persistentInstance )
    {
        log.trace( "removing WorkStep instance" );
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

    public WorkStep merge( WorkStep detachedInstance )
    {
        log.trace( "merging WorkStep instance" );
        try
        {
            WorkStep result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public WorkStep findById( long objid )
    {
        log.trace( "getting WorkStep instance with name: " + objid );
        try
        {
            WorkStep instance = entityManager.find( WorkStep.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public WorkStep findByApk( String apk )
    {
        final TypedQuery<WorkStep> query = entityManager.createNamedQuery( "workStep.apk", WorkStep.class );
        query.setParameter( "apk", apk );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nrex )
        {
            return null;
        }
    }
}