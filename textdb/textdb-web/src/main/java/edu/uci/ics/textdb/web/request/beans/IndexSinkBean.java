package edu.uci.ics.textdb.web.request.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.HashMap;

/**
 * This class defines the properties/data members specific to the IndexSink operator
 * and extends the OperatorBean class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
@JsonTypeName("IndexSink")
public class IndexSinkBean extends OperatorBean {
    @JsonProperty("operator_type")
    private String operatorType;
    @JsonProperty("index_path")
    private String indexPath;
    @JsonProperty("index_name")
    private String indexName;

    public IndexSinkBean() {
    }

    public IndexSinkBean(String operatorID, String attributes, String limit, String offset, String operatorType, String indexPath, String indexName) {
        super(operatorID, attributes, limit, offset);
        this.operatorType = operatorType;
        this.indexPath = indexPath;
        this.indexName = indexName;
    }

    @JsonProperty("operator_type")
    public String getOperatorType() {
        return operatorType;
    }

    @JsonProperty("operator_type")
    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    @JsonProperty("index_path")
    public String getIndexPath() {
        return indexPath;
    }

    @JsonProperty("index_path")
    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    @JsonProperty("index_name")
    public String getIndexName() {
        return indexName;
    }

    @JsonProperty("index_name")
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public HashMap<String, String> getOperatorProperties() {
        HashMap<String, String> operatorProperties = super.getOperatorProperties();
        //TODO - Check on properties for IndexSink, IndexSinkBuilder seems to be missing
        return operatorProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof OperatorBean)) return false;
        IndexSinkBean indexSinkBean = (IndexSinkBean) other;
        return super.equals(other) &&
                this.getOperatorType().equals(indexSinkBean.getOperatorType()) &&
                this.getIndexName().equals(indexSinkBean.getIndexName()) &&
                this.getIndexPath().equals(indexSinkBean.getIndexPath());
    }
}
