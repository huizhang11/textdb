package edu.uci.ics.textdb.web.request.operatorbean;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.textdb.plangen.operatorbuilder.DictionaryMatcherBuilder;
import edu.uci.ics.textdb.plangen.operatorbuilder.FuzzyTokenMatcherBuilder;
import edu.uci.ics.textdb.plangen.operatorbuilder.OperatorBuilderUtils;
import edu.uci.ics.textdb.web.request.OperatorBean;

import java.util.HashMap;

/**
 * This class defines the properties/data members specific to the FuzzyTokenSource operator
 * and extends the OperatorBean class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
public class FuzzyTokenSourceBean extends OperatorBean {

    @JsonProperty("query")
    private String query;
    @JsonProperty("threshold_ratio")
    private String thresholdRatio;
    @JsonProperty("data_source")
    private String dataSource;

    public FuzzyTokenSourceBean() {
    }

    public FuzzyTokenSourceBean(String operatorID, String operatorType, String query, String thresholdRatio, String dataSource) {
        super(operatorID, operatorType);
        this.query = query;
        this.thresholdRatio = thresholdRatio;
        this.dataSource = dataSource;
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

    @JsonProperty("data_source")
    public String getDataSource() {
        return dataSource;
    }

    @JsonProperty("data_source")
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public HashMap<String, String> getOperatorProperties() {
        HashMap<String, String> operatorProperties = super.getOperatorProperties();
        operatorProperties.put(FuzzyTokenMatcherBuilder.FUZZY_STRING, this.getQuery());
        operatorProperties.put(FuzzyTokenMatcherBuilder.THRESHOLD_RATIO, this.getThresholdRatio());
        operatorProperties.put(OperatorBuilderUtils.DATA_DIRECTORY, this.getDataSource());
        return operatorProperties;
    }
}
