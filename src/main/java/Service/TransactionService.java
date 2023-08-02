package Service;

import Control.SecurityFilter;
import Entities.CartItem;
import Entities.Product;
import Entities.Transaction;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Date;
import java.util.List;

@Path("transaction")
public class TransactionService extends EntityUtil<Transaction>
{

    @Inject ProductService productService;
    @Inject UserAccountService userAccountService;

    @Inject CartService cartService;

    @Inject WalletService walletService;

    SecurityFilter securityFilter=new SecurityFilter();
    public TransactionService() {
        super(Transaction.class);
    }


    @POST
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@HeaderParam("token")String token) {
        Transaction transaction=new Transaction();


        String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();


        List<CartItem> myCartList= cartService.getAll(token);

        for (int i  = 0 ; i < myCartList.size() ; i++){
            //Get the cartItem in the cart
            CartItem cartItem =myCartList.get(i);

            //Get the corresponding product for the cart Item id
            Product product=productService.find(cartItem.getProductId());


            if(product!=null){

                //Set the product id
                transaction.setProductID(cartItem.getProductId());
                transaction.setTimestamp(new Date());//set the timestamp for now

                transaction.setProductOwnerID(product.getUserAccountID()); //set the id for the buying customer
                transaction.setQty(cartItem.getNumberOfProduct());//set the cart item qty

                double buyerBalance=walletService.balance(token);//get the balance for the customer
                double soldAt=cartItem.getNumberOfProduct()*product.getPrice();//calculate the due amount

                //check if customer has enough amount and the owner has enough product
                if(buyerBalance>=soldAt&&(cartItem.getNumberOfProduct()<=product.getQty())){
                    //set the amount product sold at
                    transaction.setPrice(soldAt);
                    transaction.setCustomerID(userID);//now set the customer id

                    walletService.withdrew(soldAt,token);//charge the customer for transaction
                    walletService.addAmount(product.getUserAccountID(), soldAt);//add to the  product owner balance

                    product.setQty(product.getQty()-cartItem.getNumberOfProduct());
                    productService.edit(product);

                    cartService.remove(cartItem.getProductId(),token);

                    super.create(transaction);//create a transaction
                    return Response.status(200).build();

                }else{
                    return Response.status(204).build();
                }


            }





        }

        return Response.status(405).build();
    }


    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transaction> allMyTransaction(@HeaderParam("token") String token){
        if (securityFilter.isValid(token)){

            String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();


            return    datasourceConnector.getDatastore().createQuery(Transaction.class).filter("customerID",userID).filter("productOwnerID",userID) .asList();
       //    return super.datasourceConnector.getDatastore().find(Transaction.class).filter("productOwnerID",userID).filter( "customerID",userID).asList();
//            return super.datasourceConnector.getDatastore().find(Transaction.class).field(
//                    "productOwnerID").equal(userID).asList();
        }else{
            throw new NotAuthorizedException(Response.status(401).build());
        }
    }


    @GET
    @Path("as/customer")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transaction> customer(@HeaderParam("token") String token){
        if (securityFilter.isValid(token)){

            String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();


            return    datasourceConnector.getDatastore().createQuery(Transaction.class).filter("customerID",userID).asList();

        }else{
            throw new NotAuthorizedException(Response.status(401).build());
        }
    }

    @GET
    @Path("as/owner")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transaction> productOwner(@HeaderParam("token") String token){
        if (securityFilter.isValid(token)){

            String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();


            return    datasourceConnector.getDatastore().createQuery(Transaction.class).filter("productOwnerID",userID).asList();

        }else{
            throw new NotAuthorizedException(Response.status(401).build());
        }
    }

}
