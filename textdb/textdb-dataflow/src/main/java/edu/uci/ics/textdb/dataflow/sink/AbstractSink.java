package edu.uci.ics.textdb.dataflow.sink;

import edu.uci.ics.textdb.api.common.ITuple;
import edu.uci.ics.textdb.api.common.Schema;
import edu.uci.ics.textdb.api.dataflow.IOperator;
import edu.uci.ics.textdb.api.dataflow.ISink;
import edu.uci.ics.textdb.api.exception.TextDBException;
import edu.uci.ics.textdb.common.constants.SchemaConstants;
import edu.uci.ics.textdb.common.utils.Utils;

/**
 * Created by chenli on 5/11/16.
 *
 * This abstract class leaves the @processOneTuple() function to be implemented
 * by the subclass based on the logic of handling each tuple coming from the
 * subtree.
 *
 */
public abstract class AbstractSink implements ISink {

    private IOperator inputOperator;
    private Schema inputSchema;
    private Schema outputSchema;

    /**
     * @about Opens the child operator.
     */
    @Override
    public void open() throws TextDBException {
        inputSchema = inputOperator.getOutputSchema();
        outputSchema = Utils.removeAttributeFromSchema(inputSchema, SchemaConstants.PAYLOAD);
        this.inputOperator.open();
    }

    public void setInputOperator(IOperator inputOperator) {
        this.inputOperator = inputOperator;
    }

    public IOperator getInputOperator() {
        return this.inputOperator;
    }

    @Override
    public void processTuples() throws TextDBException {
        ITuple nextTuple;

        while ((nextTuple = inputOperator.getNextTuple()) != null) {
            processOneTuple(nextTuple);
        }
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

    /**
     *
     * @param nextTuple
     *            A tuple that needs to be processed during each iteration
     */
    protected abstract void processOneTuple(ITuple nextTuple) throws TextDBException;

    @Override
    public void close() throws TextDBException {
        inputOperator.close();
    }
    
    public Schema getOutputSchema() {
        return outputSchema;
    }
}
