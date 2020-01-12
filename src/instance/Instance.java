package instance;


import datamodel.primitives.Vectorclock;
import instance.assets.IdentifierGenerator;
import instance.assets.ObjectStorage;
import instance.assets.OperationStorage;

public class Instance {

    // incremented for each operation performed
    // ONLY ops incrementing this by one for single field are allowed - on concurrency resolution join resolved vector clocks
    private Vectorclock global_vectorclock;

    // globally unique identifier for the instance
    private int instance_id;

    // generates globally unique id values for new objects
    private IdentifierGenerator idgen;

    // stores operations performed on the structures
    private OperationStorage opStorage;

    // stores created objects
    private ObjectStorage objStorage;

    public Instance(int instance_id){
        this.instance_id = instance_id;
        this.idgen = new IdentifierGenerator(instance_id);
        this.opStorage = new OperationStorage();
//        this.objStorage = new ObjectStorage();
    }








}
