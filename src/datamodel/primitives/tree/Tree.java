package datamodel.primitives.tree;

import datamodel.operations.Operation;
import datamodel.primitives.Vectorclock;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Jarl on 09-Dec-19.
 */
public class Tree {

    private TreeNode root;

//    private Map<Integer, TreeNode> leaflist;
    private List<TreeNode> leaflist; //TODO mby this way?

    // represent aggregate state of instance the tree governs, updated on operations
    // updates to joined version of concurrent ops vector clocks on resolution
    // also used to communicate what updates are lacking when exchanging data
    // TODO possibly poor idea as it is insufficient as anchor for new op - instead use leaflist.getVectorClocks()? - Nope, see below
    // TODO used to store op progress -> join of vector clocks for each op performed - leaves (represents completed ops)
    private Vectorclock treestate;

    // connected datatype
    private DataType dataType;


    // add/rm nodes functionality
    public Tree(Operation op){
        this.root = new TreeNode(op, this, null, op.getVectorClock());
        this.leaflist = new ArrayList<>();
        this.leaflist.add(this.root);

        this.updateTreeState();
    }


    /**
     * Called on completion of operation to increment tree vector clock
     * @param operation the most recently performed operation
     */
    public void updateTreeVectorClock(Operation operation){
        this.treestate.join(operation.getVectorClock());
    }


    /**
     * Creates a new node from the existing tree state at current client/instance
     * @param op
     */
    public void createNode(Operation op){
        TreeNode newNode = new TreeNode(op, this, this.leaflist == null ? Collections.singletonList(this.root) : this.leaflist, op.getVectorClock());

        //update leaves w/ new children
        for (TreeNode leaf : leaflist){
            leaf.addChildren(Collections.singletonList(newNode));
        }

        this.leaflist = new ArrayList<>();
        this.leaflist.add(newNode);

        // TODO perform op
    }


    /**
     * Creates a new TreeNode for an operation performed at a different client/instance
     * @param op
     */
    public OperationResolveObject addNode(Operation op){
        // as ops are queued based on global vector clock we know this is a legal op for current instance of tree
        try {
            List<TreeNode> parentNodes = getTreeNodesForVectorClocks(op.getPrecedingOperationVectorClocks());

            TreeNode newNode = new TreeNode(op, this, parentNodes, op.getVectorClock());
            for (TreeNode parentNode : parentNodes){
                parentNode.addChildren(Collections.singletonList(newNode));
                // remove if exists
                this.leaflist.remove(parentNode);
            }
            this.leaflist.add(newNode);

            if (this.leaflist.size() > 1){
                return new OperationResolveObject(op, getRootOfConcurrentOps(newNode));
            } else {
                // no concurrency
                return new OperationResolveObject(op, null);
            }

        } catch (IllegalStateException e) {
            // TODO handle
            return null;
        }
    }



    /**
     * Gets TreeNode objects for provided vector clocks
     * @param vectorClocks
     * @return
     */
    private List<TreeNode> getTreeNodesForVectorClocks(List<Vectorclock> vectorClocks){
        LinkedBlockingQueue<TreeNode> nodeQueue = new LinkedBlockingQueue<>(leaflist);
        List<TreeNode> returnList = new ArrayList<>();
        TreeNode tn;
        while (vectorClocks.size() > 0){
            tn = nodeQueue.poll();

            if (tn == null){
                throw new IllegalStateException("Preceding operations does not exist - operation cannot be performed at this time");
            }

            try {
                for (TreeNode parent : tn.getParents()) {
                    nodeQueue.put(parent);
                }
            } catch (InterruptedException e){
                // TODO handle
                throw new RuntimeException("Error when searching for concurrent op root : " + e.getMessage());
            }

            for (int i = 0; i < vectorClocks.size(); i++){
                if (tn.getVectorclock().isMatching(vectorClocks.get(i))){
                    returnList.add(tn);
                    vectorClocks.remove(i);
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
        if (this.treestate == null){
            this.treestate = leaflist.get(0).getVectorclock();
        }

        for (TreeNode leafNode: leaflist){
            // TODO naive approach
            this.treestate.join(leafNode.getVectorclock());
        }
    }





    public TreeNode getRootOfConcurrentOps(TreeNode leaf){
        TreeNode tn = this.root;
        LinkedBlockingQueue<TreeNode> nodeQueue = new LinkedBlockingQueue<>();

        // TODO complete this more gracefully
        while (true) {
            for (TreeNode treeNode : tn.getChildren()) {
                if (treeNode.getVectorclock().isConcurrentTo(leaf.getVectorclock())) {
                    return tn;
                }
                try {
                    nodeQueue.put(treeNode);
                } catch (InterruptedException e) {
                    // TODO handle
                    throw new RuntimeException("Error when searching for concurrent op root : " + e.getMessage());
                }
            }
            tn = nodeQueue.poll();
        }
    }



    public TreeNode getTreeNodeByVectorClock(Vectorclock vectorclock){

        return null;
    }





//    /**
//     * For the given tree, returns treenode where clients diverged
//     * @return
//     */
//    public TreeNode getRootOfConcurrentOps(){
//        // TODO if single leaf OR multiple leaves dominated by state of tree/datastruct then no concurrency
//        // if not then extract all v_clck, then traverse up ONE until Node found that is dominated by EACH leaf TODO verify that this holds [SEE PAPER DOC]
//
////        Vectorclock treeState = this.treestate;
////        for (TreeNode node : this.leaflist){
////            if (treeState.isConcurrentTo(node.getContents().getVectorClock())){
////                // traverse upwards until node is sequential to treestate
////                TreeNode itrNode = node.getParents().get(0); // which path is selected up the tree is irrelevant TODO verify
////                while (itrNode.getVectorclock().isConcurrentTo(treeState)){
////                    itrNode = itrNode.getParents().get(0);
////                }
////                return itrNode;
////            }
////        }
//        // TODO does not hold for n separate concurrent ops - rm this, replace w/ checkconcurrency(treeNode)
//
//
//
//    }
}
