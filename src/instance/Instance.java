package instance;


import datamodel.primitives.Vectorclock;

public class Instance {

    // incremented for each operation performed
    // ONLY ops incrementing this by one for single field are allowed - on concurrency resolution join resolved vector clocks
    private Vectorclock global_vectorclock;


}
