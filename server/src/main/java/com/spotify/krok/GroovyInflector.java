package com.spotify.krok;

import com.fasterxml.jackson.databind.JsonNode;

import groovy.lang.Binding;
import groovy.lang.Script;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.glassfish.grizzly.utils.Exceptions;
import org.glassfish.jersey.process.Inflector;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

/**
 * Created by elipsion on 7/15/15.
 */
final public class GroovyInflector implements Inflector<ContainerRequestContext, Object> {

  private final Class scriptClass;
  private final JsonNode config;
  private String method;

  protected GroovyInflector(Class scriptClass, JsonNode endpointConfiguration, String method) {
    this.scriptClass = scriptClass;
    this.config = endpointConfiguration;
    this.method = method;
  }

  protected Binding getBindings(ContainerRequestContext context) {
    Binding b = new Binding();
    OutputStream os = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(os);
    try {
      Iterator<Map.Entry<String, JsonNode>> i = config.get("parameters").fields();
      while (i.hasNext()) {
        Map.Entry<String, ?> entry = i.next();
        b.setVariable(entry.getKey(), entry.getValue());
      }
    } catch (NullPointerException e){
      // Pass
    }
    // Provided by Jersey
    b.setVariable("context", context);
    // Set this to prevent printing to console
    b.setProperty("out", ps);
    // Just put this here to be able to get output later
    b.setProperty("outBuffer", os);
    return b;
  }

  @Override
  public Object apply(ContainerRequestContext containerRequestContext) {
    Binding binding = getBindings(containerRequestContext);
    Object ret;
    Response.ResponseBuilder response;
    try {
      Script script = InvokerHelper.createScript(scriptClass, binding);
      // Call either the named method or the base script
      if (null != method) {
        ret = script.invokeMethod(method, containerRequestContext);
      } else {
        ret = script.run();
      }
      // Check for return value
      if (null != ret && Integer.class == ret.getClass()) {
        response = Response.status((Integer) ret);
      } else {
        response = Response.ok();
      }
      // Attach script output
      response.entity(binding.getProperty("outBuffer").toString());
    } catch (Exception e) {
      response = Response.serverError().entity(Exceptions.getStackTraceAsString(e));
    }
    return response.build();
  }
}
