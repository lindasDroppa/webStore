package Service;

import Control.SecurityFilter;
import DTOs.ProductDTO;
import DTOs.RemoveRequest;
import Entities.Cart;
import Entities.CartItem;
import Entities.Product;
import Entities.UserAccount;
import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;

@Path("cart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CartService extends EntityUtil<Cart> {
    public CartService() {
        super(Cart.class);
    }



        SecurityFilter securityFilter=new SecurityFilter();

    @Inject UserAccountService userAccountService;

    @Inject ProductService productService;


    @POST
    @Path("add/item")
    public Response addItemtoCart(CartItem cartItem,@HeaderParam("token")String token){
        System.out.println("===================="+cartItem.toString());
        System.out.println("token=================================:"+token);
        addItemToCart(cartItem,token);
        return Response.ok().build();
    }






    private void addItemToCart(CartItem cartItem,String token){


                if(securityFilter.isVerified(token)){
                    String email=securityFilter.getIssuer(token);

                    UserAccount user= userAccountService.findByEmail(email);

                    Cart cart=super.datasourceConnector.getDatastore().find(Cart.class).field("accountID").equal(user.getId().toString()).get();

                  List<CartItem> myListOfCartItems=cart.getItems();


                  if(myListOfCartItems==null){
                      List<CartItem> newList=new ArrayList<>();
                      newList.add(cartItem);
                      cart.setItems(newList);
                  }else{
                      boolean change=false;
                      for (int i = 0 ; i< myListOfCartItems.size() ; i++){
                          if(myListOfCartItems.get(i).getProductId().equals(cartItem.getProductId())){
                          int itemsBefore =   myListOfCartItems.get(i).getNumberOfProduct();
                          myListOfCartItems.get(i).setNumberOfProduct(itemsBefore+1);
                          change = true;
                          break;
                          }
                      }

                      if(change){
                          cart.setItems(myListOfCartItems);
                      }else{
                          myListOfCartItems.add(cartItem);
                      }


                  }



                  super.edit(cart);

                  total(token);


              }else{
                    throw new NotAuthorizedException(Response.status(401).build());
                }
    }



    public List<CartItem> getAll(String token){
        System.out.println("token=================================:"+token);



        String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();
        return super.datasourceConnector.getDatastore().find(Cart.class)
                .field("accountID").equal(userID).get().getItems();

    }
    @GET
    @Path("get/products")
    public Response getAllProduct(@HeaderParam("token")String token){
        return Response.ok(allP(token)).build();
    }
    private  List<ProductDTO> allP(String token){
        List<CartItem> getAll=getAll(token);
        List<ProductDTO> myProductDTOList=new ArrayList<>();
        if (getAll!=null){
            for (int i =0 ;i<getAll.size();i++){
                ProductDTO product=productService.findDTO(getAll.get(i).getProductId());
                product.qtyCart=getAll.get(i).getNumberOfProduct();
                myProductDTOList.add(product);
            }
        }


        return myProductDTOList;
    }
    @POST
    @Path("remove")
    public Response remove(RemoveRequest removeRequest, @HeaderParam("token")String token){
        System.out.println("token=================================:"+token);
        String productId=removeRequest.getProductId();
        System.out.println("Removing from cart ID :"+productId);
        String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();

        boolean removed=false;
        Cart cart=super.datasourceConnector.getDatastore().find(Cart.class).field("accountID").equal(userID).get();
        List<CartItem> myListOfCartItems=cart.getItems();

        for (int i = 0 ; i< myListOfCartItems.size() ; i++){

            if(productId.equals(myListOfCartItems.get(i).getProductId())){
                myListOfCartItems.remove(i);

                removed=true;
                break;
            }
        }


        if(myListOfCartItems.isEmpty()){
            cart.setItems(new ArrayList<>());
        }else{
            cart.setItems(myListOfCartItems);
        }


        total(token);
        if(removed){
            super.create(cart);

            return Response.status(200).build();
        }else{

            return Response.status(204).build();
        }





    }

    @GET
    @Path("total")
    public Response total(@HeaderParam("token") String token){
        return Response.ok(getCartT(token)).build();
    }
    private double getCartT(String token){
        String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();
        Cart cart=super.datasourceConnector.getDatastore().find(Cart.class).field("accountID").equal(userID).get();
        double amount = 0;
        if(cart!=null){
            List<CartItem> myListOfCartItems=cart.getItems();
            if(myListOfCartItems==null){
                return 0;
            }
            if(myListOfCartItems!=null||myListOfCartItems.size()!=0){
                for (int i = 0 ;  i< myListOfCartItems.size() ; i++){


                    amount=amount+ productService.find(myListOfCartItems.get(i).getProductId()).getPrice();
                }
            }
            cart.setTotal(amount);
            super.edit(cart);
            return amount;
        }else{
            return 0;
        }

    }

    @POST
    @Path("clear")
        public Response clearCart(@HeaderParam("token") String token){
        String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();
        Cart cart=super.datasourceConnector.getDatastore().find(Cart.class).field("accountID").equal(userID).get();
        cart=new Cart();
        cart.setAccountID(userID);
        create(cart);
    return Response.ok().build();

    }

    @GET
    @Path("size")
    public Response getCartSize(@HeaderParam("token") String token){

        System.out.println("User :"+userAccountService.findByEmail(securityFilter.getIssuer(token)).getEmail());
        return Response.ok(allP(token).size()).build();
    }


}

