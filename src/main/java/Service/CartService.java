package Service;

import Control.SecurityFilter;
import Entities.Cart;
import Entities.CartItem;
import Entities.UserAccount;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
    public void addItemToCart(CartItem cartItem,@HeaderParam("token")String token){


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

    @GET
    @Path("get")

    public List<CartItem> getAll(@HeaderParam("token")String token){
        String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();
        return super.datasourceConnector.getDatastore().find(Cart.class)
                .field("accountID").equal(userID).get().getItems();

    }

    @POST
    @Path("remove/{productId}")
    public Response remove(@PathParam("productId") String productId,@HeaderParam("token")String token){
        String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();

        boolean removed=false;
        Cart cart=super.datasourceConnector.getDatastore().find(Cart.class).field("accountID").equal(userID).get();
        List<CartItem> myListOfCartItems=cart.getItems();


        System.out.println(cart.getId().toString());


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
    public double total(@HeaderParam("token") String token){
        String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();
        Cart cart=super.datasourceConnector.getDatastore().find(Cart.class).field("accountID").equal(userID).get();
        double amount = 0;
        List<CartItem> myListOfCartItems=cart.getItems();
        if(myListOfCartItems!=null||myListOfCartItems.size()!=0){
            for (int i = 0 ;  i< myListOfCartItems.size() ; i++){


                amount=amount+ productService.find(myListOfCartItems.get(i).getProductId()).getPrice();
            }
        }
        cart.setTotal(amount);
        super.edit(cart);
        return amount;
    }


}

