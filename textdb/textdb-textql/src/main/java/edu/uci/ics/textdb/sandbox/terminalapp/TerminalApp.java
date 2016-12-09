package edu.uci.ics.textdb.sandbox.terminalapp;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONObject;
import org.json.JSONTokener;

import edu.uci.ics.textdb.common.constants.DataConstants.KeywordMatchingType;
import edu.uci.ics.textdb.common.exception.PlanGenException;
import edu.uci.ics.textdb.plangen.LogicalPlan;
import edu.uci.ics.textdb.plangen.operatorbuilder.DictionarySourceBuilder;
import edu.uci.ics.textdb.plangen.operatorbuilder.FileSinkBuilder;
import edu.uci.ics.textdb.plangen.operatorbuilder.KeywordMatcherBuilder;
import edu.uci.ics.textdb.plangen.operatorbuilder.OperatorBuilderUtils;
import edu.uci.ics.textdb.textql.languageparser.ParseException;
import edu.uci.ics.textdb.textql.languageparser.TextQLParser;
import edu.uci.ics.textdb.textql.languageparser.CreateViewStatement;
import edu.uci.ics.textdb.textql.languageparser.KeywordExtractPredicate;
import edu.uci.ics.textdb.textql.languageparser.SelectStatement;
import edu.uci.ics.textdb.textql.languageparser.Statement;
import edu.uci.ics.textdb.web.request.beans.DictionarySourceBean;
import edu.uci.ics.textdb.web.request.beans.FileSinkBean;
import edu.uci.ics.textdb.web.request.beans.KeywordMatcherBean;
import edu.uci.ics.textdb.web.request.beans.KeywordSourceBean;
import edu.uci.ics.textdb.web.request.beans.OperatorBean;
import edu.uci.ics.textdb.web.request.beans.OperatorLinkBean;
import edu.uci.ics.textdb.web.request.beans.ProjectionBean;

//The process of conversion from statements to a logical plan:
//The parser returns a List of main statements on the parsed input
//(main statements means that not all generated statements are on the list, e.g. the substatement of a create view statement)
//This program first extract these substatements
//The program check if one statement has the ID "out"(a view named out)
//if not just end, if so:
//    convert the statements into beans
//        create a map of <StatementId, Statement>
//        run a bfs starting from the 'out' view
//            append operator beans generated by Statement(an operator might create multiple beans)
//            append link beans generated by Statement
//            get list of dependency(other views required)
//                if the name of the dependency is a statement
//                    add a link going from one 
//                if not, treat the dependency as a table/directory(?)
//                    if there's no ben already for the scan table
//                        create a bean to scan the the table
//                    add link from the bean to the operator bean
//    manually add beans for a FileSink
//    simplify the beans:
//        remove the pass through bean
//        merge source and extract
//    create logical plan
//        append operator beans
//        append link beans
//        build plan
//
// TODO:
// -Limit/offset

public class TerminalApp {

    public static void main(String[] args) throws ParseException, PlanGenException {
        
    	// Parse input and print statements as they are parsed
        TextQLParser parser = new TextQLParser(System.in);
        List<Statement> parsedStatements = parser.mainStatementList(statement -> {
            System.out.println(statement.toString());
        });
        System.out.println("EOF");

        // extract subStatements to the main list
        extractSubstatements(parsedStatements);

        // Create a LogicalPlan if there's a view named 'out'
        if (parsedStatements.stream().anyMatch(s -> s.id.equals("out"))) {
            System.out.println("Found view named 'out!'");
            System.out.flush();
            // Bean containers
            ArrayList<OperatorBean> operatorBeans = new ArrayList<>();
            ArrayList<OperatorLinkBean> linkBeans = new ArrayList<>();

            // convert the statements into beans, that's the first step to
            // create a basic logical plan
            statementListToBeans(parsedStatements, "out", operatorBeans, linkBeans);

            // add FileSink using the view 'out' as input(so we can generate the
            // results)
            operatorBeans.add(new FileSinkBean("finaloutputsink", "FileSink", "", null, null, "out.txt"));
            linkBeans.add(new OperatorLinkBean("out", "finaloutputsink"));

            // print the first approach for a plan
            System.out.println("First Approach Graph:");
            System.out.println(new JSONObject(beansToMap(linkBeans, operatorBeans)).toString(4));

            // apply some basic simplifications to the beans
            removePassThoughBeans(operatorBeans, linkBeans);
            mergeSourceAndExtractBeans(operatorBeans, linkBeans);

            // print the plan after some optimizations
            System.out.println("Simplified Operator Graph:");
            System.out.println(new JSONObject(beansToMap(linkBeans, operatorBeans)).toString(4));

            // Create a LogicalPlan for real, add operators, links and build
            LogicalPlan lp = new LogicalPlan();
            for (OperatorBean operator : operatorBeans) {
                lp.addOperator(operator.getOperatorID(), operator.getOperatorType(), operator.getOperatorProperties());
            }
            for (OperatorLinkBean link : linkBeans) {
                lp.addLink(link.getFromOperatorID(), link.getToOperatorID());
            }
            lp.buildQueryPlan();
            // if no exception is thrown then the query plan was generated successfully
        }
    }

    /**
     * Convert a list of operator beans and a list of link beans to a map containing links and operators on the format
     * {
     *     "operators": [
     *         { "operator_id": "...", "operator_type": "...", operator_properties... },
     *         ...
     *     ],
     *        "links": [
     *            { "from": "...", "to": "..." },
     *         ...
     *     ]
     * }
     * 
     * @param linkBeans the list of link beans
     * @param operatorBeans the list of operator beans
     * @return The list of main statements
     */
    private static Map<String, List<Map<String, String>>> beansToMap(List<OperatorLinkBean> linkBeans,
            List<OperatorBean> operatorBeans) {
        // Create a list of { from, to } from the list of links
        List<Map<String, String>> links = linkBeans.stream()
                                                   .map(lb -> new HashMap<String, String>() {
                                                        {
                                                            put("from", lb.getFromOperatorID());
                                                            put("to", lb.getToOperatorID());
                                                        }
                                                    })
                                                   .collect(Collectors.toList());
        // Create a list of { operator_id, operator_type, other_properties } from the list of operators
        List<Map<String, String>> operators = operatorBeans.stream()
                                                    .map(ob -> new HashMap<String, String>() {
                                                        {
                                                            putAll(ob.getOperatorProperties());
                                                            put("operator_id", ob.getOperatorID());
                                                            put("operator_type", ob.getOperatorType());
                                                        }
                                                    })
                                                    .collect(Collectors.toList());
        // Create a graph as a map of { operators, links } with the lists created earlier
        HashMap<String, List<Map<String, String>>> graph = new HashMap<String, List<Map<String, String>>>() {
            {
                put("operators", operators);
                put("links", links);
            }
        };
        return graph;
    }

    /**
     * Extract substatements contained in the main statement list and append it to the list
     * Some statements such as CreateViewStatement have substatements contained within that the parser does not
     * return on the main statement list. This function add there statements to the list If a substatement is
     * already extracted, it will be duplicated!
     * 
     * @param parsedStatements
     * @return The list of main statements
     */
    private static void extractSubstatements(List<Statement> parsedStatements) {
        // iterate over the list of statements
        for (int i = 0; i < parsedStatements.size(); i++) {
            Statement statement = parsedStatements.get(i);
            // extract the substatement if the current statement is a CreateViewStatement
            if (statement instanceof CreateViewStatement) {
                CreateViewStatement createViewStatement = (CreateViewStatement) statement;
                parsedStatements.add(createViewStatement.innerStatement);
            }
        }
    }

    /**
     * Convert a list of statements into a list of OperatorBeans and OperatorLinkBean
     * A tree representing a possible query plan will be generated containing initialStatementId
     * on the top of the tree
     * 
     * @param statements The list of available statements to convert
     * @param initialStatementId The ID of the initial statement to be converted(top of the
     *            tree)
     * @param operators
     *            List of OperatorBean generated from the conversion
     * @param links
     *            List of OperatorLinkBean generated from the conversion
     * @return If the conversion was finished successfully
     */
    private static boolean statementListToBeans(List<Statement> statements, String initialStatementId,
            List<OperatorBean> operators, List<OperatorLinkBean> links) {
        // clear the output lists, so only the generated result will be in them
        operators.clear();
        links.clear();
        // build a map from StatementId to Statement and check for duplicate IDs
        Map<String, Statement> nameVsStatement = new HashMap<>();
        for (Statement statement : statements) {
            if (nameVsStatement.put(statement.id, statement) != null) {
                return false; // return fail if there are multiple statements with same name
            }
        }
        // Map of table(?directory) name to bean representing the ScanSourceBean of that table
        Map<String, ScanSourceBean> createdSourceBeans = new HashMap<>();
        // Execute a BFS and create the DAG
        Set<String> visitedStatements = new HashSet<>();
        Queue<String> toVisitStatements = new LinkedList<>(Arrays.asList(initialStatementId));// use initialStatementId as stating point
        while (!toVisitStatements.isEmpty()) {
            // get the name of next statement to visit
            String statementName = toVisitStatements.poll();
            // move statement from toVisit to visitedStatements
            if (visitedStatements.add(statementName) == false)
                continue; // skip if the statement is already on the visited statement list
            Statement statement = nameVsStatement.get(statementName);
            
            // add internal operators and links generated by the statement
            operators.addAll(statement.getInternalOperatorBeans());
            links.addAll(statement.getInternalLinkBeans());
            
            statement.getRequiredViews().forEach(dependency -> {
                if (nameVsStatement.containsKey(dependency)) {
                    // If there's a statement with the required name:
                    // add link and append it to the list of statements to visit
                    Statement requiredView = nameVsStatement.get(dependency);
                    links.add(new OperatorLinkBean(requiredView.getOutputID(), statement.getInputID()));
                    toVisitStatements.add(dependency);
                } else {
                    // No statement with the required name was found,
                    // assume it's a table name and create a ScanSourceBean for that table if it's not created yet
                    // and link it to the input of the statement
                    if (!createdSourceBeans.containsKey(dependency)) {
                        ScanSourceBean scanSourceBean = new ScanSourceBean(dependency, "ScanSource", "", null, null, dependency);
                        createdSourceBeans.put(dependency, scanSourceBean);
                        operators.add(scanSourceBean);
                    }
                    links.add(new OperatorLinkBean(dependency, statement.getInputID()));
                }
            });
        }
        //everything converted successfully!
        return true;
    }

    /**
     * Simplify the graph by removing passthrough operators from the operator list and changing links accordingly
     * E.g. the following subgraph:
     *      ... OperatorBean0 <- PassThroughBean <- OperatorBean1 ...
     *   Will be simplified to 
     *      ... OperatorBean0 <- OperatorBean1 ...
     * The input arity of the passthrough operator must always be 1,
     * The output arity of the passthrough operator may be more than 1
     * 
     * @param operators List of OperatorBean generated from the conversion
     * @param links List of OperatorLinkBean generated from the conversion
     * @return If the simplification was finished successfully
     */
    private static boolean removePassThoughBeans(List<OperatorBean> operators, List<OperatorLinkBean> links) {
        // Iterate over the list of operators looking for a PassThroughBean
        for (ListIterator<OperatorBean> operatorsIt = operators.listIterator(); operatorsIt.hasNext();) {
            OperatorBean operator = operatorsIt.next();
            if (operator instanceof PassThroughBean) {
                /*
                 *  found a PassThroughBean!
                 *  look for the links coming into it (linkIn) and
                 *  look for the link going out of it (linkOut), currently limited to one at maximum
                 *  here is the subgraph we are looking right now:
                 *  
                 *      ... OperatorBean0 <- PassThroughBean <- OperatorBean1 ...
                    *                         ^ (linkOut)        ^ (linkIn)
                    *                         
                    *  move the source of the linkOut to the source of the operator bean (skip this bean), we get
                    *  
                    *                     v (linkIn)
                 *                     v---- OperatorBean1 ...
                 *      ... OperatorBean0 <- PassThroughBean 
                 *                         ^ (linkOut)  
                 *                         
                 *  discard linkIn and the operator bean
                 *  
                 *      ... OperatorBean0 <- OperatorBean1 ...
                 *                         ^ (linkIn)  
                 *                         
                 *  just like removing an item in the middle of a linked list
                 */
                for (ListIterator<OperatorLinkBean> linkInIt = links.listIterator(); linkInIt.hasNext();) {
                    OperatorLinkBean linkIn = linkInIt.next();
                    if (!linkIn.getToOperatorID().equals(operator.getOperatorID()))
                        continue; //ignore if the link does not go to the current PassThroughBean
                    for (ListIterator<OperatorLinkBean> linkOutIt = links.listIterator(); linkOutIt.hasNext();) {
                        OperatorLinkBean linkOut = linkOutIt.next();
                        if (!linkOut.getFromOperatorID().equals(operator.getOperatorID()))
                            continue; //ignore if the link does not come from the current PassThroughBean
                        // for each output link, move the source of the link to the source of the PassThroughBean
                        linkOut.setFromOperatorID(linkIn.getFromOperatorID());
                    }
                    // remove the link going into the PassThroughBean
                    linkInIt.remove();
                }
                // remove the PassThroughBean
                operatorsIt.remove();
            }
        }
        // everything converted successfully!
        return true;

    }


    /**
     * Simplify and optimize the graph by merging stream matcher operators (such as keyword matcher) with scan source 
     * operators and generating the equivalent source operator
     * E.g. the following subgraph:
     *      ... KeywordMatcherBean <- ScanSourceBean 
     *   Will be simplified to 
     *      ... KeywordSourceBean 
     * 
     * @param operators List of OperatorBean generated from the conversion
     * @param links List of OperatorLinkBean generated from the conversion
     * @return If the simplification was finished successfully
     */
    private static boolean mergeSourceAndExtractBeans(List<OperatorBean> operators,    List<OperatorLinkBean> links) {
        // create a map of <operator type, merger function>, where the merger function takes one OperatorBean and
        // one ScanSourceBean, merge them and return the generated OperatorBean
        Map<String, BiFunction<OperatorBean, ScanSourceBean, OperatorBean>> matcherBeanToSourceBean;
        matcherBeanToSourceBean = new HashMap<String, BiFunction<OperatorBean, ScanSourceBean, OperatorBean>>() {
            {
                put("KeywordMatcher", (matcherBean, scanSourceBean) -> {
                    KeywordMatcherBean keywordMatcherBean = (KeywordMatcherBean)matcherBean;
                    return new KeywordSourceBean(keywordMatcherBean.getOperatorID(), "KeywordSource", 
                            keywordMatcherBean.getAttributes(), keywordMatcherBean.getLimit(),
                            keywordMatcherBean.getOffset(), keywordMatcherBean.getKeyword(),
                            keywordMatcherBean.getMatchingType(), scanSourceBean.getDataSource());
                    }
                );
                // add more operators in the future, dictionary, fuzzy token, regex, ...
            }
        };
        // We are going to iterate over operators, which should not be modified during iteration,
        // thus we are storing the operators to remove and to add separately so we can apply the changes
        // after the iteration is done
        List<OperatorBean> operatorsToRemove = new ArrayList<>();
        List<OperatorBean> operatorsToAdd = new ArrayList<>();
        // iterate over the operator list and look for an operator which has type that can be converted
        for (OperatorBean operator : operators) {
            if (matcherBeanToSourceBean.containsKey(operator.getOperatorType())) {
                // found a matcher bean that might be converted
                // look for the link that connects to the input of the matcher bean
                OperatorLinkBean operatorInputLink = links.stream()
                                                          .filter(t -> t.getToOperatorID().equals(operator.getOperatorID()) )
                                                          .findAny()
                                                          .orElse(null);
                // look for the operator bean that is used as the input for the matcher operator
                OperatorBean operatorInput = operators.stream()
                                                      .filter(t -> t.getOperatorID().equals(operatorInputLink.getFromOperatorID()) )
                                                      .findAny()
                                                      .orElse(null);
                if (operatorInput.getOperatorType().equals("ScanSource")) {// is the source the input of a matcher?
                    // create the new operator
                    OperatorBean newOperator = matcherBeanToSourceBean.get(operator.getOperatorType())
                                                                      .apply(operator, (ScanSourceBean) operatorInput);
                    // remove old operators and the link between them
                    links.remove(operatorInputLink);
                    operatorsToRemove.add(operatorInput);
                    operatorsToRemove.add(operator);
                    // add the created operator
                    operatorsToAdd.add(newOperator);
                }

            }
        }
        // apply changes to the operator list
        operators.removeAll(operatorsToRemove);
        operators.addAll(operatorsToAdd);
        // everything converted successfully!
        return true;
    }

}
