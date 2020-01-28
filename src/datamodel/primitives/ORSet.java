package datamodel.primitives;

import datamodel.operations.Operation;
import datamodel.operations.OperationType;
import datamodel.operations.contents.ORUpdateContents;
import datamodel.primitives.tree.OperationResolveObject;
import datamodel.primitives.tree.Tree;
import datamodel.primitives.tree.TreeNode;

import java.util.*;

/**
 * Created by Jarl on 30-Nov-19.
 */
public class ORSet implements DataType {

    // reflects contents resulting from performed ops at the given time
    private Set<String> state_contents;

    // represents ownership of instance
    private int client_id;

    private Map<String, Tree> entries;

    public ORSet(int client_id){
        this.client_id = client_id;
        this.state_contents = new HashSet<>();

        this.entries = new HashMap<>();
    }

    public boolean processOperation(Operation operation){
        switch (operation.getOperationType()){
            case OperationType.ORSET_ADD: return addEntry(operation);
            case OperationType.ORSET_REMOVE: return deleteEntry(operation);
            default: return false;
        }
    }


    private boolean addEntry(Operation operation) {
        if (!(operation.getOperationContents().getClass() == ORUpdateContents.class)){
           return false;
        }

        ORUpdateContents operationContents = (ORUpdateContents)operation.getOperationContents();
        Tree opTree = getTreeForOperation(operationContents.getId());

        if (opTree == null){
            opTree = new Tree(operation);
            this.entries.put(operationContents.getId(), opTree);
        } else {
            OperationResolveObject operationResolveObject;
            if (operation.getClientId() == this.client_id) {
                operationResolveObject = opTree.createNode(operation);
            } else {
                operationResolveObject = opTree.addNode(operation, false);
            }
        }
        // concurrent ops not necessary to study as add wins concurrently
        this.state_contents.add(operationContents.getId());
        return true;
    }



    private boolean deleteEntry(Operation operation){
        if (!(operation.getOperationContents().getClass() == ORUpdateContents.class)){
            return false;
        }

        ORUpdateContents operationContents = (ORUpdateContents)operation.getOperationContents();
        Tree opTree = getTreeForOperation(operationContents.getId());
        if (opTree == null){
            return false;
        }

        try {
            OperationResolveObject operationResolveObject;
            if (operation.getClientId() == this.client_id) {
                operationResolveObject = opTree.createNode(operation);
            } else {
                operationResolveObject = opTree.addNode(operation, true);
            }

            if (operationResolveObject.getConcurrentOperationsRoot() == null){
                this.state_contents.remove(operationContents.getId());
            } else {
                // TODO test that this holds
                // we know the new op is a leaf AND that leaves are concurrent, as such if a single leaf is of type ORSET_ADD the entry persists
                List<TreeNode> leaves = operationResolveObject.getConcurrentOperationsRoot().getTree().getLeaflist();
                boolean concurrentAdd = false;
                for (TreeNode leaf : leaves){
                    if (leaf.getOperation().getOperationType() == OperationType.ORSET_ADD){
                        concurrentAdd = true;
                        break;
                    }
                }
                if (!concurrentAdd){
                    this.state_contents.remove(((ORUpdateContents)operation.getOperationContents()).getId());
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }


    private Tree getTreeForOperation(String key){
        return this.entries.get(key);
    }


    public Set<String> getStateContents() {
        return state_contents;
    }
}
