package Service;

import Control.SecurityFilter;
import DTOs.ProductDTO;
import Entities.CartItem;
import Entities.Product;

import Entities.UserAccount;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;


@Path("product")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class ProductService extends EntityUtil<Product>
{


        SecurityFilter securityFilter=new SecurityFilter();

        @Inject UserAccountService userAccountService;

        @Inject  CartService cartService;

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
        public List<ProductDTO> all () {
        List<Product> myProducts=super.findAll();
        List<ProductDTO> retProduct=new ArrayList<>();
        ProductDTO prod=null;
        Product product=null;
        for(int i =0 ;i<myProducts.size();i++){
            product=myProducts.get(i);
            prod=new ProductDTO();
            productToDTO(product, prod);

            retProduct.add(prod);

        }
        return retProduct;
        }


        @DELETE
        @Path("remove")
        @Consumes(MediaType.APPLICATION_JSON)
        public void remove (String productID, @HeaderParam("token") String token){
            System.out.println("token=================================:"+token);
            if (securityFilter.isVerified(token)) {
            super.remove(productID);
            }

        }
    public Product find( String productID){
        System.out.println("==============================================================================="+productID);
        return super.find(productID);
    }

       @GET
       @Path("find/{id}")
        public ProductDTO findDTO(@PathParam("id") String productID){
           System.out.println("==============================================================================="+productID);
           Product product=super.find(productID);
           ProductDTO prod=new ProductDTO();
           productToDTO(product, prod);

           return prod;
       }

    private void productToDTO(Product product, ProductDTO prod) {
        prod.id=product.getId().toString();
        prod.description=product.getDescription();
        prod.nameofProduct=product.getNameofProduct();
        prod.image=product.getImage();
        prod.price=product.getPrice();
        prod.qty=product.getQty();
        prod.userAccountID=product.getUserAccountID();
    }


    @GET
       @Path("selling")
        public List<Product> mySellingProduct(@HeaderParam("token")String token){
           System.out.println("token=================================:"+token);
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
    public List<ProductDTO> availableToShop(@HeaderParam("token")String token){
        System.out.println("token=================================:"+token);
        if(securityFilter.isVerified(token)){
            String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();
            List<CartItem> cartItemsList=cartService.getAll(token);
            List<ProductDTO> myStoreDTO=new ArrayList<>();
            List<Product> myStore=super.datasourceConnector.getDatastore().find(Product.class).field(
                    "userAccountID").notEqual(userID).asList();
            if(cartItemsList!=null){
                for(int i = 0 ; i<myStore.size();i++){
                    Product product=myStore.get(i);
                    ProductDTO productDTO=new ProductDTO();
                    productToDTO(product,productDTO);
                    productDTO.isOnCart=false;
                    for(int x = 0 ; x<cartItemsList.size();x++){
                        if(cartItemsList.get(x).getProductId().equals(productDTO.id)){

                            productDTO.isOnCart=true;
                            break;
                        }
                    }
                    myStoreDTO.add(productDTO);

                }
            }else{
                for(int i = 0 ; i<myStore.size();i++){
                    Product product=myStore.get(i);
                    ProductDTO productDTO=new ProductDTO();
                    productToDTO(product,productDTO);
                    myStoreDTO.add(productDTO);
                }
            }



            return  myStoreDTO;

        }else{
            throw new NotAuthorizedException(Response.status(401).build());
        }


    }

}
