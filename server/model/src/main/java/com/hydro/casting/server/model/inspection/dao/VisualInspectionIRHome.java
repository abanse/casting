package com.hydro.casting.server.model.inspection.dao;

import com.hydro.casting.server.model.inspection.VisualInspectionIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class VisualInspectionIRHome
{
    private final static Logger log = LoggerFactory.getLogger( VisualInspectionIRHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( VisualInspectionIR transientInstance )
    {
        log.trace( "persisting VisualInspectionIR instance" );
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

    public void remove( VisualInspectionIR persistentInstance )
    {
        log.trace( "removing VisualInspectionIR instance" );
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

    public VisualInspectionIR merge( VisualInspectionIR detachedInstance )
    {
        log.trace( "merging CasterPositionsIR instance" );
        try
        {
            VisualInspectionIR result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public VisualInspectionIR findById( long objid )
    {
        log.trace( "getting VisualInspectionIR instance with name: " + objid );
        try
        {
            VisualInspectionIR instance = entityManager.find( VisualInspectionIR.class, objid );
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
