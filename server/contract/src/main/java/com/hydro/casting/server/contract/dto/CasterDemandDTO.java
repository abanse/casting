package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class CasterDemandDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private String combinedKey;
    private String orderId;
    private String orderPosition;
    private String purchaseOrder;
    private String customerId;
    private String customerName;
    private String materialType;
    private String materialTypeDescription;
    private String alloy;
    private String quality;
    private int height;
    private int width;
    private int length;
    private Integer doubleLength;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDateFrom;
    private LocalDateTime deliveryDateTo;
    private int amount;
    private int planned;
    private int retrieved;
    private int delivered;
    private String experimentNumber;

    @Override
    public long getId()
    {
        if ( combinedKey == null )
        {
            return 0;
        }
        return combinedKey.hashCode();
    }

    public String getCombinedKey()
    {
        return combinedKey;
    }

    public void setCombinedKey( String combinedKey )
    {
        this.combinedKey = combinedKey;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId( String orderId )
    {
        this.orderId = orderId;
    }

    public String getOrderPosition()
    {
        return orderPosition;
    }

    public void setOrderPosition( String orderPosition )
    {
        this.orderPosition = orderPosition;
    }

    public String getPurchaseOrder()
    {
        return purchaseOrder;
    }

    public void setPurchaseOrder( String purchaseOrder )
    {
        this.purchaseOrder = purchaseOrder;
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId( String customerId )
    {
        this.customerId = customerId;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName( String customerName )
    {
        this.customerName = customerName;
    }

    public String getMaterialType()
    {
        return materialType;
    }

    public void setMaterialType( String materialType )
    {
        this.materialType = materialType;
    }

    public String getMaterialTypeDescription()
    {
        return materialTypeDescription;
    }

    public void setMaterialTypeDescription( String materialTypeDescription )
    {
        this.materialTypeDescription = materialTypeDescription;
    }

    public String getAlloy()
    {
        return alloy;
    }

    public void setAlloy( String alloy )
    {
        this.alloy = alloy;
    }

    public String getQuality()
    {
        return quality;
    }

    public void setQuality( String quality )
    {
        this.quality = quality;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight( int height )
    {
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth( int width )
    {
        this.width = width;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength( int length )
    {
        this.length = length;
    }

    public Integer getDoubleLength()
    {
        return doubleLength;
    }

    public void setDoubleLength( Integer doubleLength )
    {
        this.doubleLength = doubleLength;
    }

    public LocalDateTime getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate( LocalDateTime orderDate )
    {
        this.orderDate = orderDate;
    }

    public LocalDateTime getDeliveryDateFrom()
    {
        return deliveryDateFrom;
    }

    public void setDeliveryDateFrom( LocalDateTime deliveryDateFrom )
    {
        this.deliveryDateFrom = deliveryDateFrom;
    }

    public LocalDateTime getDeliveryDateTo()
    {
        return deliveryDateTo;
    }

    public void setDeliveryDateTo( LocalDateTime deliveryDateTo )
    {
        this.deliveryDateTo = deliveryDateTo;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount( int amount )
    {
        this.amount = amount;
    }

    public int getPlanned()
    {
        return planned;
    }

    public void setPlanned( int planned )
    {
        this.planned = planned;
    }

    public int getRetrieved()
    {
        return retrieved;
    }

    public void setRetrieved( int retrieved )
    {
        this.retrieved = retrieved;
    }

    public int getDelivered()
    {
        return delivered;
    }

    public void setDelivered( int delivered )
    {
        this.delivered = delivered;
    }

    public String getExperimentNumber()
    {
        return experimentNumber;
    }

    public void setExperimentNumber( String experimentNumber )
    {
        this.experimentNumber = experimentNumber;
    }

    public int getToProduce()
    {
        return amount - delivered;
    }

    public void setToProduce( int toProduce )
    {
        // Nothing to do, calc field
    }

    /*
    public int getToSchedule()
    {
        return amount - delivered - planned;
    }

    public void setToSchedule( int toSchedule )
    {
        // Nothing to do, calc field
    }
     */

    public String getRepresentation()
    {
        final StringBuilder representationSB = new StringBuilder();
        if ( getOrderId() != null )
        {
            if ( getOrderId().length() > 5 )
            {
                representationSB.append( getOrderId().substring( 5 ) );
            }
            else
            {
                representationSB.append( getOrderId() );
            }
        }
        if ( getOrderPosition() != null )
        {
            if ( getOrderPosition().length() > 4 )
            {
                representationSB.append( "-" + getOrderPosition().substring( 4 ) );
            }
            else
            {
                representationSB.append( "-" + getOrderPosition() );
            }
        }
        if ( getCustomerName() != null )
        {
            representationSB.append( " " + getCustomerName() );
        }
        return representationSB.toString();
    }

    public void setRepresentation( String representation )
    {
        // Nothing to do, calc field
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        CasterDemandDTO demandDTO = (CasterDemandDTO) o;
        return height == demandDTO.height && width == demandDTO.width && length == demandDTO.length && amount == demandDTO.amount && planned == demandDTO.planned && retrieved == demandDTO.retrieved
                && delivered == demandDTO.delivered && Objects.equals( combinedKey, demandDTO.combinedKey ) && Objects.equals( orderId, demandDTO.orderId ) && Objects
                .equals( orderPosition, demandDTO.orderPosition ) && Objects.equals( purchaseOrder, demandDTO.purchaseOrder ) && Objects.equals( customerId, demandDTO.customerId ) && Objects
                .equals( customerName, demandDTO.customerName ) && Objects.equals( materialType, demandDTO.materialType ) && Objects
                .equals( materialTypeDescription, demandDTO.materialTypeDescription ) && Objects.equals( alloy, demandDTO.alloy ) && Objects.equals( quality, demandDTO.quality ) && Objects
                .equals( doubleLength, demandDTO.doubleLength ) && Objects.equals( orderDate, demandDTO.orderDate ) && Objects.equals( deliveryDateFrom, demandDTO.deliveryDateFrom ) && Objects
                .equals( deliveryDateTo, demandDTO.deliveryDateTo ) && Objects.equals( experimentNumber, demandDTO.experimentNumber );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( combinedKey, orderId, orderPosition, purchaseOrder, customerId, customerName, materialType, materialTypeDescription, alloy, quality, height, width, length, doubleLength,
                orderDate, deliveryDateFrom, deliveryDateTo, amount, planned, retrieved, delivered, experimentNumber );
    }

    @Override
    public String toString()
    {
        return "CasterDemandDTO{" + "combinedKey='" + combinedKey + '\'' + ", orderId='" + orderId + '\'' + ", orderPosition='" + orderPosition + '\'' + ", purchaseOrder='" + purchaseOrder + '\''
                + ", customerId='" + customerId + '\'' + ", customerName='" + customerName + '\'' + ", materialType='" + materialType + '\'' + ", materialTypeDescription='" + materialTypeDescription
                + '\'' + ", alloy='" + alloy + '\'' + ", quality='" + quality + '\'' + ", height=" + height + ", width=" + width + ", length=" + length + ", doubleLength=" + doubleLength
                + ", orderDate=" + orderDate + ", deliveryDateFrom=" + deliveryDateFrom + ", deliveryDateTo=" + deliveryDateTo + ", amount=" + amount + ", planned=" + planned + ", retrieved="
                + retrieved + ", delivered=" + delivered + ", experimentNumber='" + experimentNumber + '\'' + '}';
    }
}