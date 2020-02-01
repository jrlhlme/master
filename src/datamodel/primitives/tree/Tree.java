package datamodel.primitives.tree;

import datamodel.operations.Operation;
import datamodel.primitives.DataType;
import datamodel.primitives.Vectorclock;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Jarl on 09-Dec-19.
 */
public class Tree { // TODO purge contents of node if is dominated cascading op as garbage collection

    private TreeNode root;

//    private Map<Integer, TreeNode> leaflist;
    private List<TreeNode> leaflist;

    // represent aggregate state of instance the tree governs, updated on operations
    private Vectorclock treestate;

    // connected datatype
    private DataType dataType;


    // add/rm nodes functionality
    public Tree(Operation op){
        this.treestate = new Vectorclock();
        this.root = new TreeNode(null, this, null, this.treestate.clone());

        this.leaflist = new ArrayList<>();

        op.setPreceding_operations_vectorclocks(Collections.singletonList(this.treestate.clone()));
        this.createNode(op);
        this.updateTreeState();
    }

    public List<TreeNode> getLeaflist() {
        return leaflist;
    }


    /**
     * Creates a new node from the existing tree state at current client/instance
     * @param op
     */
    public OperationResolveObject createNode(Operation op){
        TreeNode newNode = new TreeNode(op, this, this.leaflist.isEmpty() ? Collections.singletonList(this.root) : this.leaflist,
                this.treestate.incrementFrom(op.getClientId()));
//                op.getVectorClock());

        //update leaves w/ new children
        List<Vectorclock> precedingOps = new ArrayList<>();
        if (leaflist.isEmpty()){
            this.root.addChildren(Collections.singletonList(newNode));
        } else {
            for (TreeNode leaf : leaflist) {
                leaf.addChildren(Collections.singletonList(newNode));
                precedingOps.add(leaf.getVectorclock());
            }
        }

        if (precedingOps.size() == 0){
            precedingOps.add(this.root.getVectorclock().clone());
        }

        op.setPreceding_operations_vectorclocks(precedingOps);
        this.leaflist = new ArrayList<>();
        this.leaflist.add(newNode);

        return new OperationResolveObject(op, null);
    }


    /**
     * Creates a new TreeNode for an operation performed at a different client/instance
     * @param op
     * @param provideConcurrentOps whether concurrent ops should be calculated and returned (unnecessary for some op types)
     */
    public OperationResolveObject addNode(Operation op, boolean provideConcurrentOps){
        // as ops are queued based on global vector clock we know this is a legal op for current instance of tree
        try {
            List<TreeNode> parentNodes = getTreeNodesForVectorClocks(op.getPrecedingOperationVectorClocks());
            if (parentNodes == null){
                throw new IllegalStateException("Preceding operations does not exist - operation cannot be performed at this time");
            }

            TreeNode newNode = new TreeNode(op, this, parentNodes, new Vectorclock());
            for (TreeNode parentNode : parentNodes){
                parentNode.addChildren(Collections.singletonList(newNode));
                newNode.getVectorclock().join(parentNode.getVectorclock());
                // remove if exists
                this.leaflist.remove(parentNode);
            }
            newNode.getVectorclock().increment(op.getClientId());
            this.leaflist.add(newNode);
            this.updateTreeState();

            if (this.leaflist.size() > 1 && provideConcurrentOps){
                return new OperationResolveObject(op, getRootOfConcurrentOps(newNode));
            } else {
                // no concurrency
                return new OperationResolveObject(op, null);
            }


        } catch (IllegalStateException e) {
            throw new IllegalStateException("Operation cannot be performed at this time, message : " + e.getMessage());
        }
    }



    /**
     * Gets TreeNode objects for provided vector clocks
     * @param vectorClocks
     * @return
     */
    private List<TreeNode> getTreeNodesForVectorClocks(List<Vectorclock> vectorClocks){
        LinkedBlockingQueue<TreeNode> nodeQueue = this.leaflist.isEmpty() ? new LinkedBlockingQueue<>(Collections.singletonList(this.root)) : new LinkedBlockingQueue<>(leaflist);

        List<TreeNode> returnList = new ArrayList<>();
        TreeNode tn;

        List<Integer> foundVectorClocks = new ArrayList<>();
        while (vectorClocks.size() > foundVectorClocks.size()){
            tn = nodeQueue.poll();

            if (tn == null){
                return null;
            }

            try {
                for (TreeNode parent : tn.getParents()) {
                    nodeQueue.put(parent);
                }
            } catch (InterruptedException e){
                throw new RuntimeException("Error when searching for concurrent op root : " + e.getMessage());
            }

            for (int i = 0; i < vectorClocks.size() && !foundVectorClocks.contains(i); i++){
                if (tn.getVectorclock().isMatching(vectorClocks.get(i))){
                    returnList.add(tn);
                    foundVectorClocks.add(i);
                    break;
                }
            }
        }

        return returnList;
    }


    /**
     * Updates tree vector clock to be the intersection of child vector clocks
     */
    public void updateTreeState(){
        if (this.treestate == null || this.leaflist.isEmpty()){
            this.treestate = this.root.getVectorclock().clone();
        } else {
            for (TreeNode leafNode : leaflist) {
                this.treestate.join(leafNode.getVectorclock());
            }
        }
    }





    public TreeNode getRootOfConcurrentOps(TreeNode leaf){
        TreeNode tn = this.root;
        LinkedBlockingQueue<TreeNode> nodeQueue = new LinkedBlockingQueue<>();

        while (true) {
            for (TreeNode treeNode : tn.getChildren()) {
                if (treeNode.getVectorclock().isConcurrentTo(leaf.getVectorclock())) {
                    return tn;
                }
                try {
                    nodeQueue.put(treeNode);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Error when searching for concurrent op root : " + e.getMessage());
                }
            }
            tn = nodeQueue.poll();
        }
    }


}
