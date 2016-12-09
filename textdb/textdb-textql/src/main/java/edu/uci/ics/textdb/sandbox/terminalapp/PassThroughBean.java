package edu.uci.ics.textdb.sandbox.terminalapp;

import java.util.HashMap;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import edu.uci.ics.textdb.sandbox.terminalapp.PassThroughBean;
import edu.uci.ics.textdb.web.request.beans.OperatorBean;

public class PassThroughBean extends OperatorBean {
    public PassThroughBean(String operatorID, String operatorType, String attributes, String limit, String offset) {
        super(operatorID, "PassThrough", attributes, limit, offset);
    }
    public HashMap<String, String> getOperatorProperties() {
        HashMap<String, String> operatorProperties = super.getOperatorProperties();
        if(operatorProperties == null)
            return null;
        return operatorProperties;
    }
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof PassThroughBean)) return false;
        PassThroughBean passThroughBean = (PassThroughBean) other;
        return super.equals(passThroughBean);
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
        			.toHashCode();
    }
}