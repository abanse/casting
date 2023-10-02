package com.hydro.casting.server.model.inspection.dao;

import com.hydro.casting.server.model.inspection.InspectionCategory;
import com.hydro.casting.server.model.inspection.InspectionValue;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.Schedulable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class InspectionValueHome
{
    private final static Logger log = LoggerFactory.getLogger( InspectionValueHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( InspectionValue transientInstance )
    {
        log.trace( "persisting InspectionValue instance" );
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

    public void remove( InspectionValue persistentInstance )
    {
        log.trace( "removing InspectionValue instance" );
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

    public InspectionValue merge( InspectionValue detachedInstance )
    {
        log.trace( "merging InspectionValue instance" );
        try
        {
            InspectionValue result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public InspectionValue findById( long objid )
    {
        log.trace( "getting InspectionValue instance with name: " + objid );
        try
        {
            InspectionValue instance = entityManager.find( InspectionValue.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public List<InspectionValue> findBySchedulableAndCategory( Schedulable schedulable, InspectionCategory inspectionCategory )
    {
        final TypedQuery<InspectionValue> query = entityManager.createNamedQuery( "inspectionValue.findBySchedulableAndCategory", InspectionValue.class );
        query.setParameter( "schedulable", schedulable );
        query.setParameter( "inspectionCategory", inspectionCategory );
        return query.getResultList();
    }

    public Integer getInspectionResult( Schedulable schedulable, String inspectionCategoryApk )
    {
        final TypedQuery<Integer> query = entityManager.createNamedQuery( "inspectionValue.getInspectionResult", Integer.class );
        query.setParameter( "schedulable", schedulable );
        query.setParameter( "inspectionCategoryApk", inspectionCategoryApk );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException noResultException )
        {
            return null;
        }
    }

    public List<InspectionValue> findBySchedulable( CastingBatch schedulable )
    {
        final TypedQuery<InspectionValue> query = entityManager.createNamedQuery( "inspectionValue.findBySchedulable", InspectionValue.class );
        query.setParameter( "schedulable", schedulable );
        return query.getResultList();
    }
}