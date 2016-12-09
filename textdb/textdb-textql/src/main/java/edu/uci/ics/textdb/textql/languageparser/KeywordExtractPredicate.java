package edu.uci.ics.textdb.textql.languageparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import edu.uci.ics.textdb.common.constants.DataConstants.KeywordMatchingType;
import edu.uci.ics.textdb.web.request.beans.KeywordMatcherBean;
import edu.uci.ics.textdb.web.request.beans.OperatorBean;

public class KeywordExtractPredicate extends ExtractPredicate {
    //matchingFields is the list of fields which the keyword search should be performed
    public List<String> matchingFields;
    //keywords is the keyword(s) used for keyword search
    public String keywords;
    //matchingType specifies the type of keyword search to be done
    public String matchingType;

	private static final KeywordMatchingType DEFAULT_MATCH_TYPE = KeywordMatchingType.CONJUNCTION_INDEXBASED;
	
    public KeywordExtractPredicate() {
      this(null, null, null);
    }
    public KeywordExtractPredicate(List<String> matchingFields, String keywords, String matchingType) {
        this.matchingFields = matchingFields;
        this.keywords = keywords;
        this.matchingType = matchingType;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null) { return false; }
        if (other.getClass() != getClass()) { return false; }
        KeywordExtractPredicate keywordExtractPredicate = (KeywordExtractPredicate) other;
        return new EqualsBuilder()
                .appendSuper(super.equals(keywordExtractPredicate))
                .append(matchingFields, keywordExtractPredicate.matchingFields)
                .append(keywords, keywordExtractPredicate.keywords)
                .append(matchingType, keywordExtractPredicate.matchingType)
                .isEquals();
    }
    
	@Override
	public OperatorBean getExtractOperatorBean(String extractionOperatorId) {
		KeywordMatchingType keywordMatchingType = DEFAULT_MATCH_TYPE;
		if(this.matchingType!=null){
			Map<String, KeywordMatchingType> keywordMatchingTypeAlias = new HashMap<String, KeywordMatchingType>(){
				{
					put("conjunction", KeywordMatchingType.CONJUNCTION_INDEXBASED);
					put("phrase", KeywordMatchingType.PHRASE_INDEXBASED);
					put("substring", KeywordMatchingType.SUBSTRING_SCANBASED);
				}
			};
			keywordMatchingType = keywordMatchingTypeAlias.get(this.matchingType.toLowerCase());
		}
		return new KeywordMatcherBean(extractionOperatorId, "KeywordMatcher", String.join(",", this.matchingFields), null, 
					null, this.keywords, keywordMatchingType);
	}
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
      	stringBuilder.append("KeywordMatch(\n");
      	stringBuilder.append("\t\tMatchFields = " + this.matchingFields + "\n");
      	stringBuilder.append("\t\tKeyword = " + this.keywords + "\n");
      	stringBuilder.append("\t\tMatchType = " + this.matchingType + "\n");
      	stringBuilder.append("\t)\n");
      	return stringBuilder.toString();
	}
}