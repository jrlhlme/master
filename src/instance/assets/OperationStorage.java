package instance.assets;

import datamodel.operations.Operation;
import datamodel.operations.wrappers.ExportOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class OperationStorage {

    private List<Operation> performedOperations;

    // POC structure to provide single concurrent instance with operations, to be expanded for full version
    private Queue<ExportOperation> exportOperations;

    public OperationStorage(){
        this.performedOperations = new ArrayList<>();
        this.exportOperations = new LinkedBlockingQueue<>();
    }

    public void addOperation(Operation operation, int targetObjectType, boolean isExternal){
        this.performedOperations.add(operation);
        if (!isExternal) {
            this.exportOperations.add(new ExportOperation(operation, targetObjectType));
        }
    }

    public ExportOperation getExportOperation(){
        if (this.exportOperations.isEmpty()){
            return null;
        } else {
            return this.exportOperations.poll().clone();
        }
    }


}
