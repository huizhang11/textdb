package edu.uci.ics.textdb.web.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.textdb.common.constants.DataConstants;
import edu.uci.ics.textdb.web.request.beans.DictionaryMatcherBean;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by kishorenarendran on 10/20/16.
 */
public class DictionaryMatcherBeanTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void testDeserialization() throws IOException{
        final DictionaryMatcherBean dictionaryMatcherBean = new DictionaryMatcherBean("operator1", "attributes", "10", "100", "DictionaryMatcher", "dictionary", DataConstants.KeywordMatchingType.PHRASE_INDEXBASED);
        String jsonString = "{\n" +
                "    \"operator_id\": \"operator1\",\n" +
                "    \"operator_type\": \"DictionaryMatcher\",\n" +
                "    \"attributes\":  \"attributes\",\n" +
                "    \"limit\": \"10\",\n" +
                "    \"offset\": \"100\",\n" +
                "    \"dictionary\": \"dictionary\",\n" +
                "    \"matching_type\": \"PHRASE_INDEXBASED\"\n" +
                "}";
        Object deserializedObject = MAPPER.readValue(jsonString, DictionaryMatcherBean.class);
        assertEquals(dictionaryMatcherBean.equals(deserializedObject), true);
    }
}
