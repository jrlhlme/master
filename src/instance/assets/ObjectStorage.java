package instance.assets;

import datamodel.objects.*;
import datamodel.operations.OperationType;
import datamodel.operations.contents.ORUpdateContents;
import datamodel.operations.wrappers.ExportOperation;
import datamodel.primitives.ORSet;

import java.util.HashMap;
import java.util.Map;

public class ObjectStorage {

    private int client_id;

    // objects resulting from performed operations containing data and operation sequences
    private Map<String, Operation> operationObjects;
    private Map<String, Unit> unitObjects;
    private Map<String, Mission> missionObjects;

    // raw object existence determiners
    private ORSet operations;
    private ORSet units;
    private ORSet missions;

    // generates globally unique id-values
    private IdentifierGenerator identifierGenerator;

    // stores and manages export of operations performed
    private OperationStorage operationStorage;


    public ObjectStorage(int client_id){
        this.operationObjects = new HashMap<>();
        this.unitObjects = new HashMap<>();
        this.missionObjects = new HashMap<>();

        this.operations = new ORSet(client_id);
        this.units = new ORSet(client_id);
        this.missions = new ORSet(client_id);

        this.identifierGenerator = new IdentifierGenerator(client_id);
        this.operationStorage = new OperationStorage();

        this.client_id = client_id;
    }

    public ExportOperation getExportOperation(){
        return this.operationStorage.getExportOperation();
    }



    /**
     * Handles operations generated at another interface
     * @param operationWrapper
     */
    public void processExternalOperation(ExportOperation operationWrapper){
        switch (operationWrapper.getOperation().getOperationType()){
            case OperationType.ORSET_ADD:
                createOrAdd(operationWrapper.getTargetObjectType(),
                        ((ORUpdateContents)operationWrapper.getOperation().getOperationContents()).getId(),
                        operationWrapper.getOperation().isCascadingOp(), operationWrapper.getOperation());
                break;
            case OperationType.ORSET_REMOVE:
                remove(operationWrapper.getTargetObjectType(),
                        ((ORUpdateContents)operationWrapper.getOperation().getOperationContents()).getId(),
                        operationWrapper.getOperation().isCascadingOp(), operationWrapper.getOperation());
                break;

        }
    }

    /**
     * Internal helper function applying generated operation update the correct structures
     * @param operation
     */
    private void processOperation(datamodel.operations.Operation operation, int targetObjectType, boolean isExternal){
        switch (operation.getOperationType()){
            case OperationType.ORSET_ADD:
            case OperationType.ORSET_REMOVE:
//                ORUpdateContents orOperationContents = (ORUpdateContents) operation.getOperationContents();
                switch (targetObjectType){
                    case ObjectType.OPERATION:
                        this.operations.processOperation(operation);
                        break;
                    case ObjectType.UNIT:
                        this.units.processOperation(operation);
                        break;
                    case ObjectType.MISSION:
                        this.missions.processOperation(operation);
                        break;
                }
                break;
                // idk if we need this tbh
//            case OperationType.MVR_SET:
//            case OperationType.MVR_CLEAR:
//            case OperationType.MVR_REMOVE:
//                MVRUpdateContents mvrOperationContents = (MVRUpdateContents) operation.getOperationContents();


        }
        this.operationStorage.addOperation(operation, targetObjectType, isExternal);
    }



    /**
     * Handler for creating new objects or re-adding existing ones
     * @param objectType id contained in datamodel.object.ObjectType
     * @param id null value creates new object, provided value attempts to perform an add-operation on object
     *           corresponding to id
     * @param isCascading flags generated operation as a result of cascading or not - has impact on garbage collection
     * @param externalOp operation, provided if processing external operations
     */
    private void createOrAdd(int objectType, String id, boolean isCascading, datamodel.operations.Operation externalOp){
        datamodel.operations.Operation createOp = externalOp;
        switch (objectType){
            case ObjectType.OPERATION:
                Operation operation;
                if (id == null || (externalOp != null && getOperationInternal(id) == null)){
                    operation = new Operation(this.client_id,
                            externalOp != null ? ((ORUpdateContents)externalOp.getOperationContents()).getId() : this.identifierGenerator.getIdentifier(),
                            this);
                } else {
                    operation = getOperationInternal(id);
                    if (operation == null){
                        throw new IllegalStateException("Cannot add, no operation corresponding to id : '" + id + "' exists");
                    }
                }
                if (createOp == null) {
                    createOp = new datamodel.operations.Operation(
                            this.client_id,
                            new ORUpdateContents(operation.getId()),
                            null,
                            OperationType.ORSET_ADD,
                            isCascading
                    );
                }
                processOperation(createOp, ObjectType.OPERATION, externalOp != null);
                this.operationObjects.put(operation.getId(), operation);
                break;
            case ObjectType.UNIT:
                Unit unit;
                if (id == null || (externalOp != null && getUnitInternal(id) == null)){
                    unit = new Unit(client_id, externalOp != null ? ((ORUpdateContents)externalOp.getOperationContents()).getId() : this.identifierGenerator.getIdentifier());
                } else {
                    unit = getUnitInternal(id);
                    if (unit == null){
                        throw new IllegalStateException("Cannot add, no operation corresponding to id : '" + id + "' exists");
                    }
                }
                if (createOp == null) {
                    createOp = new datamodel.operations.Operation(
                            this.client_id,
                            new ORUpdateContents(unit.getId()),
                            null,
                            OperationType.ORSET_ADD,
                            isCascading
                    );
                }
                processOperation(createOp, ObjectType.UNIT, externalOp != null);
                this.unitObjects.put(unit.getId(), unit);
                break;
            case ObjectType.MISSION:
                Mission mission;
                if (id == null || (externalOp != null && getMissionInternal(id) == null)){
                    mission = new Mission(client_id, externalOp != null ? ((ORUpdateContents)externalOp.getOperationContents()).getId() : this.identifierGenerator.getIdentifier());
                } else {
                    mission = getMissionInternal(id);
                    if (mission == null){
                        throw new IllegalStateException("Cannot add, no operation corresponding to id : '" + id + "' exists");
                    }
                }
                if (createOp == null) {
                    createOp = new datamodel.operations.Operation(
                            this.client_id,
                            new ORUpdateContents(mission.getId()),
                            null,
                            OperationType.ORSET_ADD,
                            isCascading
                    );
                }
                processOperation(createOp, ObjectType.MISSION, externalOp != null);
                this.missionObjects.put(mission.getId(), mission);
                break;
        }
    }



    /**
     * Handler for deleting existing objects
     * @param objectType
     * @param id
     * @param isCascading flags generated operation as a result of cascading or not - has impact on garbage collection
     * @param externalOp operation, provided if processing external operations
     */
    private void remove(int objectType, String id, boolean isCascading, datamodel.operations.Operation externalOp){
        datamodel.operations.Operation removeOp = externalOp;
        switch (objectType){
            case ObjectType.OPERATION:
                Operation operation = getOperationInternal(id);
                if (operation == null){
                    throw new IllegalStateException("Operation with id : '" + id + "'not found, could not call removeOperation()");
                }
                if (removeOp == null) {
                    removeOp = new datamodel.operations.Operation(
                            this.client_id,
                            new ORUpdateContents(operation.getId()),
                            null,
                            OperationType.ORSET_REMOVE,
                            isCascading
                    );
                }
                processOperation(removeOp, ObjectType.OPERATION, externalOp != null);
                break;
            case ObjectType.UNIT:
                Unit unit = getUnitInternal(id);
                if (unit == null){
                    throw new IllegalStateException("Operation with id : '" + id + "'not found, could not call removeOperation()");
                }
                if (removeOp == null) {
                    removeOp = new datamodel.operations.Operation(
                            this.client_id,
                            new ORUpdateContents(unit.getId()),
                            null,
                            OperationType.ORSET_REMOVE,
                            isCascading
                    );
                }
                processOperation(removeOp, ObjectType.UNIT, externalOp != null);
                break;
            case ObjectType.MISSION:
                Mission mission = getMissionInternal(id);
                if (mission == null){
                    throw new IllegalStateException("Operation with id : '" + id + "'not found, could not call removeOperation()");
                }
                if (removeOp == null) {
                    removeOp = new datamodel.operations.Operation(
                            this.client_id,
                            new ORUpdateContents(mission.getId()),
                            null,
                            OperationType.ORSET_REMOVE,
                            isCascading
                    );
                }
                processOperation(removeOp, ObjectType.MISSION, externalOp != null);
                break;
        }
    }



    /**
     * Operation
     */


    private Operation getOperationInternal(String id){
        return this.operationObjects.get(id);
    }


    /**
     * public function to get operation if sequence of operations deems it to exist
     * @param key
     * @return
     */
    public Operation getOperation(String key){
        if (this.operations.getStateContents().contains(key)) {
            return this.operationObjects.get(key);
        } else {
            return null;
        }
    }



    public void createOperation(){
        createOrAdd(ObjectType.OPERATION, null, false, null);
    }



    public void addOperation(String id, boolean isCascading){
        createOrAdd(ObjectType.OPERATION, id, isCascading, null);
    }



    public void removeOperation(String id){
        remove(ObjectType.OPERATION, id, false, null);
    }



    /**
     * Unit
     */

    private Unit getUnitInternal(String id){
        return this.unitObjects.get(id);
    }



    public Unit getUnit(String key){
        if (this.units.getStateContents().contains(key)) {
            return this.unitObjects.get(key);
        } else {
            return null;
        }
    }



    public void createUnit(){
        createOrAdd(ObjectType.UNIT, null, false, null);
    }



    public void addUnit(String id, boolean isCascading){
        createOrAdd(ObjectType.UNIT, id, isCascading, null);
    }



    public void removeUnit(String id){
        remove(ObjectType.UNIT, id, false, null);
    }



    /**
     * Mission
     */

    private Mission getMissionInternal(String id){
        return this.missionObjects.get(id);
    }



    public Mission getMission(String key){
        if (this.missions.getStateContents().contains(key)) {
            return this.missionObjects.get(key);
        } else {
            return null;
        }
    }



    public void createMission(){
        createOrAdd(ObjectType.MISSION, null, false, null);
    }



    public void addMission(String id, boolean isCascading){
        createOrAdd(ObjectType.MISSION, id, isCascading, null);
    }



    public void removeMission(String id){
        remove(ObjectType.MISSION, id, false, null);
    }




}
