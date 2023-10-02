package com.hydro.casting.server.model.res.dao;

import com.hydro.casting.server.model.res.MachineCalendar;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MachineCalendarHome
{
    private final static Logger log = LoggerFactory.getLogger( MachineCalendarHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( MachineCalendar transientInstance )
    {
        log.trace( "persisting MachineCalendar instance" );
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

    public void remove( MachineCalendar persistentInstance )
    {
        log.trace( "removing MachineCalendar instance" );
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

    public MachineCalendar merge( MachineCalendar detachedInstance )
    {
        log.trace( "merging MachineCalendar instance" );
        try
        {
            MachineCalendar result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public MachineCalendar findById( long objid )
    {
        log.trace( "getting MachineCalendar instance with name: " + objid );
        try
        {
            MachineCalendar instance = entityManager.find( MachineCalendar.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public MachineCalendar detach( MachineCalendar persistentInstance )
    {
        log.debug( "detach MachineCalendar instance" );
        final MachineCalendar instance = (MachineCalendar) Hibernate.unproxy( persistentInstance );
        try
        {
            entityManager.detach( instance );
            log.debug( "detach successful" );
        }
        catch ( RuntimeException re )
        {
            log.error( "detach failed", re );
            throw re;
        }
        instance.setObjid( 0 );
        return instance;
    }

}