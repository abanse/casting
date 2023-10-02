package com.hydro.casting.server.model.inspection.dao;

import com.hydro.casting.server.model.inspection.InspectionCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class InspectionCategoryHome
{
    private final static Logger log = LoggerFactory.getLogger( InspectionCategoryHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( InspectionCategory transientInstance )
    {
        log.trace( "persisting InspectionCategory instance" );
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

    public void remove( InspectionCategory persistentInstance )
    {
        log.trace( "removing InspectionCategory instance" );
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

    public InspectionCategory merge( InspectionCategory detachedInstance )
    {
        log.trace( "merging InspectionCategory instance" );
        try
        {
            InspectionCategory result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public InspectionCategory findById( long objid )
    {
        log.trace( "getting InspectionCategory instance with name: " + objid );
        try
        {
            InspectionCategory instance = entityManager.find( InspectionCategory.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public InspectionCategory findByApk( String apk )
    {
        final TypedQuery<InspectionCategory> query = entityManager.createNamedQuery( "inspectionCategory.apk", InspectionCategory.class );
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