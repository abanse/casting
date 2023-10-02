package com.hydro.casting.server.ejb.main.service;

import com.hydro.casting.server.model.mat.Analysis;
import com.hydro.casting.server.model.mat.AnalysisElement;
import com.hydro.casting.server.model.mat.dao.AnalysisElementHome;
import com.hydro.casting.server.model.mat.dao.AnalysisHome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless
public class ElektrolyseService
{
    private static final Logger log = LoggerFactory.getLogger( ElektrolyseService.class );

    //@formatter:off
    private final static String ELY_AVG_QUERY =
              "select avg(KONZ_B) as B,\n"
            + "       avg(KONZ_BE) as BE,\n"
            + "       avg(KONZ_BI) as BI,\n"
            + "       avg(KONZ_CA) as CA,\n"
            + "       avg(KONZ_CD) as CD,\n"
            + "       avg(KONZ_CR) as CR,\n"
            + "       avg(KONZ_CU) as CU,\n"
            + "       avg(KONZ_FE) as FE,\n"
            + "       avg(KONZ_GA) as GA,\n"
            + "       avg(KONZ_LI) as LI,\n"
            + "       avg(KONZ_MG) as MG,\n"
            + "       avg(KONZ_MN) as MN,\n"
            + "       avg(KONZ_NA) as NA,\n"
            + "       avg(KONZ_NI) as NI,\n"
            + "       avg(KONZ_P) as P,\n"
            + "       avg(KONZ_PB) as PB,\n"
            + "       avg(KONZ_SB) as SB,\n"
            + "       avg(KONZ_SI) as SI,\n"
            + "       avg(KONZ_SN) as SN,\n"
            + "       avg(KONZ_SR) as SR,\n"
            + "       avg(KONZ_TI) as TI,\n"
            + "       avg(KONZ_V) as V,\n"
            + "       avg(KONZ_ZN) as ZN,\n"
            + "       avg(KONZ_ZR) as ZR\n"
            + "from V_RAWMETAL_ELEKTROLYSE\n"
            + "where HALLE = :area";
    //@formatter:on

    @EJB
    private AnalysisHome analysisHome;

    @EJB
    private AnalysisElementHome analysisElementHome;

    @PersistenceContext( unitName = "lims" )
    private EntityManager entityManager;

    public Analysis loadAverageAnalyse( String name, String area )
    {
        final Analysis analysis = new Analysis();
        analysis.setName( name );

        final Query query = entityManager.createNativeQuery( ELY_AVG_QUERY, Tuple.class );
        query.setParameter( "area", area );

        final List<Tuple> rows = query.getResultList();
        for ( Tuple row : rows )
        {
            for ( TupleElement<?> element : row.getElements() )
            {
                final String elementName = element.getAlias();
                Number value = (Number) row.get( element );
                if ( value != null )
                {
                    final AnalysisElement analysisElement = new AnalysisElement();
                    analysisElement.setName( elementName.substring( 0, 1 ).toUpperCase() + elementName.substring( 1 ).toLowerCase() );
                    analysisElement.setStandardValue( value.doubleValue() );
                    analysisElementHome.persist( analysisElement );
                    analysis.addToAnalysisElements( analysisElement );
                }
            }
        }

        analysisHome.persist( analysis );
        return analysis;
    }
}
