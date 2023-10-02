package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.MaterialLockType;
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
public class MaterialLockTypeHome
{
    private final static Logger log = LoggerFactory.getLogger( MaterialLockTypeHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( MaterialLockType transientInstance )
    {
        log.trace( "persisting MaterialLockType instance" );
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

    public void remove( MaterialLockType persistentInstance )
    {
        log.trace( "removing MaterialLockType instance" );
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

    public MaterialLockType merge( MaterialLockType detachedInstance )
    {
        log.trace( "merging MaterialLockType instance" );
        try
        {
            MaterialLockType result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public MaterialLockType findById( long objid )
    {
        log.trace( "getting MaterialLockType instance with name: " + objid );
        try
        {
            MaterialLockType instance = entityManager.find( MaterialLockType.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public MaterialLockType findByApk( String apk )
    {
        final TypedQuery<MaterialLockType> query = entityManager.createNamedQuery( "materialLockType.apk", MaterialLockType.class );
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
              "   SELECT new com.hydro.core.server.contract.workplace.dto.SimpleKeyDescriptionDTO(mlt.apk, mlt.description) "
            + "     FROM MaterialLockType mlt "
            + " ORDER BY mlt.apk";
        //@formatter:on
        final TypedQuery<KeyDescriptionDTO> query = entityManager.createQuery( hql, KeyDescriptionDTO.class );

        return query.getResultList();
    }

}