package com.hydro.casting.server.contract.dto;

public class CastingPreparationExaminationDTO extends InspectionDTO
{
    private static final long serialVersionUID = 1L;

    public final static String CATEGORY_APK = "cpExamination";

    @Override
    public long getId()
    {
        return CATEGORY_APK.hashCode();
    }
}
