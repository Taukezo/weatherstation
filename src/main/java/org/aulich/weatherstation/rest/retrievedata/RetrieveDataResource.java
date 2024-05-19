package org.aulich.weatherstation.rest.retrievedata;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/retrievedata")
public class RetrieveDataResource {
    @GET
    @Path("/test")
    @Produces("text/plain")
    public String Test() {
        return "API " + this.getClass().getName() + " available.";
    }

    @GET
    @Path("/test2")
    @Produces("text/plain")
    public String Test2() {
        return "API2 " + this.getClass().getName() + " available.";
    }
}
