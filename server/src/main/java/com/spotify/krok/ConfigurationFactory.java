package com.spotify.krok;

import com.fasterxml.jackson.databind.JsonNode;

import groovy.lang.GroovyClassLoader;

import org.codehaus.groovy.control.ConfigurationException;

import java.io.File;
import java.io.IOException;


/**
 * Created by elipsion on 7/17/15.
 */
public class ConfigurationFactory {

  public static Endpoint parseConfig(String endpoint, JsonNode conf) {
    try {
      if (conf.has("type")) {
        switch (conf.get("type").asText()) {
          case "groovyScript":
            return groovyScript(endpoint, conf);
          case "groovyModule":
            return groovyModule(endpoint, conf);
          case "groovy":
            return groovyMagic(endpoint, conf);
          case "module":
            return module(endpoint, conf);
          default:
            break;
        }
      } else {
        Endpoint endpointConfiguration = generalMagic(conf);
        if (endpointConfiguration != null) {
          return endpointConfiguration;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    throw new ConfigurationException("Failed to detect endpoint type on section " + endpoint);
  }

  private static Endpoint groovyScript(String endpoint, JsonNode conf) {
    Class scriptClass = new GroovyClassLoader().parseClass(conf.get("script").asText());
    return new GroovyEndpoint(endpoint, conf, scriptClass);
  }

  private static Endpoint groovyModule(String endpoint, JsonNode conf) throws IOException {
    Class scriptClass =
        new GroovyClassLoader().parseClass(new File(conf.get("scriptPath").asText()));
    return new GroovyEndpoint(endpoint, conf, scriptClass);
  }

  private static Endpoint module(String endpoint, JsonNode conf)
      throws ReflectiveOperationException {
    return new ModuleEndpoint(endpoint, conf);
  }

  //Todo: implement
  private static Endpoint generalMagic(JsonNode conf) {
    return null;
  }

  //Todo: implement
  private static Endpoint groovyMagic(String endpoint, JsonNode conf) {
    return null;
  }
}
