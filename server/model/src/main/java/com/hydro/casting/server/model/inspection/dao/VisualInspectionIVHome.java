package com.hydro.casting.server.model.inspection.dao;

import com.hydro.casting.server.model.inspection.VisualInspectionIV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class VisualInspectionIVHome
{
    private final static Logger log = LoggerFactory.getLogger( VisualInspectionIVHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( VisualInspectionIV transientInstance )
    {
        log.trace( "persisting VisualInspectionIV instance" );
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

    public void remove( VisualInspectionIV persistentInstance )
    {
        log.trace( "removing VisualInspectionIV instance" );
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

    public VisualInspectionIV merge( VisualInspectionIV detachedInstance )
    {
        log.trace( "merging CasterPositionsIV instance" );
        try
        {
            VisualInspectionIV result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public VisualInspectionIV findById( long objid )
    {
        log.trace( "getting VisualInspectionIV instance with name: " + objid );
        try
        {
            VisualInspectionIV instance = entityManager.find( VisualInspectionIV.class, objid );
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
