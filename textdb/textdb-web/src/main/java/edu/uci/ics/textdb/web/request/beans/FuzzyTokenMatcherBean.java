package edu.uci.ics.textdb.web.request.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.textdb.plangen.operatorbuilder.FuzzyTokenMatcherBuilder;

import java.util.HashMap;

/**
 * This class defines the properties/data members specific to the FuzzyTokenMatcher operator
 * and extends the OperatorBean class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
public class FuzzyTokenMatcherBean extends OperatorBean {
    @JsonProperty("query")
    private String query;
    @JsonProperty("threshold_ratio")
    private String thresholdRatio;

    public FuzzyTokenMatcherBean() {
    }

    public FuzzyTokenMatcherBean(String operatorID, String operatorType, String query, String thresholdRatio) {
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
}
