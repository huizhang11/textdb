package edu.uci.ics.textdb.sandbox.terminalapp;

import java.util.HashMap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import edu.uci.ics.textdb.plangen.operatorbuilder.OperatorBuilderUtils;
import edu.uci.ics.textdb.web.request.beans.OperatorBean;

public class ScanSourceBean extends OperatorBean {
    private String dataSource;
    public ScanSourceBean(String operatorID, String operatorType, String attributes, String limit, String offset, String dataSource) {
        super(operatorID, "ScanSource", attributes, limit, offset);
        this.dataSource = dataSource;
    }
    public String getDataSource() {
        return dataSource;
    }
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
    public HashMap<String, String> getOperatorProperties() {
        HashMap<String, String> operatorProperties = super.getOperatorProperties();
        if(this.getDataSource() == null || operatorProperties == null)
            return null;
        operatorProperties.put(OperatorBuilderUtils.DATA_DIRECTORY, this.getDataSource());
        return operatorProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof ScanSourceBean)) return false;
        ScanSourceBean scanSourceBean = (ScanSourceBean) other;
        return new EqualsBuilder()
        		.appendSuper(super.equals(scanSourceBean))
                .append(dataSource, scanSourceBean.getDataSource())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
        		.append(super.hashCode())
                .append(dataSource)
                .toHashCode();
    }
}