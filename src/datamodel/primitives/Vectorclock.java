package datamodel.primitives;

import java.util.HashMap;

/**
 * Created by Jarl on 04-Dec-19.
 */
public class Vectorclock {

    private HashMap<Integer, Integer> v_clck;

    public void increment(Integer client_id){
        // if exists ++ else create, set 1
    }

    public void join(Vectorclock diff_vclck){
        // foreach entry grab biggest val
    }

}
