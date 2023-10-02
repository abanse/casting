package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.MaterialLockLocation;
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
public class MaterialLockLocationHome
{
    private final static Logger log = LoggerFactory.getLogger( MaterialLockLocationHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( MaterialLockLocation transientInstance )
    {
        log.trace( "persisting MaterialLockLocation instance" );
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

    public void remove( MaterialLockLocation persistentInstance )
    {
        log.trace( "removing MaterialLockLocation instance" );
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

    public MaterialLockLocation merge( MaterialLockLocation detachedInstance )
    {
        log.trace( "merging MaterialLockLocation instance" );
        try
        {
            MaterialLockLocation result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public MaterialLockLocation findById( long objid )
    {
        log.trace( "getting MaterialLockLocation instance with name: " + objid );
        try
        {
            MaterialLockLocation instance = entityManager.find( MaterialLockLocation.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public MaterialLockLocation findByApk( String apk )
    {
        final TypedQuery<MaterialLockLocation> query = entityManager.createNamedQuery( "materialLockLocation.apk", MaterialLockLocation.class );
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
                "   SELECT new com.hydro.core.server.contract.workplace.dto.SimpleKeyDescriptionDTO(mll.apk, mll.description) "
                        + "     FROM MaterialLockLocation mll "
                        + " ORDER BY mll.apk";
        //@formatter:on
        final TypedQuery<KeyDescriptionDTO> query = entityManager.createQuery( hql, KeyDescriptionDTO.class );

        return query.getResultList();
    }
}