package com.spotify.krok;

import org.glassfish.jersey.process.Inflector;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

/**
 * Created by elipsion on 7/15/15.
 */
final class ModuleInflector implements Inflector<ContainerRequestContext, Object> {

  private final Object instance;
  private final Method method;

  public ModuleInflector(Object instance, Method method) {
    this.instance = instance;
    this.method = method;
  }

  @Override
  public Object apply(ContainerRequestContext containerRequestContext) {
    Response.ResponseBuilder response;
    Object ret;
    try {
      ret = method.invoke(instance, containerRequestContext);
      if (null != ret && Integer.class == ret.getClass()) {
        response = Response.status((Integer) ret);
      } else {
        response = Response.ok();
      }
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      response = Response.serverError().entity(sw);
    }
    containerRequestContext.abortWith(response.build());
    return null;
  }
}
