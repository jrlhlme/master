package datamodel.primitives.tree;

import datamodel.operations.Operation;
import datamodel.primitives.Vectorclock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jarl on 09-Dec-19.
 */
public class TreeNode {

    private Tree tree;

    // contents
    private Operation contents;
    private Vectorclock vectorclock;
    // /contents


    // client_id : node mapping TODO mby better as lists?
//    private Map<Integer, TreeNode> parents;
//    private Map<Integer, TreeNode> children;
    private  List<TreeNode> parents;
    private  List<TreeNode> children;


    public TreeNode(Operation op, Tree tree, List<TreeNode> parents, Vectorclock vectorclock){
        this.tree = tree;

        this.parents = parents != null ? parents : new ArrayList<>();
        this.children = new ArrayList<>();
        this.contents = op;
        this.vectorclock = vectorclock;
    }

    public void addChildren(List<TreeNode> children){
        this.children.addAll(children);
    }

    public Vectorclock getVectorclock(){
        return this.vectorclock;
    }

    public Operation getContents() {
        return contents;
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
