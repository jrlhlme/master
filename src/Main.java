import instance.assets.ObjectStorage;

public class Main {


    public static void main(String[] args) {
        int client_id_1 = 1;
        int client_id_2 = 2;
        int object_id = 0;


        ObjectStorage db = new ObjectStorage(client_id_1);
        db.createOperation();
        db.addOperation("1-1", false);
//        db.createUnit();
//        db.createMission();

        ObjectStorage db2 = new ObjectStorage(client_id_2);
        db2.processExternalOperation(db.getExportOperation());
        db2.removeOperation("1-1");

        db.processExternalOperation(db2.getExportOperation());
        db2.processExternalOperation(db.getExportOperation());

        db.createUnit();

        db.assignOperationUnit(db.getOperation("1-1"), db.getUnit("1-2"), "22B", false);

        db.createMission();
        db.assignOperationMission(db.getOperation("1-1"), db.getMission("1-3"), "", false);
        db.assignUnitMission(db.getUnit("1-2"), db.getMission("1-3"), "", false);
        db.removeUnitFromOperation(db.getOperation("1-1"), db.getUnit("1-2"), "22B", false);


        db2.processExternalOperation(db.getExportOperation());
        db2.processExternalOperation(db.getExportOperation());
        db2.processExternalOperation(db.getExportOperation());
        db2.processExternalOperation(db.getExportOperation());



        int i = 0;

        // MVR test
//        MVR mvrClient1 = new MVR(client_id_1);
//        Vectorclock client1VectorClock = new Vectorclock();
//        client1VectorClock.increment(client_id_1);
//        datamodel.operations.Operation op11 = new datamodel.operations.Operation(client_id_1, new MVRUpdateContents("21", "1"), null, OperationType.MVR_SET, false);
//        mvrClient1.processOperation(op11);
//
//        client1VectorClock.increment(client_id_1);
//        datamodel.operations.Operation op12 = new datamodel.operations.Operation(client_id_1, new MVRUpdateContents("21", "2"), null, OperationType.MVR_SET, false);
//        mvrClient1.processOperation(op12);
//
//
//        MVR mvrClient2 = new MVR(client_id_2);
//        mvrClient2.processOperation(op11);
//
//        Vectorclock client2VectorClock = new Vectorclock();
//        client2VectorClock.increment(client_id_2);
//        datamodel.operations.Operation op21 = new datamodel.operations.Operation(client_id_2, new MVRUpdateContents("21", "3"), null, OperationType.MVR_SET, false);
//        mvrClient2.processOperation(op21);
//
//
//        mvrClient2.processOperation(op12);
//        mvrClient1.processOperation(op21);
//
//        int i = 0;






        // orset test
//        ORSet client1ORSet = new ORSet(client_id_1);
//        datamodel.operations.Operation op11 = new datamodel.operations.Operation(client_id_1, new ORUpdateContents("1"), null, OperationType.ORSET_ADD);
//        client1ORSet.processOperation(op11);
//        // 1: op11
//        datamodel.operations.Operation op12 = new datamodel.operations.Operation(client_id_1, new ORUpdateContents("1"), null, OperationType.ORSET_REMOVE);
//        client1ORSet.processOperation(op12);
//        // 1: op11, op12
//
//
//        ORSet client2ORSet = new ORSet(client_id_2);
//        client2ORSet.processOperation(op11);
//        // 2: op11
//        datamodel.operations.Operation op21 = new datamodel.operations.Operation(client_id_2, new ORUpdateContents("1"), null, OperationType.ORSET_ADD);
//        client2ORSet.processOperation(op21);
//        // 2: op11, op21
//        datamodel.operations.Operation op22 = new datamodel.operations.Operation(client_id_2, new ORUpdateContents("1"), null, OperationType.ORSET_REMOVE);
//        client2ORSet.processOperation(op22);
//        // 2: op11, op21, op22
//
//        client1ORSet.processOperation(op22); //should fail
//        client1ORSet.processOperation(op21); // 1 exists
//
//
//        int i = 0;



        // treetest
//        Vectorclock opposingVectorClock = vectorclock.clone();
//
//        Tree tree1 = new Tree(op1);
//
//        Tree tree2 = new Tree(op1);
//
//        List<Vectorclock> precedingOperationVectorclocks = new ArrayList<>(Collections.singletonList(vectorclock.clone()));
//        vectorclock.increment(client_id_1);
//        Operation op11 = new Operation(vectorclock.clone(), client_id_1, object_id, new Payload("12"), precedingOperationVectorclocks);
//        tree1.createNode(op11);
//        tree1.updateTreeState();
//
//        precedingOperationVectorclocks = new ArrayList<>(Collections.singletonList(vectorclock.clone()));
//        vectorclock.increment(client_id_1);
//        Operation op12 = new Operation(vectorclock.clone(), client_id_1, object_id, new Payload("13"), precedingOperationVectorclocks);
//        tree1.createNode(op12);
//        tree1.updateTreeState();
//
//        precedingOperationVectorclocks = new ArrayList<>(Collections.singletonList(opposingVectorClock.clone()));
//        opposingVectorClock.increment(client_id_2);
//        Operation op21 = new Operation(opposingVectorClock.clone(), client_id_2, object_id, new Payload("21"), precedingOperationVectorclocks);
//        tree1.addNode(op21);
//        tree1.updateTreeState();







//        int i = 0;

    }
}

