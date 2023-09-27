package Service;


import Connector.DatasourceConnector;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.List;

public abstract class EntityUtil<T>
{
    DatasourceConnector datasourceConnector=new DatasourceConnector();

    private Class<T> entityClass;

    public EntityUtil(Class<T> entityClass) {
        this.entityClass = entityClass;
    }



    public Response create(T entity) {

        datasourceConnector.getDatastore().save(entity);
        return null;
    }
    public void edit(T entity){
        datasourceConnector.getDatastore().merge(entity);
    }
    public void remove(String id)
    {
        datasourceConnector.getDatastore().delete(datasourceConnector.getDatastore().find(entityClass).field("_id").equal(id).get());
    }
    public List<T> findAll(){
        return datasourceConnector.getDatastore().find(entityClass).asList();
    }
    public T find(String id){

        T t= datasourceConnector.getDatastore().find(entityClass).field("_id").equal(new ObjectId(id)).get();
        if(t!=null){
            System.out.println("Found ;+++++++++++++++"+t);
            return t;

        }else{
            throw new BadRequestException();
        }

    }
    public Long count(){
        return datasourceConnector.getDatastore().find(entityClass).count();
    }




}
