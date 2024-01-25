package com.hydro.casting.common;

public interface SecurityCasting
{


    interface GANTT
    {
        interface CASTER
        {
            String VIEW = "casting.gantt.caster.view";
        }

        interface FURNACE
        {
            String VIEW = "casting.gantt.furnace.view";
        }
    }

    interface MABE
    {
        interface CASTER_50
        {
            String VIEW = "casting.mabe.caster-50.view";
        }

        interface CASTER_60
        {
            String VIEW = "casting.mabe.caster-60.view";
        }

        interface CASTER_70
        {
            String VIEW = "casting.mabe.caster-70.view";
        }

        interface CASTER_80
        {
            String VIEW = "casting.mabe.caster-80.view";
        }

        interface MELTING_S1
        {
            String VIEW = "casting.mabe.melting-S1.view";
        }

        interface MELTING_S2
        {
            String VIEW = "casting.mabe.melting-S2.view";
        }

        interface DETAILS
        {
            interface MABE_INFO
            {
                String VIEW = "casting.mabe.detail.mabe-info.view";
            }
        }

        interface ACTION
        {
            String CREATE_ENTRY = "casting.mabe.action.create-entry";
            String EDIT_ENTRY = "casting.mabe.action.edit-entry";
            String RELEASE_ENTRY = "casting.mabe.action.release-entry";
            String REORGANIZE_CHARGE = "casting.mabe.action.reorganize-charge";
        }
    }

    interface CHARGING
    {
        interface CASTER_50
        {
            String VIEW = "casting.charging.caster-50.view";
        }

        interface CASTER_60
        {
            String VIEW = "casting.charging.caster-60.view";
        }

        interface CASTER_70
        {
            String VIEW = "casting.charging.caster-70.view";
        }

        interface CASTER_80
        {
            String VIEW = "casting.charging.caster-80.view";
        }
    }

    interface MELTING
    {
        interface DETAILS
        {
            interface ACTIVATE_CHARGE
            {
                String VIEW = "casting.melting.detail.activate-charge.view";
            }
        }

        interface ACTION
        {
            String ACTIVATE_CHARGE = "casting.melting.action.activate-charge";
            String DEACTIVATE_CHARGE = "casting.melting.action.deactivate-charge";
            String CHARGING = "casting.melting.action.charging";
            String MELTING = "casting.melting.action.melting";
            String SKIMMING = "casting.melting.action.skimming";
            String END_SKIMMING = "casting.melting.action.end-skimming";
            String SKIMMING_MELTING_CHAMBER = "casting.melting.action.skimming-melting-chamber";
            String END_SKIMMING_MELTING_CHAMBER = "casting.melting.action.end-skimming-melting-chamber";
            String TREATING = "casting.melting.action.treating";
            String TREATING_SKIMMING_ONLY = "casting.melting.action.treating-skimming-only";
            String HEATING = "casting.melting.action.heating";
            String POURING = "casting.melting.action.pouring";
            String END_POURING = "casting.melting.action.end-pouring";
            String DREDGING = "casting.melting.action.dredging";
            String MIXING = "casting.melting.action.mixing";
            String END_MIXING = "casting.melting.action.end-mixing";
            String CREATE_NEW_MELTING_CHARGE = "casting.melting.action.create-new-melting-charge";
            String CONFIGURE_NEW_MELTING_CHARGE = "casting.melting.action.configure-new-melting-charge";
            String FINISH_CHARGE = "casting.melting.action.finish-charge";
            String ACTIVATE_NEXT_MELTING_CHARGE = "casting.melting.action.activate-next-melting-charge";
        }

        interface KNOWLEDGE
        {
            String VIEW = "melting.knowledge.view";
        }

        interface GANTT_S1
        {
            String VIEW = "melting.gantt-s1.view";
        }

        interface GANTT_S2
        {
            String VIEW = "melting.gantt-s2.view";
        }

        interface MOBILE
        {
            interface SKIMMING
            {
                String VIEW = "melting.mobile.skimming.view";
            }
        }

        interface PROCESS_DOCU
        {
            String VIEW = "melting.process-docu.view";
        }

        interface CHARGES_S2
        {
            String VIEW = "melting.charges-s2.view";
        }
    }

    interface PROD
    {
        interface MOBILE
        {
            interface SKIMMING
            {
                String VIEW = "casting.prod.mobile.skimming.view";
            }
        }

        interface MELTER_S1
        {
            String VIEW = "casting.prod.melter-s1.view";
        }

        interface MELTER_S2
        {
            String VIEW = "casting.prod.melter-s2.view";
        }

        interface CASTER_50
        {
            String VIEW = "casting.prod.caster-50.view";
        }

        interface FURNACE_51
        {
            String VIEW = "casting.prod.furnace-51.view";
        }

        interface FURNACE_52
        {
            String VIEW = "casting.prod.furnace-52.view";
        }

        interface CHARGING_51
        {
            String VIEW = "casting.prod.charging-51.view";
        }

        interface CHARGING_52
        {
            String VIEW = "casting.prod.charging-52.view";
        }

        interface CASTER_60
        {
            String VIEW = "casting.prod.caster-60.view";
        }

        interface FURNACE_61
        {
            String VIEW = "casting.prod.furnace-61.view";
        }

        interface FURNACE_62
        {
            String VIEW = "casting.prod.furnace-62.view";
        }

        interface CHARGING_61
        {
            String VIEW = "casting.prod.charging-61.view";
        }

        interface CHARGING_62
        {
            String VIEW = "casting.prod.charging-62.view";
        }

        interface CASTER_70
        {
            String VIEW = "casting.prod.caster-70.view";
        }

        interface FURNACE_71
        {
            String VIEW = "casting.prod.furnace-71.view";
        }

        interface FURNACE_72
        {
            String VIEW = "casting.prod.furnace-72.view";
        }

        interface CHARGING_71
        {
            String VIEW = "casting.prod.charging-71.view";
        }

        interface CHARGING_72
        {
            String VIEW = "casting.prod.charging-72.view";
        }

        interface CASTER_80
        {
            String VIEW = "casting.prod.caster-80.view";
        }

        interface FURNACE_81
        {
            String VIEW = "casting.prod.furnace-81.view";
        }

        interface FURNACE_82
        {
            String VIEW = "casting.prod.furnace-82.view";
        }

        interface CHARGING_81
        {
            String VIEW = "casting.prod.charging-81.view";
        }

        interface CHARGING_82
        {
            String VIEW = "casting.prod.charging-82.view";
        }

        interface MOLD_DEPARTMENT
        {
            String VIEW = "casting.prod.mold-department.view";
        }

        interface PROCESS_DOCU
        {
            String VIEW = "casting.prod.process-docu.view";
        }

        interface CASTING_PRODUCTION_GANTT
        {
            String VIEW = "casting.prod.casting-production-gantt.view";
        }

        interface MATERIALS
        {
            String VIEW = "casting.prod.materials.view";
        }

        interface DETAILS
        {
            interface ACTIVATE_CHARGE
            {
                String VIEW = "casting.prod.detail.activate-charge.view";
            }

            interface CHARGING_NON_LIQUID_FURNACE
            {
                String VIEW = "casting.prod.detail.charging-non-liquid-furnace.view";
            }

            interface CHARGING_LIQUID_FURNACE
            {
                String VIEW = "casting.prod.detail.charging-liquid-furnace.view";
            }

            interface TREATING_FURNACE
            {
                String VIEW = "casting.prod.detail.treating-furnace.view";
            }

            interface UNLOAD_CASTER
            {
                String VIEW = "casting.prod.detail.unload-caster.view";
            }

            interface EQUIPMENT_CONDITION
            {
                String VIEW = "casting.prod.detail.equipment-condition.view";
            }

            interface VISUAL_INSPECTION
            {
                String VIEW = "casting.prod.detail.visual-inspection.view";
            }

            interface CASTING_PREPARATION
            {
                String VIEW = "casting.prod.detail.casting-preparation.view";
            }

            interface CASTING_PREPARATION_EXAMINATION
            {
                String VIEW = "casting.prod.detail.casting-preparation-examination.view";
            }
        }

        interface ACTION
        {
            String VALIDATE_TASK = "casting.prod.action.validate-task";
            String RELOAD = "casting.prod.action.reload";
            String EDIT_ENTRY = "casting.prod.action.edit-entry";
            String CHARGING_WIZARD = "casting.prod.action.charging-wizard";
            String ACTIVATE_CHARGE = "casting.prod.action.activate-charge";
            String CLEAN_FURNACE = "casting.prod.action.clean-furnace";
            String REQUEST_ANALYSE = "casting.prod.action.request-analyse";
            String CHARGING_FURNACE = "casting.prod.action.charging-furnace";
            String TREATING_FURNACE = "casting.prod.action.treating-furnace";
            String SKIMMING_FURNACE = "casting.prod.action.skimming-furnace";
            String RESTING_FURNACE = "casting.prod.action.resting-furnace";
            String RELEASE_FURNACE = "casting.prod.action.release-furnace";
            String DEACTIVATE_CHARGE = "casting.prod.action.deactivate-charge";
            String START_CASTING = "casting.prod.action.start-casting";
            String COOL_DOWN_SLABS = "casting.prod.action.cool-down-slabs";
            String UNLOAD_CASTER = "casting.prod.action.unload-caster";
            String SEND_CHARGING_SPECIFICATION = "casting.prod.action.send-charging-specification";
            String SAVE_CHARGING_MATERIALS = "casting.prod.action.save-charging-materials";
            String DELETE_CHARGING_MATERIALS = "casting.prod.action.delete-charging-materials";
            String CHANGE_CHARGING_MATERIALS = "casting.prod.action.change-charging-materials";
        }
    }

    interface STOCK
    {
        interface SLAB
        {
            String VIEW = "casting.stock.slab.view";
        }

        interface STOCK_MATERIAL
        {
            String VIEW = "casting.stock.stock-material.view";
        }

        interface CRUCIBLE_MATERIAL
        {
            String VIEW = "casting.stock.crucible-material.view";
        }
    }

    interface KNOWLEDGE
    {
        String VIEW = "casting.knowledge.view";
    }

    interface MAIN
    {
        interface MACHINE_CALENDAR
        {
            String VIEW = "casting.main.machine-calendar.view";
        }

        interface PRODUCTION_ORDER
        {
            String VIEW = "casting.main.production-order.view";
        }

        interface MATERIAL_TYPE_MAINTENANCE
        {
            String VIEW = "casting.main.material-type-maintenance.view";
        }

        interface ACTION
        {
            String CREATE_CHARGE = "casting.main.action.create-charge";
        }
    }

    interface ANALYSIS
    {
        String VIEW = "casting.analysis.view";
    }

    interface LABEL
    {
        String VIEW = "casting.label.view";
    }
    interface ALLOY
    {
        String VIEW = "casting.alloy.view";
    }

    interface REPORTING
    {
        interface ACTION
        {
            String MODIFY_KPI_OUTPUT_TARGETS = "casting.reporting.action.modify-kpi-output-targets";
        }

        interface DETAIL
        {
            String VIEW = "reporting.detail.view";
        }
    }

    interface DOWNTIME
    {
        interface ACTION
        {
            String CREATE = "downtime.${costCenter}.create";
        }

        interface DASHBOARD
        {
            String VIEW = "downtime.dashboard.view";
        }

        interface HISTORY
        {
            String VIEW = "downtime.history.view";
            String ADD = "downtime.history.${costCenter}.add";
            String EDIT = "downtime.history.${costCenter}.edit";
            String DELETE = "downtime.history.${costCenter}.delete";
            String ADDITIONAL_DESCRIPTION = "downtime.history.${costCenter}.additional-description";
        }
    }

    interface LOCKING
    {
        interface MATERIAL
        {
            interface LOCK_MATERIAL
            {
                String VIEW = "casting.locking.material.lock-material.view";
            }
        }

        interface LOCKING_WORKFLOW
        {
            String VIEW = "casting.locking.locking_workflow.view";
            String PROD = "casting.locking.locking-workflow.prod";
            String QS = "casting.locking.locking-workflow.qs";
            String AV = "casting.locking.locking-workflow.av";
            String TCS = "casting.locking.locking-workflow.tcs";
        }

        interface LOCKING_WORKFLOW_HISTORY
        {
            String VIEW = "casting.locking.locking-workflow-history.view";
        }

        interface ACTION
        {
            String PRINT = "casting.locking.action.print";
            String SEND_MAIL = "casting.locking.action.send-mail";
            String UNLOCK = "casting.locking.action.unlock";
            String SCRAP = "casting.locking.action.scrap";
            String MOVE_TO_CONTAINER = "casting.locking.action.move-to-container";
            String ASSIGN_PRODUCTION = "casting.locking.action.assign-production";
            String ASSIGN_QS = "casting.locking.action.assign-qs";
            String ASSIGN_AV = "casting.locking.action.assign-av";
            String ASSIGN_TCS = "casting.locking.action.assign-tcs";
            String ADD_MESSAGE = "casting.locking.action.add-message";
            String CHANGE_INITIATOR = "casting.locking.action.change-initiator";
            String CHANGE_SCRAP_CODE = "casting.locking.action.change-scrap-code";
            String CHANGE_MATERIAL_LOCK_LOCATION = "casting.locking.action.change-material-lock-location";
            String LOCK_MATERIAL = "casting.locking.lock-material";
        }
    }

}