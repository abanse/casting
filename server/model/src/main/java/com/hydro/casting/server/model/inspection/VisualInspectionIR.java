package com.hydro.casting.server.model.inspection;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
@Entity
@DiscriminatorValue( "visual_inspection" )
public class VisualInspectionIR extends InspectionRule
{
}
