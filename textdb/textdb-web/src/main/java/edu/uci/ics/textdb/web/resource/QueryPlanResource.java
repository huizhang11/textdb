package edu.uci.ics.textdb.web.resource;

import edu.uci.ics.textdb.web.request.QueryPlanRequest;
import edu.uci.ics.textdb.web.request.operatorbean.*;
import edu.uci.ics.textdb.web.response.SampleResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

/**
 * This class will be the resource class for accepting a query plan request and executing the
 * query plan to get the query response
 * Created by kishorenarendran on 10/17/16.
 */
@Path("/queryplan")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QueryPlanResource {

    public static final HashMap<String, Class> OPERATOR_BEAN_MAP = new HashMap<String, Class>() {{
        put("DictionaryMatcher", DictionaryMatcherBean.class);
        put("DictionarySource", DictionarySourceBean.class);
        put("FileSink", FileSinkBean.class);
        put("FuzzyTokenMatcher", FuzzyTokenMatcherBean.class);
        put("FuzzyTokenSource", FuzzyTokenSourceBean.class);
        put("IndexSink", IndexSinkBean.class);
        put("Join", JoinBean.class);
        put("KeywordMatcher", KeywordMatcherBean.class);
        put("KeywordSource", KeywordSourceBean.class);
        put("NlpExtractor", NlpExtractorBean.class);
        put("Projection", ProjectionBean.class);
        put("RegexMatcher", RegexMatcherBean.class);
        put("RegexSource", RegexSourceBean.class);
    }};

    @POST
    @Path("/execute")
    public SampleResponse executeQueryPlan(QueryPlanRequest queryPlanRequest) {
        return new SampleResponse(1, "hello, world!");
    }
}
