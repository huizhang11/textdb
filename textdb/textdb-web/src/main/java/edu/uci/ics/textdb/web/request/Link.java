package edu.uci.ics.textdb.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by kishorenarendran on 10/12/16.
 */

/**
 * This class defines a link in the operator graph
 */
public class Link {
    @JsonProperty("from")
    private String fromOperatorID;
    @JsonProperty("to")
    private String toOperatorID;

    public Link() {
    }

    public Link(String fromOperatorID, String toOperatorID) {
        this.fromOperatorID = fromOperatorID;
        this.toOperatorID = toOperatorID;
    }

    public String getFromOperatorID() {
        return fromOperatorID;
    }

    public void setFromOperatorID(String fromOperatorID) {
        this.fromOperatorID = fromOperatorID;
    }

    public String getToOperatorID() {
        return toOperatorID;
    }

    public void setToOperatorID(String toOperatorID) {
        this.toOperatorID = toOperatorID;
    }
}
