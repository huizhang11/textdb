package edu.uci.ics.textdb.web.request.operatorbean;

import edu.uci.ics.textdb.web.request.OperatorBean;

/**
 * This class defines the properties/data members specific to the RegexMatcher operator
 * and extends the OperatorBean class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
public class RegexMatcherBean extends OperatorBean {
    private String regex;

    public RegexMatcherBean() {
    }

    public RegexMatcherBean(String operatorID, String operatorType, String regex) {
        super(operatorID, operatorType);
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
