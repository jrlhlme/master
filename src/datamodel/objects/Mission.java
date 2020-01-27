package datamodel.objects;

import datamodel.primitives.MVR;
import datamodel.primitives.ORSet;
import instance.assets.IdentifierGenerator;

public class Mission {

    private String id;

//    private ORSet units;
//    private MVR unit_callsign_mapping;

    // updated as a result of altering the identifier mapping in the Operation-Mission relation
    // locally maintained, derived from distributed objects
    private String identifier;


    public Mission(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
