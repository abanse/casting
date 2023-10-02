package com.hydro.casting.gui.planning.view.content.vo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

public class MoldChange
{
    private String caster;
    private LocalDateTime neededTS;
    private Double[] posWidths;

    public String getCaster()
    {
        return caster;
    }

    public void setCaster( String caster )
    {
        this.caster = caster;
    }

    public LocalDateTime getNeededTS()
    {
        return neededTS;
    }

    public void setNeededTS( LocalDateTime neededTS )
    {
        this.neededTS = neededTS;
    }

    public Double[] getPosWidths()
    {
        return posWidths;
    }

    public void setPosWidths( Double[] posWidths )
    {
        this.posWidths = posWidths;
    }

    public boolean posWidthsEquals( Double[] posWidths )
    {
        return Objects.deepEquals( posWidths, this.posWidths );
    }

    @Override
    public String toString()
    {
        return "MoldChange{" + "caster='" + caster + '\'' + ", neededTS=" + neededTS + ", posWidths=" + Arrays.toString( posWidths ) + '}';
    }
}
