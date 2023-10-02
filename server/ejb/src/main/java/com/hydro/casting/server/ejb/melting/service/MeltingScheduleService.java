package com.hydro.casting.server.ejb.melting.service;

import com.hydro.casting.server.contract.dto.MeltingScheduleDTO;
import com.hydro.core.server.contract.workplace.SearchType;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Stateless
public class MeltingScheduleService
{
    private static final String QUERY = "select mb.objid as id, mb.executingMachine.apk as machine, mb.charge as charge, mb.alloyName as alloy, mb.executionState as executionState from MeltingBatch mb ";
    private static final String SELECT_MACHINE = "where mb.executingMachine.apk = (:machine) and mb.executionState in (:executionStates) ";

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    @SuppressWarnings( "unchecked" )
    public List<MeltingScheduleDTO> loadBySearchType( SearchType searchType, Map<String, Object> parameters )
    {
        if ( searchType == SearchType.MACHINE && parameters != null && !parameters.isEmpty() )
        {
            final String machine = (String) parameters.get( "MACHINE" );
            final List<Integer> executionStates = (List<Integer>) parameters.get( "EXECUTION_STATES" );

            return loadMachineByExecutionStates( machine, executionStates );
        }

        return Collections.emptyList();
    }

    @SuppressWarnings( "unchecked" )
    private List<MeltingScheduleDTO> loadMachineByExecutionStates( String machine, List<Integer> executionStates )
    {
        if ( machine != null && executionStates != null )
        {
            final org.hibernate.query.Query<MeltingScheduleDTO> query = entityManager.unwrap( Session.class ).createQuery( QUERY + SELECT_MACHINE );
            query.setParameter( "machine", machine );
            query.setParameter( "executionStates", executionStates );
            query.setResultTransformer( new AliasToBeanResultTransformer( MeltingScheduleDTO.class ) );

            return query.list();
        }

        return Collections.emptyList();
    }
}
