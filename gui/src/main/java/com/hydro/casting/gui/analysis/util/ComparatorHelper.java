package com.hydro.casting.gui.analysis.util;

public abstract class ComparatorHelper
{

    /**
     * Compares two sample numbers to each other. The logical ordering of samples is as follows:
     * 01 - 10 (Melter samples, to get to target alloy), 90 - 99 (Casting sample, to confirm the slab is ready to be
     * cast), 11-89 (Client samples, to provide the client with data regarding the alloy of the slab).
     *
     * @param sampleNumber1 First sample number as int
     * @param sampleNumber2 Second sample number as int
     * @return 0 if both numbers are equal, 1 if first sample number comes after second sample number by logical order,
     * -1 if first sample number comes before second sample number by logical order
     */
    public static int compareSampleNumbers( int sampleNumber1, int sampleNumber2 )
    {
        if ( sampleNumber1 < 90 )
        {
            if ( sampleNumber2 < 90 )
            {
                return Integer.compare( sampleNumber1, sampleNumber2 );
            }
            else
            {
                if ( sampleNumber1 < 11 )
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
        }
        else
        {
            if ( sampleNumber2 < 11 )
            {
                return 1;
            }
            else if ( sampleNumber2 < 90 )
            {
                return -1;
            }
            else
            {
                return Integer.compare( sampleNumber1, sampleNumber2 );
            }
        }
    }
}
