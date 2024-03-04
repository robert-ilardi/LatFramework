/**
 * Created Jan 16, 2021
 */
package com.ilardi.systems.lat.web;

import com.ilardi.systems.lat.LatException;

/**
 * @author rilardi
 *
 */

public interface LightWeightWebServerJmxMBean {

  public void stopServer() throws LatException;

}
