package com.hydro.casting.server.model.downtime.dao;

import com.hydro.casting.server.model.downtime.DowntimeModule;
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
public class DowntimeModuleHome
{
    private final static Logger log = LoggerFactory.getLogger( DowntimeModuleHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( DowntimeModule transientInstance )
    {
        log.trace( "persisting DowntimeModule instance" );
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

    public void remove( DowntimeModule persistentInstance )
    {
        log.trace( "removing DowntimeModule instance" );
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

    public DowntimeModule merge( DowntimeModule detachedInstance )
    {
        log.trace( "merging DowntimeModule instance" );
        try
        {
            DowntimeModule result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public DowntimeModule findById( long objid )
    {
        log.trace( "getting DowntimeModule instance with name: " + objid );
        try
        {
            DowntimeModule instance = entityManager.find( DowntimeModule.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public DowntimeModule findByApk( String costCenter, String module, String component )
    {
        final TypedQuery<DowntimeModule> query = entityManager.createNamedQuery( "downtimeModule.apk", DowntimeModule.class );
        query.setParameter( "costCenter", costCenter );
        query.setParameter( "module", module );
        query.setParameter( "component", component );
        try
        {
            return query.getSingleResult();
        }
        catch ( NoResultException nrex )
        {
            return null;
        }
    }

    public List<DowntimeModule> findByCostCenter( String costCenter )
    {
        final TypedQuery<DowntimeModule> query = entityManager.createNamedQuery( "downtimeModule.costCenter", DowntimeModule.class );
        query.setParameter( "costCenter", costCenter );
        return query.getResultList();
    }

    public List<DowntimeModule> findByCostCenters( String[] costCenters )
    {
        final TypedQuery<DowntimeModule> query = entityManager.createNamedQuery( "downtimeModule.costCenters", DowntimeModule.class );
        query.setParameter( "costCenters", Arrays.asList( costCenters ) );
        return query.getResultList();
    }
}