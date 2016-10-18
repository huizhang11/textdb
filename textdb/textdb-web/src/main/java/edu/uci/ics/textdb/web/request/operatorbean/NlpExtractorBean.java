package edu.uci.ics.textdb.web.request.operatorbean;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.textdb.dataflow.nlpextrator.NlpPredicate.NlpTokenType;
import edu.uci.ics.textdb.web.request.OperatorBean;

/**
 * This class defines the properties/data members specific to the NlpExtractor operator
 * and extends the OperatorBean class which defines the data members general to all operators
 * Created by kishorenarendran on 10/17/16.
 */
public class NlpExtractorBean extends OperatorBean {
    @JsonProperty("nlp_type")
    private NlpTokenType nlpTokenType;

    public NlpExtractorBean() {
    }

    public NlpExtractorBean(String operatorID, String operatorType, NlpTokenType nlpTokenType) {
        super(operatorID, operatorType);
        this.nlpTokenType = nlpTokenType;
    }

    @JsonProperty("nlp_type")
    public NlpTokenType getNlpTokenType() {
        return nlpTokenType;
    }

    @JsonProperty("nlp_type")
    public void setNlpTokenType(NlpTokenType nlpTokenType) {
        this.nlpTokenType = nlpTokenType;
    }
}
