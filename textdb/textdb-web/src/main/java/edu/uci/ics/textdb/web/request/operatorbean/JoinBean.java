package edu.uci.ics.textdb.web.request.operatorbean;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.textdb.web.request.Operator;

/**
 * This class defines the properties/data members specific to the Join operator
 * and extends the Operator class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
public class JoinBean extends Operator{
    @JsonProperty("id_attribute")
    private String idAttribute;
    @JsonProperty("distance")
    private String distance;

    // A bean variable for the predicate type of the join has been omitted for now and will be included in the future

    public JoinBean() {
    }

    public JoinBean(String operatorID, String operatorType, String attributes, String idAttribute, String distance) {
        super(operatorID, operatorType);
        this.idAttribute = idAttribute;
        this.distance = distance;
    }

    @JsonProperty("id_attribute")
    public String getIdAttribute() {
        return idAttribute;
    }

    @JsonProperty("id_attribute")
    public void setIdAttribute(String idAttribute) {
        this.idAttribute = idAttribute;
    }

    @JsonProperty("distance")
    public String getDistance() {
        return distance;
    }

    @JsonProperty("distance")
    public void setDistance(String distance) {
        this.distance = distance;
    }
}
