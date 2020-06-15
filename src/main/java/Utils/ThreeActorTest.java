package Utils;


import org.apache.commons.lang3.builder.ToStringBuilder;

public class ThreeActorTest {
    private String id;
    private String childProduct;
    private String parentProduct;
    private String isChildConfigWithPTM;
    private String isParentConfigWithPTM;
    private String isChildAssociatedWithParent;
    private String request;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    /**
     * No args constructor for use in serialization
     */
    public ThreeActorTest() {
    }

    /**
     * @param id
     * @param isChildAssociatedWithParent
     * @param isChildConfigWithPTM
     * @param isParentConfigWithPTM
     * @param parentProduct
     * @param childProduct
     */
    public ThreeActorTest(String id, String childProduct, String parentProduct, String isChildConfigWithPTM, String isParentConfigWithPTM, String isChildAssociatedWithParent) {
        super();
        this.id = id;
        this.childProduct = childProduct;
        this.parentProduct = parentProduct;
        this.isChildConfigWithPTM = isChildConfigWithPTM;
        this.isParentConfigWithPTM = isParentConfigWithPTM;
        this.isChildAssociatedWithParent = isChildAssociatedWithParent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChildProduct() {
        return childProduct;
    }

    public void setChildProduct(String childProduct) {
        this.childProduct = childProduct;
    }

    public String getParentProduct() {
        return parentProduct;
    }

    public void setParentProduct(String parentProduct) {
        this.parentProduct = parentProduct;
    }

    public String getIsChildConfigWithPTM() {
        return isChildConfigWithPTM;
    }

    public void setIsChildConfigWithPTM(String isChildConfigWithPTM) {
        this.isChildConfigWithPTM = isChildConfigWithPTM;
    }

    public String getIsParentConfigWithPTM() {
        return isParentConfigWithPTM;
    }

    public void setIsParentConfigWithPTM(String isParentConfigWithPTM) {
        this.isParentConfigWithPTM = isParentConfigWithPTM;
    }

    public String getIsChildAssociatedWithParent() {
        return isChildAssociatedWithParent;
    }

    public void setIsChildAssociatedWithParent(String isChildAssociatedWithParent) {
        this.isChildAssociatedWithParent = isChildAssociatedWithParent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("childProduct", childProduct).append("parentProduct", parentProduct).append("isChildConfigWithPTM", isChildConfigWithPTM).append("isParentConfigWithPTM", isParentConfigWithPTM).append("isChildAssociatedWithParent", isChildAssociatedWithParent).toString();
    }


}
