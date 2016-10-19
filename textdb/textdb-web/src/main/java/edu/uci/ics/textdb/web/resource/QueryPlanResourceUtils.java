package edu.uci.ics.textdb.web.resource;

import edu.uci.ics.textdb.web.request.beans.*;

import java.util.HashMap;

/**
 * Contains some constants and utility functions for the QueryPlanResource in order to
 * create the requisite HashMap of properties for the different operators.
 * Created by kishorenarendran on 10/18/16.
 */
public class QueryPlanResourceUtils {
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
}
