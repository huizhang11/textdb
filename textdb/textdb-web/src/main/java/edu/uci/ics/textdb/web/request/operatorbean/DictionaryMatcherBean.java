package edu.uci.ics.textdb.web.request.operatorbean;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.textdb.common.constants.DataConstants;
import edu.uci.ics.textdb.web.request.OperatorBean;

/**
 * This class defines the properties/data members specific to the DictionaryMatcher operator
 * and extends the OperatorBean class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
public class DictionaryMatcherBean extends OperatorBean {
    @JsonProperty("dictionary")
    private String dictionary;
    @JsonProperty("matching_type")
    private DataConstants.KeywordMatchingType matchingType;

    public DictionaryMatcherBean() {
    }

    public DictionaryMatcherBean(String operatorID, String operatorType, String dictionary, DataConstants.KeywordMatchingType matchingType) {
        super(operatorID, operatorType);
        this.dictionary = dictionary;
        this.matchingType = matchingType;
    }

    @JsonProperty("dictionary")
    public String getDictionary() {
        return dictionary;
    }

    @JsonProperty("dictionary")
    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    @JsonProperty("matching_type")
    public DataConstants.KeywordMatchingType getMatchingType() {
        return matchingType;
    }

    @JsonProperty("matching_type")
    public void setMatchingType(DataConstants.KeywordMatchingType matchingType) {
        this.matchingType = matchingType;
    }
}
