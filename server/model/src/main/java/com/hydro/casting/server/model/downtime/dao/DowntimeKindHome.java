package com.hydro.casting.server.model.downtime.dao;

import com.hydro.casting.server.model.downtime.DowntimeKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;

@Stateless
public class DowntimeKindHome
{
    private final static Logger log = LoggerFactory.getLogger( DowntimeKindHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( DowntimeKind transientInstance )
    {
        log.trace( "persisting DowntimeKind instance" );
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

    public void remove( DowntimeKind persistentInstance )
    {
        log.trace( "removing DowntimeKind instance" );
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

    public DowntimeKind merge( DowntimeKind detachedInstance )
    {
        log.trace( "merging DowntimeKind instance" );
        try
        {
            DowntimeKind result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public DowntimeKind findById( long objid )
    {
        log.trace( "getting DowntimeKind instance with name: " + objid );
        try
        {
            DowntimeKind instance = entityManager.find( DowntimeKind.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public DowntimeKind findByApk( String costCenter, String kind1, String kind2, String kind3 )
    {
        final TypedQuery<DowntimeKind> query = entityManager.createNamedQuery( "downtimeKind.apk", DowntimeKind.class );
        query.setParameter( "costCenter", costCenter );
        query.setParameter( "kind1", kind1 );
        query.setParameter( "kind2", kind2 );
        query.setParameter( "kind3", kind3 );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nrex )
        {
            return null;
        }
    }

    public List<DowntimeKind> findByCostCenter( String costCenter )
    {
        final TypedQuery<DowntimeKind> query = entityManager.createNamedQuery( "downtimeKind.costCenter", DowntimeKind.class );
        query.setParameter( "costCenter", costCenter );
        return query.getResultList();
    }

    public List<DowntimeKind> findByCostCenters( String[] costCenters )
    {
        final TypedQuery<DowntimeKind> query = entityManager.createNamedQuery( "downtimeKind.costCenters", DowntimeKind.class );
        query.setParameter( "costCenters", Arrays.asList( costCenters ) );
        return query.getResultList();
    }
}