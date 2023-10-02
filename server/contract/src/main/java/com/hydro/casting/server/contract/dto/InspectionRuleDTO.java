package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

public class InspectionRuleDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private String type;
    private String description;
    private long targetValue;
    private long initialValue;
    private String interventionDescription;
    private String additionalInfoDescription;
    private String subDescription;
    private int filledResult;
    private int emptyResult;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public long getTargetValue()
    {
        return targetValue;
    }

    public void setTargetValue( long targetValue )
    {
        this.targetValue = targetValue;
    }

    public long getInitialValue()
    {
        return initialValue;
    }

    public void setInitialValue( long initialValue )
    {
        this.initialValue = initialValue;
    }

    public String getInterventionDescription()
    {
        return interventionDescription;
    }

    public void setInterventionDescription( String interventionDescription )
    {
        this.interventionDescription = interventionDescription;
    }

    public String getAdditionalInfoDescription()
    {
        return additionalInfoDescription;
    }

    public void setAdditionalInfoDescription( String additionalInfoDescription )
    {
        this.additionalInfoDescription = additionalInfoDescription;
    }

    public String getSubDescription()
    {
        return subDescription;
    }

    public void setSubDescription( String subDescription )
    {
        this.subDescription = subDescription;
    }

    public int getFilledResult()
    {
        return filledResult;
    }

    public void setFilledResult( int filledResult )
    {
        this.filledResult = filledResult;
    }

    public int getEmptyResult()
    {
        return emptyResult;
    }

    public void setEmptyResult( int emptyResult )
    {
        this.emptyResult = emptyResult;
    }
}
