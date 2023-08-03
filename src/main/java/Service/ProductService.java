package Service;

import Control.SecurityFilter;
import Entities.Product;

import Entities.UserAccount;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;


@Path("product")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class ProductService extends EntityUtil<Product>
{


        SecurityFilter securityFilter=new SecurityFilter();

        @Inject UserAccountService userAccountService;

    public ProductService() {
        super(Product.class);
    }

    @POST
    @Path("create")
    public void create(Product entity, @HeaderParam("token") String token) {

        if(securityFilter.isVerified(token)){
            String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();

            entity.setUserAccountID(userID);
           super.create(entity);
        }else{
            throw new NotAuthorizedException("User not authorized");
        }

    }



        @GET
        @Path("all")
        @Produces(MediaType.APPLICATION_JSON)
        public List<Product> all () {
            return super.findAll();
        }


        @DELETE
        @Path("remove")
        @Consumes(MediaType.APPLICATION_JSON)
        public void remove (String productID, @HeaderParam("token") String token){
            if (securityFilter.isVerified(token)) {
            super.remove(productID);
            }

        }

       @GET
       @Path("find/{id}")
        public Product find(@PathParam("id") String productID){
        return super.find(productID);
       }

       @GET
       @Path("selling")
        public List<Product> mySellingProduct(@HeaderParam("token")String token){

        if(securityFilter.isVerified(token)){
            String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();


            return  super.datasourceConnector.getDatastore().find(Product.class).field(
                    "userAccountID").equal(userID).asList();

        }else{
            throw new NotAuthorizedException(Response.status(401).build());
        }


       }


    @GET
    @Path("store")
    public List<Product> availableToShop(@HeaderParam("token")String token){

        if(securityFilter.isVerified(token)){
            String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();


            return  super.datasourceConnector.getDatastore().find(Product.class).field(
                    "userAccountID").notEqual(userID).asList();

        }else{
            throw new NotAuthorizedException(Response.status(401).build());
        }


    }

}
