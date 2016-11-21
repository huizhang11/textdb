package edu.uci.ics.textdb.plangen.operatorbuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.textdb.api.common.Attribute;
import edu.uci.ics.textdb.api.common.FieldType;
import edu.uci.ics.textdb.api.common.Schema;
import edu.uci.ics.textdb.common.constants.LuceneAnalyzerConstants;
import edu.uci.ics.textdb.common.exception.PlanGenException;
import edu.uci.ics.textdb.dataflow.dictionarymatcher.DictionaryMatcherSourceOperator;
import edu.uci.ics.textdb.storage.relation.RelationManager;
import junit.framework.Assert;

public class DictionarySourceBuilderTest {
    
    public static final String testTableName = "DictionarySourceBuilderTest_Table";
    public static final String testTableDirectory = "DictionarySourceBuilderTest_Table";
    public static final Schema testTableSchema = new Schema(new Attribute("test", FieldType.STRING));
    
    @Before
    public void writeToCatalog() throws Exception {
        RelationManager.getRelationManager().createTable(
                testTableName,
                testTableDirectory,
                testTableSchema,
                LuceneAnalyzerConstants.standardAnalyzerString());
    }
    
    @Test
    public void testDictionarySourceBuilder1() throws Exception {

        String dictionaryStr = "Irvine, Anaheim, Costa Mesa, Santa Ana";
        List<String> dictionaryList = Arrays.asList(
                "Irvine",
                "Anaheim",
                "Costa Mesa",
                "Santa Ana");
        
        HashMap<String, String> operatorProperties = new HashMap<>();
        operatorProperties.put(DictionaryMatcherBuilder.DICTIONARY, dictionaryStr);
        operatorProperties.put(DictionaryMatcherBuilder.MATCHING_TYPE, "PHRASE_INDEXBASED");
        operatorProperties.put(OperatorBuilderUtils.ATTRIBUTE_NAMES, "test");
        operatorProperties.put(OperatorBuilderUtils.DATA_SOURCE, testTableName);
        
        DictionaryMatcherSourceOperator sourceOperator = DictionarySourceBuilder.buildSourceOperator(operatorProperties);

        String dictionaryEntry = null;
        ArrayList<String> actualDictionary = new ArrayList<>();
        while ((dictionaryEntry = sourceOperator.getPredicate().getNextDictionaryEntry()) != null) {
            actualDictionary.add(dictionaryEntry);
        }
        
        // compare dict
        Assert.assertEquals(dictionaryList, actualDictionary);
        // compare dataStore directory
        Assert.assertEquals(testTableDirectory, sourceOperator.getDataStore().getDataDirectory());
        // compare dataStore schema
        Assert.assertEquals(
                testTableSchema, 
                sourceOperator.getDataStore().getSchema());
        // compare dictMatcher attribute list
        Assert.assertEquals(Arrays.asList("test"), sourceOperator.getPredicate().getAttributeList().toString());

    }

}
