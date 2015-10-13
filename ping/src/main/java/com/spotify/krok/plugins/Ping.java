package com.spotify.krok.plugins;

import com.fasterxml.jackson.databind.JsonNode;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

/**
 * Created by elipsion on 8/6/15.
 *
 * Remember to put this package in your local m2 repository
 */
public class Ping {
  Ping(JsonNode conf) {
    // Pass
  }

  Response main (ContainerRequestContext context) {
    return ping(context);
  }

  Response ping(ContainerRequestContext context) {
    return Response.ok().entity("Pong").build();
  }
}
