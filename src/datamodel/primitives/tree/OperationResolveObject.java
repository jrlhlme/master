package datamodel.primitives.tree;

import datamodel.operations.Operation;

import java.util.List;

public class OperationResolveObject {

    private Operation operation;
    private TreeNode concurrentOperationsRoot;

    /**
     * Object storing operation to be executed along with concurrent operations that needs to be reasoned with
     * @param operation to be executed
     * @param concurrentOperationsRoot root of concurrent operations
     */
    public OperationResolveObject(Operation operation, TreeNode concurrentOperationsRoot){
        this.concurrentOperationsRoot = concurrentOperationsRoot;
        this.operation = operation;
    }


    public Operation getOperation() {
        return operation;
    }

    public TreeNode getConcurrentOperationsRoot() {
        return concurrentOperationsRoot;
    }


}
