package datamodel.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operation implements DBObject{

    private String id;
//    private int client_id;

    private List<Unit> units;
    private List<Mission> missions;

    private Map<String, Unit> callsignUnitMapping;
    private Map<String, Mission> nameMissionMapping;



    public Operation(String id){
//        this.client_id = client_id;
        this.id = id;
    }


    public String getId() {
        return id;
    }






}
