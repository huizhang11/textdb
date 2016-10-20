package edu.uci.ics.textdb.web.request.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.uci.ics.textdb.plangen.operatorbuilder.RegexMatcherBuilder;

import java.util.HashMap;

/**
 * This class defines the properties/data members specific to the RegexMatcher operator
 * and extends the OperatorBean class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
@JsonTypeName("RegexMatcher")
public class RegexMatcherBean extends OperatorBean {
    @JsonProperty("operator_type")
    private String operatorType;
    @JsonProperty("regex")
    private String regex;

    public RegexMatcherBean() {
    }

    public RegexMatcherBean(String operatorID, String attributes, String limit, String offset, String operatorType, String regex) {
        super(operatorID, attributes, limit, offset);
        this.operatorType = operatorType;
        this.regex = regex;
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

    @Override
    public HashMap<String, String> getOperatorProperties() {
        HashMap<String, String> operatorProperties = super.getOperatorProperties();
        operatorProperties.put(RegexMatcherBuilder.REGEX, this.getRegex());
        return operatorProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof OperatorBean)) return false;
        RegexMatcherBean regexMatcherBean = (RegexMatcherBean) other;
        return super.equals(other) &&
                this.getOperatorType().equals(regexMatcherBean.getOperatorType()) &&
                this.getRegex().equals(regexMatcherBean.getRegex());
    }
}
