package edu.uci.ics.textdb.web.request.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.textdb.plangen.operatorbuilder.OperatorBuilderUtils;
import edu.uci.ics.textdb.plangen.operatorbuilder.RegexMatcherBuilder;

import java.util.HashMap;

/**
 * This class defines the properties/data members specific to the RegexSource operator
 * and extends the OperatorBean class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
public class RegexSourceBean extends OperatorBean {
    @JsonProperty("regex")
    private String regex;
    @JsonProperty("data_source")
    private String dataSource;

    public RegexSourceBean() {
    }

    public RegexSourceBean(String operatorID, String operatorType, String regex, String dataSource) {
        super(operatorID, operatorType);
        this.regex = regex;
        this.dataSource = dataSource;
    }

    @JsonProperty("regex")
    public String getRegex() {
        return regex;
    }

    @JsonProperty("regex")
    public void setRegex(String regex) {
        this.regex = regex;
    }

    @JsonProperty("data_source")
    public String getDataSource() {
        return dataSource;
    }

    @JsonProperty("data_source")
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public HashMap<String, String> getOperatorProperties() {
        HashMap<String, String> operatorProperties = super.getOperatorProperties();
        operatorProperties.put(RegexMatcherBuilder.REGEX, this.getRegex());
        operatorProperties.put(OperatorBuilderUtils.DATA_DIRECTORY, this.getDataSource());
        return operatorProperties;
    }
}
