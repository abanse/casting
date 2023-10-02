package com.hydro.casting.server.model.res.dao;

import com.hydro.casting.server.model.res.MeltingFurnace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class MeltingFurnaceHome
{
    private final static Logger log = LoggerFactory.getLogger( MeltingFurnaceHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( MeltingFurnace transientInstance )
    {
        log.trace( "persisting MeltingFurnace instance" );
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

    public void remove( MeltingFurnace persistentInstance )
    {
        log.trace( "removing MeltingFurnace instance" );
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

    public MeltingFurnace merge( MeltingFurnace detachedInstance )
    {
        log.trace( "merging MeltingFurnace instance" );
        try
        {
            MeltingFurnace result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public MeltingFurnace findById( long objid )
    {
        log.trace( "getting MeltingFurnace instance with name: " + objid );
        try
        {
            MeltingFurnace instance = entityManager.find( MeltingFurnace.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public MeltingFurnace findByApk( String apk )
    {
        final TypedQuery<MeltingFurnace> query = entityManager.createNamedQuery( "meltingFurnace.apk", MeltingFurnace.class );
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