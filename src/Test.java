import datamodel.operations.wrappers.ExportOperation;
import instance.assets.ObjectStorage;

public class Test {

    public static void main(String[] args) {

        int client_id_1 = 1;
        int client_id_2 = 2;

        ObjectStorage db_client1 = new ObjectStorage(client_id_1);
        ObjectStorage db_client2 = new ObjectStorage(client_id_2);

        print("- Start demonstration 1 -");
        db_client1.createOperation();
        db_client1.createUnit();
        db_client1.createMission();
        db_client1.assignOperationUnit(db_client1.getOperation("1-1"), db_client1.getUnit("1-2"), "21");
        syncDBs(db_client1, db_client2);
        print("expected outcome : identical");
        print(db_client1.toString());
        print(db_client2.toString());

        db_client1.removeUnitFromOperation(db_client1.getOperation("1-1"), db_client1.getUnit("1-2"), "21");
        db_client2.assignUnitMission(db_client2.getUnit("1-2"), db_client2.getMission("1-3"), "Mission 1");
        print("# expected outcome : client_1 should have removed all its relations while client_2 has relations between operation 1-1 and unit 1-2");
        print(db_client1.toString());
        print(db_client2.toString());

        print("expected outcome : operation in client_2 should take precedence over the removal of the relation in client_1, resulting in operation 1-1 still being related to unit 1-2");
        syncDBs(db_client1, db_client2);
        print(db_client1.toString());
        print(db_client2.toString());
        print("- finished demonstation 1 -\n");

        // reset states
        db_client1 = new ObjectStorage(client_id_1);
        db_client2 = new ObjectStorage(client_id_2);
        // /reset states


        print("- Start demonstration 2 -");
        db_client1.createOperation();
        db_client1.createUnit();
        db_client1.createMission();
        db_client1.assignOperationUnit(db_client1.getOperation("1-1"), db_client1.getUnit("1-2"), "21");
        db_client1.assignOperationMission(db_client1.getOperation("1-1"), db_client1.getMission("1-3"), "Mission 1");
        syncDBs(db_client1, db_client2);
        print("# expected outcome : identical states");
        print(db_client1.toString());
        print(db_client2.toString());

        db_client1.removeUnitFromOperation(db_client1.getOperation("1-1"), db_client1.getUnit("1-2"), "21");
        db_client1.removeOperationMission(db_client1.getOperation("1-1"), db_client1.getMission("1-3"), "Mission 1");
        db_client2.assignUnitMission(db_client2.getUnit("1-2"), db_client2.getMission("1-3"), "1st mission");
        print("# expected outcome : client_1 should have removed all its relations while client_2 has relations between operation 1-1 and unit 1-2 and mission 1-3");
        print(db_client1.toString());
        print(db_client2.toString());

        print("expected outcome : operation in client_2 should take precedence over the removal of the relations in client_1, resulting in operation 1-1 still being related to unit 1-2 and mission 1-3");
        syncDBs(db_client1, db_client2);
        print(db_client1.toString());
        print(db_client2.toString());
        print("- finished demonstation 1 -\n");



    }








    public static void print(String message){
        System.out.println(message);
    }

    public static void performOpsFromForeignDB(ObjectStorage local, ObjectStorage foreign){
        ExportOperation operation = foreign.getExportOperation();
        while(operation != null){
            local.processExternalOperation(operation);
            operation = foreign.getExportOperation();
        }
    }

    public static void syncDBs(ObjectStorage db1, ObjectStorage db2){
        performOpsFromForeignDB(db1, db2);
        performOpsFromForeignDB(db2, db1);
    }



}
