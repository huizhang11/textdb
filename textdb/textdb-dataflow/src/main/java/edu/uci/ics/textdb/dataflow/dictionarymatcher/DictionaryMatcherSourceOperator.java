
package edu.uci.ics.textdb.dataflow.dictionarymatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uci.ics.textdb.api.common.Attribute;
import edu.uci.ics.textdb.api.common.FieldType;
import edu.uci.ics.textdb.api.common.ITuple;
import edu.uci.ics.textdb.api.common.Schema;
import edu.uci.ics.textdb.api.dataflow.ISourceOperator;
import edu.uci.ics.textdb.api.storage.IDataStore;
import edu.uci.ics.textdb.common.constants.DataConstants;
import edu.uci.ics.textdb.common.constants.DataConstants.KeywordMatchingType;
import edu.uci.ics.textdb.common.constants.SchemaConstants;
import edu.uci.ics.textdb.common.exception.DataFlowException;
import edu.uci.ics.textdb.common.field.Span;
import edu.uci.ics.textdb.common.utils.Utils;
import edu.uci.ics.textdb.dataflow.common.DictionaryPredicate;
import edu.uci.ics.textdb.dataflow.common.KeywordPredicate;
import edu.uci.ics.textdb.dataflow.keywordmatch.KeywordMatcher;
import edu.uci.ics.textdb.dataflow.source.IndexBasedSourceOperator;
import edu.uci.ics.textdb.dataflow.source.ScanBasedSourceOperator;

/**
 * @author Sudeep (inkudo)
 * @author Zuozhi Wang (zuozhi)
 * 
 */
public class DictionaryMatcherSourceOperator implements ISourceOperator {

    private ISourceOperator indexSource;
    private KeywordMatcher keywordMatcher;

    private Schema inputSchema;
    private Schema outputSchema;

    private ITuple sourceTuple;
    private String currentDictionaryEntry;

    private final DictionaryPredicate predicate;
    private IDataStore dataStore;

    private int resultCursor;
    private int limit;
    private int offset;

    /**
     * Constructs a DictionaryMatcher with a dictionary predicate
     * 
     * @param predicate
     * 
     */
    public DictionaryMatcherSourceOperator(DictionaryPredicate predicate, IDataStore dataStore) {
        this.resultCursor = -1;
        this.limit = Integer.MAX_VALUE;
        this.offset = 0;
        this.predicate = predicate;
        this.dataStore = dataStore;
    }

    /**
     * @about Opens dictionary matcher. Must call open() before calling
     *        getNextTuple().
     */
    @Override
    public void open() throws DataFlowException {
        try {
            currentDictionaryEntry = predicate.getNextDictionaryEntry();
            if (currentDictionaryEntry == null) {
                throw new DataFlowException("Dictionary is empty");
            }

            if (predicate.getKeywordMatchingType() == DataConstants.KeywordMatchingType.SUBSTRING_SCANBASED) {
                // For Substring matching, create a scan source operator.
                indexSource = new ScanBasedSourceOperator(dataStore, predicate.getAnalyzer());
                indexSource.open();

                // Substring matching's output schema needs to contains span
                // list.
                inputSchema = indexSource.getOutputSchema();
                outputSchema = inputSchema;
                if (!inputSchema.containsField(SchemaConstants.SPAN_LIST)) {
                    outputSchema = Utils.addAttributeToSchema(outputSchema, SchemaConstants.SPAN_LIST_ATTRIBUTE);
                }

            } else {
                // For other keyword matching types (conjunction and phrase),
                // create keyword matcher based on index.
                KeywordPredicate keywordPredicate = new KeywordPredicate(currentDictionaryEntry,
                        predicate.getAttributeList(), predicate.getAnalyzer(), predicate.getKeywordMatchingType());

                IndexBasedSourceOperator indexInputOperator = new IndexBasedSourceOperator(
                        keywordPredicate.generateDataReaderPredicate(dataStore));
                keywordMatcher = new KeywordMatcher(keywordPredicate);
                keywordMatcher.setInputOperator(indexInputOperator);
                keywordMatcher.open();

                // Other keyword matching types uses a KeywordMatcher, so the
                // output schema is the same as keywordMatcher's schema
                inputSchema = indexInputOperator.getOutputSchema();
                outputSchema = keywordMatcher.getOutputSchema();
            }

        } catch (Exception e) {
            throw new DataFlowException(e.getMessage(), e);
        }
    }

    /**
     * @about Gets the next matched tuple. <br>
     *        Returns the tuple with results in spanList. <br>
     * 
     *        Performs SCAN, KEYWORD_BASIC, or KEYWORD_PHRASE depends on the
     *        dictionary predicate. <br>
     * 
     *        DictionaryOperatorType.SCAN: <br>
     *        Scan the tuples using ScanSourceOperator. <br>
     *        For each tuple, loop through the dictionary and find results. <br>
     *        We assume the dictionary is smaller than the data at the source
     *        operator, we treat the data source as the outer relation to reduce
     *        the number of disk IOs. <br>
     * 
     *        DictionaryOperatorType.KEYWORD_BASIC, KEYWORD_PHRASE: <br>
     *        Use KeywordMatcher to find results. <br>
     * 
     *        KEYWORD_BASIC corresponds to KeywordOperatorType.BASIC, which
     *        performs keyword search on the document. The input query is
     *        tokenized. The order of the tokens doesn't matter. <br>
     * 
     *        KEYWORD_PHRASE corresponds to KeywordOperatorType.PHRASE, which
     *        performs phrase search on the document. The input query is
     *        tokenized. The order of the tokens does matter. Stopwords are
     *        treated as placeholders to indicate an arbitary token. <br>
     * 
     */
    @Override
    public ITuple getNextTuple() throws Exception {
        if (resultCursor >= limit + offset - 1) {
            return null;
        }
        if (predicate.getKeywordMatchingType() == DataConstants.KeywordMatchingType.PHRASE_INDEXBASED
                || predicate.getKeywordMatchingType() == DataConstants.KeywordMatchingType.CONJUNCTION_INDEXBASED) {
            // For each dictionary entry,
            // get all result from KeywordMatcher.

            while (true) {
                // If there's result from current keywordMatcher, return it.
                if ((sourceTuple = keywordMatcher.getNextTuple()) != null) {
                    resultCursor++;
                    if (resultCursor >= offset) {
                        return sourceTuple;
                    }
                    continue;
                }
                // If all results from current keywordMatcher are consumed,
                // advance to next dictionary entry, and
                // return null if reach the end of dictionary.
                if ((currentDictionaryEntry = predicate.getNextDictionaryEntry()) == null) {
                    return null;
                }

                // Construct a new KeywordMatcher with the new dictionary entry.
                KeywordMatchingType keywordMatchingType;
                if (predicate.getKeywordMatchingType() == DataConstants.KeywordMatchingType.PHRASE_INDEXBASED) {
                    keywordMatchingType = KeywordMatchingType.PHRASE_INDEXBASED;
                } else {
                    keywordMatchingType = KeywordMatchingType.CONJUNCTION_INDEXBASED;
                }

                keywordMatcher.close();

                KeywordPredicate keywordPredicate = new KeywordPredicate(currentDictionaryEntry,
                        predicate.getAttributeList(), predicate.getAnalyzer(), keywordMatchingType);

                IndexBasedSourceOperator indexInputOperator = new IndexBasedSourceOperator(
                        keywordPredicate.generateDataReaderPredicate(dataStore));
                keywordMatcher = new KeywordMatcher(keywordPredicate);
                keywordMatcher.setInputOperator(indexInputOperator);

                keywordMatcher.open();
            }
        }
        // Substring matching (based on scan)
        else {
            ITuple sourceTuple;
            ITuple resultTuple = null;
            while ((sourceTuple = indexSource.getNextTuple()) != null) {
                if (!inputSchema.containsField(SchemaConstants.SPAN_LIST)) {
                    sourceTuple = Utils.getSpanTuple(sourceTuple.getFields(), new ArrayList<Span>(), outputSchema);
                }
                resultTuple = computeMatchingResult(currentDictionaryEntry, sourceTuple);
                if (resultTuple != null) {
                    resultCursor++;
                }
                if (resultTuple != null && resultCursor >= offset) {
                    break;
                }
            }
            return resultTuple;
        }
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return this.offset;
    }

    /*
     * Advance the cursor of dictionary. if reach the end of the dictionary,
     * advance the cursor of tuples and reset dictionary
     */
    private void advanceDictionaryCursor() throws Exception {
        if ((currentDictionaryEntry = predicate.getNextDictionaryEntry()) != null) {
            return;
        }
        predicate.resetDictCursor();
        currentDictionaryEntry = predicate.getNextDictionaryEntry();
    }

    /*
     * Match the key against the dataTuple. if there's no match, returns the
     * original dataTuple object, if there's a match, return a new dataTuple
     * with span list added
     */
    private ITuple computeMatchingResult(String key, ITuple sourceTuple) throws Exception {

        List<Attribute> attributeList = predicate.getAttributeList();
        List<Span> matchingResults = new ArrayList<>();

        for (Attribute attr : attributeList) {
            String fieldName = attr.getFieldName();
            String fieldValue = sourceTuple.getField(fieldName).getValue().toString();

            // if attribute type is not TEXT, then key needs to match the
            // fieldValue exactly
            if (attr.getFieldType() != FieldType.TEXT) {
                if (fieldValue.equals(key)) {
                    matchingResults.add(new Span(fieldName, 0, fieldValue.length(), key, fieldValue));
                }
            }
            // if attribute type is TEXT, then key can match a substring of
            // fieldValue
            else {
                String regex = key.toLowerCase();
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(fieldValue.toLowerCase());
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();

                    matchingResults.add(new Span(fieldName, start, end, key, fieldValue.substring(start, end)));
                }
            }
        }

        advanceDictionaryCursor();

        if (matchingResults.size() == 0) {
            return null;
        }

        List<Span> spanList = (List<Span>) sourceTuple.getField(SchemaConstants.SPAN_LIST).getValue();
        spanList.addAll(matchingResults);

        return sourceTuple;
    }

    /**
     * @about Closes the operator
     */
    @Override
    public void close() throws DataFlowException {
        try {
            if (keywordMatcher != null) {
                keywordMatcher.close();
            }
            if (indexSource != null) {
                indexSource.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataFlowException(e.getMessage(), e);
        }
    }

    @Override
    public Schema getOutputSchema() {
        return outputSchema;
    }

    public DictionaryPredicate getPredicate() {
        return this.predicate;
    }

    public IDataStore getDataStore() {
        return this.dataStore;
    }
    
}
