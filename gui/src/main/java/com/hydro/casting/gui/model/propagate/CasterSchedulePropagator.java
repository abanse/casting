package com.hydro.casting.gui.model.propagate;

import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.model.*;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.MachineCalendarDTO;
import com.hydro.casting.server.contract.dto.MachineDTO;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.core.common.util.StringTools;
import javafx.util.Callback;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CasterSchedulePropagator implements Callback<Collection<CasterScheduleDTO>, Object>
{
    private CastingClientModel model;

    public CasterSchedulePropagator( CastingClientModel model )
    {
        this.model = model;
    }

    @Override
    public Void call( Collection<CasterScheduleDTO> casterScheduleDTOs )
    {
        // Replicate ResourceCalendarEntries
        replicateResourceCalendarEntries( Casting.ALL_CASTERS, Casting.ALL_MELTING_FURNACES );

        Arrays.stream( Casting.ALL_CASTERS ).forEach( machine -> calculateCharge( casterScheduleDTOs, machine ) );

        // remove deleted entries
        final List<Long> casterScheduleIDs = casterScheduleDTOs.stream().map( CasterScheduleDTO::getId ).collect( Collectors.toList() );
        for ( String casterName : Casting.ALL_CASTERS )
        {
            final Caster caster = model.getEntity( Caster.class, casterName );
            final List<Batch> batches = caster.getBatches();
            for ( Batch batch : new ArrayList<>( batches ) )
            {
                if ( !casterScheduleIDs.contains( batch.getObjid() ) )
                {
                    caster.removeBatch( batch );
                }
            }
            caster.invalidate();
        }

        model.refreshCaster();
        return null;
    }

    private void calculateCharge( Collection<CasterScheduleDTO> casterSchedules, String machine )
    {
        final List<CasterScheduleDTO> sortedList = casterSchedules.stream().filter( casterScheduleDTO -> Objects.equals( casterScheduleDTO.getMachine(), machine ) )
                .sorted( Comparator.comparing( CasterScheduleDTO::getExecutingSequenceIndex ) ).collect( Collectors.toList() );

        String lastCharge = null;
        for ( CasterScheduleDTO casterScheduleDTO : sortedList )
        {
            if ( casterScheduleDTO.getCharge() != null )
            {
                if ( lastCharge == null )
                {
                    lastCharge = casterScheduleDTO.getCharge();
                    continue;
                }
                try
                {
                    final int lastChargeInt = StringTools.N4F.parse( lastCharge.substring( 3, 7 ) ).intValue();
                    final int currentChargeInt = StringTools.N4F.parse( casterScheduleDTO.getCharge().substring( 3, 7 ) ).intValue();
                    if ( currentChargeInt > lastChargeInt )
                    {
                        lastCharge = casterScheduleDTO.getCharge();
                    }
                    else if ( lastChargeInt > 9000 && currentChargeInt < 1000 )
                    {
                        lastCharge = casterScheduleDTO.getCharge();
                    }
                }
                catch ( ParseException e )
                {
                    throw new RuntimeException( e );
                }
            }
        }
        String nextCharge = null;
        if ( lastCharge == null )
        {
            MachineDTO casterMachineDTO = model.getEntity( MachineDTO.class, machine );
            if ( casterMachineDTO != null )
            {
                final StringBuilder chargeSB = new StringBuilder();
                chargeSB.append( StringTools.YEARDF.format( new Date() ) );
                chargeSB.append( casterMachineDTO.getApk().charAt( 0 ) );
                chargeSB.append( StringTools.N4F.format( casterMachineDTO.getLastCharge() ) );

                nextCharge = chargeSB.toString();
            }
            else
            {
                nextCharge = Casting.getNextCharge( machine, null );
            }
        }
        else
        {
            nextCharge = Casting.getNextCharge( machine, lastCharge );
        }

        CasterScheduleDTO lastCasterScheduleDTO = null;
        String lastUsedMeltingFurnace = null;
        for ( CasterScheduleDTO casterScheduleDTO : sortedList )
        {
            if ( lastCasterScheduleDTO == null )
            {
                casterScheduleDTO.setInProgressTS( LocalDateTime.now() );
            }
            if ( "setup".equals( casterScheduleDTO.getType() ) )
            {
                casterScheduleDTO.setPrevious( lastCasterScheduleDTO );
                if ( lastCasterScheduleDTO != null )
                {
                    lastCasterScheduleDTO.setNext( casterScheduleDTO );
                }
                lastCasterScheduleDTO = casterScheduleDTO;
                continue;
            }
            if ( casterScheduleDTO.getCharge() == null )
            {
                casterScheduleDTO.setCharge( nextCharge + "?" );
                nextCharge = Casting.getNextCharge( machine, nextCharge );
            }
            if ( casterScheduleDTO.getMeltingFurnace() == null )
            {
                casterScheduleDTO.setMeltingFurnace( Casting.getOppositeMeltingFurnace( casterScheduleDTO.getMachine(), lastUsedMeltingFurnace ) );
            }
            lastUsedMeltingFurnace = casterScheduleDTO.getMeltingFurnace();
            casterScheduleDTO.setPrevious( lastCasterScheduleDTO );
            if ( lastCasterScheduleDTO != null )
            {
                lastCasterScheduleDTO.setNext( casterScheduleDTO );
            }
            replicateCaster( casterScheduleDTO );
            lastCasterScheduleDTO = casterScheduleDTO;
        }
    }

    private void replicateResourceCalendarEntries( String[] casters, String[] meltingFurnaces )
    {
        final List<Resource> resources = new ArrayList<>();
        for ( String casterName : casters )
        {
            resources.add( model.getEntity( Caster.class, casterName ) );
        }
        for ( String meltingFurnaceName : meltingFurnaces )
        {
            resources.add( model.getEntity( Furnace.class, meltingFurnaceName ) );
        }
        for ( Resource resource : resources )
        {
            final Collection<MachineCalendarDTO> machineCalendarDTOs = model.getMachineCalendarMachineIndex().get( resource.getName() );
            for ( MachineCalendarDTO machineCalendarDTO : machineCalendarDTOs )
            {
                ResourceCalendarEntry resourceCalendarEntry = null;
                for ( ResourceCalendarEntry rce : resource.getResourceCalendarEntries() )
                {
                    if ( rce.getObjid() == machineCalendarDTO.getId() )
                    {
                        resourceCalendarEntry = rce;
                        break;
                    }
                }
                if ( resourceCalendarEntry == null )
                {
                    resourceCalendarEntry = new ResourceCalendarEntry();
                    resourceCalendarEntry.setObjid( machineCalendarDTO.getId() );
                    resource.addResourceCalendarEntry( resourceCalendarEntry );
                }
                resourceCalendarEntry.setName( resource.getName() + " " + machineCalendarDTO.getStartTS() );
                resourceCalendarEntry.setStartTS( machineCalendarDTO.getStartTS() );
                resourceCalendarEntry.setDuration( machineCalendarDTO.getDuration() );
                resourceCalendarEntry.setDescription( machineCalendarDTO.getDescription() );
            }
            for ( ResourceCalendarEntry resourceCalendarEntry : new ArrayList<>( resource.getResourceCalendarEntries() ) )
            {
                final long objid = resourceCalendarEntry.getObjid();
                boolean found = false;
                for ( MachineCalendarDTO machineCalendarDTO : machineCalendarDTOs )
                {
                    if ( machineCalendarDTO.getId() == objid )
                    {
                        found = true;
                        break;
                    }
                }
                if ( !found )
                {
                    resource.removeResourceCalendarEntry( resourceCalendarEntry );
                }
            }
        }
    }

    private void replicateCaster( CasterScheduleDTO casterScheduleDTO )
    {
        // Replicate Caster
        final Caster caster = model.getEntity( Caster.class, casterScheduleDTO.getMachine() );
        Batch batch = caster.findBatch( casterScheduleDTO.getId() );
        if ( batch == null )
        {
            batch = new Batch();
            batch.setObjid( casterScheduleDTO.getId() );
            caster.addBatch( batch );
        }
        if ( casterScheduleDTO.getCharge() != null && casterScheduleDTO.getCharge().length() > 2 )
        {
            batch.setName( casterScheduleDTO.getCharge().substring( 2 ) );
        }
        batch.setDetail( casterScheduleDTO.getAlloy() + " " + StringTools.N02F.format( casterScheduleDTO.getPlannedWeight() / 1000.0 ) + "t Net. " + StringTools.N02F.format(
                casterScheduleDTO.getNetWeight() / 1000.0 ) + "t" );
        batch.setAlloy( casterScheduleDTO.getAlloy() );
        batch.setCastingLength( casterScheduleDTO.getPlannedLength() );
        batch.setCastingWeight( casterScheduleDTO.getPlannedWeight() );
        batch.setFurnaceTargetWeight( Casting.getFurnaceWeight( casterScheduleDTO.getMeltingFurnace() ) );
        Double[] posWidths;
        if ( Casting.MACHINE.CASTER_80.equals( caster.getName() ) )
        {
            posWidths = new Double[5];
        }
        else
        {
            posWidths = new Double[4];
        }
        posWidths[0] = casterScheduleDTO.getPos1Width();
        posWidths[1] = casterScheduleDTO.getPos2Width();
        posWidths[2] = casterScheduleDTO.getPos3Width();
        posWidths[3] = casterScheduleDTO.getPos4Width();
        if ( posWidths.length > 4 )
        {
            posWidths[4] = casterScheduleDTO.getPos5Width();
        }
        batch.setPosWidths( posWidths );
        final List<Transfer> transfers = batch.getTransfers();
        if ( transfers.isEmpty() )
        {
            final Transfer transfer = new Transfer();
            transfer.setName( batch.getName() );
            // set From
            final Furnace furnace = model.getEntity( Furnace.class, casterScheduleDTO.getMeltingFurnace() );
            if ( caster.getFurnaces().contains( furnace ) == false )
            {
                caster.addFurnace( furnace );
            }
            transfer.setFrom( furnace );
            batch.addTransfer( Collections.singletonList( transfer ), 0, false );
        }
        final Transfer transfer = batch.getTransfers().get( 0 );
        transfer.setName( batch.getName() );
        transfer.setTargetWeight( batch.getFurnaceTargetWeight() );
        transfer.setWeight( batch.getCastingWeight() );

        // Replication TransferMaterials
        final Collection<MaterialDTO> batchMaterials = model.getPlannedConsumedMaterialScheduleIndex().get( batch.getObjid() );
        for ( MaterialDTO batchMaterial : batchMaterials )
        {
            final long objid = batchMaterial.getId();
            boolean found = false;
            for ( TransferMaterial transferMaterial : transfer.getTransferMaterials() )
            {
                if ( transferMaterial.getObjid() == objid )
                {
                    transferMaterial.setType( batchMaterial.getSource() );
                    transferMaterial.setWeight( batchMaterial.getWeight() );
                    found = true;
                    break;
                }
            }
            if ( found )
            {
                continue;
            }
            final TransferMaterial transferMaterial = new TransferMaterial();
            transferMaterial.setObjid( objid );
            transferMaterial.setType( batchMaterial.getSource() );
            transferMaterial.addWeight( batchMaterial.getWeight() );
            transfer.addTransferMaterial( transferMaterial );
        }
        // gelöschte noch entfernen
        for ( TransferMaterial transferMaterial : new ArrayList<>( transfer.getTransferMaterials() ) )
        {
            final long objid = transferMaterial.getObjid();
            boolean found = false;
            for ( MaterialDTO batchMaterial : batchMaterials )
            {
                if ( batchMaterial.getId() == objid )
                {
                    found = true;
                    break;
                }
            }
            if ( !found )
            {
                transfer.removeTransferMaterial( transferMaterial );
            }
        }
    }
}
