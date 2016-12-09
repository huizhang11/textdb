package edu.uci.ics.textdb.textql.languageparser;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import edu.uci.ics.textdb.sandbox.terminalapp.PassThroughBean;
import edu.uci.ics.textdb.web.request.beans.OperatorBean;
import edu.uci.ics.textdb.web.request.beans.OperatorLinkBean;

public class CreateViewStatement extends Statement {
	/**
     * innerStatement is the statement to which the create view statement creates an alias for
     * e.g. in "CREATE VIEW v AS SELECT *FROM t; the view with id 'v' will have the
     * select statement "SELECT *FROM t" as innerStatement (in a SlectStatement object)
     */
    public Statement innerStatement;
    
  	public CreateViewStatement() {
		this(null, null);
  	}
  	public CreateViewStatement(String id, Statement innerStatement) {
  	  	super(id);
        this.innerStatement = innerStatement;
  	}
  	@Override
  	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj.getClass() != this.getClass()) { return false; }
		CreateViewStatement otherCreateViewStatement = (CreateViewStatement) obj;
		return new EqualsBuilder()
					.appendSuper(super.equals(otherCreateViewStatement))
					.append(innerStatement, otherCreateViewStatement.innerStatement)
					.isEquals();
  	}
  	@Override
  	public String toString() {
  		StringBuilder stringBuilder = new StringBuilder();
  		stringBuilder.append("CreateViewStatement(\n");
  		stringBuilder.append("\tName = " + this.id + "\n");
  		stringBuilder.append("\tinnerStatementName = " + this.innerStatement.id + "\n");
  		stringBuilder.append(")\n");
  		stringBuilder.append(innerStatement.toString());
  		return stringBuilder.toString();
  	}
  	public String getInputID(){
  		return this.id;
  	}
  	public String getOutputID(){
  		return this.id;
  	}
  	public List<OperatorLinkBean> getInternalLinkBeans(){
		return Collections.emptyList();
  	}
  	public List<OperatorBean> getInternalOperatorBeans(){
		return Arrays.asList(new PassThroughBean(this.id, "PassThrough", "", null, null));
  	}
  	public List<String> getRequiredViews(){
		return Arrays.asList(innerStatement.id);
  	}
}