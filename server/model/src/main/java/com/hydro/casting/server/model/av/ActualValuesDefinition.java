package com.hydro.casting.server.model.av;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table( name = "actual_values_definition" )
@NamedQuery( name = "actualValuesDefinition.lastDefinition", query = "select avd from ActualValuesDefinition avd where avd.name = :name order by avd.version desc" )
public class ActualValuesDefinition extends BaseEntity
{
    @Column( name = "name", length = 30 )
    private String name;
    @Column( name = "version" )
    private int version;
    @Column( name = "value_01_name", length = 50 )
    private String value01Name;
    @Column( name = "value_01_type", length = 20 )
    private String value01Type;
    @Column( name = "value_02_name", length = 50 )
    private String value02Name;
    @Column( name = "value_02_type", length = 20 )
    private String value02Type;
    @Column( name = "value_03_name", length = 50 )
    private String value03Name;
    @Column( name = "value_03_type", length = 20 )
    private String value03Type;
    @Column( name = "value_04_name", length = 50 )
    private String value04Name;
    @Column( name = "value_04_type", length = 20 )
    private String value04Type;
    @Column( name = "value_05_name", length = 50 )
    private String value05Name;
    @Column( name = "value_05_type", length = 20 )
    private String value05Type;
    @Column( name = "value_06_name", length = 50 )
    private String value06Name;
    @Column( name = "value_06_type", length = 20 )
    private String value06Type;
    @Column( name = "value_07_name", length = 50 )
    private String value07Name;
    @Column( name = "value_07_type", length = 20 )
    private String value07Type;
    @Column( name = "value_08_name", length = 50 )
    private String value08Name;
    @Column( name = "value_08_type", length = 20 )
    private String value08Type;
    @Column( name = "value_09_name", length = 50 )
    private String value09Name;
    @Column( name = "value_09_type", length = 20 )
    private String value09Type;
    @Column( name = "value_10_name", length = 50 )
    private String value10Name;
    @Column( name = "value_10_type", length = 20 )
    private String value10Type;
    @Column( name = "value_11_name", length = 50 )
    private String value11Name;
    @Column( name = "value_11_type", length = 20 )
    private String value11Type;
    @Column( name = "value_12_name", length = 50 )
    private String value12Name;
    @Column( name = "value_12_type", length = 20 )
    private String value12Type;
    @Column( name = "value_13_name", length = 50 )
    private String value13Name;
    @Column( name = "value_13_type", length = 20 )
    private String value13Type;
    @Column( name = "value_14_name", length = 50 )
    private String value14Name;
    @Column( name = "value_14_type", length = 20 )
    private String value14Type;
    @Column( name = "value_15_name", length = 50 )
    private String value15Name;
    @Column( name = "value_15_type", length = 20 )
    private String value15Type;
    @Column( name = "value_16_name", length = 50 )
    private String value16Name;
    @Column( name = "value_16_type", length = 20 )
    private String value16Type;
    @Column( name = "value_17_name", length = 50 )
    private String value17Name;
    @Column( name = "value_17_type", length = 20 )
    private String value17Type;
    @Column( name = "value_18_name", length = 50 )
    private String value18Name;
    @Column( name = "value_18_type", length = 20 )
    private String value18Type;
    @Column( name = "value_19_name", length = 50 )
    private String value19Name;
    @Column( name = "value_19_type", length = 20 )
    private String value19Type;
    @Column( name = "value_20_name", length = 50 )
    private String value20Name;
    @Column( name = "value_20_type", length = 20 )
    private String value20Type;
    @Column( name = "value_21_name", length = 50 )
    private String value21Name;
    @Column( name = "value_21_type", length = 20 )
    private String value21Type;
    @Column( name = "value_22_name", length = 50 )
    private String value22Name;
    @Column( name = "value_22_type", length = 20 )
    private String value22Type;
    @Column( name = "value_23_name", length = 50 )
    private String value23Name;
    @Column( name = "value_23_type", length = 20 )
    private String value23Type;
    @Column( name = "value_24_name", length = 50 )
    private String value24Name;
    @Column( name = "value_24_type", length = 20 )
    private String value24Type;
    @Column( name = "value_25_name", length = 50 )
    private String value25Name;
    @Column( name = "value_25_type", length = 20 )
    private String value25Type;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion( int version )
    {
        this.version = version;
    }

    public String getValue01Name()
    {
        return value01Name;
    }

    public void setValue01Name( String value01Name )
    {
        this.value01Name = value01Name;
    }

    public String getValue01Type()
    {
        return value01Type;
    }

    public void setValue01Type( String value01Type )
    {
        this.value01Type = value01Type;
    }

    public String getValue02Name()
    {
        return value02Name;
    }

    public void setValue02Name( String value02Name )
    {
        this.value02Name = value02Name;
    }

    public String getValue02Type()
    {
        return value02Type;
    }

    public void setValue02Type( String value02Type )
    {
        this.value02Type = value02Type;
    }

    public String getValue03Name()
    {
        return value03Name;
    }

    public void setValue03Name( String value03Name )
    {
        this.value03Name = value03Name;
    }

    public String getValue03Type()
    {
        return value03Type;
    }

    public void setValue03Type( String value03Type )
    {
        this.value03Type = value03Type;
    }

    public String getValue04Name()
    {
        return value04Name;
    }

    public void setValue04Name( String value04Name )
    {
        this.value04Name = value04Name;
    }

    public String getValue04Type()
    {
        return value04Type;
    }

    public void setValue04Type( String value04Type )
    {
        this.value04Type = value04Type;
    }

    public String getValue05Name()
    {
        return value05Name;
    }

    public void setValue05Name( String value05Name )
    {
        this.value05Name = value05Name;
    }

    public String getValue05Type()
    {
        return value05Type;
    }

    public void setValue05Type( String value05Type )
    {
        this.value05Type = value05Type;
    }

    public String getValue06Name()
    {
        return value06Name;
    }

    public void setValue06Name( String value06Name )
    {
        this.value06Name = value06Name;
    }

    public String getValue06Type()
    {
        return value06Type;
    }

    public void setValue06Type( String value06Type )
    {
        this.value06Type = value06Type;
    }

    public String getValue07Name()
    {
        return value07Name;
    }

    public void setValue07Name( String value07Name )
    {
        this.value07Name = value07Name;
    }

    public String getValue07Type()
    {
        return value07Type;
    }

    public void setValue07Type( String value07Type )
    {
        this.value07Type = value07Type;
    }

    public String getValue08Name()
    {
        return value08Name;
    }

    public void setValue08Name( String value08Name )
    {
        this.value08Name = value08Name;
    }

    public String getValue08Type()
    {
        return value08Type;
    }

    public void setValue08Type( String value08Type )
    {
        this.value08Type = value08Type;
    }

    public String getValue09Name()
    {
        return value09Name;
    }

    public void setValue09Name( String value09Name )
    {
        this.value09Name = value09Name;
    }

    public String getValue09Type()
    {
        return value09Type;
    }

    public void setValue09Type( String value09Type )
    {
        this.value09Type = value09Type;
    }

    public String getValue10Name()
    {
        return value10Name;
    }

    public void setValue10Name( String value10Name )
    {
        this.value10Name = value10Name;
    }

    public String getValue10Type()
    {
        return value10Type;
    }

    public void setValue10Type( String value10Type )
    {
        this.value10Type = value10Type;
    }

    public String getValue11Name()
    {
        return value11Name;
    }

    public void setValue11Name( String value11Name )
    {
        this.value11Name = value11Name;
    }

    public String getValue11Type()
    {
        return value11Type;
    }

    public void setValue11Type( String value11Type )
    {
        this.value11Type = value11Type;
    }

    public String getValue12Name()
    {
        return value12Name;
    }

    public void setValue12Name( String value12Name )
    {
        this.value12Name = value12Name;
    }

    public String getValue12Type()
    {
        return value12Type;
    }

    public void setValue12Type( String value12Type )
    {
        this.value12Type = value12Type;
    }

    public String getValue13Name()
    {
        return value13Name;
    }

    public void setValue13Name( String value13Name )
    {
        this.value13Name = value13Name;
    }

    public String getValue13Type()
    {
        return value13Type;
    }

    public void setValue13Type( String value13Type )
    {
        this.value13Type = value13Type;
    }

    public String getValue14Name()
    {
        return value14Name;
    }

    public void setValue14Name( String value14Name )
    {
        this.value14Name = value14Name;
    }

    public String getValue14Type()
    {
        return value14Type;
    }

    public void setValue14Type( String value14Type )
    {
        this.value14Type = value14Type;
    }

    public String getValue15Name()
    {
        return value15Name;
    }

    public void setValue15Name( String value15Name )
    {
        this.value15Name = value15Name;
    }

    public String getValue15Type()
    {
        return value15Type;
    }

    public void setValue15Type( String value15Type )
    {
        this.value15Type = value15Type;
    }

    public String getValue16Name()
    {
        return value16Name;
    }

    public void setValue16Name( String value16Name )
    {
        this.value16Name = value16Name;
    }

    public String getValue16Type()
    {
        return value16Type;
    }

    public void setValue16Type( String value16Type )
    {
        this.value16Type = value16Type;
    }

    public String getValue17Name()
    {
        return value17Name;
    }

    public void setValue17Name( String value17Name )
    {
        this.value17Name = value17Name;
    }

    public String getValue17Type()
    {
        return value17Type;
    }

    public void setValue17Type( String value17Type )
    {
        this.value17Type = value17Type;
    }

    public String getValue18Name()
    {
        return value18Name;
    }

    public void setValue18Name( String value18Name )
    {
        this.value18Name = value18Name;
    }

    public String getValue18Type()
    {
        return value18Type;
    }

    public void setValue18Type( String value18Type )
    {
        this.value18Type = value18Type;
    }

    public String getValue19Name()
    {
        return value19Name;
    }

    public void setValue19Name( String value19Name )
    {
        this.value19Name = value19Name;
    }

    public String getValue19Type()
    {
        return value19Type;
    }

    public void setValue19Type( String value19Type )
    {
        this.value19Type = value19Type;
    }

    public String getValue20Name()
    {
        return value20Name;
    }

    public void setValue20Name( String value20Name )
    {
        this.value20Name = value20Name;
    }

    public String getValue20Type()
    {
        return value20Type;
    }

    public void setValue20Type( String value20Type )
    {
        this.value20Type = value20Type;
    }

    public String getValue21Name()
    {
        return value21Name;
    }

    public void setValue21Name( String value21Name )
    {
        this.value21Name = value21Name;
    }

    public String getValue21Type()
    {
        return value21Type;
    }

    public void setValue21Type( String value21Type )
    {
        this.value21Type = value21Type;
    }

    public String getValue22Name()
    {
        return value22Name;
    }

    public void setValue22Name( String value22Name )
    {
        this.value22Name = value22Name;
    }

    public String getValue22Type()
    {
        return value22Type;
    }

    public void setValue22Type( String value22Type )
    {
        this.value22Type = value22Type;
    }

    public String getValue23Name()
    {
        return value23Name;
    }

    public void setValue23Name( String value23Name )
    {
        this.value23Name = value23Name;
    }

    public String getValue23Type()
    {
        return value23Type;
    }

    public void setValue23Type( String value23Type )
    {
        this.value23Type = value23Type;
    }

    public String getValue24Name()
    {
        return value24Name;
    }

    public void setValue24Name( String value24Name )
    {
        this.value24Name = value24Name;
    }

    public String getValue24Type()
    {
        return value24Type;
    }

    public void setValue24Type( String value24Type )
    {
        this.value24Type = value24Type;
    }

    public String getValue25Name()
    {
        return value25Name;
    }

    public void setValue25Name( String value25Name )
    {
        this.value25Name = value25Name;
    }

    public String getValue25Type()
    {
        return value25Type;
    }

    public void setValue25Type( String value25Type )
    {
        this.value25Type = value25Type;
    }

    //======================================================================================

    public void setValueName( int pos, String valueName ) throws IndexOutOfBoundsException
    {
        if ( pos <= 0 )
        {
            throw new IndexOutOfBoundsException( "pos <= 0 not allowed" );
        }
        switch ( pos )
        {
        case 1:
            setValue01Name( valueName );
            break;
        case 2:
            setValue02Name( valueName );
            break;
        case 3:
            setValue03Name( valueName );
            break;
        case 4:
            setValue04Name( valueName );
            break;
        case 5:
            setValue05Name( valueName );
            break;
        case 6:
            setValue06Name( valueName );
            break;
        case 7:
            setValue07Name( valueName );
            break;
        case 8:
            setValue08Name( valueName );
            break;
        case 9:
            setValue09Name( valueName );
            break;
        case 10:
            setValue10Name( valueName );
            break;
        case 11:
            setValue11Name( valueName );
            break;
        case 12:
            setValue12Name( valueName );
            break;
        case 13:
            setValue13Name( valueName );
            break;
        case 14:
            setValue14Name( valueName );
            break;
        case 15:
            setValue15Name( valueName );
            break;
        case 16:
            setValue16Name( valueName );
            break;
        case 17:
            setValue17Name( valueName );
            break;
        case 18:
            setValue18Name( valueName );
            break;
        case 19:
            setValue19Name( valueName );
            break;
        case 20:
            setValue20Name( valueName );
            break;
        case 21:
            setValue21Name( valueName );
            break;
        case 22:
            setValue22Name( valueName );
            break;
        case 23:
            setValue23Name( valueName );
            break;
        case 24:
            setValue24Name( valueName );
            break;
        case 25:
            setValue25Name( valueName );
            break;
        default:
            throw new IndexOutOfBoundsException( "pos not valid" );
        }
    }

    public void setValueType( int pos, String valueType ) throws IndexOutOfBoundsException
    {
        if ( pos <= 0 )
        {
            throw new IndexOutOfBoundsException( "pos <= 0 not allowed" );
        }
        switch ( pos )
        {
        case 1:
            setValue01Type( valueType );
            break;
        case 2:
            setValue02Type( valueType );
            break;
        case 3:
            setValue03Type( valueType );
            break;
        case 4:
            setValue04Type( valueType );
            break;
        case 5:
            setValue05Type( valueType );
            break;
        case 6:
            setValue06Type( valueType );
            break;
        case 7:
            setValue07Type( valueType );
            break;
        case 8:
            setValue08Type( valueType );
            break;
        case 9:
            setValue09Type( valueType );
            break;
        case 10:
            setValue10Type( valueType );
            break;
        case 11:
            setValue11Type( valueType );
            break;
        case 12:
            setValue12Type( valueType );
            break;
        case 13:
            setValue13Type( valueType );
            break;
        case 14:
            setValue14Type( valueType );
            break;
        case 15:
            setValue15Type( valueType );
            break;
        case 16:
            setValue16Type( valueType );
            break;
        case 17:
            setValue17Type( valueType );
            break;
        case 18:
            setValue18Type( valueType );
            break;
        case 19:
            setValue19Type( valueType );
            break;
        case 20:
            setValue20Type( valueType );
        case 21:
            setValue21Type( valueType );
        case 22:
            setValue22Type( valueType );
        case 23:
            setValue23Type( valueType );
        case 24:
            setValue24Type( valueType );
        case 25:
            setValue25Type( valueType );
            break;
        default:
            throw new IndexOutOfBoundsException( "pos not valid" );
        }
    }

    public String getValueName( int pos ) throws IndexOutOfBoundsException
    {
        if ( pos <= 0 )
        {
            throw new IndexOutOfBoundsException( "pos <= 0 not allowed" );
        }
        switch ( pos )
        {
        case 1:
            return getValue01Name();
        case 2:
            return getValue02Name();
        case 3:
            return getValue03Name();
        case 4:
            return getValue04Name();
        case 5:
            return getValue05Name();
        case 6:
            return getValue06Name();
        case 7:
            return getValue07Name();
        case 8:
            return getValue08Name();
        case 9:
            return getValue09Name();
        case 10:
            return getValue10Name();
        case 11:
            return getValue11Name();
        case 12:
            return getValue12Name();
        case 13:
            return getValue13Name();
        case 14:
            return getValue14Name();
        case 15:
            return getValue15Name();
        case 16:
            return getValue16Name();
        case 17:
            return getValue17Name();
        case 18:
            return getValue18Name();
        case 19:
            return getValue19Name();
        case 20:
            return getValue20Name();
        case 21:
            return getValue21Name();
        case 22:
            return getValue22Name();
        case 23:
            return getValue23Name();
        case 24:
            return getValue24Name();
        case 25:
            return getValue25Name();
        default:
            throw new IndexOutOfBoundsException( "pos not valid" );
        }
    }

    public String getValueType( int pos ) throws IndexOutOfBoundsException
    {
        if ( pos <= 0 )
        {
            throw new IndexOutOfBoundsException( "pos <= 0 not allowed" );
        }
        switch ( pos )
        {
        case 1:
            return getValue01Type();
        case 2:
            return getValue02Type();
        case 3:
            return getValue03Type();
        case 4:
            return getValue04Type();
        case 5:
            return getValue05Type();
        case 6:
            return getValue06Type();
        case 7:
            return getValue07Type();
        case 8:
            return getValue08Type();
        case 9:
            return getValue09Type();
        case 10:
            return getValue10Type();
        case 11:
            return getValue11Type();
        case 12:
            return getValue12Type();
        case 13:
            return getValue13Type();
        case 14:
            return getValue14Type();
        case 15:
            return getValue15Type();
        case 16:
            return getValue16Type();
        case 17:
            return getValue17Type();
        case 18:
            return getValue18Type();
        case 19:
            return getValue19Type();
        case 20:
            return getValue20Type();
        case 21:
            return getValue21Type();
        case 22:
            return getValue22Type();
        case 23:
            return getValue23Type();
        case 24:
            return getValue24Type();
        case 25:
            return getValue25Type();
        default:
            throw new IndexOutOfBoundsException( "pos not valid" );
        }
    }

    public int getPos( String valueName )
    {
        if ( valueName == null )
        {
            return -1;
        }
        if ( Objects.equals( valueName, getValue01Name() ) )
        {
            return 1;
        }
        if ( Objects.equals( valueName, getValue02Name() ) )
        {
            return 2;
        }
        if ( Objects.equals( valueName, getValue03Name() ) )
        {
            return 3;
        }
        if ( Objects.equals( valueName, getValue04Name() ) )
        {
            return 4;
        }
        if ( Objects.equals( valueName, getValue05Name() ) )
        {
            return 5;
        }
        if ( Objects.equals( valueName, getValue06Name() ) )
        {
            return 6;
        }
        if ( Objects.equals( valueName, getValue07Name() ) )
        {
            return 7;
        }
        if ( Objects.equals( valueName, getValue08Name() ) )
        {
            return 8;
        }
        if ( Objects.equals( valueName, getValue09Name() ) )
        {
            return 9;
        }
        if ( Objects.equals( valueName, getValue10Name() ) )
        {
            return 10;
        }
        if ( Objects.equals( valueName, getValue11Name() ) )
        {
            return 11;
        }
        if ( Objects.equals( valueName, getValue12Name() ) )
        {
            return 12;
        }
        if ( Objects.equals( valueName, getValue13Name() ) )
        {
            return 13;
        }
        if ( Objects.equals( valueName, getValue14Name() ) )
        {
            return 14;
        }
        if ( Objects.equals( valueName, getValue15Name() ) )
        {
            return 15;
        }
        if ( Objects.equals( valueName, getValue16Name() ) )
        {
            return 16;
        }
        if ( Objects.equals( valueName, getValue17Name() ) )
        {
            return 17;
        }
        if ( Objects.equals( valueName, getValue18Name() ) )
        {
            return 18;
        }
        if ( Objects.equals( valueName, getValue19Name() ) )
        {
            return 19;
        }
        if ( Objects.equals( valueName, getValue20Name() ) )
        {
            return 20;
        }
        if ( Objects.equals( valueName, getValue21Name() ) )
        {
            return 21;
        }
        if ( Objects.equals( valueName, getValue22Name() ) )
        {
            return 22;
        }
        if ( Objects.equals( valueName, getValue23Name() ) )
        {
            return 23;
        }
        if ( Objects.equals( valueName, getValue24Name() ) )
        {
            return 24;
        }
        if ( Objects.equals( valueName, getValue25Name() ) )
        {
            return 25;
        }
        return -1;
    }
}
