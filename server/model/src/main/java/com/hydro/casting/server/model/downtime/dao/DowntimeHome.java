package com.hydro.casting.server.model.downtime.dao;

import com.hydro.casting.server.model.downtime.Downtime;
import com.hydro.core.common.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class DowntimeHome
{
    private final static Logger log = LoggerFactory.getLogger( DowntimeHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( Downtime transientInstance )
    {
        log.trace( "persisting Downtime instance" );
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

    public void remove( Downtime persistentInstance )
    {
        log.trace( "removing Downtime instance" );
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

    public Downtime merge( Downtime detachedInstance )
    {
        log.trace( "merging Downtime instance" );
        try
        {
            Downtime result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public Downtime findById( long objid )
    {
        log.trace( "getting Downtime instance with name: " + objid );
        try
        {
            Downtime instance = entityManager.find( Downtime.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public Downtime findOpenDowntimeByCostCenter( String costCenter, String machine )
    {
        final TypedQuery<Downtime> query;
        if ( StringTools.isFilled( machine ) )
        {
            query = entityManager.createNamedQuery( "downtime.costCenter.machine", Downtime.class );
            query.setParameter( "machine", machine );
        }
        else
        {
            query = entityManager.createNamedQuery( "downtime.costCenter", Downtime.class );
        }
        query.setParameter( "costCenter", costCenter );
        List<Downtime> openDowntimes = query.getResultList();
        if ( openDowntimes.isEmpty() )
        {
            return null;
        }
        return openDowntimes.get( openDowntimes.size() - 1 );
    }

    public Downtime findClosedDowntimeByCostCenter( String costCenter, String machine, LocalDateTime after )
    {
        final TypedQuery<Downtime> query;
        if ( StringTools.isFilled( machine ) )
        {
            query = entityManager.createNamedQuery( "closed.downtime.costCenter.machine", Downtime.class );
            query.setParameter( "machine", machine );
        }
        else
        {
            query = entityManager.createNamedQuery( "closed.downtime.costCenter", Downtime.class );
        }
        query.setParameter( "costCenter", costCenter );
        query.setParameter( "after", after );
        List<Downtime> openDowntimes = query.getResultList();
        if ( openDowntimes.isEmpty() )
        {
            return null;
        }
        return openDowntimes.get( openDowntimes.size() - 1 );
    }
}