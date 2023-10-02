package com.hydro.casting.server.ejb.util;

import com.hydro.core.common.util.DateTimeUtil;

import javax.json.JsonNumber;
import javax.json.JsonValue;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public abstract class JSONUtil
{
    private final static String TIMESTAMP_PATTERN = "yyyy.MM.dd HH:mm:ss";

    /**
     * Parses the timestamp from the JSON file to an epoch (long value of unix seconds)
     *
     * @param jsonTimestamp The timestamp string taken from the current machine signal JSON
     * @return long value that represents the timestamp in epoch format (unix seconds)
     */
    public static long parseTimestampToLong( String jsonTimestamp )
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( TIMESTAMP_PATTERN );
        LocalDateTime localDateTime = LocalDateTime.parse( jsonTimestamp, formatter );
        return DateTimeUtil.asEpoch( localDateTime );
    }

    /**
     * Returns the value from the JSON file as a double, meaning a binary value is parsed while a number is return as a double.
     * Defaults to -1. if the value cannot be parsed or extracted.
     *
     * @param value The value to parse ot a double
     * @return The value as a double it it's a number, the value as 0. or 1. if it's a binary, -1. otherwise
     */
    public static double getDoubleValue( JsonValue value )
    {
        if ( value == null )
        {
            return 0.;
        }
        if ( value.getValueType() == JsonValue.ValueType.TRUE )
        {
            return 1.;
        }
        else if ( value.getValueType() == JsonValue.ValueType.FALSE )
        {
            return 0.;
        }
        else if ( value.getValueType() == JsonValue.ValueType.NUMBER )
        {
            return ( (JsonNumber) value ).doubleValue();
        }
        return -1.;
    }
}
