package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.AlloyElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AlloyElementHome
{
    private final static Logger log = LoggerFactory.getLogger( AlloyElementHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( AlloyElement transientInstance )
    {
        log.trace( "persisting AlloyElement instance" );
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

    public void remove( AlloyElement persistentInstance )
    {
        log.trace( "removing AlloyElement instance" );
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

    public AlloyElement merge( AlloyElement detachedInstance )
    {
        log.trace( "merging AlloyElement instance" );
        try
        {
            AlloyElement result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public AlloyElement findById( long objid )
    {
        log.trace( "getting AlloyElement instance with name: " + objid );
        try
        {
            AlloyElement instance = entityManager.find( AlloyElement.class, objid );
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