package org.aulich.weatherstation.rest.retrievedata;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aulich.model.MessageEntryModel;
import org.aulich.model.WiFiMessage;
import org.aulich.model.WiFiMessageModel;

import java.util.Iterator;
import java.util.Map;

@Path("/retrievedata")
public class RetrieveDataResource {
    private static final Logger LOG = LogManager.getLogger(RetrieveDataResource.class);
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String Test() {
        LOG.debug("Test() was called.");
        return "API " + this.getClass().getName() + " available.";
    }

    @POST
    @Path("/ecowitt")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes("application/x-www-form-urlencoded")
    public String handleEcowittRequest(MultivaluedMap<String, String> formParams) {
        LOG.debug("handleEcowittRequest() was called.");
        WiFiMessage wiFiMessage = new WiFiMessage();
        WiFiMessageModel wiFiMessageModel = new WiFiMessageModel();
        wiFiMessageModel.setMessageType(WiFiMessage.MESSAGETYPE_ECOWITT);
        Iterator<String> it = formParams.keySet().iterator();
        String passKey = "";
        String dateUTC = "";
        while(it.hasNext()){
            String theKey = (String)it.next();
            String theValue = (String) formParams.getFirst(theKey);
            wiFiMessageModel.getMessageEntryModels().add(new MessageEntryModel(theKey, theValue));
            if("PASSKEY".equals(theKey)) {
                passKey = theValue;
            }
            if("dateutc".equals(theKey)) {
                dateUTC = theValue;
            }
        }
        wiFiMessage.setWiFiMessageModel(wiFiMessageModel);
        wiFiMessage.save(dateUTC + "-" + passKey);
        return "OK";
    }
}
