//@formatter:off
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(type=java.util.Date.class, value=com.hydro.core.common.xml.DateTimeAdapter.class),
    @XmlJavaTypeAdapter(type=Double.class, value=com.hydro.core.common.xml.DoubleAdapter.class)
})
//@formatter:on
package com.hydro.casting.gui.locking.workflow.report.model;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;