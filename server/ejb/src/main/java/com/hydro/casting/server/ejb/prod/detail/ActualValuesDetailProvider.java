package com.hydro.casting.server.ejb.prod.detail;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.ActualValuesDTO;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.casting.server.model.av.ActualValues;
import com.hydro.casting.server.model.av.ActualValuesHeader;
import com.hydro.casting.server.model.av.dao.ActualValuesHeaderHome;
import com.hydro.core.server.contract.workplace.DetailProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.InputStream;
import java.util.Map;

@Stateless( name = "ActualValuesDetailProvider" )
public class ActualValuesDetailProvider implements DetailProvider<ProcessDocuDTO, ActualValuesDTO>
{
    private final static Logger log = LoggerFactory.getLogger( ActualValuesDetailProvider.class );

    @EJB
    private ActualValuesHeaderHome actualValuesHeaderHome;

    @Override
    public ActualValuesDTO loadDetail( ProcessDocuDTO master, Map<String, String> context )
    {
        if ( master == null )
        {
            return null;
        }

        String application = Casting.ACTUAL_VALUES.APPLICATION.PLC_SIGNALS;
        String machine;
        String definitionName;
        String charge = master.getChargeWithoutYear();

        if ( charge == null )
        {
            log.info( "No ActualValues found" );
            return null;
        }

        // First char of the charge number always corresponds to the machine the charge is cast at.
        switch ( charge.charAt( 0 ) )
        {
        case '8':
            machine = Casting.MACHINE.CASTER_80;
            definitionName = "SIGNALA80";
            break;
        case '7':
            machine = Casting.MACHINE.CASTER_70;
            definitionName = "SIGNALA70";
            break;
        case '6':
            machine = Casting.MACHINE.CASTER_60;
            definitionName = "SIGNALA60";
            break;
        case '5':
            machine = Casting.MACHINE.CASTER_50;
            definitionName = "SIGNALA50";
            break;
        case '2':
            machine = Casting.MACHINE.MELTING_FURNACE_S2;
            definitionName = "SIGNALAS2";
            break;
        default:
            log.info(
                    "No ActualValues found: Unexpected charge number ({}) in ProcessDocuDTO while loading ActualValues, first character ({}) does not correspond to valid casting machine (should be one of 8, 7, 6, 5).",
                    charge, charge.charAt( 0 ) );
            return null;
        }

        final ActualValuesHeader header = actualValuesHeaderHome.findByMeasurement( application, machine, definitionName, charge );
        if ( header == null )
        {
            log.info( "No ActualValues found: No ActualValuesHeader found for application {}, machine {}, definition {} and charge {}.", application, machine, definitionName, charge );
            return null;
        }
        final String actualValueName;
        final String additionalValueName;
        if ( context != null && context.containsKey( "actualValueName" ) )
        {
            actualValueName = context.get( "actualValueName" );

            // Optional parameter
            additionalValueName = context.getOrDefault( "additionalValueName", null );
        }
        else
        {
            log.info( "No ActualValues found: No context entry for actualValueName." );
            return null;
        }

        int pos = header.getDefinition().getPos( actualValueName );
        int additionalPos = additionalValueName != null ? header.getDefinition().getPos( additionalValueName ) : -1;
        if ( pos < 0 )
        {
            log.info( "No ActualValues found: Name of the value ({}) not contained in ActualValueDefinition.", actualValueName );
            return null;
        }

        // Additional value name provided, but not found in the definition
        if ( additionalValueName != null && additionalPos < 0 )
        {
            log.info( "No ActualValues found: Additional value name provided ({}), but not contained in ActualValueDefinition.", additionalValueName );
            return null;
        }

        final ActualValuesDTO actualValuesDTO = new ActualValuesDTO();
        actualValuesDTO.setCharge( charge );

        final ActualValues actualValues = header.getValues();
        try ( InputStream stream = actualValues.getValue( pos ).getBinaryStream() )
        {
            actualValuesDTO.setValues( stream.readAllBytes() );
        }
        catch ( Exception ex )
        {
            log.error( "error getting values", ex );
            return null;
        }

        // If additionalPos > 0 at this point: Additional value name provided and found in definition
        if ( additionalPos > 0 )
        {
            try ( InputStream stream = actualValues.getValue( additionalPos ).getBinaryStream() )
            {
                actualValuesDTO.setAdditionalValues( stream.readAllBytes() );
            }
            catch ( Exception ex )
            {
                log.error( "error getting values for additional value name", ex );
                return null;
            }
        }

        return actualValuesDTO;
    }
}
