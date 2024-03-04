/*
 * Created on Dec 4, 2008
 */

package com.ilardi.systems.util;

import java.util.HashMap;

/**
 * @author rilardi
 */

public class GlobalMap {

  private HashMap<Object, Object> cache;
  private static GlobalMap globalSpace = null;

  private GlobalMap() {
    cache = new HashMap<Object, Object>();
  }

  public static synchronized GlobalMap getInstance() {
    if (globalSpace == null) {
      globalSpace = new GlobalMap();
    }

    return globalSpace;
  }

  public synchronized void clear() {
    cache.clear();
  }

  public synchronized void store(Object key, Object value) {
    cache.put(key, value);
  }

  public synchronized Object retrieve(Object key) {
    return cache.get(key);
  }

  public synchronized Object remove(Object key) {
    return cache.remove(key);
  }

  public synchronized boolean containsKey(Object key) {
    return cache.containsKey(key);
  }

}
