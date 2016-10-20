package edu.uci.ics.textdb.web.request.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.uci.ics.textdb.plangen.operatorbuilder.OperatorBuilderUtils;
import edu.uci.ics.textdb.plangen.operatorbuilder.RegexMatcherBuilder;

import java.util.HashMap;

/**
 * This class defines the properties/data members specific to the RegexSource operator
 * and extends the OperatorBean class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
@JsonTypeName("RegexSource")
public class RegexSourceBean extends OperatorBean {
    @JsonProperty("operator_type")
    private String operatorType;
    @JsonProperty("regex")
    private String regex;
    @JsonProperty("data_source")
    private String dataSource;

    public RegexSourceBean() {
    }

    public RegexSourceBean(String operatorID, String attributes, String limit, String offset, String operatorType, String regex, String dataSource) {
        super(operatorID, attributes, limit, offset);
        this.operatorType = operatorType;
        this.regex = regex;
        this.dataSource = dataSource;
    }

    @JsonProperty("operator_type")
    public String getOperatorType() {
        return operatorType;
    }

    @JsonProperty("operator_type")
    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
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

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof OperatorBean)) return false;
        RegexSourceBean regexSourceBean = (RegexSourceBean) other;
        return super.equals(other) &&
                this.getOperatorType().equals(regexSourceBean.getOperatorType()) &&
                this.getRegex().equals(regexSourceBean.getRegex()) &&
                this.getDataSource().equals(regexSourceBean.getDataSource());
    }
}
