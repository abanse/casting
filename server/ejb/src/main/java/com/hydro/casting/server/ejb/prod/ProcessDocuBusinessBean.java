package com.hydro.casting.server.ejb.prod;

import com.hydro.casting.server.contract.dto.InspectionDTO;
import com.hydro.casting.server.contract.dto.InspectionValueDTO;
import com.hydro.casting.server.contract.prod.ProcessDocuBusiness;
import com.hydro.casting.server.model.inspection.*;
import com.hydro.casting.server.model.inspection.dao.InspectionRuleHome;
import com.hydro.casting.server.model.inspection.dao.InspectionValueHome;
import com.hydro.casting.server.model.sched.Schedulable;
import com.hydro.casting.server.model.sched.dao.SchedulableHome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class ProcessDocuBusinessBean implements ProcessDocuBusiness
{
    private final static Logger log = LoggerFactory.getLogger( ProcessDocuBusinessBean.class );

    @EJB
    private SchedulableHome schedulableHome;

    @EJB
    private InspectionRuleHome inspectionRuleHome;

    @EJB
    private InspectionValueHome inspectionValueHome;

    @Override
    public void saveInspection( String currentUser, InspectionDTO inspectionDTO )
    {
        if ( inspectionDTO == null )
        {
            log.warn( "inspectionDTO == null" );
            return;
        }
        final Schedulable schedulable = schedulableHome.findById( inspectionDTO.getCasterSchedule().getId() );
        if ( schedulable == null )
        {
            log.warn( "schedulable == null" );
            return;
        }
        final List<InspectionValueDTO> values = inspectionDTO.getValues();
        if ( values == null )
        {
            log.warn( "values == null" );
            return;
        }
        for ( InspectionValueDTO value : values )
        {
            final InspectionRule rule = inspectionRuleHome.findById( value.getRuleId() );
            if ( rule == null )
            {
                log.warn( "rule not found " + value );
                continue;
            }
            InspectionValue inspectionValue;
            if ( value.getId() > 0 )
            {
                inspectionValue = inspectionValueHome.findById( value.getId() );
                inspectionValue.setUpdTS( LocalDateTime.now() );
                inspectionValue.setUpdUser( currentUser );
            }
            else
            {
                if ( rule instanceof BitSetIR )
                {
                    inspectionValue = new BitSetIV();
                }
                else if ( rule instanceof CasterPositionsIR )
                {
                    inspectionValue = new CasterPositionsIV();
                }
                else if ( rule instanceof TextIR )
                {
                    inspectionValue = new TextIV();
                }
                else if ( rule instanceof VisualInspectionIR )
                {
                    inspectionValue = new VisualInspectionIV();
                }
                else
                {
                    log.warn( "rule instance not found " + rule );
                    continue;
                }
                inspectionValue.setAddTS( LocalDateTime.now() );
                inspectionValue.setAddUser( currentUser );
                inspectionValueHome.persist( inspectionValue );
            }
            inspectionValue.setInspectionRule( rule );
            inspectionValue.setSchedulable( schedulable );
            inspectionValue.setResult( value.getResult() );
            if ( inspectionValue instanceof BitSetIV )
            {
                ( (BitSetIV) inspectionValue ).setValue( value.getValue() );
                ( (BitSetIV) inspectionValue ).setRemark( value.getRemark() );
                ( (BitSetIV) inspectionValue ).setIntervention( value.getIntervention() );
                ( (BitSetIV) inspectionValue ).setAdditionalInfo( value.getAdditionalInfo() );
            }
            else if ( inspectionValue instanceof CasterPositionsIV )
            {
                final long[] cpValues = value.getCasterPositionValues();
                if ( cpValues != null && cpValues.length == 5 )
                {
                    ( (CasterPositionsIV) inspectionValue ).setPos1Value( cpValues[0] );
                    ( (CasterPositionsIV) inspectionValue ).setPos2Value( cpValues[1] );
                    ( (CasterPositionsIV) inspectionValue ).setPos3Value( cpValues[2] );
                    ( (CasterPositionsIV) inspectionValue ).setPos4Value( cpValues[3] );
                    ( (CasterPositionsIV) inspectionValue ).setPos5Value( cpValues[4] );
                }
            }
            else if ( inspectionValue instanceof TextIV )
            {
                ( (TextIV) inspectionValue ).setText( value.getText() );
            }
            else if ( inspectionValue instanceof VisualInspectionIV )
            {
                ( (VisualInspectionIV) inspectionValue ).setVisualInspectionValue( value.getVisualInspectionValue() );
                ( (VisualInspectionIV) inspectionValue ).setVisualInspectionIssue( value.getVisualInspectionIssue() );
                ( (VisualInspectionIV) inspectionValue ).setVisualInspectionUpdateUserNets( value.getVisualInspectionUpdateUserNets() );
                ( (VisualInspectionIV) inspectionValue ).setVisualInspectionUpdateTSNets( value.getVisualInspectionUpdateTSNets() );
                ( (VisualInspectionIV) inspectionValue ).setVisualInspectionUpdateUserCast( value.getVisualInspectionUpdateUserCast() );
                ( (VisualInspectionIV) inspectionValue ).setVisualInspectionUpdateTSCast( value.getVisualInspectionUpdateTSCast() );
            }
        }
    }
}
