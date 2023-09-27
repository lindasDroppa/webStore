package Appplication;


import Service.*;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("api")
public class JAXRSConfiguration extends Application {

    private static final  Set<Class<?>> resources;

    static {
        resources = new HashSet<>();
        resources.add(RestCorsRequestFilter.class);
        resources.add(RestCorsResponseFilter.class);

        resources.add(CartService.class);
        resources.add(ProductService.class);
        resources.add(TransactionService.class);
        resources.add(WalletService.class);
        resources.add(UserAccountService.class);

    }

    @Override
    public Set<Class<?>> getClasses() {
        return resources;
    }

}
