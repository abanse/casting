package com.hydro.casting.server.model.res.dao;

import com.hydro.casting.server.model.res.Caster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class CasterHome
{
    private final static Logger log = LoggerFactory.getLogger( CasterHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( Caster transientInstance )
    {
        log.trace( "persisting Caster instance" );
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

    public void remove( Caster persistentInstance )
    {
        log.trace( "removing Caster instance" );
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

    public Caster merge( Caster detachedInstance )
    {
        log.trace( "merging Caster instance" );
        try
        {
            Caster result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public Caster findById( long objid )
    {
        log.trace( "getting Caster instance with name: " + objid );
        try
        {
            Caster instance = entityManager.find( Caster.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public Caster findByApk( String apk )
    {
        final TypedQuery<Caster> query = entityManager.createNamedQuery( "caster.apk", Caster.class );
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