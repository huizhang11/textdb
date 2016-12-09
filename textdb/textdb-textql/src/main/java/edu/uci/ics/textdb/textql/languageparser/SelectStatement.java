package edu.uci.ics.textdb.textql.languageparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import edu.uci.ics.textdb.common.constants.DataConstants.KeywordMatchingType;
import edu.uci.ics.textdb.sandbox.terminalapp.PassThroughBean;
import edu.uci.ics.textdb.sandbox.terminalapp.ScanSourceBean;
import edu.uci.ics.textdb.web.request.beans.KeywordMatcherBean;
import edu.uci.ics.textdb.web.request.beans.OperatorBean;
import edu.uci.ics.textdb.web.request.beans.OperatorLinkBean;
import edu.uci.ics.textdb.web.request.beans.ProjectionBean;

public class SelectStatement extends Statement {
	//projectAll is set to true when '*' is used on the fields to be projected, as in "SELECT * ..."
    public Boolean projectAll;
    //projectedFields is the list of fields to be projected if it is specified as in "SELECT a, b, c ...""
    public List<String> projectedFields;
    //extractPredicate is the predicate for used for data extraction as keyword match in "KEYWORDMATCH(a,"word")"
    public ExtractPredicate extractPredicate;
    //fromClause is the identifier of a view, as in "SELECT... FROM viewName"
    public String fromClause;
    //limitClause is the maximum number of tuples to be returned, as in "SELECT...FROM... LIMIT 5"
    public Integer limitClause;
    //offsetClause is the number of tuples to be skipped before returning, as in "SELECT...FROM... OFFSET 5"
    public Integer offsetClause;

    public SelectStatement() {
        this(null, null, null, null, null, null, null);
    }
    public SelectStatement(String id, Boolean projectAll,
                          List<String> projectedFields, ExtractPredicate extractPredicate,
                          String fromClause, Integer limitClause, Integer offsetClause) {
        super(id);
        this.projectAll = projectAll;
        this.projectedFields = projectedFields;
        this.extractPredicate = extractPredicate;
        this.fromClause = fromClause;
        this.limitClause = limitClause;
        this.offsetClause = offsetClause;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) { return false; }
        if (other.getClass() != this.getClass()) { return false; }
        SelectStatement selectStatement = (SelectStatement) other;
        return new EqualsBuilder()
                    .appendSuper(super.equals(selectStatement))
                    .append(projectAll, selectStatement.projectAll)
                    .append(projectedFields, selectStatement.projectedFields)
                    .append(extractPredicate, selectStatement.extractPredicate)
                    .append(fromClause, selectStatement.fromClause)
                    .append(limitClause, selectStatement.limitClause)
                    .append(offsetClause, selectStatement.offsetClause)
                    .isEquals();
    }

	  //create a pass-through from __lid to __lidp
	  //create __lidX_p as project or pass-through
	  //create __lidX_e as extract or pass-through
	  //create __lidX_s as source or pass-through
  	public String getInputID(){
  		return this.id+"_s";
  	}
  	public String getOutputID(){
  		return this.id;
  	}
  	public List<OperatorLinkBean> getInternalLinkBeans(){
		return Arrays.asList(
				new OperatorLinkBean(this.id+"_p", this.id),
				new OperatorLinkBean(this.id+"_e", this.id+"_p"),
				new OperatorLinkBean(this.id+"_s", this.id+"_e")
			   );
  	}
  	public List<OperatorBean> getInternalOperatorBeans(){
  		List<OperatorBean> operators = new ArrayList<>();
  		//Select name pass through to project
  		operators.add(new PassThroughBean(this.id, "PassThrough", "", null, null));
  		//project
  		if(this.projectedFields==null){
  			operators.add(new PassThroughBean(this.id+"_p", "PassThrough", "", null, null));
  		}else{
			ProjectionBean pb = new ProjectionBean();
			pb.setOperatorID(this.id+"_p");
			pb.setOperatorType("Projection");
			pb.setAttributes(String.join(",", this.projectedFields));
			operators.add(pb);
		}
  		//extract
		if(this.extractPredicate==null){
			operators.add(new PassThroughBean(this.id+"_e", "PassThrough", "", null, null));
		}else{
			operators.add(this.extractPredicate.getExtractOperatorBean(this.id+"_e"));
		}
		//source
		operators.add(new PassThroughBean(this.id+"_s", "PassThrough", "", null, null));
		return operators;
  	}
  	public List<String> getRequiredViews(){
		return Arrays.asList(this.fromClause);
  	}
  	@Override
  	public String toString(){
  		StringBuilder stringBuilder = new StringBuilder();
  		stringBuilder.append("SelectStatement(\n");
  		stringBuilder.append("\tName = " + this.id + "\n");
  		stringBuilder.append("\tProjectAll = " + this.projectAll + "\n");
  		stringBuilder.append("\tProjectFields = " + this.projectedFields + "\n");
          if(this.extractPredicate==null){
        	  stringBuilder.append("\tExtractPredicate = null\n");
          }else{
        	  stringBuilder.append("\tExtractPredicate = " + this.extractPredicate);
          }
          stringBuilder.append("\tFrom = " + this.fromClause + "\n");
          stringBuilder.append("\tLimit = " + this.limitClause + "\n");
          stringBuilder.append("\tOffset = " + this.offsetClause + "\n");
          stringBuilder.append(")\n");
  		return stringBuilder.toString();
  	}
}




