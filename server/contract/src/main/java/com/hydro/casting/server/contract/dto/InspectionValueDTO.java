package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;

public class InspectionValueDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    public final static int RESULT_OK = 0;
    public final static int RESULT_OK_WITH_LIMITATIONS = 1;
    public final static int RESULT_FAILED = 2;
    public final static int RESULT_NOT_FILLED = 3;

    private long id;
    private long ruleId;
    private int result;
    private long value;
    private String remark;
    private String intervention;
    private String additionalInfo;
    private long[] casterPositionValues;
    private String text;
    private int visualInspectionValue;
    private String visualInspectionIssue;
    private String visualInspectionUpdateUserNets;
    private LocalDateTime visualInspectionUpdateTSNets;
    private String visualInspectionUpdateUserCast;
    private LocalDateTime visualInspectionUpdateTSCast;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public long getRuleId()
    {
        return ruleId;
    }

    public void setRuleId( long ruleId )
    {
        this.ruleId = ruleId;
    }

    public int getResult()
    {
        return result;
    }

    public void setResult( int result )
    {
        this.result = result;
    }

    public long getValue()
    {
        return value;
    }

    public void setValue( long value )
    {
        this.value = value;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark( String remark )
    {
        this.remark = remark;
    }

    public String getIntervention()
    {
        return intervention;
    }

    public void setIntervention( String intervention )
    {
        this.intervention = intervention;
    }

    public String getAdditionalInfo()
    {
        return additionalInfo;
    }

    public void setAdditionalInfo( String additionalInfo )
    {
        this.additionalInfo = additionalInfo;
    }

    public long[] getCasterPositionValues()
    {
        return casterPositionValues;
    }

    public void setCasterPositionValues( long[] casterPositionValues )
    {
        this.casterPositionValues = casterPositionValues;
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    public int getVisualInspectionValue()
    {
        return visualInspectionValue;
    }

    public void setVisualInspectionValue( int visualInspectionValue )
    {
        this.visualInspectionValue = visualInspectionValue;
    }

    public String getVisualInspectionIssue()
    {
        return visualInspectionIssue;
    }

    public void setVisualInspectionIssue( String visualInspectionIssue )
    {
        this.visualInspectionIssue = visualInspectionIssue;
    }

    public String getVisualInspectionUpdateUserNets()
    {
        return visualInspectionUpdateUserNets;
    }

    public void setVisualInspectionUpdateUserNets( String visualInspectionUpdateUserNets )
    {
        this.visualInspectionUpdateUserNets = visualInspectionUpdateUserNets;
    }

    public LocalDateTime getVisualInspectionUpdateTSNets()
    {
        return visualInspectionUpdateTSNets;
    }

    public void setVisualInspectionUpdateTSNets( LocalDateTime visualInspectionUpdateTSNets )
    {
        this.visualInspectionUpdateTSNets = visualInspectionUpdateTSNets;
    }

    public String getVisualInspectionUpdateUserCast()
    {
        return visualInspectionUpdateUserCast;
    }

    public void setVisualInspectionUpdateUserCast( String visualInspectionUpdateUserCast )
    {
        this.visualInspectionUpdateUserCast = visualInspectionUpdateUserCast;
    }

    public LocalDateTime getVisualInspectionUpdateTSCast()
    {
        return visualInspectionUpdateTSCast;
    }

    public void setVisualInspectionUpdateTSCast( LocalDateTime visualInspectionUpdateTSCast )
    {
        this.visualInspectionUpdateTSCast = visualInspectionUpdateTSCast;
    }
}
