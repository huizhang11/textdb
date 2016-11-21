package edu.uci.ics.textdb.plangen.operatorbuilder;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.textdb.api.common.Attribute;
import edu.uci.ics.textdb.api.common.FieldType;
import edu.uci.ics.textdb.api.common.Schema;
import edu.uci.ics.textdb.common.constants.LuceneAnalyzerConstants;
import edu.uci.ics.textdb.common.exception.PlanGenException;
import edu.uci.ics.textdb.common.utils.Utils;
import edu.uci.ics.textdb.dataflow.keywordmatch.KeywordMatcherSourceOperator;
import edu.uci.ics.textdb.storage.relation.RelationManager;
import junit.framework.Assert;

public class KeywordSourceBuilderTest {
    
    public static final String testTableName = "KeywordSourceBuilderTest_Table";
    public static final String testTableDirectory = "KeywordSourceBuilderTest_Table";
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
    public void testKeywordSourceBuilder1() throws Exception {
        String keyword = "Irvine";

        HashMap<String, String> operatorProperties = new HashMap<>();
        operatorProperties.put(KeywordMatcherBuilder.KEYWORD, keyword);
        operatorProperties.put(KeywordMatcherBuilder.MATCHING_TYPE, "PHRASE_INDEXBASED");
        operatorProperties.put(OperatorBuilderUtils.ATTRIBUTE_NAMES, "test");
        operatorProperties.put(OperatorBuilderUtils.DATA_SOURCE, testTableName);
        
        KeywordMatcherSourceOperator sourceOperator = KeywordSourceBuilder.buildSourceOperator(operatorProperties);

        
        // compare the keyword
        Assert.assertEquals(keyword, sourceOperator.getPredicate().getQuery());
        // compare the dataStore directory
        Assert.assertEquals(new File(testTableDirectory).getCanonicalPath(), sourceOperator.getDataStore().getDataDirectory());
        // compare the dataStore schema
        Assert.assertEquals(Utils.getSchemaWithID(testTableSchema), sourceOperator.getDataStore().getSchema());
        // compare the keyword matcher attribute list
        Assert.assertEquals(
                Arrays.asList("test"),
                sourceOperator.getPredicate().getAttributeNames());

    }
    
    /*
     * Test an invalid keyword source builder with non-exist data source name
     */
    @Test (expected = PlanGenException.class)
    public void testInvalidKeywordSourceBuilder() throws Exception {
        String keyword = "Irvine";

        HashMap<String, String> operatorProperties = new HashMap<>();
        operatorProperties.put(KeywordMatcherBuilder.KEYWORD, keyword);
        operatorProperties.put(KeywordMatcherBuilder.MATCHING_TYPE, "PHRASE_INDEXBASED");
        operatorProperties.put(OperatorBuilderUtils.ATTRIBUTE_NAMES, "test");
        operatorProperties.put(OperatorBuilderUtils.DATA_SOURCE, "non_exist_table_name");
        
        KeywordMatcherSourceOperator sourceOperator = KeywordSourceBuilder.buildSourceOperator(operatorProperties);

    }
    
    @After
    public void deleteTestTable() throws Exception {
        RelationManager.getRelationManager().deleteTable(testTableName);
    }
    

}
