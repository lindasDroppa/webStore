package DTOs;

import java.io.Serializable;

public class RemoveRequest implements Serializable
{
    String productId;

    public RemoveRequest() {
    }

    public RemoveRequest(String productId) {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
