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
    @JsonProperty("operator_type")
    private String operatorType;

    public ProjectionBean() {
    }

    public ProjectionBean(String operatorID, String attributes, String limit, String offset, String operatorType) {
        super(operatorID, attributes, limit, offset);
        this.operatorType = operatorType;
    }

    @JsonProperty("operator_type")
    public String getOperatorType() {
        return operatorType;
    }

    @JsonProperty("operator_type")
    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof OperatorBean)) return false;
        ProjectionBean projectionBean = (ProjectionBean) other;
        return super.equals(other) &&
                this.getOperatorType().equals(projectionBean.getOperatorType());
    }
}
