/**
 * Created Feb 19, 2024
 */
package com.ilardi.systems.lat.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ilardi.systems.lat.LatException;
import com.ilardi.systems.lat.objmodel.LatHelloWorldRequest;
import com.ilardi.systems.lat.objmodel.LatHelloWorldResponse;
import com.ilardi.systems.util.StringUtils;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * @author robert.ilardi
 *
 */

@Path("/ChatService")
public class LatChatWebService {

  private static final Logger logger = LogManager.getLogger(LatChatWebService.class);

  public LatChatWebService() {}

  @Path("/hello-world")
  @GET
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public String helloWorld(@QueryParam("name")
  String name) throws LatException {
    StringBuilder sb;
    String hwMesg;

    sb = new StringBuilder();
    sb.append("Embedded Jetty Jakarta REST Web Service - ");

    if (name != null && !name.isBlank()) {
      sb.append(" Hello ");

      name = name.trim();
      sb.append(name);

      sb.append(" - ");
    }

    sb.append(StringUtils.GetTimeStamp());

    hwMesg = sb.toString();
    sb = null;

    return hwMesg;
  }

  @Path("/hello-world")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public LatHelloWorldResponse helloWorld(LatHelloWorldRequest latReq) throws LatException {
    LatHelloWorldResponse latRes;
    StringBuilder sb;
    String hwMesg, name;

    sb = new StringBuilder();
    sb.append("Embedded Jetty Jakarta REST Web Service - ");

    if (latReq != null) {
      name = latReq.getName();

      if (name != null && !name.isBlank()) {
        sb.append(" Hello ");

        name = name.trim();
        sb.append(name);

        sb.append(" - ");
      }
    }

    sb.append(StringUtils.GetTimeStamp());

    hwMesg = sb.toString();
    sb = null;
    name = null;

    latRes = new LatHelloWorldResponse();
    latRes.setHelloMesg(hwMesg);
    hwMesg = null;

    return latRes;
  }

}
