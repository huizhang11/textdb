package edu.uci.ics.textdb.web.request.operatorbean;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.textdb.common.constants.DataConstants.KeywordMatchingType;
import edu.uci.ics.textdb.web.request.Operator;

/**
 * This class defines the properties/data members specific to the KeywordMatcher operator
 * and extends the Operator class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
public class KeywordMatcherBean extends Operator {
    @JsonProperty("keyword")
    private String keyword;
    @JsonProperty("matching_type")
    private KeywordMatchingType matchingType;

    public KeywordMatcherBean() {
    }

    public KeywordMatcherBean(String operatorID, String operatorType, String keyword, KeywordMatchingType matchingType) {
        super(operatorID, operatorType);
        this.keyword = keyword;
        this.matchingType = matchingType;
    }

    @JsonProperty("keyword")
    public String getKeyword() {
        return keyword;
    }

    @JsonProperty("keyword")
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @JsonProperty("matching_type")
    public KeywordMatchingType getMatchingType() {
        return matchingType;
    }

    @JsonProperty("matching_type")
    public void setMatchingType(KeywordMatchingType matchingType) {
        this.matchingType = matchingType;
    }
}
