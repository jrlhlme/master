package datamodel.objects.relations;

import datamodel.objects.Operation;
import datamodel.objects.Unit;
import datamodel.primitives.MVR;
import datamodel.primitives.ORSet;

public class OperationUnitRelation {

    private ORSet units;
    private MVR unit_callsign_mapping;

    public OperationUnitRelation(int client_id){
        this.units = new ORSet(client_id);
        this.unit_callsign_mapping = new MVR(client_id);
    }



    public void addUnit(Unit unit){
//        this.units.processOperation(new datamodel.operations.Operation(
//            this.clie
//        ));
    }

}
