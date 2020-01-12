package datamodel.operations.contents;


public class ORUpdateContents implements OperationContents {

    public String getId() {
        return id;
    }

    private String id;
//    private int targetObjectType;
//    private String targetObjectId;

    public ORUpdateContents(String id/*, int targetObjectType/*, String targetObjectId*/){
        this.id = id;
//        this.targetObjectId = targetObjectId;
//        this.targetObjectType = targetObjectType;
    }


    @Override
    public ORUpdateContents clone(){
        return new ORUpdateContents(this.id);
    }

//    public int getTargetObjectType(){
//        return this.targetObjectType;
//    }
//    public String getTargetObjectId(){
//        return this.targetObjectId;
//    }


}
