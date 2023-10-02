package com.hydro.casting.server.model.av.dao;

import com.hydro.casting.server.model.av.ActualValuesHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Stateless
public class ActualValuesHeaderHome
{
    private final static Logger log = LoggerFactory.getLogger( ActualValuesHeaderHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( ActualValuesHeader transientInstance )
    {
        log.trace( "persisting ActualValuesHeader instance" );
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

    public void remove( ActualValuesHeader persistentInstance )
    {
        log.trace( "removing ActualValuesHeader instance" );
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

    public ActualValuesHeader merge( ActualValuesHeader detachedInstance )
    {
        log.trace( "merging ActualValuesHeader instance" );
        try
        {
            ActualValuesHeader result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public ActualValuesHeader findById( long objid )
    {
        log.trace( "getting ActualValuesHeader instance with name: " + objid );
        try
        {
            ActualValuesHeader instance = entityManager.find( ActualValuesHeader.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public ActualValuesHeader findByMeasurement( String application, String source, String type, String refName )
    {
        final TypedQuery<ActualValuesHeader> query = entityManager.createNamedQuery( "actualValuesHeader.measurement", ActualValuesHeader.class );
        query.setParameter( "application", application );
        query.setParameter( "source", source );
        query.setParameter( "type", type );
        query.setParameter( "refName", refName );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nrex )
        {
            return null;
        }
    }

    public List<ActualValuesHeader> findByMachinesAndTimeRange( String application, Collection<String> machines, LocalDateTime fromTS, LocalDateTime toTS )
    {
        final TypedQuery<ActualValuesHeader> query = entityManager.createNamedQuery( "actualValuesHeader.machinesAndTimeRange", ActualValuesHeader.class );
        query.setParameter( "application", application );
        query.setParameter( "machines", machines );
        query.setParameter( "fromTS", fromTS );
        query.setParameter( "toTS", toTS );
        return query.getResultList();
    }

    public ActualValuesHeader findLastHeaderBefore( String application, String machine, ActualValuesHeader currentHeader )
    {
        final TypedQuery<ActualValuesHeader> query = entityManager.createNamedQuery( "actualValuesHeader.lastHeaderBefore", ActualValuesHeader.class );
        query.setParameter( "application", application );
        query.setParameter( "machine", machine );
        query.setParameter( "currentHeader", currentHeader );
        query.setMaxResults( 1 );
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