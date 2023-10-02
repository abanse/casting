package com.hydro.casting.server.model.inspection.dao;

import com.hydro.casting.server.model.inspection.BitSetIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class BitSetIRHome
{
    private final static Logger log = LoggerFactory.getLogger( BitSetIRHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( BitSetIR transientInstance )
    {
        log.trace( "persisting BitSetIR instance" );
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

    public void remove( BitSetIR persistentInstance )
    {
        log.trace( "removing BitSetIR instance" );
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

    public BitSetIR merge( BitSetIR detachedInstance )
    {
        log.trace( "merging BitSetIR instance" );
        try
        {
            BitSetIR result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public BitSetIR findById( long objid )
    {
        log.trace( "getting BitSetIR instance with name: " + objid );
        try
        {
            BitSetIR instance = entityManager.find( BitSetIR.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

}