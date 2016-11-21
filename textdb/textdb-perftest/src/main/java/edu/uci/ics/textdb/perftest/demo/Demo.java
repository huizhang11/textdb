package edu.uci.ics.textdb.perftest.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.uci.ics.textdb.api.common.Attribute;
import edu.uci.ics.textdb.api.common.FieldType;
import edu.uci.ics.textdb.api.common.ITuple;
import edu.uci.ics.textdb.api.common.Schema;
import edu.uci.ics.textdb.common.constants.LuceneAnalyzerConstants;
import edu.uci.ics.textdb.common.field.DataTuple;
import edu.uci.ics.textdb.common.field.TextField;
import edu.uci.ics.textdb.storage.relation.RelationManager;

public class Demo {
    
    public static Schema demoSchema = new Schema(new Attribute("content", FieldType.TEXT));
    
    public static String demoTableName = "demo";
    public static String demoTableDirectory = "../index/demo";
        
    public static void main(String[] args) throws Exception {
        System.out.println("write demo index");
        
        RelationManager.getRelationManager().createTable(
                demoTableName, 
                demoTableDirectory, 
                demoSchema,
                LuceneAnalyzerConstants.standardAnalyzerString());
        
        System.out.println("demo table have been successfully created");
        System.out.println("demo index path: " + RelationManager.getRelationManager().getTableDirectory(demoTableName));
        System.out.println("demo schema: " + RelationManager.getRelationManager().getTableSchema(demoTableName));

        File sourceFileFolder = new File("./data-files/CrawlerResultPromed");
        ArrayList<String> fileContents = new ArrayList<>();
        for (File htmlFile : sourceFileFolder.listFiles()) {
            StringBuilder sb = new StringBuilder();
            Scanner scanner = new Scanner(htmlFile);
            while (scanner.hasNext()) {
                sb.append(scanner.nextLine());
            }
            scanner.close();
            fileContents.add(sb.toString());
        }
        
        for (String fileStr : fileContents) {
            try {
                RelationManager.getRelationManager().insertTuple(
                        demoTableName, parsePromedHTML(fileStr));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        
        System.out.println("demo index have been successfully writeen");
    }
    
    
    public static ITuple parsePromedHTML(String content) throws Exception {
        Document parsedDocument = Jsoup.parse(content);
        String mainText = parsedDocument.getElementById("preview").text();
        ITuple tuple = new DataTuple(demoSchema, new TextField(mainText));
        return tuple;
    }
    
    
    
    

}
