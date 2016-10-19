package edu.uci.ics.textdb.web.request.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.textdb.common.constants.DataConstants;
import edu.uci.ics.textdb.plangen.operatorbuilder.DictionarySourceBuilder;
import edu.uci.ics.textdb.plangen.operatorbuilder.OperatorBuilderUtils;

import java.util.HashMap;

/**
 * This class defines the properties/data members specific to the DictionarySource operator
 * and extends the OperatorBean class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
public class DictionarySourceBean extends OperatorBean {
    @JsonProperty("dictionary")
    private String dictionary;
    @JsonProperty("matching_type")
    private DataConstants.KeywordMatchingType matchingType;
    @JsonProperty("data_source")
    private String dataSource;

    public DictionarySourceBean() {
    }

    public DictionarySourceBean(String operatorID, String operatorType, String dictionary, DataConstants.KeywordMatchingType matchingType, String dataSource) {
        super(operatorID, operatorType);
        this.dictionary = dictionary;
        this.matchingType = matchingType;
        this.dataSource = dataSource;
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
        operatorProperties.put(DictionarySourceBuilder.DICTIONARY, this.getDictionary());
        operatorProperties.put(DictionarySourceBuilder.MATCHING_TYPE, this.getMatchingType().name());
        operatorProperties.put(OperatorBuilderUtils.DATA_DIRECTORY, this.getDataSource());
        return operatorProperties;
    }
}
