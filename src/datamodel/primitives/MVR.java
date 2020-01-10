package datamodel.primitives;

import datamodel.operations.Operation;
import datamodel.operations.OperationType;
import datamodel.operations.contents.MVRUpdateContents;
import datamodel.operations.contents.ORUpdateContents;

import java.util.*;

public class MVR implements DataType {

    private Map<String, ORSet> entries;

    // represents ownership of instance
    private int client_id;

    public MVR(int client_id){
        this.client_id = client_id;
        this.entries = new HashMap<>();
    }


    /**
     * Appends sequence of operations used for altering the sub-structures of the register to the contents of the provided operation
     * @return
     */
    private void generateOperations(Operation operation){
        MVRUpdateContents operationContents = (MVRUpdateContents) operation.getOperationContents();
        ORSet targetSet = this.entries.get(operationContents.getKey());
        if (targetSet == null) {
            targetSet = new ORSet(this.client_id);
            this.entries.put(operationContents.getKey(), targetSet);
        }

        Set<String> ORContents = new HashSet<>(targetSet.getStateContents());
        List<Operation> OROperations = new ArrayList<>();
        Operation OROperation;
        switch (operation.getOperationType()){
            case OperationType.MVR_SET:
                ORContents.add(operationContents.getValue());
                for (String orEntry : ORContents){
                    OROperation = new Operation(
                            operation.getClientId(),
                            new ORUpdateContents(orEntry),
                            null,
                            orEntry.equals(operationContents.getValue()) ? OperationType.ORSET_ADD : OperationType.ORSET_REMOVE,
                            false
                    );
                    targetSet.processOperation(OROperation);
                    OROperations.add(OROperation);
                }
                break;
            case OperationType.MVR_CLEAR:
                for (String orEntry : ORContents){
                    OROperation = new Operation(
                            operation.getClientId(),
                            new ORUpdateContents(orEntry),
                            null,
                            OperationType.ORSET_REMOVE,
                            false
                    );
                    targetSet.processOperation(OROperation);
                    OROperations.add(OROperation);
                }
        }

        operationContents.appendOperations(OROperations);
    }


    /**
     *
     * @param operation
     * @return
     */
    public boolean processOperation(Operation operation){
        MVRUpdateContents operationContents = (MVRUpdateContents) operation.getOperationContents();
        if (operation.getClientId() == this.client_id){
            generateOperations(operation);
        } else {
            ORSet targetSet = getSetFromKey(operationContents.getKey());
            if (targetSet == null){
                // set does not exist, create
                targetSet = new ORSet(this.client_id);
                this.entries.put(operationContents.getKey(), targetSet);
            }
            for (Operation op : operationContents.getOperationSequence()){
                targetSet.processOperation(op);
            }
        }
        return true;
    }



    public Set<String> get(String key){
        ORSet set = this.entries.get(key);
        return set == null ? null : set.getStateContents();
    }


    private ORSet getSetFromKey(String key){
        return this.entries.get(key);
    }

}
