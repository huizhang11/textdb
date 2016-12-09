package edu.uci.ics.textdb.textql.languageparser;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import edu.uci.ics.textdb.web.request.beans.OperatorBean;
import edu.uci.ics.textdb.web.request.beans.OperatorLinkBean;

/**
 * Statement class and subclasses(SelectStatement, CreateViewStatement)
 * Each Statement class has an id. Subclasses of Statements have specific
 * fields related to its function. These classes have no methods, they are
 * used only as containers to move data.
 * Statement --+ SelectStatement
 *             + CreateViewStatement
 */
public abstract class Statement {
    //Identifier of each Statement
	public String id;
	
  	public Statement() {
		this(null);
  	}
  	
  	public Statement(String id) {
		this.id = id;
  	}
  	
  	public abstract String getInputID();
  	public abstract String getOutputID();
  	public abstract List<OperatorLinkBean> getInternalLinkBeans();
  	public abstract List<OperatorBean> getInternalOperatorBeans();
  	public abstract List<String> getRequiredViews();
  	
  	@Override
  	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj.getClass() != this.getClass()) { return false; }
		Statement kep = (Statement) obj;
		return new EqualsBuilder()
					.append(id, kep.id)
					.isEquals();
  	}
}
