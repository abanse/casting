package com.hydro.casting.server.model.shift.dao;

import com.hydro.casting.server.model.shift.ShiftAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class ShiftAssignmentHome
{
    private final static Logger log = LoggerFactory.getLogger( ShiftAssignmentHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( ShiftAssignment transientInstance )
    {
        log.trace( "persisting ShiftAssignment instance" );
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

    public void remove( ShiftAssignment persistentInstance )
    {
        log.trace( "removing ShiftAssignment instance" );
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

    public ShiftAssignment merge( ShiftAssignment detachedInstance )
    {
        log.trace( "merging ShiftAssignment instance" );
        try
        {
            ShiftAssignment result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public ShiftAssignment findById( long objid )
    {
        log.trace( "getting ShiftAssignment instance with name: " + objid );
        try
        {
            ShiftAssignment instance = entityManager.find( ShiftAssignment.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public ShiftAssignment findByCostCenter( String costCenter )
    {
        final TypedQuery<ShiftAssignment> query = entityManager.createNamedQuery( "shiftAssignment.costCenter", ShiftAssignment.class );
        query.setParameter( "costCenter", costCenter );
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