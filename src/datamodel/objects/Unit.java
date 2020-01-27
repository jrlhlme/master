package datamodel.objects;

import datamodel.primitives.MVR;
import datamodel.primitives.ORSet;
import instance.assets.IdentifierGenerator;

import java.util.List;

public class Unit {

    private String id;

    public Unit(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }


}
