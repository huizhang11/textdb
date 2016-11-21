package edu.uci.ics.textdb.plangen.operatorbuilder;

import java.util.List;
import java.util.Map;

import edu.uci.ics.textdb.api.common.Attribute;
import edu.uci.ics.textdb.common.constants.DataConstants.KeywordMatchingType;
import edu.uci.ics.textdb.common.constants.LuceneAnalyzerConstants;
import edu.uci.ics.textdb.common.exception.PlanGenException;
import edu.uci.ics.textdb.dataflow.common.Dictionary;
import edu.uci.ics.textdb.dataflow.common.DictionaryPredicate;
import edu.uci.ics.textdb.dataflow.dictionarymatcher.DictionaryMatcherSourceOperator;
import edu.uci.ics.textdb.plangen.PlanGenUtils;
import edu.uci.ics.textdb.storage.DataStore;

/**
 * DictionarySourceBuilder provides a static function that builds a DictionaryMatcherSourceOperator.
 * 
 * DictionarySourceBuilder currently needs the following properties:
 * 
 *   dictionary (required)
 *   matchingType (required)
 *   
 *   properties required for constructing attributeList, see OperatorBuilderUtils.constructAttributeList
 *   properties required for constructing dataStore, see OperatorBuilderUtils.constructDataStore
 * 
 * 
 * @author Zuozhi Wang
 *
 */
public class DictionarySourceBuilder {
    
    public static String DICTIONARY = "dictionary";
    public static final String MATCHING_TYPE = KeywordMatcherBuilder.MATCHING_TYPE;

    
    public static DictionaryMatcherSourceOperator buildSourceOperator(Map<String, String> operatorProperties) throws PlanGenException {
        String dictionaryStr = OperatorBuilderUtils.getRequiredProperty(DICTIONARY, operatorProperties);
        String matchingTypeStr = OperatorBuilderUtils.getRequiredProperty(MATCHING_TYPE, operatorProperties);

        // check if dictionary is empty       
        PlanGenUtils.planGenAssert(!dictionaryStr.trim().isEmpty(), "dictionary is empty");
        List<String> dictionaryList = OperatorBuilderUtils.splitStringByComma(dictionaryStr);
        Dictionary dictionary = new Dictionary(dictionaryList);

        // generate attribute list
        List<Attribute> attributeList = OperatorBuilderUtils.constructAttributeList(operatorProperties);

        // generate matching type        
        KeywordMatchingType matchingType = KeywordMatcherBuilder.getKeywordMatchingType(matchingTypeStr);
        PlanGenUtils.planGenAssert(matchingType != null, 
                "matching type: "+ matchingTypeStr +" is not valid, "
                + "must be one of " + KeywordMatcherBuilder.keywordMatchingTypeMap.keySet());
        
        DictionaryPredicate predicate = new DictionaryPredicate(
                dictionary, attributeList, LuceneAnalyzerConstants.getStandardAnalyzer(), matchingType);
        
        DataStore dataStore = OperatorBuilderUtils.constructDataStore(operatorProperties);
        
        DictionaryMatcherSourceOperator sourceOperator = new DictionaryMatcherSourceOperator(predicate, dataStore);
                
        return sourceOperator;
    }

}
