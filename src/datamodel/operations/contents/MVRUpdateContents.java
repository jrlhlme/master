package datamodel.operations.contents;

import datamodel.operations.Operation;

import java.util.ArrayList;
import java.util.List;

public class MVRUpdateContents implements OperationContents {

    private String key;
    private String value;
    private List<Operation> operationSequence;

    public MVRUpdateContents(String key, String value/*, String targetObjectType, String targetObjectId*/){
        this.key = key;
        this.value = value;
        this.operationSequence = new ArrayList<>();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void appendOperations(List<Operation> operations){
        this.operationSequence.addAll(operations);
    }

    public List<Operation> getOperationSequence() {
        return operationSequence;
    }

    @Override
    public MVRUpdateContents clone(){
        MVRUpdateContents oc = new MVRUpdateContents(this.key, this.value);
        this.operationSequence.forEach((op) -> oc.operationSequence.add(op.clone()));
        return oc;
    }

}
