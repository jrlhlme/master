package instance.assets;

import datamodel.objects.*;
import datamodel.operations.OperationType;
import datamodel.operations.contents.MVRUpdateContents;
import datamodel.operations.contents.ORUpdateContents;
import datamodel.primitives.ORSet;

import java.util.HashMap;
import java.util.Map;

public class ObjectStorage {

    private int client_id;

    // objects resulting from performed operations
    private Map<String, Operation> operationObjects;
    private Map<String, Unit> unitObjects;
    private Map<String, Mission> missionObjects;

    // raw object existence determiners
    private ORSet operations;
    private ORSet units;
    private ORSet missions;

    IdentifierGenerator identifierGenerator;

    OperationStorage operationStorage;


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

    /**
     * Handles operations generated at another interface
     * @param operation
     */
    public void processExternalOperation(datamodel.operations.Operation operation){

    }

    /**
     * Internal helper function applying generated operation update the correct structures
     * @param operation
     */
    private void processInternalOperation(datamodel.operations.Operation operation, int targetObjectType){
        switch (operation.getOperationType()){
            case OperationType.ORSET_ADD:
            case OperationType.ORSET_REMOVE:
                ORUpdateContents orOperationContents = (ORUpdateContents) operation.getOperationContents();
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
        this.operationStorage.addOperation(operation);

    }


    /**
     * Handler for creating new objects
     * @param objectType id contained in datamodel.object.ObjectType
     * @param id
     * @param isCascading
     */
    private void createOrAdd(int objectType, String id, boolean isCascading){
        datamodel.operations.Operation createOp;
        switch (objectType){
            case ObjectType.OPERATION:
                Operation operation;
                if (id == null){
                    operation = new Operation(this.client_id, this.identifierGenerator.getIdentifier());
                } else {
                    operation = getOperationInternal(id);
                    if (operation == null){
                        throw new IllegalStateException("Cannot add, no operation corresponding to id : '" + id + "' exists");
                    }
                }
                createOp = new datamodel.operations.Operation(
                        this.client_id,
                        new ORUpdateContents(operation.getId()),
                        null,
                        OperationType.ORSET_ADD,
                        isCascading
                );
                processInternalOperation(createOp, ObjectType.OPERATION);
                this.operationObjects.put(operation.getId(), operation);
                break;
            case ObjectType.UNIT:
                Unit unit;
                if (id == null){
                    unit = new Unit(client_id, this.identifierGenerator.getIdentifier());
                } else {
                    unit = getUnitInternal(id);
                    if (unit == null){
                        throw new IllegalStateException("Cannot add, no operation corresponding to id : '" + id + "' exists");
                    }
                }
                createOp = new datamodel.operations.Operation(
                        this.client_id,
                        new ORUpdateContents(unit.getId()),
                        null,
                        OperationType.ORSET_ADD,
                        isCascading
                );
                processInternalOperation(createOp, ObjectType.UNIT);
                this.unitObjects.put(unit.getId(), unit);
                break;
            case ObjectType.MISSION:
                Mission mission;
                if (id == null){
                    mission = new Mission(client_id, this.identifierGenerator.getIdentifier());
                } else {
                    mission = getMissionInternal(id);
                    if (mission == null){
                        throw new IllegalStateException("Cannot add, no operation corresponding to id : '" + id + "' exists");
                    }
                }
                createOp = new datamodel.operations.Operation(
                        this.client_id,
                        new ORUpdateContents(mission.getId()),
                        null,
                        OperationType.ORSET_ADD,
                        isCascading
                );
                processInternalOperation(createOp, ObjectType.MISSION);
                this.missionObjects.put(mission.getId(), mission);
                break;
        }
    }



    private void add(int objectType, String id, boolean isCascading){


    }



    /**
     * Handler for deleting existing objects
     * @param objectType
     * @param id
     */
    private void remove(int objectType, String id){
        datamodel.operations.Operation removeOp;
        switch (objectType){
            case ObjectType.OPERATION:
                Operation operation = getOperationInternal(id);
                if (operation == null){
                    throw new IllegalStateException("Operation with id : '" + id + "'not found, could not call removeOperation()");
                }
                removeOp = new datamodel.operations.Operation(
                        this.client_id,
                        new ORUpdateContents(operation.getId()),
                        null,
                        OperationType.ORSET_REMOVE,
                        false
                );
                processInternalOperation(removeOp, ObjectType.OPERATION);
                break;
            case ObjectType.UNIT:
                Unit unit = getUnitInternal(id);
                if (unit == null){
                    throw new IllegalStateException("Operation with id : '" + id + "'not found, could not call removeOperation()");
                }
                removeOp = new datamodel.operations.Operation(
                        this.client_id,
                        new ORUpdateContents(unit.getId()),
                        null,
                        OperationType.ORSET_REMOVE,
                        false
                );
                processInternalOperation(removeOp, ObjectType.UNIT);
                break;
            case ObjectType.MISSION:
                Mission mission = getMissionInternal(id);
                if (mission == null){
                    throw new IllegalStateException("Operation with id : '" + id + "'not found, could not call removeOperation()");
                }
                removeOp = new datamodel.operations.Operation(
                        this.client_id,
                        new ORUpdateContents(mission.getId()),
                        null,
                        OperationType.ORSET_REMOVE,
                        false
                );
                processInternalOperation(removeOp, ObjectType.MISSION);
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
        createOrAdd(ObjectType.OPERATION, null, false);
    }



    public void addOperation(String id, boolean isCascading){
        createOrAdd(ObjectType.OPERATION, id, isCascading);
    }



    public void removeOperation(String id){
        remove(ObjectType.OPERATION, id);
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
        createOrAdd(ObjectType.UNIT, null, false);
    }



    public void addUnit(String id, boolean isCascading){
        createOrAdd(ObjectType.UNIT, id, isCascading);
    }



    public void removeUnit(String id){
        remove(ObjectType.UNIT, id);
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
        createOrAdd(ObjectType.MISSION, null, false);
    }



    public void addMission(String id, boolean isCascading){
        createOrAdd(ObjectType.MISSION, id, isCascading);
    }



    public void removeMission(String id){
        remove(ObjectType.MISSION, id);
    }




}
