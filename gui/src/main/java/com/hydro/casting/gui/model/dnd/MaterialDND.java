package com.hydro.casting.gui.model.dnd;

import javafx.scene.input.DragEvent;

public class MaterialDND
{
    public final static String IDENT = "Material:";

    private long objid;
    private double weight;
    private boolean createVariableMaterials;

    public MaterialDND( long objid, double weight, boolean createVariableMaterials )
    {
        this.objid = objid;
        this.weight = weight;
        this.createVariableMaterials = createVariableMaterials;
    }

    public MaterialDND( String dndContent )
    {
        if ( dndContent.contains( ";" ) )
        {
            String[] dndParts = dndContent.split( ";" );
            objid = Long.parseLong( dndParts[0].substring( IDENT.length() ) );
            for ( int i = 1; i < dndParts.length; i++ )
            {
                String dndPart = dndParts[i];

                if ( dndPart.startsWith( "weight=" ) )
                {
                    String[] parts = dndPart.split( "=" );
                    weight = Double.parseDouble( parts[1] );
                }
                if ( dndPart.startsWith( "createVariableMaterials=" ) )
                {
                    String[] parts = dndPart.split( "=" );
                    createVariableMaterials = Boolean.parseBoolean( parts[1] );
                }
            }
        }
        else
        {
            objid = Long.parseLong( dndContent.substring( IDENT.length() ) );
            weight = 0;
            createVariableMaterials = false;
        }
    }

    public long getObjid()
    {
        return objid;
    }

    public double getWeight()
    {
        return weight;
    }

    public boolean isCreateVariableMaterials()
    {
        return createVariableMaterials;
    }

    //    public Material getMaterial()
    //    {
    //        return MeltingPlan.getInstance().findMaterial( objid );
    //    }

    public static double calcTransferdWeight( DragEvent dragEvent )
    {
        String dragboardString = dragEvent.getDragboard().getString();
        if ( dragboardString != null && dragboardString.startsWith( MaterialDND.IDENT ) )
        {
            MaterialDND materialDND = new MaterialDND( dragboardString );
            if ( materialDND.getWeight() > 0 )
            {
                return materialDND.getWeight();
            }
        }
//        if ( dragboardString != null && ( dragboardString.startsWith( TransferBookmark.DND_IDENT ) || dragboardString.startsWith( Transfer.DND_IDENT ) ) )
//        {
//            return 0;
//        }
        return 1000;
    }

    @Override
    public String toString()
    {
        if ( weight > 0 )
        {
            return IDENT + objid + ";weight=" + weight + ";createVariableMaterials=" + createVariableMaterials;
        }
        else
        {
            return IDENT + objid;
        }
    }
}
