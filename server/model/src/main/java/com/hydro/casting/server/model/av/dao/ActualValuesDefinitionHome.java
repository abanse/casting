package com.hydro.casting.server.model.av.dao;

import com.hydro.casting.server.model.av.ActualValuesDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class ActualValuesDefinitionHome
{
    private final static Logger log = LoggerFactory.getLogger( ActualValuesDefinitionHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( ActualValuesDefinition transientInstance )
    {
        log.trace( "persisting ActualValuesDefinition instance" );
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

    public void remove( ActualValuesDefinition persistentInstance )
    {
        log.trace( "removing ActualValuesDefinition instance" );
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

    public ActualValuesDefinition merge( ActualValuesDefinition detachedInstance )
    {
        log.trace( "merging ActualValuesDefinition instance" );
        try
        {
            ActualValuesDefinition result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public ActualValuesDefinition findById( long objid )
    {
        log.trace( "getting ActualValuesDefinition instance with name: " + objid );
        try
        {
            ActualValuesDefinition instance = entityManager.find( ActualValuesDefinition.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public ActualValuesDefinition findLastDefinition( String name )
    {
        final TypedQuery<ActualValuesDefinition> query = entityManager.createNamedQuery( "actualValuesDefinition.lastDefinition", ActualValuesDefinition.class );
        query.setParameter( "name", name );
        query.setMaxResults( 1 );
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