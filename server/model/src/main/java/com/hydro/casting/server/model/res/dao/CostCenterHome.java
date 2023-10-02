package com.hydro.casting.server.model.res.dao;

import com.hydro.casting.server.model.res.CostCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class CostCenterHome
{
    private final static Logger log = LoggerFactory.getLogger( CostCenterHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( CostCenter transientInstance )
    {
        log.trace( "persisting CostCenter instance" );
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

    public void remove( CostCenter persistentInstance )
    {
        log.trace( "removing CostCenter instance" );
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

    public CostCenter merge( CostCenter detachedInstance )
    {
        log.trace( "merging CostCenter instance" );
        try
        {
            CostCenter result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public CostCenter findById( long objid )
    {
        log.trace( "getting CostCenter instance with name: " + objid );
        try
        {
            CostCenter instance = entityManager.find( CostCenter.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public CostCenter findByApk( String apk )
    {
        final TypedQuery<CostCenter> query = entityManager.createNamedQuery( "costCenter.apk", CostCenter.class );
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