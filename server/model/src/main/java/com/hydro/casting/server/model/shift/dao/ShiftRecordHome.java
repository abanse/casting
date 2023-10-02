package com.hydro.casting.server.model.shift.dao;

import com.hydro.casting.server.model.shift.ShiftRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class ShiftRecordHome
{
    private final static Logger log = LoggerFactory.getLogger( ShiftRecordHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( ShiftRecord transientInstance )
    {
        log.trace( "persisting ShiftRecord instance" );
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

    public void remove( ShiftRecord persistentInstance )
    {
        log.trace( "removing ShiftRecord instance" );
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

    public ShiftRecord merge( ShiftRecord detachedInstance )
    {
        log.trace( "merging ShiftRecord instance" );
        try
        {
            ShiftRecord result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public ShiftRecord findById( long objid )
    {
        log.trace( "getting ShiftRecord instance with name: " + objid );
        try
        {
            ShiftRecord instance = entityManager.find( ShiftRecord.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public List<ShiftRecord> findShiftRecordsByShiftType( String shiftType )
    {
        final TypedQuery<ShiftRecord> query = entityManager.createNamedQuery( "shiftRecord.shiftType", ShiftRecord.class );
        query.setParameter( "shiftType", shiftType );
        return query.getResultList();
    }

}