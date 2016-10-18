package edu.uci.ics.textdb.web.request.operatorbean;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.textdb.common.constants.DataConstants;
import edu.uci.ics.textdb.web.request.OperatorBean;

/**
 * This class defines the properties/data members specific to the KeywordSource operator
 * and extends the OperatorBean class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
public class KeywordSourceBean extends OperatorBean {
    @JsonProperty("keyword")
    private String keyword;
    @JsonProperty("matching_type")
    private DataConstants.KeywordMatchingType matchingType;
    @JsonProperty("data_source")
    private String dataSource;

    public KeywordSourceBean() {
    }

    public KeywordSourceBean(String operatorID, String operatorType, String keyword, DataConstants.KeywordMatchingType matchingType, String dataSource) {
        super(operatorID, operatorType);
        this.keyword = keyword;
        this.matchingType = matchingType;
        this.dataSource = dataSource;
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
    public DataConstants.KeywordMatchingType getMatchingType() {
        return matchingType;
    }

    @JsonProperty("matching_type")
    public void setMatchingType(DataConstants.KeywordMatchingType matchingType) {
        this.matchingType = matchingType;
    }

    @JsonProperty("data_source")
    public String getDataSource() {
        return dataSource;
    }

    @JsonProperty("data_source")
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
