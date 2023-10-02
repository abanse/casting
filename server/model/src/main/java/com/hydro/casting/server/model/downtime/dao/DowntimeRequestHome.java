package com.hydro.casting.server.model.downtime.dao;

import com.hydro.casting.server.model.downtime.DowntimeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class DowntimeRequestHome
{
    private final static Logger log = LoggerFactory.getLogger( DowntimeRequestHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( DowntimeRequest transientInstance )
    {
        log.trace( "persisting DowntimeRequest instance" );
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

    public void remove( DowntimeRequest persistentInstance )
    {
        log.trace( "removing DowntimeRequest instance" );
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

    public DowntimeRequest merge( DowntimeRequest detachedInstance )
    {
        log.trace( "merging DowntimeRequest instance" );
        try
        {
            DowntimeRequest result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public DowntimeRequest findById( long objid )
    {
        log.trace( "getting DowntimeRequest instance with name: " + objid );
        try
        {
            DowntimeRequest instance = entityManager.find( DowntimeRequest.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public DowntimeRequest findUniqueOpenDowntimeRequest( String costCenter, String phase, String description )
    {
        log.trace( "getting downtime request with parameters costCenter=" + costCenter + ", phase=" + phase + ", description=" + description );
        final TypedQuery<DowntimeRequest> query = entityManager.createNamedQuery( "downtimeRequest.findOpenByContent", DowntimeRequest.class );
        query.setParameter( "costCenter", costCenter );
        query.setParameter( "phase", phase );
        query.setParameter( "description", description );
        query.setMaxResults( 1 );
        try
        {
            DowntimeRequest instance = query.getSingleResult();
            log.trace( "match found" );
            return instance;
        }
        catch ( NoResultException nrex )
        {
            log.trace( "no match found" );
            return null;
        }
    }

    public List<DowntimeRequest> findAllOpenForCostCenterWithoutEnd( String costCenter )
    {
        log.trace( "getting downtime requests for cost center " + costCenter + " with endTS" );
        final TypedQuery<DowntimeRequest> query = entityManager.createNamedQuery( "downtimeRequest.findAllOpenForCostCenterWithoutEnd", DowntimeRequest.class );
        query.setParameter( "costCenter", costCenter );
        try
        {
            List<DowntimeRequest> instances = query.getResultList();
            log.trace( "matches found" );
            return instances;
        }
        catch ( NoResultException nrex )
        {
            log.trace( "no matches found" );
            return null;
        }
    }

}