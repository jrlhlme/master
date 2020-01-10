package instance.assets;

import java.util.HashMap;
import java.util.Map;

public class IdentifierGenerator {

    private int client_id;
    private int identifier;


    public IdentifierGenerator(int client_id){
        this.client_id = client_id;
        this.identifier = 0;
    }

    public String getIdentifier(){
        this.identifier++;
        return client_id + "-" + identifier;
    }

}
