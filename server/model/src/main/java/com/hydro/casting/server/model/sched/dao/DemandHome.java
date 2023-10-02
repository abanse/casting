package com.hydro.casting.server.model.sched.dao;

import com.hydro.casting.server.model.mat.MaterialType;
import com.hydro.casting.server.model.sched.Demand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

@Stateless
public class DemandHome
{
    private final static Logger log = LoggerFactory.getLogger( DemandHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( Demand transientInstance )
    {
        log.trace( "persisting Demand instance" );
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

    public void remove( Demand persistentInstance )
    {
        log.trace( "removing Demand instance" );
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

    public Demand merge( Demand detachedInstance )
    {
        log.trace( "merging Demand instance" );
        try
        {
            Demand result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public Demand findById( long objid )
    {
        log.trace( "getting Demand instance with name: " + objid );
        try
        {
            Demand instance = entityManager.find( Demand.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public Demand findByPLANValues( MaterialType intermediateType, int position, LocalDateTime plannedSuccessTs )
    {
        final TypedQuery<Demand> query = entityManager.createNamedQuery( "demand.findByPLANValues", Demand.class );
        query.setParameter( "intermediateType", intermediateType );
        query.setParameter( "position", position );
        query.setParameter( "plannedSuccessTs", plannedSuccessTs );
        query.setMaxResults( 1 );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nre )
        {
            return null;
        }
    }
}