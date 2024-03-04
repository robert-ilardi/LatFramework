/**
 * Created Jan 16, 2021
 */
package com.ilardi.systems.lat.web;

import com.ilardi.systems.lat.LatException;

/**
 * @author rilardi
 *
 */

public class LightWeightWebServerJmx implements LightWeightWebServerJmxMBean {

  private LightWeightWebServer lwWebServer;

  public LightWeightWebServerJmx() {}

  public void setLightWeightWebServer(LightWeightWebServer lwWebServer) {
    this.lwWebServer = lwWebServer;
  }

  @Override
  public void stopServer() throws LatException {
    lwWebServer.stopServer();
  }

}
