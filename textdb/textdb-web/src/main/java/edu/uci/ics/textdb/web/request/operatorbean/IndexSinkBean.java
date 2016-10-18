package edu.uci.ics.textdb.web.request.operatorbean;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.textdb.web.request.Operator;

/**
 * This class defines the properties/data members specific to the IndexSink operator
 * and extends the Operator class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
public class IndexSinkBean extends Operator{
    @JsonProperty("index_path")
    private String indexPath;
    @JsonProperty("index_name")
    private String indexName;

    public IndexSinkBean() {
    }

    public IndexSinkBean(String operatorID, String operatorType, String indexPath, String indexName) {
        super(operatorID, operatorType);
        this.indexPath = indexPath;
        this.indexName = indexName;
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
}
