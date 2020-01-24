package datamodel.primitives.tree;

import datamodel.operations.Operation;
import datamodel.primitives.Vectorclock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jarl on 09-Dec-19.
 */
public class TreeNode {

    private Tree tree;

    // contents
    private Operation operation;
    private Vectorclock vectorclock;

    private  List<TreeNode> parents;
    private  List<TreeNode> children;


    public TreeNode(Operation op, Tree tree, List<TreeNode> parents, Vectorclock vectorclock){
        this.tree = tree;

        this.parents = parents != null ? parents : new ArrayList<>();
        this.children = new ArrayList<>();
        this.operation = op;
        this.vectorclock = vectorclock;
    }

    public void addChildren(List<TreeNode> children){
        this.children.addAll(children);
    }

    public Vectorclock getVectorclock(){
        return this.vectorclock;
    }

    public Operation getOperation() {
        return operation;
    }

    public List<TreeNode> getParents() {
        return parents;
    }

    public Tree getTree() {
        return tree;
    }

    public List<TreeNode> getChildren() {
        return children;
    }
}
