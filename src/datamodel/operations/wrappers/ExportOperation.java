package datamodel.operations.wrappers;

import datamodel.operations.Operation;

public class ExportOperation {

    private Operation operation;

    private int target_object_type;

    // optional param, used for editing relations
    private String target_id;

    public ExportOperation(Operation operation, int target_object_type, String target_id){
        this.operation = operation;
        this.target_object_type = target_object_type;
        this.target_id = target_id;
    }

    @Override
    public ExportOperation clone(){
        return new ExportOperation(operation.clone(), target_object_type, target_id);
    }

    public Operation getOperation() {
        return operation;
    }

    public int getTargetObjectType() {
        return target_object_type;
    }

    public String getTargetId() {
        return target_id;
    }
}
