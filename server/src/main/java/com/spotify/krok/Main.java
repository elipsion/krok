package com.spotify.krok;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;

/**
 * Main class.
 */
public class Main {

  // Base URI the Grizzly HTTP server will listen on
  public static final String BASE_URI = "http://localhost:8080/";
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  /**
   * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
   *
   * @return Grizzly HTTP server.
   */
  public static HttpServer startServer(JsonNode configuration) throws URISyntaxException {
    ResourceConfig rc = getEndpoints(configuration);
    URI baseURI;
    if (configuration.hasNonNull("baseURI")) {
      baseURI = new URI(configuration.get("baseURI").asText());
    } else {
      baseURI = new URI(BASE_URI);
    }
    return GrizzlyHttpServerFactory.createHttpServer(baseURI, rc);
  }

  private static ResourceConfig getEndpoints(JsonNode configuration) {
    ResourceConfig resourceConfig = new ResourceConfig();
    Map.Entry<String, JsonNode> endpoint = null;
    Iterator<Map.Entry<String, JsonNode>> i = configuration.get("endpoints").fields();
    while (i.hasNext()) {
      endpoint = i.next();
      resourceConfig.registerResources(registerEndpoint(endpoint.getKey(), endpoint.getValue()));
    }

    return resourceConfig;
  }

  protected static Resource registerEndpoint(String endpoint, JsonNode conf) {
    Resource.Builder resourceBuilder = Resource.builder(endpoint);
    LOG.info("Registering endpoint {}", endpoint);
    Endpoint endpointConfiguration = ConfigurationFactory.parseConfig(endpoint, conf);
    for (Map.Entry<String, Inflector> method : endpointConfiguration.getMethods().entrySet()) {
      LOG.debug("Adding handler for method {} on endpoint {}", method, endpoint);
      resourceBuilder.addMethod(method.getKey()).handledBy(method.getValue());
    }
    return resourceBuilder.build();
  }

  /**
   * Main method.
   */
  public static void main(String[] args) throws IOException, URISyntaxException {
    String configPath = args[0];
    JsonNode config = getConfig(new File(configPath).toURI());
    final HttpServer server = startServer(config);
    System.out.println(String.format("Jersey app started with WADL available at "
                                     + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
    System.in.read();
    server.shutdownNow();
  }

  protected static JsonNode getConfig(URI configPath) throws IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    return mapper.readTree(new File(configPath));
  }
}

