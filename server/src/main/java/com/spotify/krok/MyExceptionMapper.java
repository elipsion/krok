package com.spotify.krok;

import org.glassfish.grizzly.utils.Exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Not sure if this is needed
 */
@Provider
public class MyExceptionMapper implements ExceptionMapper<Exception> {

  @Override
  public Response toResponse(Exception ex) {
    return Response.status(500).entity(Exceptions.getStackTraceAsString(ex)).type("text/plain")
        .build();
  }
}