package com.hydro.casting.server.model.log.dao;

import com.hydro.casting.server.model.log.ProductionLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

@Stateless
public class ProductionLogHome
{
    private final static Logger log = LoggerFactory.getLogger( ProductionLogHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( ProductionLog transientInstance )
    {
        log.trace( "persisting ProductionLog instance" );
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

    public void remove( ProductionLog persistentInstance )
    {
        log.trace( "removing ProductionLog instance" );
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

    public ProductionLog merge( ProductionLog detachedInstance )
    {
        log.trace( "merging ProductionLog instance" );
        try
        {
            ProductionLog result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public ProductionLog findById( long objid )
    {
        log.trace( "getting ProductionLog instance with name: " + objid );
        try
        {
            ProductionLog instance = entityManager.find( ProductionLog.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public ProductionLog createEntry( String machineApk, String refName, String userName, String message )
    {
        final ProductionLog productionLog = new ProductionLog();
        productionLog.setEventTS( LocalDateTime.now() );
        productionLog.setMachineApk( machineApk );
        productionLog.setRefName( refName );
        productionLog.setUserName( userName );
        productionLog.setMessage( message );

        persist( productionLog );
        return productionLog;
    }
}