package edu.uci.ics.textdb.dataflow.source;

import java.util.Collection;
import java.util.Iterator;

import edu.uci.ics.textdb.api.common.ITuple;
import edu.uci.ics.textdb.api.common.Schema;
import edu.uci.ics.textdb.api.dataflow.ISourceOperator;
import edu.uci.ics.textdb.api.exception.TextDBException;

/*
 * This operator takes an in-memory list of tuples and serves them as a source operator.
 * 
 */
public class TupleStreamSourceOperator implements ISourceOperator {
    
    private Collection<ITuple> tupleInputs;
    private Iterator<ITuple> tupleIterator;
    private Schema outputSchema;
    
    private boolean isOpen;
    
    public TupleStreamSourceOperator(Collection<ITuple> tupleInputs, Schema schema) {
        this.tupleInputs = tupleInputs;
        this.outputSchema = schema;
        this.tupleIterator = null;
        this.isOpen = false;
    }

    @Override
    public void open() throws TextDBException {
        if (isOpen) {
            return;
        }
        tupleIterator = tupleInputs.iterator();        
    }

    @Override
    public ITuple getNextTuple() throws TextDBException {
        if (tupleIterator.hasNext()) {
            return tupleIterator.next();
        } else {
            return null;
        }
    }

    @Override
    public void close() throws TextDBException {
        isOpen = false;        
    }

    @Override
    public Schema getOutputSchema() {
        return outputSchema;
    }
    
}
