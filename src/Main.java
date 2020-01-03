import datamodel.operations.Operation;
import datamodel.primitives.Vectorclock;
import datamodel.primitives.tree.Tree;

import java.util.*;

public class Main {


    public static void main(String[] args) {
        int client_id_1 = 1;
        int client_id_2 = 2;
        int object_id = 0;

        Vectorclock vectorclock = new Vectorclock();
        Operation op1 = new Operation(vectorclock.increment(client_id_1), client_id_1, object_id, "0", null);

        Tree tree1 = new Tree(op1);

        Tree tree2 = new Tree(op1);

        List<Vectorclock> precedingOperationVectorclocks = new ArrayList<>();
        precedingOperationVectorclocks.add(vectorclock);
        Operation op11 = new Operation(vectorclock.incrementFrom(client_id_1), client_id_1, object_id, "11", precedingOperationVectorclocks);
        tree1.createNode(op11);
        tree1.updateTreeState();

        Operation op2 = new Operation(vectorclock.incrementFrom(client_id_2), client_id_2, object_id, "21", precedingOperationVectorclocks);
        tree1.addNode(op2);
        tree1.updateTreeState();





        int i = 0;

    }
}

