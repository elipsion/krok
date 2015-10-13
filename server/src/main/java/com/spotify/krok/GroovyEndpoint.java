package com.spotify.krok;

import com.fasterxml.jackson.databind.JsonNode;

import org.glassfish.jersey.process.Inflector;

/**
 * Created by elipsion on 7/15/15.
 */
public class GroovyEndpoint extends Endpoint {

  private final String endpoint;
  private final JsonNode conf;
  private final Class plugin;

  public GroovyEndpoint(String endpoint, JsonNode conf, Class plugin) {
    super(endpoint, conf);
    this.endpoint = endpoint;
    this.conf = conf;
    this.plugin = plugin;
  }

  @Override
  protected Inflector getInflector() {
    return new GroovyInflector(plugin, conf, null);
  }

  @Override
  protected Inflector getInflector(String method) {
    return new GroovyInflector(plugin, conf, method);
  }
}
