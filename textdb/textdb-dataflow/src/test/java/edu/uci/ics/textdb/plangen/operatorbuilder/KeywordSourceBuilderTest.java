package edu.uci.ics.textdb.plangen.operatorbuilder;

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
import edu.uci.ics.textdb.api.exception.TextDBException;
import edu.uci.ics.textdb.common.constants.LuceneAnalyzerConstants;
import edu.uci.ics.textdb.common.exception.PlanGenException;
import edu.uci.ics.textdb.common.utils.Utils;
import edu.uci.ics.textdb.dataflow.keywordmatch.KeywordMatcherSourceOperator;
import edu.uci.ics.textdb.storage.relation.RelationManager;
import junit.framework.Assert;

public class KeywordSourceBuilderTest {
    
    @Before
    public void createTestTables() {
        try {
            Schema schema = new Schema(
                    new Attribute("id", FieldType.INTEGER), new Attribute("city", FieldType.STRING),
                    new Attribute("location", FieldType.STRING), new Attribute("content", FieldType.TEXT));
            RelationManager relationManager = RelationManager.getRelationManager();
            relationManager.createTable("keyword_src_table_city", 
                    "./index/keyword_src_table_city", schema, LuceneAnalyzerConstants.standardAnalyzerString());
        } catch (TextDBException e) {
            
        }
    }
    
//    @Test
//    public void testKeywordSourceBuilder1() throws Exception {
//        String directoryStr = "./index";
//        JSONObject schemaJsonJSONObject = new JSONObject();
//        schemaJsonJSONObject.put(OperatorBuilderUtils.ATTRIBUTE_NAMES, "id, city, location, content");
//        schemaJsonJSONObject.put(OperatorBuilderUtils.ATTRIBUTE_TYPES, "integer, string, string, text");
//                
//        List<Attribute> schemaAttrs = Arrays.asList(
//                new Attribute("id", FieldType.INTEGER),
//                new Attribute("city", FieldType.STRING),
//                new Attribute("location", FieldType.STRING),
//                new Attribute("content", FieldType.TEXT));
//        
//        String keyword = "Irvine";
//        List<Attribute> keywordAttributes = schemaAttrs.stream()
//                .filter(attr -> ! attr.getFieldName().equals("id")).collect(Collectors.toList());
//
//        
//        HashMap<String, String> operatorProperties = new HashMap<>();
//        operatorProperties.put(KeywordMatcherBuilder.KEYWORD, keyword);
//        operatorProperties.put(KeywordMatcherBuilder.MATCHING_TYPE, "PHRASE_INDEXBASED");
//        operatorProperties.put(OperatorBuilderUtils.ATTRIBUTE_NAMES, "city, location, content");
//        operatorProperties.put(OperatorBuilderUtils.DATA_DIRECTORY, directoryStr);
//        operatorProperties.put(OperatorBuilderUtils.SCHEMA, schemaJsonJSONObject.toString());
//        
//        KeywordMatcherSourceOperator sourceOperator = KeywordSourceBuilder.buildSourceOperator(operatorProperties);
//
//        
//        // compare the keyword
//        Assert.assertEquals(keyword, sourceOperator.getPredicate().getQuery());
//        // compare the dataStore directory
//        Assert.assertEquals(directoryStr, sourceOperator.getDataStore().getDataDirectory());
//        // compare the dataStore schema
//        Assert.assertEquals(
//                schemaAttrs.stream().collect(Collectors.toList()).toString(), 
//                sourceOperator.getDataStore().getSchema().getAttributes().stream().collect(Collectors.toList()).toString());
//        // compare the keyword matcher attribute list
//        Assert.assertEquals(
//                Utils.getAttributeNames(keywordAttributes).toString(),
//                sourceOperator.getPredicate().getAttributeNames().toString());
//
//    }

}
