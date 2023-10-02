package com.hydro.casting.server.model.inspection.dao;

import com.hydro.casting.server.model.inspection.InspectionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class InspectionRuleHome
{
    private final static Logger log = LoggerFactory.getLogger( InspectionRuleHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( InspectionRule transientInstance )
    {
        log.trace( "persisting InspectionRule instance" );
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

    public void remove( InspectionRule persistentInstance )
    {
        log.trace( "removing InspectionRule instance" );
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

    public InspectionRule merge( InspectionRule detachedInstance )
    {
        log.trace( "merging InspectionRule instance" );
        try
        {
            InspectionRule result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public InspectionRule findById( long objid )
    {
        log.trace( "getting InspectionRule instance with name: " + objid );
        try
        {
            InspectionRule instance = entityManager.find( InspectionRule.class, objid );
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