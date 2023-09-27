package Service;



import Control.LoginDTO;
import Control.LoginSuccess;
import Control.SecurityFilter;
import Entities.Cart;
import Entities.CartItem;
import Entities.UserAccount;
import Entities.Wallet;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Path("useraccount")
@Stateless
public class UserAccountService extends EntityUtil<UserAccount>
{



    SecurityFilter securityFilter=new SecurityFilter();

    @Inject CartService cartService;

    @Inject WalletService walletService;

    public UserAccountService() {
        super(UserAccount.class);
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(UserAccount userAccount) {

        if(!userExist(userAccount.getEmail())){
            String unencryptedPassword= userAccount.getPassword();

            try {
                userAccount.setPassword(securityFilter.encrypt(unencryptedPassword));
            }catch (Exception e){
                return Response.status(100).build();
            }


            super.create(userAccount);

            //Get the user by email
            UserAccount newUser=findByEmail(userAccount.getEmail());
            Cart newCart=new Cart();

            newCart.setAccountID(newUser.getId().toString());
            List<CartItem> cartItemList=new ArrayList<>();
            CartItem cartItem=new CartItem();

            newCart.setItems(cartItemList);
            newCart.setTotal(0);
            newCart.setNumberOfItems(0);


            //Create a cart for new user
            cartService.create(newCart);

            //Create wallet
            Wallet wallet=new Wallet();
            wallet.setBalance(0);
            wallet.setUserAccountID(newUser.getId().toString());

            walletService.create(wallet);


            return Response.ok("CRT").build();
        }
        else
        {
            return Response.ok("NCRT").build();
        }

    }

    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(LoginDTO loginDTO){
        return Response.ok(login(loginDTO)).build();
    }




    public LoginSuccess login(LoginDTO loginDTO){

        String decryptedPasword=null;
        UserAccount loginUser= findByEmail(loginDTO.getEmail());
        try {
            decryptedPasword= securityFilter.decrypt(loginUser.getPassword());
        }catch (Exception ex){
            throw new RuntimeException();
        }

        if( decryptedPasword.equals(loginDTO.getPassword())){
            String token = securityFilter.createJWTToken(loginDTO.getEmail(),loginDTO.getPassword());



            return new LoginSuccess(loginUser.getName(),token,loginUser.getEmail(),loginUser.getId().toString());

        }else{
            throw new NotAuthorizedException(Response.status(401).build());
        }
    }

    @Override
    public void edit(UserAccount entity) {

        super.edit(entity);


    }


    @Override
    public List<UserAccount> findAll() {
        return super.findAll();
    }





    @GET
    @Path("count")
    @Override
    public Long count() {

        //return number of account available
        return super.count();
    }


    //Check if user exist
    public boolean userExist(String email){
        //Loop through the array
        List<UserAccount> userAccountList=super.findAll();
        for (int i= 0 ; i < userAccountList.size() ; i++){
            //Check if entered email exist within the database
            if(email.equalsIgnoreCase(userAccountList.get(i).getEmail())){

                //return true if exist
                return true;

            }
        }

        //return false if user email was not found
        return false;
    }


    public UserAccount findByEmail(String email){
        //Loop through the array
        List<UserAccount> userAccountList=super.findAll();
        for (int i= 0 ; i < userAccountList.size() ; i++){
            if(userAccountList.get(i).getEmail().equalsIgnoreCase(email)){
                return userAccountList.get(i);
            }
        }
        return null;
    }
    @Path("by/token")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserAccount getByToken(@HeaderParam("token")String token){

        System.out.println(":---------------------------"+token);
        if(securityFilter.isVerified(token)){
            System.out.println("================================================================================="+findByEmail(securityFilter.getIssuer(token)).getEmail());
            return findByEmail(securityFilter.getIssuer(token));
        }else{
            throw new NotAuthorizedException(Response.status(401).build());
        }
    }




}
