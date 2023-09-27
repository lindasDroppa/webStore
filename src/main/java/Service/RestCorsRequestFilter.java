package Service;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


@Provider
@PreMatching
public class RestCorsRequestFilter implements ContainerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(RestCorsRequestFilter.class);

    @Override
    public void filter(ContainerRequestContext requestCtx) throws IOException {


        logger.info("Executing REST request filter");

        if (requestCtx.getRequest().getMethod().equals("OPTIONS")) {
            logger.info("HTTP Method (OPTIONS) - Detected!");
            requestCtx.abortWith(Response.status(Response.Status.OK).build());
        }
    }
}
