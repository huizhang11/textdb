package edu.uci.ics.textdb.web.request.operatorbean;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.textdb.web.request.Operator;

/**
 * This class defines the properties/data members specific to the FuzzyTokenMatcher operator
 * and extends the Operator class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
public class FuzzyTokenMatcherBean extends Operator {
    @JsonProperty("query")
    private String query;
    @JsonProperty("threshold_ratio")
    private double thresholdRatio;

    public FuzzyTokenMatcherBean() {
    }

    public FuzzyTokenMatcherBean(String operatorID, String operatorType, String query, double thresholdRatio) {
        super(operatorID, operatorType);
        this.query = query;
        this.thresholdRatio = thresholdRatio;
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
    public double getThresholdRatio() {
        return thresholdRatio;
    }

    @JsonProperty("threshold_ratio")
    public void setThresholdRatio(double thresholdRatio) {
        this.thresholdRatio = thresholdRatio;
    }
}
