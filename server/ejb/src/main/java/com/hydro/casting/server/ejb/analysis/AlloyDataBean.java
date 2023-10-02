package com.hydro.casting.server.ejb.analysis;

import com.hydro.casting.server.contract.analysis.AlloyData;
import com.hydro.casting.server.ejb.analysis.service.LimsAlloyService;
import com.hydro.casting.server.ejb.main.service.AlloyService;
import com.hydro.casting.server.model.mat.Alloy;
import com.hydro.casting.server.model.mat.AlloyElement;
import com.hydro.eai.lims.model.LimsAlloy;
import com.hydro.eai.lims.model.LimsAlloyElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class AlloyDataBean implements AlloyData
{
    private final static Logger log = LoggerFactory.getLogger( AlloyDataBean.class );

    @EJB
    private AlloyService alloyService;

    @EJB
    private LimsAlloyService limsAlloyService;

    @Override
    public String updateAllAlloysFromLIMS()
    {
        StringBuilder result = new StringBuilder();
        log.info( "Updating all MES alloys with data from LIMS..." );
        result.append( "Updating all MES alloys with data from LIMS. Updating active status, activation time, and element precision.\n" );

        List<Alloy> alloyList = alloyService.loadAllAlloys();

        log.info( "Processing " + alloyList.size() + " alloy objects." );
        result.append( "Processing " ).append( alloyList.size() ).append( " alloy objects.\n" );

        for ( Alloy alloy : alloyList )
        {
            result.append( "Processing alloy " ).append( alloy ).append( "\n" );
            LimsAlloy limsAlloy = limsAlloyService.findBySampleTypeId( alloy.getAlloyId() );

            if ( limsAlloy == null )
            {
                result.append( "No matching alloy found in LIMS -> dummy data.\n" );

                alloy.setActive( false );
                if ( alloy.getDeactivationTime() == null )
                {
                    alloy.setDeactivationTime( LocalDateTime.now() );
                }
                alloy.setDescription( "Dummy data" );
                continue;
            }

            result.append( "Updating with data from lims alloy: " ).append( limsAlloy ).append( "\n" );

            // Status F indicates active in LIMS, inactive is U
            alloy.setActive( limsAlloy.getStatus().equals( "F" ) );
            alloy.setActivationTime( limsAlloy.getEnteredOn() );

            // If alloy is not active, deactivation time has to be set to entered time of next version
            if ( !alloy.isActive() )
            {
                LimsAlloy nextLimsAlloyVersion = limsAlloyService.findByNameAndVersion( limsAlloy.getStName(), limsAlloy.getStVers() + 1 );

                if ( nextLimsAlloyVersion != null )
                {
                    alloy.setDeactivationTime( nextLimsAlloyVersion.getEnteredOn() );
                }
            }

            for ( AlloyElement alloyElement : alloy.getAlloyElements() )
            {
                // Name is unique among alloy elements, so there should be exactly one matching lims alloy element
                LimsAlloyElement limsAlloyElement = limsAlloy.getLimsAlloyElements().stream().filter( lae -> lae.getPaName().equals( alloyElement.getName() ) ).findFirst().orElse( null );

                if ( limsAlloyElement == null )
                {
                    continue;
                }

                alloyElement.setPrecision( limsAlloyElement.getDecPrecCust() != null ? limsAlloyElement.getDecPrecCust() : limsAlloyElement.getDecPrec() );
            }
        }

        log.info( "Finished updating." );
        result.append( "Finished updating.\n" );
        return result.toString();
    }
}
