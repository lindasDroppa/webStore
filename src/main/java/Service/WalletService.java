package Service;

import Control.SecurityFilter;
import Entities.Wallet;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("wallet")
public class WalletService extends EntityUtil<Wallet>
{

    @Inject UserAccountService userAccountService;
    SecurityFilter securityFilter=new SecurityFilter();

    public WalletService() {
        super(Wallet.class);
    }

    @Override
    public Response create(Wallet entity) {
        return super.create(entity);
    }

    @POST
    @Path("deposit/{amount}")
    public void toUpWallet(@PathParam("amount") double amount, @HeaderParam("token")String token){
        System.out.println("token=================================:"+token);
        if(securityFilter.isVerified(token)){//Verify token
           String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();//Get user id using the token
           addAmount(userID,amount);//add the amount
        }

    }

    @GET
    @Path("balance")
    public double balance(@HeaderParam("token")String token){
        System.out.println("token=================================:"+token);
        String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();//Get user id using the token
        return super.datasourceConnector.getDatastore().find(Wallet.class)
                .field("userAccountID").equal(userID).get().getBalance();//find and return the balance
    }

    @GET
    @Path("withdraw/{amount}")
    public Response withdrew(@PathParam("amount")double amount,@HeaderParam("token")String token){
        System.out.println("token=================================:"+token);
        if(securityFilter.isVerified(token)){//verify token
            String userID= userAccountService.findByEmail(securityFilter.getIssuer(token)).getId().toString();//Get user id using the token
            Wallet wallet= super.datasourceConnector.getDatastore().find(Wallet.class)
                    .field("userAccountID").equal(userID).get();//get the wallet using user ID
            if(wallet.getBalance()>=amount){//check if the user has enough money
                wallet.setBalance(wallet.getBalance()-amount);//deduct from the user amount
                super.edit(wallet);//update the wallet
                return Response.status(200).build();
            }else{
                return Response.status(412).build();
            }
        }else{
            return Response.status(401).build();
        }


    }

    public void addAmount(String id,double amount){

        Wallet wallet= super.datasourceConnector.getDatastore().find(Wallet.class)
                .field("userAccountID").equal(id).get();//find the wallet using the user id
        wallet.setBalance(wallet.getBalance()+amount);//add the amount
        super.edit(wallet);//update the wallet


    }


}
