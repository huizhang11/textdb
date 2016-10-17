package edu.uci.ics.textdb.web.request;

import java.util.ArrayList;

/**
 * Created by kishorenarendran on 10/12/16.
 */
public class QueryPlanRequest {
    private ArrayList<Operator> operators;
    private ArrayList<Link> links;

    public QueryPlanRequest() {
    }

    public QueryPlanRequest(ArrayList<Operator> operators, ArrayList<Link> links) {
        this.operators = operators;
        this.links = links;
    }

    public ArrayList<Operator> getOperators() {
        return operators;
    }

    public void setOperators(ArrayList<Operator> operators) {
        this.operators = operators;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<Link> links) {
        this.links = links;
    }
}
