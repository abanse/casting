package com.hydro.casting.server.model.av;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Table( name = "actual_values" )
public class ActualValues extends BaseEntity
{
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value01" )
    private Blob value01;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value02" )
    private Blob value02;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value03" )
    private Blob value03;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value04" )
    private Blob value04;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value05" )
    private Blob value05;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value06" )
    private Blob value06;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value07" )
    private Blob value07;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value08" )
    private Blob value08;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value09" )
    private Blob value09;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value10" )
    private Blob value10;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value11" )
    private Blob value11;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value12" )
    private Blob value12;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value13" )
    private Blob value13;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value14" )
    private Blob value14;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value15" )
    private Blob value15;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value16" )
    private Blob value16;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value17" )
    private Blob value17;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value18" )
    private Blob value18;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value19" )
    private Blob value19;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value20" )
    private Blob value20;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value21" )
    private Blob value21;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value22" )
    private Blob value22;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value23" )
    private Blob value23;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value24" )
    private Blob value24;
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value25" )
    private Blob value25;

    public Blob getValue01()
    {
        return value01;
    }

    public void setValue01( Blob value01 )
    {
        this.value01 = value01;
    }

    public Blob getValue02()
    {
        return value02;
    }

    public void setValue02( Blob value02 )
    {
        this.value02 = value02;
    }

    public Blob getValue03()
    {
        return value03;
    }

    public void setValue03( Blob value03 )
    {
        this.value03 = value03;
    }

    public Blob getValue04()
    {
        return value04;
    }

    public void setValue04( Blob value04 )
    {
        this.value04 = value04;
    }

    public Blob getValue05()
    {
        return value05;
    }

    public void setValue05( Blob value05 )
    {
        this.value05 = value05;
    }

    public Blob getValue06()
    {
        return value06;
    }

    public void setValue06( Blob value06 )
    {
        this.value06 = value06;
    }

    public Blob getValue07()
    {
        return value07;
    }

    public void setValue07( Blob value07 )
    {
        this.value07 = value07;
    }

    public Blob getValue08()
    {
        return value08;
    }

    public void setValue08( Blob value08 )
    {
        this.value08 = value08;
    }

    public Blob getValue09()
    {
        return value09;
    }

    public void setValue09( Blob value09 )
    {
        this.value09 = value09;
    }

    public Blob getValue10()
    {
        return value10;
    }

    public void setValue10( Blob value10 )
    {
        this.value10 = value10;
    }

    public Blob getValue11()
    {
        return value11;
    }

    public void setValue11( Blob value11 )
    {
        this.value11 = value11;
    }

    public Blob getValue12()
    {
        return value12;
    }

    public void setValue12( Blob value12 )
    {
        this.value12 = value12;
    }

    public Blob getValue13()
    {
        return value13;
    }

    public void setValue13( Blob value13 )
    {
        this.value13 = value13;
    }

    public Blob getValue14()
    {
        return value14;
    }

    public void setValue14( Blob value14 )
    {
        this.value14 = value14;
    }

    public Blob getValue15()
    {
        return value15;
    }

    public void setValue15( Blob value15 )
    {
        this.value15 = value15;
    }

    public Blob getValue16()
    {
        return value16;
    }

    public void setValue16( Blob value16 )
    {
        this.value16 = value16;
    }

    public Blob getValue17()
    {
        return value17;
    }

    public void setValue17( Blob value17 )
    {
        this.value17 = value17;
    }

    public Blob getValue18()
    {
        return value18;
    }

    public void setValue18( Blob value18 )
    {
        this.value18 = value18;
    }

    public Blob getValue19()
    {
        return value19;
    }

    public void setValue19( Blob value19 )
    {
        this.value19 = value19;
    }

    public Blob getValue20()
    {
        return value20;
    }

    public void setValue20( Blob value20 )
    {
        this.value20 = value20;
    }

    public Blob getValue21()
    {
        return value21;
    }

    public void setValue21( Blob value21 )
    {
        this.value21 = value21;
    }

    public Blob getValue22()
    {
        return value22;
    }

    public void setValue22( Blob value22 )
    {
        this.value22 = value22;
    }

    public Blob getValue23()
    {
        return value23;
    }

    public void setValue23( Blob value23 )
    {
        this.value23 = value23;
    }

    public Blob getValue24()
    {
        return value24;
    }

    public void setValue24( Blob value24 )
    {
        this.value24 = value24;
    }

    public Blob getValue25()
    {
        return value25;
    }

    public void setValue25( Blob value25 )
    {
        this.value25 = value25;
    }

    //======================================================================================

    public void setValue( int pos, Blob value ) throws IndexOutOfBoundsException
    {
        if ( pos <= 0 )
        {
            throw new IndexOutOfBoundsException( "pos <= 0 not allowed" );
        }
        switch ( pos )
        {
        case 1:
            setValue01( value );
            break;
        case 2:
            setValue02( value );
            break;
        case 3:
            setValue03( value );
            break;
        case 4:
            setValue04( value );
            break;
        case 5:
            setValue05( value );
            break;
        case 6:
            setValue06( value );
            break;
        case 7:
            setValue07( value );
            break;
        case 8:
            setValue08( value );
            break;
        case 9:
            setValue09( value );
            break;
        case 10:
            setValue10( value );
            break;
        case 11:
            setValue11( value );
            break;
        case 12:
            setValue12( value );
            break;
        case 13:
            setValue13( value );
            break;
        case 14:
            setValue14( value );
            break;
        case 15:
            setValue15( value );
            break;
        case 16:
            setValue16( value );
            break;
        case 17:
            setValue17( value );
            break;
        case 18:
            setValue18( value );
            break;
        case 19:
            setValue19( value );
            break;
        case 20:
            setValue20( value );
            break;
        case 21:
            setValue21( value );
            break;
        case 22:
            setValue22( value );
            break;
        case 23:
            setValue23( value );
            break;
        case 24:
            setValue24( value );
            break;
        case 25:
            setValue25( value );
            break;
        default:
            throw new IndexOutOfBoundsException( "pos not valid" );
        }
    }

    public Blob getValue( int pos ) throws IndexOutOfBoundsException
    {
        if ( pos <= 0 )
        {
            throw new IndexOutOfBoundsException( "pos <= 0 not allowed" );
        }
        switch ( pos )
        {
        case 1:
            return getValue01();
        case 2:
            return getValue02();
        case 3:
            return getValue03();
        case 4:
            return getValue04();
        case 5:
            return getValue05();
        case 6:
            return getValue06();
        case 7:
            return getValue07();
        case 8:
            return getValue08();
        case 9:
            return getValue09();
        case 10:
            return getValue10();
        case 11:
            return getValue11();
        case 12:
            return getValue12();
        case 13:
            return getValue13();
        case 14:
            return getValue14();
        case 15:
            return getValue15();
        case 16:
            return getValue16();
        case 17:
            return getValue17();
        case 18:
            return getValue18();
        case 19:
            return getValue19();
        case 20:
            return getValue20();
        case 21:
            return getValue21();
        case 22:
            return getValue22();
        case 23:
            return getValue23();
        case 24:
            return getValue24();
        case 25:
            return getValue25();

        default:
            throw new IndexOutOfBoundsException( "pos not valid" );
        }
    }
}
