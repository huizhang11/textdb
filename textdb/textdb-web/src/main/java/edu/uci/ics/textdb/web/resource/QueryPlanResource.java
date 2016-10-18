package edu.uci.ics.textdb.web.resource;

import edu.uci.ics.textdb.web.request.QueryPlanRequest;
import edu.uci.ics.textdb.web.response.SampleResponse;
import org.apache.xpath.SourceTree;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This class will be the resource class for accepting a query plan request and executing the
 * query plan to get the query response
 * Created by kishorenarendran on 10/17/16.
 */
@Path("/queryplan")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QueryPlanResource {
    @POST
    @Path("/execute")
    public SampleResponse executeQueryPlan(QueryPlanRequest queryPlanRequest) {
        return new SampleResponse(1, "hello, world!");
    }
}
