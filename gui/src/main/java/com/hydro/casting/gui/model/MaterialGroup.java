package com.hydro.casting.gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;
import java.util.Optional;

public class MaterialGroup extends NamedModelElement
{
    private static final long serialVersionUID = 1L;

    private StringProperty apk;
    private StringProperty deliveryCharge;
    private final ObservableList<Material> materials;

    public MaterialGroup()
    {
        apk = new SimpleStringProperty();
        deliveryCharge = new SimpleStringProperty();
        materials = FXCollections.observableArrayList();
    }

    public String getApk()
    {
        return apk.get();
    }

    public StringProperty apkProperty()
    {
        return apk;
    }

    public void setApk( String apk )
    {
        this.apk.set( apk );
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

    public ObservableList<Material> getMaterials()
    {
        return materials;
    }

    public void addMaterial( Material material )
    {
        this.materials.add( material );
    }

    public void removeMaterial( Material material )
    {
        this.materials.remove( material );
    }

    @Override
    public void invalidate()
    {
    }

    public Material findMaterial( String apk )
    {
        final Optional<Material> materialOptional = materials.stream().filter( material -> Objects.equals(material.getApk(), apk )).findFirst();
        if ( materialOptional.isPresent() )
        {
            return materialOptional.get();
        }
        return null;
    }
}
