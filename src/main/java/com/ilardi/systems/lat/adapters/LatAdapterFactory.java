/**
 * Created Jan 27, 2024
 */
package com.ilardi.systems.lat.adapters;

/**
 * @author robert.ilardi
 *
 */

public class LatAdapterFactory {

  private static LatAdapterFactory instance = null;

  private LatAdapterFactory() {}

  public static synchronized LatAdapterFactory getInstance() {
    if (instance != null) {
      instance = new LatAdapterFactory();
    }

    return instance;
  }

  public synchronized LatAdapter getLatAdapter(String adapterName) {
    LatAdapter adapter = null;

    return adapter;
  }

}
