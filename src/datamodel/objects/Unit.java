package datamodel.objects;

import datamodel.primitives.MVR;
import datamodel.primitives.ORSet;
import instance.assets.IdentifierGenerator;

import java.util.List;

public class Unit {

    private String id;

    // updated as a result of altering the callsign mapping in the Operation-Unit relation
    // locally maintained, TODO
    private String callsign;


    public Unit(String id){
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

}
