package datamodel.objects;

import datamodel.primitives.MVR;
import datamodel.primitives.ORSet;
import instance.assets.IdentifierGenerator;

import java.util.List;

public class Unit {

    private String id;
    private int client_id;

    private List<Operation> operations;

//    private ORSet missions;

//    private ORSet personell;
//    private MVR personell_role_mapping;

    // updated as a result of altering the callsign mapping in the Operation-Unit relation
    // locally maintained,
    private String callsign;


    public Unit(int client_id, String id){
        this.client_id = client_id;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperation(Operation operation) {
//        this.operation = operation;
    }
}
