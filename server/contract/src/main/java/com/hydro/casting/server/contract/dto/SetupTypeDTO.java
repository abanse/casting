package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

public class SetupTypeDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private String objid;
    private String name;
    private int duration;
    private String instruction;

    public String getObjid()
    {
        return objid;
    }

    public void setObjid( String objid )
    {
        this.objid = objid;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration( int duration )
    {
        this.duration = duration;
    }

    public String getInstruction()
    {
        return instruction;
    }

    public void setInstruction( String instruction )
    {
        this.instruction = instruction;
    }

    @Override
    public long getId()
    {
        if ( objid == null )
        {
            return 0;
        }
        return objid.hashCode();
    }

    /*
    @SuppressWarnings( "unchecked" )
    public String getDragboardString()
    {
        final StringBuilder dragBoardStringBuilder = new StringBuilder( "SETUP_TYPE:" );

        JSONObject jsonObject = new JSONObject();
        jsonObject.put( "objid", getObjid() );
        jsonObject.put( "name", getName() );
        jsonObject.put( "duration", getDuration() );
        jsonObject.put( "instruction", getInstruction() );

        dragBoardStringBuilder.append( jsonObject.toJSONString() );

        return dragBoardStringBuilder.toString();
    }

    public static boolean isSetupTypeContent( final String dragboardString )
    {
        if ( dragboardString == null )
        {
            return false;
        }
        return dragboardString.startsWith( "SETUP_TYPE:" );
    }

    public static SetupTypeDTO fromDragboardString( String dragboardString ) throws ParseException
    {
        if ( isSetupTypeContent( dragboardString ) == false )
        {
            return null;
        }
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse( dragboardString.substring( "SETUP_TYPE:".length() ) );

        final SetupTypeDTO setupTypeDTO = new SetupTypeDTO();
        setupTypeDTO.setObjid( (String) jsonObject.get( "objid" ) );
        setupTypeDTO.setName( (String) jsonObject.get( "name" ) );
        setupTypeDTO.setDuration( ( (Number) jsonObject.get( "duration" ) ).intValue() );
        setupTypeDTO.setInstruction( (String) jsonObject.get( "instruction" ) );

        return setupTypeDTO;
    }
     */
}
