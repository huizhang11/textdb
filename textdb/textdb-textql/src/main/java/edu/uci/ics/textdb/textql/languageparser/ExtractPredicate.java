package edu.uci.ics.textdb.textql.languageparser;

import edu.uci.ics.textdb.web.request.beans.OperatorBean;

/**
 * ExtractPredicate class and its subclasses such as KeywordExtractPredicate
 * Subclasses have specific fields related to its extraction functionalities
 * These classes have no methods. They are used only as containers
 * ExtractPredicate --+ KeywordExtractPredicate
 */
public abstract class ExtractPredicate {
    @Override
    public boolean equals(Object other) {
        if (other == null) { return false; }
        if (other.getClass() != getClass()) { return false; }
        //since ExtractPredicate has no attributes, there's nothing to compare but the class itself
        return true;
    }

	public abstract OperatorBean getExtractOperatorBean(String extractionOperatorId);
}