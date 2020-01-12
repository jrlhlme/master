package datamodel.objects;

import datamodel.objects.relations.OperationUnitRelation;
import datamodel.operations.OperationType;
import datamodel.operations.contents.MVRUpdateContents;
import datamodel.operations.contents.ORUpdateContents;
import datamodel.primitives.MVR;
import datamodel.primitives.ORSet;
import datamodel.primitives.Vectorclock;
import instance.assets.IdentifierGenerator;
import instance.assets.ObjectStorage;
import instance.assets.OperationStorage;

import java.util.Optional;

public class Operation implements DBObject{

    private String id;
    private int client_id;

    // the db instance the object is connected to
    private ObjectStorage objectStorage;

    private OperationUnitRelation assignedUnits;

    public Operation(int client_id, String id, ObjectStorage objectStorage){
        this.client_id = client_id;
        this.id = id;
        this.assignedUnits = new OperationUnitRelation(client_id);

        this.objectStorage = objectStorage;
    }



    public String getId() {
        return id;
    }



    public void assignUnit(Unit unit){

    }

















//    // TODO only mute vars stored in this object - messy otherwise
//
////    /**
////     * Adds a unit to the current operation
////     * @param idgen id generator for client
////     * @param callsign optional callsign assignment
////     * @param opStorage opstorage for client
////     * @param objectStorage storage location for generated objects
////     */
//    public void createUnit(IdentifierGenerator idgen, String callsign, OperationStorage opStorage, ObjectStorage objectStorage){
//        Unit unit = new Unit(this.client_id, idgen);
//        if (callsign != null) {
//            if (this.unit_callsign_mapping.get(callsign) != null && this.unit_callsign_mapping.get(callsign).size() > 0){
//                throw new IllegalStateException("Callsign '" + callsign + "' is already in use, cannot be assigned to new unit");
//            }
//            datamodel.operations.Operation setCallsign = new datamodel.operations.Operation(
//                    this.client_id,
//                    new MVRUpdateContents(callsign, unit.getId()),
//                    null,
//                    OperationType.MVR_SET,
//                    false
//            );
//            unit_callsign_mapping.processOperation(setCallsign);
//            opStorage.addOperation(setCallsign);
//            unit.setCallsign(callsign);
//        }
//        datamodel.operations.Operation addUnit = new datamodel.operations.Operation(
//                this.client_id,
//                new ORUpdateContents(unit.getId()),
//                null,
//                OperationType.ORSET_ADD,
//                false
//        );
//        units.processOperation(addUnit);
//        opStorage.addOperation(addUnit);
//
//        objectStorage.addUnit(unit.getId(), unit);
//    }
////
////    public void removeUnit(String id, OperationStorage opStorage, ObjectStorage objectStorage) {
////        Unit unit = objectStorage.getUnit(id);
////        if (unit == null){
////            throw new IllegalStateException("No unit exists for unit of id '" + id + "'");
////        }
////        String callsign = unit.getCallsign();
////        if (callsign != null) {
////            if (this.unit_callsign_mapping.get(callsign) != null && this.unit_callsign_mapping.get(callsign).size() > 0){
////                throw new IllegalStateException("Callsign '" + callsign + "' is already in use, cannot be assigned to new unit");
////            }
////            datamodel.operations.Operation rmCallsign = new datamodel.operations.Operation(
////                    this.client_id,
////                    new MVRUpdateContents(callsign, unit.getId()),
////                    null,
////                    OperationType.MVR_REMOVE,
////                    false
////            );
////            unit_callsign_mapping.processOperation(rmCallsign);
////            opStorage.addOperation(rmCallsign);
////
////            // TODO rm
////        }
////
//////        datamodel.operations.Operation rmUnit = new datamodel.operations.Operation(
//////                this.client_id,
//////                new ORUpdateContents(unit.getId()),
//////                null,
//////                OperationType.ORSET_ADD,
//////                false
//////        );
//////        units.processOperation(rmUnit);
////
////
////
////    }
//
//    protected void addUnitCascading(String id, OperationStorage opStorage, ObjectStorage objectStorage){
//        Unit unit = objectStorage.getUnit(id);
//        if (unit == null){
//            throw new IllegalStateException("No unit exists for unit of id '" + id + "'");
//        }
//
//        if (unit.getCallsign() != null){
//            datamodel.operations.Operation setCallsign = new datamodel.operations.Operation(
//                    this.client_id,
//                    new MVRUpdateContents(unit.getCallsign(), unit.getId()),
//                    null,
//                    OperationType.MVR_SET,
//                    true
//            );
//            unit_callsign_mapping.processOperation(setCallsign);
//            opStorage.addOperation(setCallsign);
//        }
//
//        datamodel.operations.Operation addUnit = new datamodel.operations.Operation(
//                this.client_id,
//                new ORUpdateContents(unit.getId()),
//                null,
//                OperationType.ORSET_ADD,
//                true
//        );
//        units.processOperation(addUnit);
//        opStorage.addOperation(addUnit);
//    }
//
//
//
//    /**
//     * Adds a mission to the current operation
//     * @param idgen id generator for client
//     * @param identifier mission identifier parameter
//     * @param opStorage opstorage for client
//     * @param objectStorage storage location for generated objects
//     */
//    public void createMission(IdentifierGenerator idgen, String identifier, OperationStorage opStorage, ObjectStorage objectStorage){
//        Mission mission = new Mission(this.client_id, idgen);
//        datamodel.operations.Operation setIdentifier = new datamodel.operations.Operation(
//                this.client_id,
//                new MVRUpdateContents(identifier, mission.getId()),
//                null,
//                OperationType.MVR_SET,
//                false
//        );
//        unit_callsign_mapping.processOperation(setIdentifier);
//        opStorage.addOperation(setIdentifier);
//        mission.setIdentifier(identifier);
//
//        datamodel.operations.Operation addMission = new datamodel.operations.Operation(
//                this.client_id,
//                new ORUpdateContents(mission.getId()),
//                null,
//                OperationType.ORSET_ADD,
//                false
//        );
//        missions.processOperation(addMission);
//        opStorage.addOperation(addMission);
//
//        objectStorage.addMission(mission.getId(), mission);
//    }


}
