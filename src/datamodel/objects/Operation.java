package datamodel.objects;

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

    /**
     * Operation-Unit relation
     */
    private ORSet units;
    private MVR unit_callsign_mapping;

    /**
     * Operation-mission relation
     */
    private ORSet missions;
    private MVR mission_identifier_mapping;


    public Operation(int client_id, IdentifierGenerator id){
        this.client_id = client_id;
        this.id = id.getIdentifier();
        this.units = new ORSet(client_id);
        this.unit_callsign_mapping = new MVR(client_id);
    }

    public String getId() {
        return id;
    }


    /**
     * Adds a unit to the current operation
     * @param idgen id generator for client
     * @param callsign optional callsign assignment
     * @param opStorage opstorage for client
     * @param objectStorage storage location for generated objects
     */
    public void createUnit(IdentifierGenerator idgen, String callsign, OperationStorage opStorage, ObjectStorage objectStorage){
        Unit unit = new Unit(this.client_id, idgen);
        if (callsign != null) {
            datamodel.operations.Operation setCallsign = new datamodel.operations.Operation(
                    this.client_id,
                    new MVRUpdateContents(callsign, unit.getId()),
                    null,
                    OperationType.MVR_SET,
                    false
            );
            unit_callsign_mapping.processOperation(setCallsign);
            opStorage.addOperation(setCallsign);
            unit.setCallsign(callsign);
        }
        datamodel.operations.Operation addUnit = new datamodel.operations.Operation(
                this.client_id,
                new ORUpdateContents(unit.getId()),
                null,
                OperationType.ORSET_ADD,
                false
        );
        units.processOperation(addUnit);
        opStorage.addOperation(addUnit);

        objectStorage.addUnit(unit.getId(), unit);
    }

    public void removeUnit(String id){

    }

    protected void addUnitCascading(String id, OperationStorage opStorage, ObjectStorage objectStorage){
        Unit unit = objectStorage.getUnit(id);
        if (unit.getCallsign() != null){
            datamodel.operations.Operation setCallsign = new datamodel.operations.Operation(
                    this.client_id,
                    new MVRUpdateContents(unit.getCallsign(), unit.getId()),
                    null,
                    OperationType.MVR_SET,
                    true
            );
        }


    }



    /**
     * Adds a mission to the current operation
     * @param idgen id generator for client
     * @param identifier mission identifier parameter
     * @param opStorage opstorage for client
     * @param objectStorage storage location for generated objects
     */
    public void createMission(IdentifierGenerator idgen, String identifier, OperationStorage opStorage, ObjectStorage objectStorage){
        Mission mission = new Mission(this.client_id, idgen);
        datamodel.operations.Operation setIdentifier = new datamodel.operations.Operation(
                this.client_id,
                new MVRUpdateContents(identifier, mission.getId()),
                null,
                OperationType.MVR_SET,
                false
        );
        unit_callsign_mapping.processOperation(setIdentifier);
        opStorage.addOperation(setIdentifier);
        mission.setIdentifier(identifier);

        datamodel.operations.Operation addMission = new datamodel.operations.Operation(
                this.client_id,
                new ORUpdateContents(mission.getId()),
                null,
                OperationType.ORSET_ADD,
                false
        );
        missions.processOperation(addMission);
        opStorage.addOperation(addMission);

        objectStorage.addMission(mission.getId(), mission);
    }


}
