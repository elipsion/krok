package com.spotify.krok;

import com.fasterxml.jackson.databind.JsonNode;

import groovy.grape.Grape;
import groovy.lang.GroovyClassLoader;

import org.glassfish.jersey.process.Inflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;

/**
 * Created by elipsion on 7/17/15.
 */
public class ModuleEndpoint extends Endpoint {

  private static final Logger LOG = LoggerFactory.getLogger(ModuleEndpoint.class);
  private final Class plugIn;
  private final Object instance;

  public ModuleEndpoint(String endpoint, JsonNode conf) throws ReflectiveOperationException {
    super(endpoint, conf);
    //Todo: Don't use Grape for this
    ClassLoader loader = new GroovyClassLoader();
    Map args = new HashMap();
    //Todo: Regex and bounds check this bad boy
    String[] identifier = conf.get("module").asText().split(":");
    args.put("group", identifier[0]);
    args.put("module", identifier[1]);
    args.put("version", identifier[2]);
    args.put("classLoader", loader);
    Grape.grab(args);
    //Todo: Figure out why this throws a ClassNotFound
    this.plugIn = loader.loadClass(identifier[3]);
    Constructor plugInConstructor;
    plugInConstructor = plugIn.getConstructor(JsonNode.class);
    if (plugInConstructor == null) {
      plugInConstructor = plugIn.getConstructor();
    }
    this.instance = plugInConstructor.newInstance(conf);
  }

  @Override
  protected Inflector getInflector() {
    return getInflector("main");
  }

  @Override
  protected Inflector getInflector(String method) {
    try {
      return new ModuleInflector(instance,
                                 plugIn.getDeclaredMethod(method,
                                                          ContainerRequestContext.class));
    } catch (NoSuchMethodException e) {
      return null;
    }
  }
}
