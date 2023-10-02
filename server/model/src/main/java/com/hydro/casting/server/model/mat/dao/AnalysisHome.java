package com.hydro.casting.server.model.mat.dao;

import com.hydro.casting.server.model.mat.Analysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Stateless
public class AnalysisHome
{
    private final static Logger log = LoggerFactory.getLogger( AnalysisHome.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void persist( Analysis transientInstance )
    {
        log.trace( "persisting Analysis instance" );
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

    public void remove( Analysis persistentInstance )
    {
        log.trace( "removing Analysis instance" );
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

    public Analysis merge( Analysis detachedInstance )
    {
        log.trace( "merging Analysis instance" );
        try
        {
            Analysis result = entityManager.merge( detachedInstance );
            log.trace( "merge successful" );
            return result;
        }
        catch ( RuntimeException re )
        {
            log.error( "merge failed", re );
            throw re;
        }
    }

    public Analysis findById( long objid )
    {
        log.trace( "getting Analysis instance with name: " + objid );
        try
        {
            Analysis instance = entityManager.find( Analysis.class, objid );
            log.trace( "get successful" );
            return instance;
        }
        catch ( RuntimeException re )
        {
            log.error( "get failed", re );
            throw re;
        }
    }

    public List<Analysis> findByParameters( LocalDateTime preRegTime, String charge, String melter, String sampleNumber )
    {
        LocalDateTime date = preRegTime.minusDays( 10 );
        log.trace( String.format( "Getting Analysis instance with parameters date > %s, charge(%s), melter(%s) and " + "sample number (%s).", date, charge, melter, sampleNumber ) );
        final TypedQuery<Analysis> query = entityManager.createNamedQuery( "analysis.byParameters", Analysis.class );
        query.setParameter( "date", date );
        query.setParameter( "charge", charge );
        query.setParameter( "melter", melter );
        query.setParameter( "sampleNumber", sampleNumber );
        try
        {
            return query.getResultList();
        }
        catch ( NoResultException nrex )
        {
            return null;
        }
    }

    public List<Analysis> findAllByChargeAndYear( String charge, int year )
    {
        log.trace( String.format( "Getting analyses with parameter charge(%s) and year(%s).", charge, year ) );

        // Previous year is used to ensure that a change of year does not cause problems, charge numbers are only duplicated over a span of 3-4 years
        LocalDateTime beginningOfPreviousYear = LocalDateTime.of( year > 0 ? year - 1 : 0, 1, 1, 0, 0, 0 );
        // Leading zero charge has to be checked as well since there is no guarantee the passed in charge contains the leading zero
        String chargeWithLeadingZero = "0" + charge;

        final TypedQuery<Analysis> query = entityManager.createNamedQuery( "analysis.byChargeAndYear", Analysis.class );
        query.setParameter( "charge", charge );
        query.setParameter( "chargeWithLeadingZero", chargeWithLeadingZero );
        query.setParameter( "beginningOfPreviousYear", beginningOfPreviousYear );
        try
        {
            return query.getResultList();
        }
        catch ( NoResultException nrex )
        {
            return null;
        }
    }

    public Analysis findLastActiveByName( String name )
    {
        final TypedQuery<Analysis> query = entityManager.createNamedQuery( "analysis.findLastActiveByName", Analysis.class );
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

    public Analysis findLastFurnaceAnalysis( String charge )
    {
        final TypedQuery<Analysis> query = entityManager.createNamedQuery( "analysis.findLastFurnaceAnalysis", Analysis.class );
        // Sonderbehandlung für RealAlloy
        if ( charge.startsWith( "RA" ) )
        {
            query.setParameter( "charge", charge );
        }
        else
        {
            query.setParameter( "charge", "0" + charge );
        }
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

    public List<Analysis> findBySchedulableOID( Long schedulableOID )
    {        final TypedQuery<Analysis> query = entityManager.createNamedQuery( "analysis.findBySchedulableOID", Analysis.class );
        query.setParameter( "schedulableOID", schedulableOID );
        try
        {
            return query.getResultList();
        }
        catch ( NoResultException nrex )
        {
            return Collections.emptyList();
        }
    }
}