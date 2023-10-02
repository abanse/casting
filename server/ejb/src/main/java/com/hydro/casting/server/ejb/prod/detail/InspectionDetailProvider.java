package com.hydro.casting.server.ejb.prod.detail;

import com.hydro.casting.server.contract.dto.InspectionDTO;
import com.hydro.casting.server.contract.dto.InspectionRuleDTO;
import com.hydro.casting.server.contract.dto.InspectionValueDTO;
import com.hydro.casting.server.ejb.planning.service.CasterScheduleService;
import com.hydro.casting.server.model.inspection.*;
import com.hydro.casting.server.model.inspection.dao.InspectionValueHome;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.dao.CastingBatchHome;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class InspectionDetailProvider<M extends ViewDTO, D extends InspectionDTO>
{
    @EJB
    private CastingBatchHome castingBatchHome;

    @EJB
    private CasterScheduleService casterScheduleService;

    @EJB
    private InspectionValueHome inspectionValueHome;

    public D loadInspectionDetails( M master, D dto, InspectionCategory inspectionCategory, Map<String, String> context )
    {
        CastingBatch castingBatch = null;
        if ( master != null )
        {
            castingBatch = castingBatchHome.findById( master.getId() );
            if ( castingBatch == null )
            {
                return null;
            }
            dto.setCasterSchedule( casterScheduleService.load( castingBatch ) );
        }

        if ( inspectionCategory != null && inspectionCategory.numberOfInspectionRuless() > 0 )
        {
            final List<InspectionRule> rules = new ArrayList<>( inspectionCategory.getInspectionRules() );
            rules.sort( Comparator.comparing( InspectionRule::getPos ) );
            final ArrayList<InspectionRuleDTO> ruleDTOs = new ArrayList<>();
            for ( InspectionRule rule : rules )
            {
                final InspectionRuleDTO ruleDTO = new InspectionRuleDTO();
                ruleDTO.setId( rule.getObjid() );
                ruleDTO.setType( rule.getType() );
                ruleDTO.setName( rule.getName() );
                ruleDTO.setDescription( rule.getDescription() );
                if ( rule instanceof BitSetIR )
                {
                    final BitSetIR bitSetIR = (BitSetIR) rule;
                    ruleDTO.setTargetValue( bitSetIR.getTargetValue() );
                    ruleDTO.setInitialValue( bitSetIR.getInitialValue() );
                    ruleDTO.setInterventionDescription( bitSetIR.getInterventionDescription() );
                    ruleDTO.setAdditionalInfoDescription( bitSetIR.getAdditionalInfoDescription() );
                }
                else if ( rule instanceof TextIR )
                {
                    final TextIR textIR = (TextIR) rule;
                    ruleDTO.setSubDescription( textIR.getSubDescription() );
                    ruleDTO.setEmptyResult( textIR.getEmptyResult() );
                    ruleDTO.setFilledResult( textIR.getFilledResult() );
                }

                ruleDTOs.add( ruleDTO );
            }
            dto.setRules( ruleDTOs );

            // add values
            final List<InspectionValueDTO> valueDTOs = new ArrayList<>();
            final List<InspectionValue> values = inspectionValueHome.findBySchedulableAndCategory( castingBatch, inspectionCategory );
            for ( InspectionValue value : values )
            {
                final InspectionValueDTO valueDTO = new InspectionValueDTO();
                valueDTO.setId( value.getObjid() );
                valueDTO.setRuleId( value.getInspectionRule().getObjid() );
                valueDTO.setResult( value.getResult() );
                if ( value instanceof BitSetIV )
                {
                    valueDTO.setValue( ( (BitSetIV) value ).getValue() );
                    valueDTO.setRemark( ( (BitSetIV) value ).getRemark() );
                    valueDTO.setIntervention( ( (BitSetIV) value ).getIntervention() );
                    valueDTO.setAdditionalInfo( ( (BitSetIV) value ).getAdditionalInfo() );
                }
                else if ( value instanceof CasterPositionsIV )
                {
                    final long[] posValues = new long[5];
                    posValues[0] = ( (CasterPositionsIV) value ).getPos1Value();
                    posValues[1] = ( (CasterPositionsIV) value ).getPos2Value();
                    posValues[2] = ( (CasterPositionsIV) value ).getPos3Value();
                    posValues[3] = ( (CasterPositionsIV) value ).getPos4Value();
                    posValues[4] = ( (CasterPositionsIV) value ).getPos5Value();
                    valueDTO.setCasterPositionValues( posValues );
                }
                else if ( value instanceof TextIV )
                {
                    valueDTO.setText( ( (TextIV) value ).getText() );
                }
                else if ( value instanceof VisualInspectionIV )
                {
                    valueDTO.setVisualInspectionValue( ( (VisualInspectionIV) value ).getVisualInspectionValue() );
                    valueDTO.setVisualInspectionIssue( ( (VisualInspectionIV) value ).getVisualInspectionIssue() );
                    valueDTO.setVisualInspectionUpdateUserNets( ( (VisualInspectionIV) value ).getVisualInspectionUpdateUserNets() );
                    valueDTO.setVisualInspectionUpdateTSNets( ( (VisualInspectionIV) value ).getVisualInspectionUpdateTSNets() );
                    valueDTO.setVisualInspectionUpdateUserCast( ( (VisualInspectionIV) value ).getVisualInspectionUpdateUserCast() );
                    valueDTO.setVisualInspectionUpdateTSCast( ( (VisualInspectionIV) value ).getVisualInspectionUpdateTSCast() );
                }
                valueDTOs.add( valueDTO );
            }

            dto.setValues( valueDTOs );
        }

        return dto;
    }
}
