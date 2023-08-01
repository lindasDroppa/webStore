package Entities;

import dev.morphia.annotations.*;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.bson.types.ObjectId;

@Entity(value = "Product",noClassnameStored = true)
public class Product
{

    @Id
    private ObjectId id;
    private String nameofProduct;
    private String description;
    private double price;
    private int qty;
    private  String image;

    private String userAccountID;

    public Product() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getUserAccountID() {
        return userAccountID;
    }

    public void setUserAccountID(String userAccountID) {
        this.userAccountID = userAccountID;
    }

    public String getNameofProduct() {
        return nameofProduct;
    }

    public void setNameofProduct(String nameofProduct) {
        this.nameofProduct = nameofProduct;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
