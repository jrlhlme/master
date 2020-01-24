package datamodel.operations.contents;


public class ORUpdateContents implements OperationContents {

    public String getId() {
        return id;
    }

    private String id;

    public ORUpdateContents(String id){
        this.id = id;
    }


    @Override
    public ORUpdateContents clone(){
        return new ORUpdateContents(this.id);
    }


}
