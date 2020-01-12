package instance.assets;

import datamodel.operations.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class OperationStorage {

    private List<Operation> performedOperations;

    // POC structure to provide single concurrent instance with operations, to be expanded for full version
    private Queue<Operation> exportOperations;

    public OperationStorage(){
        this.performedOperations = new ArrayList<>();
        this.exportOperations = new LinkedBlockingQueue<>();
    }

    public void addOperation(Operation operation){
        this.performedOperations.add(operation);
        this.exportOperations.add(operation);
    }

    public Operation getExportOperation(){
        if (this.exportOperations.isEmpty()){
            return null;
        } else {
            return this.exportOperations.poll().clone();
        }
    }



}
