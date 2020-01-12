package datamodel.operations;

import datamodel.operations.contents.OperationContents;
import datamodel.primitives.Vectorclock;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Jarl on 04-Dec-19.
 */
public class Operation {

    // stores parent treenode vectorclocks (vectorclocks for preceding ops)
    // generated upon performing the operation on a tree
    private List<Vectorclock> preceding_operations_vectorclocks;

    private Integer client_id;

    // whether the operation is the result of another cascading operation and can be discarded once dominated
    private boolean is_cascading_op;

    private Integer operation_type;
    private OperationContents operationContents;


    public Operation(int client_id, OperationContents payload, List<Vectorclock> preceding_operation_vectorclocks, int operation_type, boolean is_cascading_op){
        this.client_id = client_id;
        this.preceding_operations_vectorclocks = preceding_operation_vectorclocks;
        this.operation_type = operation_type;
        this.operationContents = payload;
        this.is_cascading_op = is_cascading_op;
    }

    @Override
    public Operation clone(){
        List<Vectorclock> preceding_vectorclocks = this.preceding_operations_vectorclocks.stream().map((Function<Vectorclock, Vectorclock>) Vectorclock::clone).collect(Collectors.toList());
        return new Operation(
                this.client_id,
                this.operationContents.clone(),
                preceding_vectorclocks,
                this.operation_type,
                this.isCascadingOp()
        );
    }


    public Integer getClientId(){
        return client_id;
    }

    public List<Vectorclock> getPrecedingOperationVectorClocks() {
        // tree vector clock state BEFORE op performed
        return preceding_operations_vectorclocks;
    }

    public OperationContents getOperationContents() {
        return operationContents;
    }

    public Integer getOperationType() {
        return operation_type;
    }


    public boolean isCascadingOp() {
        return is_cascading_op;
    }

    public void setPreceding_operations_vectorclocks(List<Vectorclock> preceding_operations_vectorclocks) {
        this.preceding_operations_vectorclocks = preceding_operations_vectorclocks;
    }
}
