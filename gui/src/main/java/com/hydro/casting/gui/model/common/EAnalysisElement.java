package com.hydro.casting.gui.model.common;

public enum EAnalysisElement
{
    Cu, ZN, Si, SN, CR, P, Pb, NI, Fe, Mn, CO, Zr, Mg, Ti, AG, AL, Cd, V, Na, Ca,

    AL_ZN, CU_AG, CU_SN_AG, FE_ZN, CU_SN, SN_PB, CU_NI_ZN, MG_P_TI1, MN_CO_ZR, FP, NISI, SI_PB_X, SI_SN_X, SMAX, SMIN, SVER, SI_FE_sum, FE_MN_sum, SI_FE, PB_CD_sum, CU_MN_MG_sum, MN_SI, V_TI_sum, ZR_TI_sum,

    TYPE_S( "S" ), TYPE_V( "V" ), TYPE_N( "N" ),

    LFGUSS;

    private static EAnalysisElement[] SMAX_RELEVANT = { TYPE_S, TYPE_V, TYPE_N };

    private static EAnalysisElement[] CALCULATED_ELEMENTS = { AL_ZN, CU_AG, CU_SN_AG, FE_ZN, CU_SN, SN_PB, CU_NI_ZN, MG_P_TI1, MN_CO_ZR, FP, NISI, SI_PB_X, SI_SN_X, SMIN, SMAX, SVER, SI_FE_sum,
            FE_MN_sum, SI_FE, PB_CD_sum, CU_MN_MG_sum, MN_SI, V_TI_sum, ZR_TI_sum };

    private static EAnalysisElement[] PPM_ELEMENTS = {Ti, Ca, Na};

    /*
        Das ist legierungsabhängig, für die 3104 „Familie“ sind das Si, Fe, Cu, Mn und Mg. Bitte alle – inklusive Kupfer – in %.
        Außerdem sollen immer auch Ti, Ca und Na angezeigt werden, bitte alle in ppm.
     */
    public static EAnalysisElement[] STANDARD_ELEMENTS = {Si, Fe, Cu, Mn, Mg, Ti, Ca, Na};

    private final String type;

    private EAnalysisElement()
    {
        this.type = null;
    }

    private EAnalysisElement( final String type )
    {
        this.type = type;
    }

    public static boolean isSMAXRelevant( String type )
    {
        // Bedeuted es gibt keine Vorgabe in der Spec, deshalb muss es dazu
        // gezählt werden
        if ( type == null )
        {
            return true;
        }
        EAnalysisElement[] relevants = SMAX_RELEVANT;
        for ( EAnalysisElement types : relevants )
        {
            if ( type.equals( types.getType() ) )
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isCalculatedElement( String elementName )
    {
        if ( elementName != null )
        {
            EAnalysisElement[] relevants = CALCULATED_ELEMENTS;
            for ( EAnalysisElement element : relevants )
            {
                if ( elementName.equalsIgnoreCase( element.name() ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPPMElement( String elementName )
    {
        if ( elementName != null )
        {
            EAnalysisElement[] relevants = PPM_ELEMENTS;
            for ( EAnalysisElement element : relevants )
            {
                if ( elementName.equalsIgnoreCase( element.name() ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public String getType()
    {
        return type;
    }
}
