package datamodel.primitives;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Jarl on 04-Dec-19.
 */
public class Vectorclock {

    private HashMap<Integer, Integer> v_clck;

    public void increment(Integer client_id){
        // if exists ++ else create, set 1
        Integer client_vector_clock = v_clck.get(client_id);
        if (client_vector_clock == null){
            v_clck.put(client_id, 1);
        } else {
            v_clck.put(client_id, client_vector_clock + 1);
        }
    }


    @Override
    public Vectorclock clone(){
        Vectorclock vectorclock = new Vectorclock();
        vectorclock.v_clck = new HashMap<>(this.v_clck);
        return vectorclock;
    }

    /**
     * returns a copy of the incremented vector clock
     * @param client_id
     * @return
     */
    public Vectorclock incrementFrom(Integer client_id){
        // if exists ++ else create, set 1
//        Vectorclock newVectorClock = new Vectorclock();
//        newVectorClock.v_clck = new HashMap<>(this.v_clck);
        Integer client_vector_clock = this.v_clck.get(client_id);

        if (client_vector_clock == null){
            this.v_clck.put(client_id, 1);
        } else {
            this.v_clck.put(client_id, client_vector_clock + 1);
        }
        return this.clone();
    }


    public void join(Vectorclock diff_vclck){
        // foreach entry grab biggest val

        Set<Integer> keySet = new HashSet<>(this.v_clck.keySet());
        keySet.addAll(diff_vclck.v_clck.keySet());

        Integer localVal, foreignVal;
        for (Integer i : keySet){
            localVal = this.v_clck.get(i);
            foreignVal = diff_vclck.v_clck.get(i);
            if (localVal == null || foreignVal == null){
                this.v_clck.put(i, localVal == null ? foreignVal : localVal);
            } else {
                this.v_clck.put(i, localVal.compareTo(foreignVal) < 0 ? foreignVal : localVal);
            }
        }

    }

    public Vectorclock(){
        this.v_clck = new HashMap<>();
    }




    public boolean isDominatedBy(Vectorclock compareVectorClock){
        // TODO checks if the provided v_clck dominates (is after)



        return false;
    }

    // much room for optimization here
    public boolean isMatching(Vectorclock compareVectorClock){
        if (compareVectorClock.v_clck.keySet().equals(this.v_clck.keySet())){ // prob. doesn't work
            for (Integer key : this.v_clck.keySet()){
                if (!compareVectorClock.v_clck.get(key).equals(this.v_clck.get(key))){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean isConcurrentTo(Vectorclock compareVectorClock){
        Set<Integer> compareVectorClockKeySet = compareVectorClock.v_clck.keySet();
        if (!keysetMatches(compareVectorClockKeySet)){
            return true;
        }

        boolean foreignGreaterThan = false, localGreaterThan = false;
        for (Integer key : compareVectorClockKeySet){
            int compareResult = this.v_clck.get(key).compareTo(compareVectorClock.v_clck.get(key));
            if (compareResult != 0){
                if (compareResult < 0){
                    foreignGreaterThan = true;
                } else {
                    localGreaterThan = true;
                }
            }
            if (foreignGreaterThan && localGreaterThan){
                return true;
            }
        }
        return false;
    }


    /**
     * checks if provided vector clock represents an operation that can be performed (must increment at most a single entry by at most 1)
     * @param compareVectorClock
     * @return
     */
    public boolean opCanBePerformed(Vectorclock compareVectorClock){
        // TODO keyset of comparevector must be equal or exactly one greater, each entry must be lower or equal AND one entry must be one greater

        return false;
    }


    private boolean keysetMatches(Set<Integer> foreignKeySet){
        if (foreignKeySet.size() != this.v_clck.size()){
            return false;
        }

        Set<Integer> localKeySet = this.v_clck.keySet();
        for (Integer key : foreignKeySet){
            if (!localKeySet.contains(key)){
                return false;
            }
        }
        return true;
    }

}
