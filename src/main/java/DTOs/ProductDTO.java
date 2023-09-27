package DTOs;

import org.bson.types.ObjectId;

public class ProductDTO
{
    public String id;
    public String nameofProduct;
    public String description;
    public double price;
    public int qty;
    public  String image;

    public String userAccountID;
    public boolean isOnCart;
    public int qtyCart;
}
