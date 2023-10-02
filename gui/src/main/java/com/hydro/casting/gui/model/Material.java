package com.hydro.casting.gui.model;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;

public class Material extends NamedModelElement
{
    public final static NumberFormat WEIGHT_T_FORMATTER = new DecimalFormat( "0.0" );
    public final static NumberFormat WEIGHT_KG_FORMATTER = new DecimalFormat( "0" );
    public final static NumberFormat LENGTH_FORMATTER = new DecimalFormat( "0.0" );
    private static final long serialVersionUID = 1L;
    private final SimpleStringProperty apk;

    private final SimpleDoubleProperty weight;

    private final SimpleDoubleProperty originalWeight;

    private final SimpleDoubleProperty calcWeight;

    private final SimpleDoubleProperty calcCopperWeight;

    private final SimpleDoubleProperty unitWeight;

    private final SimpleObjectProperty<Analysis> analysis;

    private final SimpleObjectProperty<MaterialGroup> materialGroup;

    private final SimpleObjectProperty<Color> presentationColor;

    private final StringProperty deliveryCharge;

    private final ObjectProperty<LocalDateTime> generationSuccessTS;

    public Material()
    {
        apk = new SimpleStringProperty();
        weight = new SimpleDoubleProperty();
        originalWeight = new SimpleDoubleProperty();
        calcWeight = new SimpleDoubleProperty();
        calcCopperWeight = new SimpleDoubleProperty();
        unitWeight = new SimpleDoubleProperty( -1 );
        analysis = new SimpleObjectProperty<>();
        materialGroup = new SimpleObjectProperty<>();
        presentationColor = new SimpleObjectProperty<>();
        deliveryCharge = new SimpleStringProperty();
        generationSuccessTS = new SimpleObjectProperty<>();
    }

    public double getWeight()
    {
        return weight.get();
    }

    public void setWeight( double weight )
    {
        this.weight.set( weight );
    }

    public void addWeight( double weigtToAdd )
    {
        this.weight.set( weight.get() + weigtToAdd );
    }

    public void subtractWeight( double weightToSubtract )
    {
        this.weight.set( weight.get() - weightToSubtract );
    }

    public SimpleDoubleProperty weightProperty()
    {
        return weight;
    }

    public Analysis getAnalysis()
    {
        return analysis.get();
    }

    public void setAnalysis( Analysis analysis )
    {
        this.analysis.set( analysis );
    }

    public SimpleObjectProperty<Analysis> analysisProperty()
    {
        return analysis;
    }

    public MaterialGroup getMaterialGroup()
    {
        return materialGroup.get();
    }

    public void setMaterialGroup( MaterialGroup materialGroup )
    {
        if ( this.materialGroup.get() != null )
        {
            this.materialGroup.get().removeMaterial( this );
        }
        this.materialGroup.set( materialGroup );
        if ( materialGroup != null )
        {
            materialGroup.addMaterial( this );
        }
    }

    public Color getPresentationColor()
    {
        return presentationColor.get();
    }

    public void setPresentationColor( Color presentationColor )
    {
        this.presentationColor.set( presentationColor );
    }

    public SimpleObjectProperty<Color> presentationColorProperty()
    {
        return presentationColor;
    }

    public final SimpleDoubleProperty originalWeightProperty()
    {
        return this.originalWeight;
    }

    public final double getOriginalWeight()
    {
        return this.originalWeightProperty().get();
    }

    public final void setOriginalWeight( final double originalWeight )
    {
        this.originalWeightProperty().set( originalWeight );
    }

    public final SimpleDoubleProperty calcWeightProperty()
    {
        return this.calcWeight;
    }

    public final double getCalcWeight()
    {
        return this.calcWeightProperty().get();
    }

    public final void setCalcWeight( final double calcWeight )
    {
        this.calcWeightProperty().set( calcWeight );
    }

    public final SimpleDoubleProperty calcCopperWeightProperty()
    {
        return this.calcCopperWeight;
    }

    public final double getCalcCopperWeight()
    {
        return this.calcCopperWeightProperty().get();
    }

    public final void setCalcCopperWeight( final double calcCopperWeight )
    {
        this.calcCopperWeightProperty().set( calcCopperWeight );
    }

    public double getUnitWeight()
    {
        return unitWeight.get();
    }

    public SimpleDoubleProperty unitWeightProperty()
    {
        return unitWeight;
    }

    public void setUnitWeight( double unitWeight )
    {
        this.unitWeight.set( unitWeight );
    }

    public final SimpleStringProperty apkProperty()
    {
        return this.apk;
    }

    public final String getApk()
    {
        return this.apkProperty().get();
    }

    public final void setApk( final String apk )
    {
        this.apkProperty().set( apk );
    }

    public String getDeliveryCharge()
    {
        return deliveryCharge.get();
    }

    public StringProperty deliveryChargeProperty()
    {
        return deliveryCharge;
    }

    public void setDeliveryCharge( String deliveryCharge )
    {
        this.deliveryCharge.set( deliveryCharge );
    }

    public LocalDateTime getGenerationSuccessTS()
    {
        return generationSuccessTS.get();
    }

    public ObjectProperty<LocalDateTime> generationSuccessTSProperty()
    {
        return generationSuccessTS;
    }

    public void setGenerationSuccessTS( LocalDateTime generationSuccessTS )
    {
        this.generationSuccessTS.set( generationSuccessTS );
    }

    public int getCountAnalysisElements()
    {
        if ( getAnalysis() != null && getAnalysis().getCompositionElements() != null )
        {
            return getAnalysis().getCompositionElements().size();
        }
        return 0;
    }

    @Override
    public void invalidate()
    {
    }
}
