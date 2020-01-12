package datamodel.operations.wrappers;

import datamodel.operations.Operation;

public class ExportOperation {

    private Operation operation;

    private int target_object_type;

    public ExportOperation(Operation operation, int target_object_type){
        this.operation = operation;
        this.target_object_type = target_object_type;
    }

    @Override
    public ExportOperation clone(){
        return new ExportOperation(operation.clone(), target_object_type);
    }

    public Operation getOperation() {
        return operation;
    }

    public int getTargetObjectType() {
        return target_object_type;
    }
}
