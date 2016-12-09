package edu.uci.ics.textdb.sandbox.huang.sample;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.uci.ics.textdb.api.common.ITuple;
import edu.uci.ics.textdb.api.dataflow.ISourceOperator;
import edu.uci.ics.textdb.api.plan.Plan;
import edu.uci.ics.textdb.common.constants.DataConstants.KeywordMatchingType;
import edu.uci.ics.textdb.common.constants.LuceneAnalyzerConstants;
import edu.uci.ics.textdb.common.constants.SchemaConstants;
import edu.uci.ics.textdb.common.field.DataTuple;
import edu.uci.ics.textdb.common.field.StringField;
import edu.uci.ics.textdb.common.field.TextField;
import edu.uci.ics.textdb.common.utils.Utils;
import edu.uci.ics.textdb.dataflow.common.IJoinPredicate;
import edu.uci.ics.textdb.dataflow.common.JoinDistancePredicate;
import edu.uci.ics.textdb.dataflow.common.KeywordPredicate;
import edu.uci.ics.textdb.dataflow.common.RegexPredicate;
import edu.uci.ics.textdb.dataflow.join.Join;
import edu.uci.ics.textdb.dataflow.keywordmatch.KeywordMatcherSourceOperator;
import edu.uci.ics.textdb.dataflow.nlpextrator.NlpExtractor;
import edu.uci.ics.textdb.dataflow.nlpextrator.NlpPredicate;
import edu.uci.ics.textdb.dataflow.projection.ProjectionOperator;
import edu.uci.ics.textdb.dataflow.projection.ProjectionPredicate;
import edu.uci.ics.textdb.dataflow.regexmatch.RegexMatcher;
import edu.uci.ics.textdb.dataflow.sink.FileSink;
import edu.uci.ics.textdb.dataflow.sink.IndexSink;
import edu.uci.ics.textdb.dataflow.source.ScanBasedSourceOperator;
import edu.uci.ics.textdb.dataflow.source.TupleStreamSourceOperator;
import edu.uci.ics.textdb.engine.Engine;
import edu.uci.ics.textdb.perftest.promed.PromedSchema;
import edu.uci.ics.textdb.perftest.utils.PerfTestUtils;
import edu.uci.ics.textdb.storage.DataStore;
import edu.uci.ics.textdb.storage.writer.DataWriter;


public class Extractor {

    public static final String promedFilesDirectory = "./sample-data-files/promed/";
    public static final String promedIndexDirectory = "./index/standard/promed/"; 
    public static final DataStore promedDataStore = new DataStore(promedIndexDirectory, PromedSchema.PROMED_SCHEMA);
    
    
    public static void main(String[] args) throws Exception {
        
        // write the index of data files
        // index only needs to be written once, after the first run, this function can be commented out
        writeSampleIndex();
        
        // perform the extraction task
//        extractPersonLocation();
//        extractPersonLocation_KeywordOnly();
        extractPersonLocation_RejexOnly();
    }
    
    public static ITuple parsePromedHTML(String fileName, String content) {
        try {
            Document parsedDocument = Jsoup.parse(content);
            String mainText = parsedDocument.getElementById("preview").text();
            ITuple tuple = new DataTuple(PromedSchema.PROMED_SCHEMA, new StringField(fileName), new TextField(mainText));
            return tuple;
        } catch (Exception e) {
            return null;
        }
    }

    
    public static void writeSampleIndex() throws Exception {
        // clear the directory
        new DataWriter(promedDataStore, LuceneAnalyzerConstants.getStandardAnalyzer()).clearData();
        
        File sourceFileFolder = new File(promedFilesDirectory);
        ArrayList<ITuple> fileTuples = new ArrayList<>();
        for (File htmlFile : sourceFileFolder.listFiles()) {
            StringBuilder sb = new StringBuilder();
            Scanner scanner = new Scanner(htmlFile);
            while (scanner.hasNext()) {
                sb.append(scanner.nextLine());
            }
            scanner.close();
            ITuple tuple = parsePromedHTML(htmlFile.getName(), sb.toString());
            if (tuple != null) {
                fileTuples.add(tuple);
            }
        }

        ISourceOperator fileSource = new TupleStreamSourceOperator(fileTuples,
                PromedSchema.PROMED_SCHEMA);

        IndexSink standardIndexSink = new IndexSink(promedIndexDirectory, PromedSchema.PROMED_SCHEMA,
                new StandardAnalyzer(), false);
        standardIndexSink.setInputOperator(fileSource);
        Plan standardIndexPlan = new Plan(standardIndexSink);

        Engine engine = Engine.getEngine();
        engine.evaluate(standardIndexPlan);

        Engine.getEngine().evaluate(standardIndexPlan);
    }
    
    /*
     * This is the DAG of KeywordOnly extraction plan.
     * Output: look for locations in a file with the Keyword
     * 
     *              KeywordSource (zika)
     *                       ↓
     *              Projection (content)
     *                       ↓
     *                NLP (location)
     *                       ↓
     * Projection (ID:filename; spanList(start:109; 
     * 									   end:115; 
     *                                     key:Location; 
     *                                     value: Brazil;
     *                                     token; offset:-1) )
     *                       ↓
     *                    FileSink
     *                    
     */
    public static void extractPersonLocation_KeywordOnly() throws Exception {
        
        String keywordZika = "zika";
        KeywordPredicate keywordPredicateZika = new KeywordPredicate(keywordZika, Arrays.asList(PromedSchema.CONTENT),
                new StandardAnalyzer(), KeywordMatchingType.CONJUNCTION_INDEXBASED);
        
        KeywordMatcherSourceOperator keywordSource = new KeywordMatcherSourceOperator(
                keywordPredicateZika, promedDataStore);       
        
        ProjectionPredicate projectionPredicateIdAndContent = new ProjectionPredicate(
                Arrays.asList(PromedSchema.ID, PromedSchema.CONTENT));
        
        ProjectionOperator projectionOperatorIdAndContent1 = new ProjectionOperator(projectionPredicateIdAndContent);

        NlpPredicate nlpPredicateLocation = new NlpPredicate(NlpPredicate.NlpTokenType.Location, Arrays.asList(PromedSchema.CONTENT_ATTR));
        NlpExtractor nlpExtractorLocation = new NlpExtractor(nlpPredicateLocation);
        //output part setting
        ProjectionPredicate projectionPredicateIdAndSpan = new ProjectionPredicate(
                Arrays.asList(PromedSchema.ID, SchemaConstants.SPAN_LIST));
        ProjectionOperator projectionOperatorIdAndSpan = new ProjectionOperator(projectionPredicateIdAndSpan);  
        
        FileSink fileSink = new FileSink( 
                new File("./sample-data-files/person-location-result-"+PerfTestUtils.formatTime(System.currentTimeMillis())+".txt"));
        fileSink.setToStringFunction((tuple -> Utils.getTupleString(tuple)));
        
        //link  stage 1
        /*			KeywordSource (zika)
        *                       ↓
        *              Projection (content)
        *                       ↓
        *                NLP (location)
        */              
        projectionOperatorIdAndContent1.setInputOperator(keywordSource); 
        nlpExtractorLocation.setInputOperator(projectionOperatorIdAndContent1);
        
        //link stage 2
        /*				    NLP (location)
         *                       ↓
         *              Projection (ID, spanList)
         *                       ↓
         *                    FileSink
         */ 
        projectionOperatorIdAndSpan.setInputOperator(nlpExtractorLocation);
        fileSink.setInputOperator(projectionOperatorIdAndSpan);
        
        Plan extractPersonPlan = new Plan(fileSink);
        Engine.getEngine().evaluate(extractPersonPlan);
    }
    
    /*
     * This is the DAG of RejexOnly extraction plan.
     * Output: locations within distance of 100 to a ReGex pattern
     * 
     *                   projection (ID:filename, Content)
     *                        ↓
     *                  regex ( A|a|(an)|(An)) .{1,40} ((woman)|(man) )
     *                  ↓          ↓
     *                  ↓        NLP (location)
     *                  ↓          ↓     
     *             Join (distance < 100)
     *                       ↓
     *              Projection ( (ID:spanList):  (ID:filename; spanList(start:109; end:115; 
     *                                                              key:(Location___\b(A|a|(an)|(An)) .{1,40} ((woman)|(man))\b); 
     *                                                              value: Brazil
     *                                                              Brazil. 28 Jan 2016. A 57-year-old Viennese woman;
     *                                                              token; offset:-1) ) )
     *                       ↓
     *                    FileSink
     *                    
     */
    public static void extractPersonLocation_RejexOnly() throws Exception {
        
        ScanBasedSourceOperator scanSource = new ScanBasedSourceOperator(promedDataStore);
        
        ProjectionPredicate projectionPredicateIdAndContent = new ProjectionPredicate(
                Arrays.asList(PromedSchema.ID, PromedSchema.CONTENT));
        
        ProjectionOperator projectionOperatorIdAndContent2 = new ProjectionOperator(projectionPredicateIdAndContent);

        String regexPerson = "\\b(A|a|(an)|(An)) .{1,40} ((woman)|(man))\\b";
        RegexPredicate regexPredicatePerson = new RegexPredicate(regexPerson, Arrays.asList(PromedSchema.CONTENT_ATTR),
                LuceneAnalyzerConstants.getNGramAnalyzer(3));
        RegexMatcher regexMatcherPerson = new RegexMatcher(regexPredicatePerson);
        
        NlpPredicate nlpPredicateLocation = new NlpPredicate(NlpPredicate.NlpTokenType.Location, Arrays.asList(PromedSchema.CONTENT_ATTR));
        NlpExtractor nlpExtractorLocation = new NlpExtractor(nlpPredicateLocation);
 
        //Condition of Join: distance less than 100
        IJoinPredicate joinPredicatePersonLocation = new JoinDistancePredicate(PromedSchema.ID, PromedSchema.CONTENT, 100);
        Join joinPersonLocation = new Join(null, null, joinPredicatePersonLocation);
        //output Part settings
        ProjectionPredicate projectionPredicateIdAndSpan = new ProjectionPredicate(
                Arrays.asList(PromedSchema.ID, SchemaConstants.SPAN_LIST));
        ProjectionOperator projectionOperatorIdAndSpan = new ProjectionOperator(projectionPredicateIdAndSpan);

        FileSink fileSink = new FileSink( 
                new File("./sample-data-files/person-location-result-"+PerfTestUtils.formatTime(System.currentTimeMillis())+".txt"));
        fileSink.setToStringFunction((tuple -> Utils.getTupleString(tuple)));
        
        //link stage 1
        regexMatcherPerson.setInputOperator(scanSource);
        /*
        *                   projection (ID:filename, Content)
        *                               ↓
        *                  regex ( A|a|(an)|(An)) .{1,40} ((woman)|(man) )
        */
        projectionOperatorIdAndContent2.setInputOperator(regexMatcherPerson);
        
        /* link stage 2
        *                  regex ( A|a|(an)|(An)) .{1,40} ((woman)|(man) )
        *                                       ↓
        *                                  NLP (location)        
        */
        nlpExtractorLocation.setInputOperator(projectionOperatorIdAndContent2);
        /* link stage 3
         * 
         *                 regex            NLP 
         *                   ↓               ↓   
         *                 Join (condition: distance < 100)
         */
        joinPersonLocation.setInnerInputOperator(regexMatcherPerson);
        joinPersonLocation.setOuterInputOperator(nlpExtractorLocation);
       /*                              ↓
        * 						projection(ID, spanlist())
        *                            ↓
        *                       FileSink
        */
        projectionOperatorIdAndSpan.setInputOperator(joinPersonLocation);
        fileSink.setInputOperator(projectionOperatorIdAndSpan);
        
        Plan extractPersonPlan = new Plan(fileSink);
        Engine.getEngine().evaluate(extractPersonPlan);
    }
    
    
    /*
     * This is the DAG of this extraction plan.
     * 
     * 
     *              KeywordSource (zika)
     *                       ↓
     *              Projection (content)
     *                  ↓          ↓
     *       regex (a...man)      NLP (location)
     *                  ↓          ↓     
     *             Join (distance < 100)
     *                       ↓
     *              Projection (spanList)
     *                       ↓
     *                    FileSink
     *                    
     */
    public static void extractPersonLocation() throws Exception {
                
        String keywordZika = "zika";
        KeywordPredicate keywordPredicateZika = new KeywordPredicate(keywordZika, Arrays.asList(PromedSchema.CONTENT),
                new StandardAnalyzer(), KeywordMatchingType.CONJUNCTION_INDEXBASED);
        
        KeywordMatcherSourceOperator keywordSource = new KeywordMatcherSourceOperator(
                keywordPredicateZika, promedDataStore);
        
        ProjectionPredicate projectionPredicateIdAndContent = new ProjectionPredicate(
                Arrays.asList(PromedSchema.ID, PromedSchema.CONTENT));
        
        ProjectionOperator projectionOperatorIdAndContent1 = new ProjectionOperator(projectionPredicateIdAndContent);
        ProjectionOperator projectionOperatorIdAndContent2 = new ProjectionOperator(projectionPredicateIdAndContent);

        String regexPerson = "\\b(A|a|(an)|(An)) .{1,40} ((woman)|(man))\\b";
        RegexPredicate regexPredicatePerson = new RegexPredicate(regexPerson, Arrays.asList(PromedSchema.CONTENT_ATTR),
                LuceneAnalyzerConstants.getNGramAnalyzer(3));
        RegexMatcher regexMatcherPerson = new RegexMatcher(regexPredicatePerson);
        
        NlpPredicate nlpPredicateLocation = new NlpPredicate(NlpPredicate.NlpTokenType.Location, Arrays.asList(PromedSchema.CONTENT_ATTR));
        NlpExtractor nlpExtractorLocation = new NlpExtractor(nlpPredicateLocation);
//less than 100 
        IJoinPredicate joinPredicatePersonLocation = new JoinDistancePredicate(PromedSchema.ID, PromedSchema.CONTENT, 100);
        Join joinPersonLocation = new Join(null, null, joinPredicatePersonLocation);
        
        ProjectionPredicate projectionPredicateIdAndSpan = new ProjectionPredicate(
                Arrays.asList(PromedSchema.ID, SchemaConstants.SPAN_LIST));
        ProjectionOperator projectionOperatorIdAndSpan = new ProjectionOperator(projectionPredicateIdAndSpan);

        FileSink fileSink = new FileSink( 
                new File("./sample-data-files/person-location-result-"+PerfTestUtils.formatTime(System.currentTimeMillis())+".txt"));
        fileSink.setToStringFunction((tuple -> Utils.getTupleString(tuple)));
        
        
        projectionOperatorIdAndContent1.setInputOperator(keywordSource);
        
        regexMatcherPerson.setInputOperator(projectionOperatorIdAndContent1);
        
        projectionOperatorIdAndContent2.setInputOperator(regexMatcherPerson);
        nlpExtractorLocation.setInputOperator(projectionOperatorIdAndContent1);
        
        joinPersonLocation.setInnerInputOperator(regexMatcherPerson);
        joinPersonLocation.setOuterInputOperator(nlpExtractorLocation);
                      
        projectionOperatorIdAndSpan.setInputOperator(joinPersonLocation);
        fileSink.setInputOperator(projectionOperatorIdAndSpan);
        
        Plan extractPersonPlan = new Plan(fileSink);
        Engine.getEngine().evaluate(extractPersonPlan);
    }
}
