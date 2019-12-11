package datamodel.primitives;

import datamodel.operations.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by Jarl on 30-Nov-19.
 */
public class ORSet {
    // state
    // v_clck(obj)

    // op-list


    private List<Integer> state_contents;

    private Vectorclock state_vclock;



    private TreeSet oplist;


    public ORSet(){
        this.state_vclock = new Vectorclock();
        this.state_contents = new ArrayList();

        this.oplist = new TreeSet();
    }

    public void performOperation(Operation operation){

    }










}
