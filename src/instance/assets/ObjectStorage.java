package instance.assets;

import datamodel.objects.*;
import datamodel.operations.OperationType;
import datamodel.operations.contents.MVRUpdateContents;
import datamodel.operations.contents.ORUpdateContents;
import datamodel.operations.wrappers.ExportOperation;
import datamodel.primitives.MVR;
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

    private Map<String, MVR> operationUnitRelations;
    private Map<String, MVR> operationMissionRelations;
    private Map<String, MVR> unitMissionRelations;


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

        this.operationUnitRelations = new HashMap<>();

        this.operationMissionRelations = new HashMap<>();

        this.unitMissionRelations = new HashMap<>();

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
                createOrAddOREntry(operationWrapper.getTargetObjectType(),
                        ((ORUpdateContents)operationWrapper.getOperation().getOperationContents()).getId(),
                        operationWrapper.getOperation().isCascadingOp(), operationWrapper.getOperation());
                break;
            case OperationType.ORSET_REMOVE:
                removeOREntry(operationWrapper.getTargetObjectType(),
                        ((ORUpdateContents)operationWrapper.getOperation().getOperationContents()).getId(),
                        operationWrapper.getOperation().isCascadingOp(), operationWrapper.getOperation());
                break;
            case OperationType.MVR_SET:
            case OperationType.MVR_CLEAR:
                MVRUpdateContents mvrUpdateContents = (MVRUpdateContents)operationWrapper.getOperation().getOperationContents();
                assignRelation(operationWrapper.getTargetObjectType(), operationWrapper.getTargetId(), mvrUpdateContents.getKey(),
                        mvrUpdateContents.getValue(), operationWrapper.getOperation().isCascadingOp(), operationWrapper.getOperation());
        }
    }



    /**
     * Internal helper function applying generated operation update the correct structures
     * @param operation
     */
    private void processOperation(datamodel.operations.Operation operation, int targetObjectType, String targetObjectId, boolean isExternal){
        switch (operation.getOperationType()){
            case OperationType.ORSET_ADD:
            case OperationType.ORSET_REMOVE:
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
            case OperationType.MVR_SET:
                switch (targetObjectType){
                    case ObjectType.OPERATION_UNIT_RELATION:
                        if (this.operationUnitRelations.get(targetObjectId) == null){
                            this.operationUnitRelations.put(targetObjectId, new MVR(this.client_id));
                        }
                        this.operationUnitRelations.get(targetObjectId).processOperation(operation);
                        break;
                    case ObjectType.OPERATION_MISSION_RELATION:
                        if (this.operationMissionRelations.get(targetObjectId) == null){
                            this.operationMissionRelations.put(targetObjectId, new MVR(this.client_id));
                        }
                        this.operationMissionRelations.get(targetObjectId).processOperation(operation);
                        break;
                    case ObjectType.UNIT_MISSION_RELATION:
                        if (this.unitMissionRelations.get(targetObjectId) == null){
                            this.unitMissionRelations.put(targetObjectId, new MVR(this.client_id));
                        }
                        this.unitMissionRelations.get(targetObjectId).processOperation(operation);
                        break;

                }
            case OperationType.MVR_REMOVE:
                switch (targetObjectType){
                    case ObjectType.OPERATION_UNIT_RELATION:
                        if (this.operationUnitRelations.get(targetObjectId) == null){
                            throw new IllegalStateException("Cannot perform MVR_REMOVE on op-unit relation, no object exists for the relation");
                        }
                        this.operationUnitRelations.get(targetObjectId).processOperation(operation);
                        break;
                    case ObjectType.OPERATION_MISSION_RELATION:
                        if (this.operationMissionRelations.get(targetObjectId) == null){
                            throw new IllegalStateException("Cannot perform MVR_REMOVE on op-mission relation, no object exists for the relation");
                        }
                        this.operationMissionRelations.get(targetObjectId).processOperation(operation);
                        break;
                    case ObjectType.UNIT_MISSION_RELATION:
                        if (this.unitMissionRelations.get(targetObjectId) == null){
                            throw new IllegalStateException("Cannot perform MVR_REMOVE on unit-mission relation, no object exists for the relation");
                        }
                        this.unitMissionRelations.get(targetObjectId).processOperation(operation);
                        break;
                }
                break;
        }
        this.operationStorage.addOperation(operation, targetObjectType, targetObjectId, isExternal);
    }



    private void assignRelation(int objectType, String leftId, String rightId, String attribute, boolean isCascading, datamodel.operations.Operation externalOp){
        datamodel.operations.Operation createOp = externalOp;
        switch (objectType){
            case ObjectType.OPERATION_UNIT_RELATION:
                if (externalOp == null && (getUnitInternal(rightId) == null || getOperationInternal(leftId) == null)){
                    throw new IllegalStateException("Cannot assign relation between operation and unit, object(s) do not exist");
                }
                if (createOp == null){
                    createOp = new datamodel.operations.Operation(
                        this.client_id,
                        new MVRUpdateContents(attribute, rightId),
                    null,
                        OperationType.MVR_SET,
                        isCascading
                    );
                }
                processOperation(createOp, ObjectType.OPERATION_UNIT_RELATION, leftId, externalOp != null);
                // TODO mute derived objects
                break;
            case ObjectType.OPERATION_MISSION_RELATION:
                if (getMissionInternal(rightId) == null || getOperationInternal(leftId) == null){
                    throw new IllegalStateException("Cannot assign relation between operation and mission, object(s) do not exist");
                }
                if (createOp == null){
                    createOp = new datamodel.operations.Operation(
                            this.client_id,
                            new MVRUpdateContents(attribute, rightId),
                            null,
                            OperationType.MVR_SET,
                            isCascading
                    );
                }
                processOperation(createOp, ObjectType.OPERATION_MISSION_RELATION, leftId, externalOp != null);
                // TODO mute derived objects
                break;
            case ObjectType.UNIT_MISSION_RELATION:
                if (getMissionInternal(rightId) == null || getUnitInternal(leftId) == null){
                    throw new IllegalStateException("Cannot assign relation between operation and mission, object(s) do not exist");
                }
                if (createOp == null){
                    createOp = new datamodel.operations.Operation(
                            this.client_id,
                            new MVRUpdateContents(attribute, rightId),
                            null,
                            OperationType.MVR_SET,
                            isCascading
                    );
                }
                processOperation(createOp, ObjectType.OPERATION_MISSION_RELATION, leftId, externalOp != null);
                // TODO mute derived objects
                break;
        }

    }


    private void unassignRelation(int objectType, String leftId, String rightId, String attribute, boolean isCascading, datamodel.operations.Operation externalOp){
        datamodel.operations.Operation removeOp = externalOp;
        switch (objectType){
            case ObjectType.OPERATION_UNIT_RELATION:
                if (getUnitInternal(rightId) == null || getOperationInternal(leftId) == null){
                    throw new IllegalStateException("Cannot assign relation between operation and unit, object(s) do not exist");
                }
                if (removeOp == null){
                    removeOp = new datamodel.operations.Operation(
                            this.client_id,
                            new MVRUpdateContents(attribute, rightId),
                            null,
                            OperationType.MVR_REMOVE,
                            isCascading
                    );
                }
                processOperation(removeOp, ObjectType.OPERATION_UNIT_RELATION, leftId, externalOp != null);
                // TODO mute derived objects
                break;
            case ObjectType.OPERATION_MISSION_RELATION:
                if (getMissionInternal(rightId) == null || getOperationInternal(leftId) == null){
                    throw new IllegalStateException("Cannot assign relation between operation and mission, object(s) do not exist");
                }
                if (removeOp == null){
                    removeOp = new datamodel.operations.Operation(
                            this.client_id,
                            new MVRUpdateContents(attribute, rightId),
                            null,
                            OperationType.MVR_REMOVE,
                            isCascading
                    );
                }
                processOperation(removeOp, ObjectType.OPERATION_MISSION_RELATION, leftId, externalOp != null);
                // TODO mute derived objects
                break;
            case ObjectType.UNIT_MISSION_RELATION:
                if (getMissionInternal(rightId) == null || getUnitInternal(leftId) == null){
                    throw new IllegalStateException("Cannot assign relation between operation and mission, object(s) do not exist");
                }
                if (removeOp == null){
                    removeOp = new datamodel.operations.Operation(
                            this.client_id,
                            new MVRUpdateContents(attribute, rightId),
                            null,
                            OperationType.MVR_REMOVE,
                            isCascading
                    );
                }
                processOperation(removeOp, ObjectType.OPERATION_MISSION_RELATION, leftId, externalOp != null);
                // TODO mute derived objects
                break;
        }
    }



    /**
     * Handler for creating new objects or re-adding existing ones
     * @param objectType id contained in datamodel.object.ObjectType
     * @param id null value creates new object, provided value attempts to perform an add-operation on object
     *           corresponding to id
     * @param isCascading flags generated operation as a result of cascading or not - has impact on garbage collection
     * @param externalOp operation, provided if processing external operations
     */
    private void createOrAddOREntry(int objectType, String id, boolean isCascading, datamodel.operations.Operation externalOp){
        datamodel.operations.Operation createOp = externalOp;
        switch (objectType){
            case ObjectType.OPERATION:
                Operation operation;
                if (id == null || (externalOp != null && getOperationInternal(id) == null)){
                    operation = new Operation(externalOp != null ? ((ORUpdateContents)externalOp.getOperationContents()).getId() : this.identifierGenerator.getIdentifier()
//                            ,externalOp != null ? ((ORUpdateContents)externalOp.getOperationContents()).getId() : this.identifierGenerator.getIdentifier(),
//                            this.operationStorage
                    );
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
                processOperation(createOp, ObjectType.OPERATION, null, externalOp != null);
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
                processOperation(createOp, ObjectType.UNIT, null, externalOp != null);
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
                processOperation(createOp, ObjectType.MISSION, null, externalOp != null);
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
    private void removeOREntry(int objectType, String id, boolean isCascading, datamodel.operations.Operation externalOp){
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
                processOperation(removeOp, ObjectType.OPERATION, null,externalOp != null);
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
                processOperation(removeOp, ObjectType.UNIT, null, externalOp != null);
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
                processOperation(removeOp, ObjectType.MISSION, null, externalOp != null);
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
     * @param id
     * @return
     */
    public Operation getOperation(String id){
        if (this.operations.getStateContents().contains(id)) {
            return this.operationObjects.get(id);
        } else {
            return null;
        }
    }



    public void createOperation(){
        createOrAddOREntry(ObjectType.OPERATION, null, false, null);
    }



    public void addOperation(String id, boolean isCascading){
        createOrAddOREntry(ObjectType.OPERATION, id, isCascading, null);
    }



    public void removeOperation(String id){
        removeOREntry(ObjectType.OPERATION, id, false, null);
        // TODO handle cascading on relations
    }



    /**
     * Unit
     */

    private Unit getUnitInternal(String id){
        return this.unitObjects.get(id);
    }



    public Unit getUnit(String id){
        if (this.units.getStateContents().contains(id)) {
            return this.unitObjects.get(id);
        } else {
            return null;
        }
    }



    public void createUnit(){
        createOrAddOREntry(ObjectType.UNIT, null, false, null);
    }



    public void addUnit(String id, boolean isCascading){
        createOrAddOREntry(ObjectType.UNIT, id, isCascading, null);
    }



    public void removeUnit(String id){
        removeOREntry(ObjectType.UNIT, id, false, null);
        // TODO handle cascading on relations
    }



    /**
     * Mission
     */

    private Mission getMissionInternal(String id){
        return this.missionObjects.get(id);
    }



    public Mission getMission(String id){
        if (this.missions.getStateContents().contains(id)) {
            return this.missionObjects.get(id);
        } else {
            return null;
        }
    }



    public void createMission(){
        createOrAddOREntry(ObjectType.MISSION, null, false, null);
    }



    public void addMission(String id, boolean isCascading){
        createOrAddOREntry(ObjectType.MISSION, id, isCascading, null);
    }



    public void removeMission(String id){
        removeOREntry(ObjectType.MISSION, id, false, null);
        // TODO handle cascading on relations
    }



    /**
     * Relations
     */


    // Operation-Unit
    public void assignOperationUnit(Operation operation, Unit unit, String callsign, boolean isCascading){
        if (getUnitInternal(unit.getId()) == null || getOperationInternal(operation.getId()) == null){
            throw new IllegalStateException("Cannot assign relation between operation and unit, object(s) do not exist");
        }

        if (callsign == null){
            callsign = "unassigned";
        }

        // cascading add to ensure relation objects exists
        addOperation(operation.getId(), true);
        addUnit(unit.getId(), true);

        assignRelation(ObjectType.OPERATION_UNIT_RELATION, operation.getId(), unit.getId(), callsign, isCascading, null);
    }

    public void removeUnitFromOperation(Operation operation, Unit unit, String callsign, boolean isCascading){
        if (getUnitInternal(unit.getId()) == null || getOperationInternal(operation.getId()) == null){
            throw new IllegalStateException("Cannot remove relation between operation and unit, object(s) do not exist");
        }

        // TODO handle cascading removes, on relation UnitMission


        unassignRelation(ObjectType.OPERATION_UNIT_RELATION, operation.getId(), unit.getId(), callsign, isCascading, null);
    }



    // Operation-Mission
    public void assignOperationMission(Operation operation, Mission mission, String name, boolean isCascading){
        if (getMissionInternal(mission.getId()) == null || getOperationInternal(operation.getId()) == null){
            throw new IllegalStateException("Cannot assign relation between operation and mission, object(s) do not exist");
        }

        if (name == null){
            name = "unassigned";
        }

        // cascading add to ensure relation objects exists
        addOperation(operation.getId(), true);
        addMission(mission.getId(), true);

        assignRelation(ObjectType.OPERATION_MISSION_RELATION, operation.getId(), mission.getId(), name, isCascading, null);
    }

    public void removeOperationMission(Operation operation, Mission mission, String name, boolean isCascading){
        if (getMissionInternal(mission.getId()) == null || getOperationInternal(operation.getId()) == null){
            throw new IllegalStateException("Cannot remove relation between operation and mission, object(s) do not exist");
        }

        // TODO handle cascading removes, on relation UnitMission


        unassignRelation(ObjectType.OPERATION_MISSION_RELATION, operation.getId(), mission.getId(), name, isCascading, null);
    }



    // Unit-Mission
    public void assignUnitMission(Unit unit, Mission mission, String name, boolean isCascading){
        if (getMissionInternal(mission.getId()) == null || getUnitInternal(unit.getId()) == null){
            throw new IllegalStateException("Cannot assign relation between unit and mission, object(s) do not exist");
        }

        if (name == null){
            name = "unassigned";
        }

        // cascading add to ensure relation objects exists
        addUnit(unit.getId(), true);
        addMission(mission.getId(), true);

        // TODO handle cascading add, on relation unitoperation

        assignRelation(ObjectType.UNIT_MISSION_RELATION, unit.getId(), mission.getId(), name, isCascading, null);
    }

    public void removeOperationMission(Unit unit, Mission mission, String name, boolean isCascading){
        if (getMissionInternal(mission.getId()) == null || getUnitInternal(unit.getId()) == null){
            throw new IllegalStateException("Cannot remove relation between unit and mission, object(s) do not exist");
        }

        // TODO handle cascading removes, on relation UnitMission


        unassignRelation(ObjectType.UNIT_MISSION_RELATION, unit.getId(), mission.getId(), name, isCascading, null);
    }




}
