package edu.uci.ics.textdb.dataflow.sink;

import edu.uci.ics.textdb.api.common.ITuple;
import edu.uci.ics.textdb.api.common.Schema;
import edu.uci.ics.textdb.api.dataflow.IOperator;
import edu.uci.ics.textdb.api.dataflow.ISink;
import edu.uci.ics.textdb.api.exception.TextDBException;
import edu.uci.ics.textdb.common.constants.SchemaConstants;
import edu.uci.ics.textdb.common.utils.Utils;

public class TupleStreamSink implements ISink {
    
    private IOperator inputOperator;
    
    private Schema inputSchema;
    private Schema outputSchema;

    @Override
    public Schema getOutputSchema() {
        return outputSchema;
    }

    @Override
    public void open() throws TextDBException {
        inputSchema = inputOperator.getOutputSchema();
        outputSchema = Utils.removeAttributeFromSchema(inputSchema, SchemaConstants.PAYLOAD);
        this.inputOperator.open();
    }

    @Override
    public void processTuples() throws TextDBException {
        // do nothing
    }

    @Override
    public void close() throws TextDBException {
        this.inputOperator.close();        
    }
    
    @Override
    public ITuple getNextTuple() throws TextDBException {
        ITuple nextTuple = inputOperator.getNextTuple();
        if (nextTuple == null) {
            return null;
        }
        if (inputSchema.containsField(SchemaConstants.PAYLOAD)) {
            return Utils.removePayload(inputOperator.getNextTuple());
        } else {
            return nextTuple;
        }
    }
    
    public void setInputOperator(IOperator inputOperator) {
        this.inputOperator = inputOperator;
    }

}
