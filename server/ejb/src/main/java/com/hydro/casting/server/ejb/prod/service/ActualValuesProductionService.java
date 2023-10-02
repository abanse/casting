package com.hydro.casting.server.ejb.prod.service;

import com.hydro.casting.server.contract.dto.ActualValueDTO;
import com.hydro.casting.server.model.av.ActualValues;
import com.hydro.casting.server.model.av.ActualValuesDefinition;
import com.hydro.casting.server.model.av.ActualValuesHeader;
import com.hydro.core.common.util.DateTimeUtil;
import com.hydro.core.common.util.TimePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class ActualValuesProductionService
{
    private final static Logger log = LoggerFactory.getLogger( ActualValuesProductionService.class );

    /**
     * This function should find a - possibly existing - casting interruption for the casting process linked to the provided header.
     *
     * @param header The header of the machine signals received for the according casting process
     * @return TimePeriod containing the first casting interruption signal as start and the current time as end; null if no casting interruption occurred.
     */
    public TimePeriod findCastingInterruptionStartAndEnd( final ActualValuesHeader header )
    {
        final ActualValues actualValues = header.getValues();
        final ActualValuesDefinition definition = header.getDefinition();
        final int posCastingInterruption = definition.getPos( "Gussabruch" );

        // Return null if casting interruption (machine signal field is "Gussabruch") is not contained in the definition
        if ( posCastingInterruption < 0 )
        {
            return null;
        }

        // Converts the machine signal data to a list of ActualValueDTOs for processing
        final List<ActualValueDTO> points = getDTOListFromBytes( actualValues, posCastingInterruption );

        // Return null if list of DTOs is empty or only contains one value (which means the casting interruption boolean value never changed and thus stay false)
        if ( points.isEmpty() || points.size() == 1 )
        {
            return null;
        }

        // Sort chronologically, since we want to find the first (and only) casting interruption
        points.sort( Comparator.comparing( ActualValueDTO::getTimestamp ) );
        // Find the first ActualValueDTO where the boolean value for casting interruption changed from false to true
        Optional<ActualValueDTO> startDTO = points.stream().filter( dto -> isDoubleEquals( dto.getValue(), 1. ) ).findFirst();

        // Return null if no start time was found, since that means that no casting interruption occurred
        if ( startDTO.isEmpty() )
        {
            return null;
        }

        // End timestamp is now, since the downtime request should be for the complete casting process since the casting interruption
        LocalDateTime startTS = DateTimeUtil.asLocalDateTime( startDTO.get().getTimestamp() );
        LocalDateTime endTS = LocalDateTime.now();

        return TimePeriod.between( startTS, endTS );
    }

    /**
     * Standard double comparison function to avoid possible precision / rounding errors in future
     *
     * @param d1 First double value to compare
     * @param d2 Second double value to compare
     * @return True if the difference between both double values is smaller than a defined value
     */
    private static boolean isDoubleEquals( double d1, double d2 )
    {
        // Large epsilon because 1. represents true and 0. represents false - other values are not possible
        return Math.abs( d1 - d2 ) < 0.001;
    }

    /**
     * This function finds the casting start and end timestamps for the casting process associated with the passed in header (representing the machine signals received during that
     * casting process).
     * This is done by analyzing the corresponding machine signals: casting start is defined as the point in time when the tilt angle ("Kippwinkel") exceeded 5 degrees; casting
     * end is the end of the last found machine signal.
     *
     * @param header The header for the machine signals of the associated casting process
     * @return A TimePeriod containing the start and the end of the casting process
     */
    public TimePeriod findCastingStartAndEnd( final ActualValuesHeader header )
    {
        final ActualValues actualValues = header.getValues();
        final ActualValuesDefinition definition = header.getDefinition();
        final int posKippwinkel = definition.getPos( "Kippwinkel" );

        // Get data from machine signals as a list of ActualValueDTOs for further processing
        final List<ActualValueDTO> points = getDTOListFromBytes( actualValues, posKippwinkel );

        if ( points.isEmpty() )
        {
            return null;
        }

        // Skipping first data point, since it contains invalid data that leads to misshaped plots
        final long minTimeStamp = points.stream().mapToLong( ActualValueDTO::getTimestamp ).min().orElse( System.currentTimeMillis() );
        final List<ActualValueDTO> correctPoints = points.stream().filter( numberNumberData -> !( numberNumberData.getTimestamp() == minTimeStamp ) )
                .sorted( Comparator.comparing( ActualValueDTO::getTimestamp ) ).collect( Collectors.toList() );

        if ( correctPoints.isEmpty() )
        {
            return null;
        }

        long castingStart = 0;

        for ( ActualValueDTO correctPoint : correctPoints )
        {
            // Kippwinkel muss größer 5 sein
            if ( correctPoint.getValue() < 5.0 )
            {
                continue;
            }

            castingStart = correctPoint.getTimestamp();
            break;
        }

        if ( castingStart <= 0 )
        {
            castingStart = correctPoints.get( 0 ).getTimestamp();
        }

        long castingEnd = correctPoints.get( correctPoints.size() - 1 ).getTimestamp();

        if ( castingEnd < castingStart )
        {
            return null;
        }

        return TimePeriod.between( DateTimeUtil.asLocalDateTime( castingStart ), DateTimeUtil.asLocalDateTime( castingEnd ) );
    }

    /**
     * Transforms the binary data of the machine signals into a list of ActualValueDTOs for further processing. Each DTO contains the timestamp and the value according to the data
     * stored as binary. Each timestamp and each value corresponds to 8 bytes of data each, written sequentially as a blob.
     *
     * @param actualValues The machine signals as the corresponding database object, which stores the data for several signals in binary format
     * @param pos          The position of the data that should be extracted. Each machine signal corresponds to one position
     * @return A list of DTOs with the requested data; or an empty list if no data was found
     */
    private List<ActualValueDTO> getDTOListFromBytes( ActualValues actualValues, int pos )
    {
        final List<ActualValueDTO> points = new ArrayList<>();
        final byte[] allValueBytes = getBytesFromBlob( actualValues.getValue( pos ) );
        if ( allValueBytes != null )
        {
            int readPos = 0;
            final byte[] timeBytes = new byte[8];
            final byte[] valueBytes = new byte[8];

            while ( allValueBytes.length > readPos )
            {
                System.arraycopy( allValueBytes, readPos, timeBytes, 0, timeBytes.length );
                readPos = readPos + timeBytes.length;
                System.arraycopy( allValueBytes, readPos, valueBytes, 0, valueBytes.length );
                readPos = readPos + valueBytes.length;
                final long timestamp = fromByteArray( timeBytes );
                final double actualValueNumber = Double.longBitsToDouble( fromByteArray( valueBytes ) );

                points.add( new ActualValueDTO( timestamp, actualValueNumber ) );
            }
        }

        return points;
    }

    private byte[] getBytesFromBlob( Blob blob )
    {
        if ( blob == null )
        {
            return null;
        }
        try
        {
            return blob.getBinaryStream().readAllBytes();
        }
        catch ( Exception e )
        {
            log.error( "error reading blob", e );
        }
        return null;
    }

    private long fromByteArray( byte[] bytes )
    {
        return fromBytes( bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7] );
    }

    private long fromBytes( byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8 )
    {
        return ( (long) b1 & 255L ) << 56 | ( (long) b2 & 255L ) << 48 | ( (long) b3 & 255L ) << 40 | ( (long) b4 & 255L ) << 32 | ( (long) b5 & 255L ) << 24 | ( (long) b6 & 255L ) << 16
                | ( (long) b7 & 255L ) << 8 | (long) b8 & 255L;
    }

}
