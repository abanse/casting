package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.AnalysisElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AnalysisElementHome
{
    private final static Logger log = LoggerFactory.getLogger( AnalysisElementHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( AnalysisElement transientInstance )
    {
        log.trace( "persisting AnalysisElement instance" );
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

    public void remove( AnalysisElement persistentInstance )
    {
        log.trace( "removing AnalysisElement instance" );
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

    public AnalysisElement merge( AnalysisElement detachedInstance )
    {
        log.trace( "merging AnalysisElement instance" );
        try
        {
            AnalysisElement result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public AnalysisElement findById( long objid )
    {
        log.trace( "getting AnalysisElement instance with name: " + objid );
        try
        {
            AnalysisElement instance = entityManager.find( AnalysisElement.class, objid );
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