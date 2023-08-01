package Entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

import org.bson.types.ObjectId;

import java.util.Date;

@Entity(value = "Transaction" , noClassnameStored = true)
public class Transaction {

    @Id
    ObjectId id;
    String productOwnerID;
    String customerID;
    String productID;
    Date timestamp;
    int qty;
    double price;


    public Transaction() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getProductOwnerID() {
        return productOwnerID;
    }

    public void setProductOwnerID(String productOwnerID) {
        this.productOwnerID = productOwnerID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


}
