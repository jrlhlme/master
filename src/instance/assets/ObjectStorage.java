package instance.assets;

import datamodel.objects.*;

import java.util.HashMap;
import java.util.Map;

public class ObjectStorage {

    private Map<String, Operation> operationStorage;
    private Map<String, Unit> unitStorage;
    private Map<String, Mission> missionStorage;

    public ObjectStorage(){
        this.operationStorage = new HashMap<>();
        this.unitStorage = new HashMap<>();
        this.missionStorage = new HashMap<>();
    }



    public Operation getOperation(String key){
        return this.operationStorage.get(key);
    }

    public void addOperation(String key, Operation operation){
        this.operationStorage.put(key, operation);
    }



    public Unit getUnit(String key){
        return this.unitStorage.get(key);
    }

    public void addUnit(String key, Unit unit){
        this.unitStorage.put(key, unit);
    }



    public Mission getMission(String key){
        return this.missionStorage.get(key);
    }

    public void addMission(String key, Mission mission){
        this.missionStorage.put(key, mission);
    }




}
