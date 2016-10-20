package edu.uci.ics.textdb.web.request.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.uci.ics.textdb.plangen.operatorbuilder.FuzzyTokenMatcherBuilder;

import java.util.HashMap;

/**
 * This class defines the properties/data members specific to the FuzzyTokenMatcher operator
 * and extends the OperatorBean class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
@JsonTypeName("FuzzyTokenMatcher")
public class FuzzyTokenMatcherBean extends OperatorBean {
    @JsonProperty("operator_type")
    private String operatorType;
    @JsonProperty("query")
    private String query;
    @JsonProperty("threshold_ratio")
    private String thresholdRatio;

    public FuzzyTokenMatcherBean() {
    }

    public FuzzyTokenMatcherBean(String operatorID, String attributes, String limit, String offset, String operatorType, String query, String thresholdRatio) {
        super(operatorID, attributes, limit, offset);
        this.operatorType = operatorType;
        this.query = query;
        this.thresholdRatio = thresholdRatio;
    }

    @JsonProperty("operator_type")
    public String getOperatorType() {
        return operatorType;
    }

    @JsonProperty("operator_type")
    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    @JsonProperty("query")
    public String getQuery() {
        return query;
    }

    @JsonProperty("query")
    public void setQuery(String query) {
        this.query = query;
    }

    @JsonProperty("threshold_ratio")
    public String getThresholdRatio() {
        return thresholdRatio;
    }

    @JsonProperty("threshold_ratio")
    public void setThresholdRatio(String thresholdRatio) {
        this.thresholdRatio = thresholdRatio;
    }

    public HashMap<String, String> getOperatorProperties() {
        HashMap<String, String> operatorProperties = super.getOperatorProperties();
        operatorProperties.put(FuzzyTokenMatcherBuilder.FUZZY_STRING, this.getQuery());
        operatorProperties.put(FuzzyTokenMatcherBuilder.THRESHOLD_RATIO, this.getThresholdRatio());
        return operatorProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof OperatorBean)) return false;
        FuzzyTokenMatcherBean fuzzyTokenMatcherBean = (FuzzyTokenMatcherBean) other;
        return super.equals(other) &&
                this.getOperatorType().equals(fuzzyTokenMatcherBean.getOperatorType()) &&
                this.getQuery().equals(fuzzyTokenMatcherBean.getQuery()) &&
                this.getThresholdRatio().equals(fuzzyTokenMatcherBean.getThresholdRatio());
    }
}
