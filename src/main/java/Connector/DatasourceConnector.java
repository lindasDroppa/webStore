package Connector;

import com.mongodb.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Produces;

@Singleton
@Startup
public final class  DatasourceConnector {

      private final MongoClient mongoClient= new MongoClient("localhost",27017);
      private final   Morphia morphia=new Morphia();


      private final    Datastore datastore=morphia.createDatastore(mongoClient,"OnlineShopDB");

      @RequestScoped
      @Produces
      public Datastore getDatastore() {
        return datastore;
    }


}
