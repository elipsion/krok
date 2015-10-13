package com.spotify.krok;

import com.fasterxml.jackson.databind.JsonNode;

import org.glassfish.jersey.process.Inflector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.xml.ws.Response;


/**
 * Created by elipsion on 6/1/15.
 */

public abstract class Endpoint {

  @Deprecated // Where do we need this?
  private final String path;
  private final JsonNode conf;

  public Endpoint(String endpoint, JsonNode conf) {
    this.path = endpoint;
    this.conf = conf;
  }

  public Map<String, Inflector> getMethods() {
    Map ret = new HashMap();
    // Todo: Add support for embedding this in the plugin
    JsonNode methods = conf.get("methods");
    if (methods.isArray()) {
      for (JsonNode method : methods) {
        String methodName = method.asText().toUpperCase();
        ret.put(methodName, getInflector());
      }
    }
    if (methods.isObject()) {
      Iterator<Map.Entry<String, JsonNode>> i = methods.fields();
      while (i.hasNext()) {
        Map.Entry<String, JsonNode> method = i.next();
        String methodName = method.getKey().toUpperCase();
        String handler = method.getValue().asText();
        ret.put(methodName, getInflector(handler));
      }
    }
    return ret;
  }

  /**
   * Get the main inflector
   */
  protected abstract Inflector<ContainerRequestContext, Response> getInflector();

  /**
   * Get the inflector specified by <i>method</i>
   */
  protected abstract Inflector<ContainerRequestContext, Response> getInflector(String method);

}
