package datamodel.operations;

import datamodel.primitives.Vectorclock;

import java.util.List;

/**
 * Created by Jarl on 04-Dec-19.
 */
public class Operation {


    // vectorclock in the Tree to contain the operation
    private Vectorclock operation_vectorclock;

    // stores parent treenode vectorclocks (vectorclocks for preceding ops)
    private List<Vectorclock> preceding_operation_vectorclocks;

    private OperationType optype;

    private Integer client_id; // TODO own class?
    private Integer object_id;

    private Object payload;


    public Operation(Vectorclock vectorclock, int client_id, int object_id, Object payload, List<Vectorclock> preceding_operation_vectorclocks){
        this.operation_vectorclock = vectorclock;
        this.client_id = client_id;
        this.object_id = object_id;
        this.preceding_operation_vectorclocks = preceding_operation_vectorclocks;

        //TODO debug
        this.payload = payload;
    }


    public Integer getClientId(){
        return client_id;
    }

    public Vectorclock getVectorClock() {
        return operation_vectorclock;
    }

    public List<Vectorclock> getPrecedingOperationVectorClocks() {
        // tree vector clock state BEFORE op performed
        return preceding_operation_vectorclocks;
    }


    public Integer getObjectId() {
        return object_id;
    }

}
