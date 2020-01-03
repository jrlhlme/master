package datamodel.primitives;

import datamodel.operations.Operation;
import datamodel.primitives.tree.DataType;
import datamodel.primitives.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jarl on 30-Nov-19.
 */
public class ORSet implements DataType {

    // reflects contents resulting from performed ops at the given time
    private List<Integer> state_contents;
    private Vectorclock state_vclock; //TODO migrate to individual trees


    // TODO these are alternatives, figure out which is the best approach
    private Map<Integer, Tree> entries; // we try this way initially - mapping btwn object id and its tree
    private Tree operationStorage;



    public ORSet(){
        this.state_vclock = new Vectorclock();
        this.state_contents = new ArrayList();

    }

    public void performOperation(Operation operation){
        Tree opTree = entries.get(operation.getObjectId());
        opTree.addNode(operation);




        opTree.updateTreeVectorClock(operation);
    }










}
