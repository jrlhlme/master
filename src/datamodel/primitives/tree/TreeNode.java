package datamodel.primitives.tree;

import datamodel.operations.Operation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jarl on 09-Dec-19.
 */
public class TreeNode {

    // contents
    private Operation contents;
    // /contents

    // client_id : node mapping
    private Map<Integer, TreeNode> parents;
    private Map<Integer, TreeNode> children;

    public TreeNode(Operation op){
        this.parents = new HashMap<>();
        this.children = new HashMap<>();
        this.contents = op;
    }


}
