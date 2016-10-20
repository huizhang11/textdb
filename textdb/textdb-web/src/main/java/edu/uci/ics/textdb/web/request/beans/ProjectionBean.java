package edu.uci.ics.textdb.web.request.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * This class defines the properties/data members specific to the Projection operator
 * and extends the OperatorBean class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
@JsonTypeName("Projection")
public class ProjectionBean extends OperatorBean {
    // Properties regarding the projection operator will go here
}
