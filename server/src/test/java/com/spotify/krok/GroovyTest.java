package com.spotify.krok;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.junit.Assert.assertEquals;

public class GroovyTest {

  private HttpServer server;
  private WebTarget target;

  @Before
  public void setUp() throws Exception {
    URI configPath = getClass().getClassLoader().getResource("groovy.yaml").toURI();
    // start the server
    server = Main.startServer(Main.getConfig(configPath));
    // create the client
    Client c = ClientBuilder.newClient();
    target = c.target(Main.BASE_URI);
  }

  @After
  public void tearDown() throws Exception {
    server.shutdownNow();
  }

  /**
   * Test to see that the message "Pong" is sent as response.
   */
  @Test
  public void testPing() {
    String responseMsg = target.path("ping").request().get(String.class);
    assertEquals("Pong", responseMsg);
  }
}
