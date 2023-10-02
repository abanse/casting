package com.hydro.casting.server.ejb.av;

import com.google.common.primitives.Longs;
import com.hydro.casting.common.Casting;
import com.hydro.casting.server.model.av.ActualValues;
import com.hydro.casting.server.model.av.ActualValuesDefinition;
import com.hydro.casting.server.model.av.ActualValuesHeader;
import com.hydro.casting.server.model.av.dao.ActualValuesDefinitionHome;
import com.hydro.casting.server.model.av.dao.ActualValuesHeaderHome;
import com.hydro.casting.server.model.av.dao.ActualValuesHome;
import com.hydro.core.common.util.DateTimeUtil;
import org.hibernate.engine.jdbc.BlobProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Stateless
public class ActualValuesManagementService
{
    private final static Logger log = LoggerFactory.getLogger( ActualValuesManagementService.class );
    @EJB
    private ActualValuesHeaderHome actualValuesHeaderHome;
    @EJB
    private ActualValuesDefinitionHome actualValuesDefinitionHome;
    @EJB
    private ActualValuesHome actualValuesHome;

    /**
     * Finds the header for a given machine, type and reference. If no header is found, a new one is created.
     *
     * @param machine   The machine the ActualValue object belongs to
     * @param type      The type of the ActualValue object
     * @param reference The reference to identify the specific ActualValue object
     * @return A header object either from the database or created newly
     */
    public ActualValuesHeader findOrCreateActualValuesHeader( String machine, String type, String reference )
    {
        ActualValuesHeader header = actualValuesHeaderHome.findByMeasurement( Casting.ACTUAL_VALUES.APPLICATION.PLC_SIGNALS, machine, type, reference );
        if ( header == null )
        {
            header = new ActualValuesHeader();
            header.setApplication( Casting.ACTUAL_VALUES.APPLICATION.PLC_SIGNALS );
            header.setSource( machine );
            header.setType( type );
            header.setRefName( reference );
            actualValuesHeaderHome.persist( header );
        }
        return header;
    }

    /**
     * Updates the definition linked to the header based on the  measurements provided if necessary.
     * If no definition is linked to the header yet, looks it up in the database and links it to the header before updating.
     * If no definition is linked to the header and nothing is found in the database, creates a new definition with version 1 and sets it based on measurements.
     *
     * @param header       The header that reflects the current ActualValues object.
     * @param measurements The measurements that should be used to update the definition.
     */
    public void createOrUpdateActualValuesDefinition( ActualValuesHeader header, Map<String, Measurement> measurements )
    {
        ActualValuesDefinition definition = header.getDefinition();

        // Header was newly created and does not have a definition attached yet
        if ( definition == null )
        {
            // Try to find suitable definition in the database
            definition = actualValuesDefinitionHome.findLastDefinition( header.getType() );

            // If nothing was found, signal of the given definition type was received for the first time, and a new definition has to be created
            if ( definition == null )
            {
                definition = new ActualValuesDefinition();
                definition.setName( header.getType() );
                definition.setVersion( 1 );
                setDefinitions( definition, measurements );
                header.setDefinition( definition );
                actualValuesDefinitionHome.persist( definition );
            }
            else
            {
                // Check if the definition is up-to-date and matches the given measurements
                if ( !definitionEquals( definition, measurements ) )
                {
                    // If the measurements differ from the definition, a new version of the existing definition has to be created
                    final int newVersion = definition.getVersion() + 1;
                    definition = new ActualValuesDefinition();
                    definition.setName( header.getType() );
                    definition.setVersion( newVersion );
                    setDefinitions( definition, measurements );
                    header.setDefinition( definition );
                    actualValuesDefinitionHome.persist( definition );
                }
                else
                {
                    // If everything checks out, just attach the definition to the header
                    header.setDefinition( definition );
                }
            }
        }
    }

    /**
     * Updates the actual values in the database with the provided measurements. If this is a new header (thus, no data is present yet), a new ActualValues object
     * is created in the database.
     *
     * @param header       The header identifying the ActualValues object
     * @param measurements The measurements that should be stored in the database
     * @param receiveTime  The time the data was received
     */
    public void createOrUpdateActualValues( ActualValuesHeader header, Map<String, Measurement> measurements, long receiveTime )
    {
        ActualValues actualValues = header.getValues();
        // Actual values can be null if this is a new object and thus a newly created header
        if ( actualValues == null )
        {
            actualValues = new ActualValues();
            actualValuesHome.persist( actualValues );
            header.setValues( actualValues );
        }

        // Update LastMeasuredTS, which represents the timestamp the data was last measured / updated
        final LocalDateTime newLastMeasuredTS = DateTimeUtil.asLocalDateTime( receiveTime );
        if ( header.getLastMeasuredTS() == null || header.getLastMeasuredTS().isBefore( newLastMeasuredTS ) )
        {
            header.setLastMeasuredTS( newLastMeasuredTS );
        }

        // Locks the actual values object in the database until the next write to prevent race conditions
        actualValuesHome.lock( actualValues );
        try
        {
            ActualValuesDefinition definition = header.getDefinition();
            for ( String key : measurements.keySet() )
            {
                int pos = definition.getPos( key );
                // position starts at 1, and is -1 if value was not found in the definition
                if ( pos > 0 )
                {
                    final Measurement measurement = measurements.get( key );
                    appendActualValue( actualValues, pos, receiveTime, measurement.value, measurement.binary );
                }
            }
        }
        catch ( Exception ex )
        {
            log.error( "error appending measurements", ex );
        }
    }

    /**
     * Sets the data of a definition based on the measurements provided in the current machine signal.
     *
     * @param definition   The ActualValuesDefinition that needs its values set
     * @param measurements The measurements of the current machine signal used to set the data of the definition
     */
    private void setDefinitions( ActualValuesDefinition definition, Map<String, Measurement> measurements )
    {
        final List<String> ids = new ArrayList<>( measurements.keySet() );
        Collections.sort( ids );

        int pos = 1;
        for ( String id : ids )
        {
            final Measurement measurement = measurements.get( id );
            // Sets the correct database field based on the pos value
            definition.setValueName( pos, id );
            if ( measurement.binary )
            {
                definition.setValueType( pos, "BINARY" );
            }
            else
            {
                definition.setValueType( pos, "DOUBLE" );
            }
            pos++;
        }
    }

    /**
     * Checks if the measurements from the current machine signal match the given definition.
     *
     * @param definition   The ActualValuesDefinition that should be checked for correctness
     * @param measurements The measurements of the current machine signal defining what the definition should look like
     * @return True if the definition reflects the measurements correctly, False if the definition needs to be updated
     */
    private boolean definitionEquals( ActualValuesDefinition definition, Map<String, Measurement> measurements )
    {
        final List<String> ids = new ArrayList<>( measurements.keySet() );
        Collections.sort( ids );

        int pos = 1;
        for ( String id : ids )
        {
            final Measurement measurement = measurements.get( id );
            if ( !Objects.equals( definition.getValueName( pos ), id ) )
            {
                return false;
            }

            if ( measurement.binary )
            {
                if ( !Objects.equals( definition.getValueType( pos ), "BINARY" ) )
                {
                    return false;
                }
            }
            else
            {
                if ( !Objects.equals( definition.getValueType( pos ), "DOUBLE" ) )
                {
                    return false;
                }
            }
            pos++;
        }

        return true;
    }

    /**
     * Appends the provided data (time, value, binary) to the correct field (identified by pos) in the ActualValues database object in binary format (as a blob).
     *
     * @param values The ActualValues object that holds the current data and needs updating
     * @param pos    The position of the data field according to the definition, used to identify the correct row in the database
     * @param time   The long timestamp of the measurement
     * @param value  The double value of the measurement
     * @param binary An identifier to define whether the provided double value is originally of type double of boolean
     * @throws SQLException Can occur when writing the data to the database (thrown by ORM framework)
     * @throws IOException  Can occur when writing the data to the database (thrown by ORM framework)
     */
    private void appendActualValue( ActualValues values, int pos, long time, double value, boolean binary ) throws SQLException, IOException
    {
        // Create 8-byte representation of the timestamp (long)
        byte[] timeBytes = Longs.toByteArray( time );
        // Transform the original double value to a long and then create its 8-byte representation
        byte[] valueBytes = Longs.toByteArray( Double.doubleToLongBits( value ) );
        // Create 16-byte array that will contain the timestamp + the value, representing one datapoint
        byte[] timeValueBytes = new byte[timeBytes.length + valueBytes.length];
        // Copy the two 8-bit arrays in to the 16-bit array, thus combining the time and the value to a datapoint
        System.arraycopy( timeBytes, 0, timeValueBytes, 0, timeBytes.length );
        System.arraycopy( valueBytes, 0, timeValueBytes, timeBytes.length, valueBytes.length );

        // Get or create the blob that stores all datapoints
        Blob blob = values.getValue( pos );
        if ( blob == null )
        {
            blob = BlobProxy.generateProxy( timeValueBytes );
            values.setValue( pos, blob );
        }
        else
        {
            if ( binary )
            {
                // If the original value was a boolean, and it did not change
                final InputStream blobStream = blob.getBinaryStream();
                byte[] currentContent;
                try
                {
                    currentContent = blobStream.readAllBytes();
                }
                finally
                {
                    blobStream.close();
                }

                // int indexOfCurrentMeasurement = Bytes.indexOf( currentContent, timeValueBytes );
                byte[] lastValueInBlobBytes = new byte[8];
                // Last 16 bytes of currentContent contain the last timestamp (8 bytes) and then the last value (8 bytes), so we need the last 8 bytes to check the last value
                System.arraycopy( currentContent, currentContent.length - 8, lastValueInBlobBytes, 0, 8 );
                // if ( indexOfCurrentMeasurement < 0 )
                if ( !Arrays.equals( lastValueInBlobBytes, valueBytes ) )
                {
                    blob.setBytes( blob.length() + 1, timeValueBytes );
                }
            }
            else
            {
                // If the original value was a double, simply attach the new datapoint at the end of the blob
                blob.setBytes( blob.length() + 1, timeValueBytes );
            }
        }
    }

    public ActualValuesHeader findLastHeaderBefore( String application, String machine, ActualValuesHeader currentHeader )
    {
        return actualValuesHeaderHome.findLastHeaderBefore( application, machine, currentHeader );
    }

    /**
     * Class used for intermediary storage of the measurements from the current machine signal JSON file. Stores a double value, and whether that double value
     * represents an actual double, or is the double representation of boolean.
     */
    public static class Measurement
    {
        public Measurement( boolean binary, double value )
        {
            this.binary = binary;
            this.value = value;
        }

        public boolean binary;
        public double value;
    }
}
