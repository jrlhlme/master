package datamodel.primitives.tree;

import datamodel.operations.Operation;

import java.util.Map;

/**
 * Created by Jarl on 09-Dec-19.
 */
public class Tree {

    private TreeNode root;

    private Map<Integer, TreeNode> leaves;


    // add/rm nodes functionality
    public Tree(Operation op){
        this.root = new TreeNode(op);
    }

}
