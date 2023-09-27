package Service;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;



@Provider
@PreMatching
public class RestCorsResponseFilter implements ContainerResponseFilter {

    private final Logger logger = LoggerFactory.getLogger(RestCorsResponseFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        logger.info("Executing REST response filter");

        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        String reqHeader =requestContext.getHeaderString("Access-Control-Request-Headers");
        if(reqHeader!=null&&reqHeader!=""){
            responseContext.getHeaders().putSingle("Access-Control-Allow-Headers",reqHeader);
        }
    }
}

