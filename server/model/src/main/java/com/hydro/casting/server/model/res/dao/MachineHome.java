package com.hydro.casting.server.model.res.dao;

import com.hydro.casting.server.model.res.Machine;
import com.hydro.core.server.contract.workplace.dto.KeyDescriptionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class MachineHome
{
    private final static Logger log = LoggerFactory.getLogger( MachineHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( Machine transientInstance )
    {
        log.trace( "persisting Machine instance" );
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

    public void remove( Machine persistentInstance )
    {
        log.trace( "removing Machine instance" );
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

    public Machine merge( Machine detachedInstance )
    {
        log.trace( "merging Machine instance" );
        try
        {
            Machine result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public Machine findById( long objid )
    {
        log.trace( "getting Machine instance with name: " + objid );
        try
        {
            Machine instance = entityManager.find( Machine.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public Machine findByApk( String apk )
    {
        final TypedQuery<Machine> query = entityManager.createNamedQuery( "machine.apk", Machine.class );
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

    public List<KeyDescriptionDTO> keyDescriptions()
    {
        //@formatter:off
        final String hql =
                "   SELECT new com.hydro.core.server.contract.workplace.dto.SimpleKeyDescriptionDTO(m.apk, m.description) "
                        + "     FROM Machine m "
                        + " ORDER BY m.apk";
        //@formatter:on
        final TypedQuery<KeyDescriptionDTO> query = entityManager.createQuery( hql, KeyDescriptionDTO.class );

        return query.getResultList();
    }

}