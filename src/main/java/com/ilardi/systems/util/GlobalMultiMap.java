/*
 * Created on Feb 19, 2024
 */

package com.ilardi.systems.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author robert.ilardi
 */

public class GlobalMultiMap {

  private String name;
  private HashMap<Object, Object> cache;

  private static GlobalMultiMap globalInstance = null;

  private GlobalMultiMap(String name) {
    this.name = name;
    this.cache = new HashMap<Object, Object>();
  }

  public static synchronized GlobalMultiMap getInstance(String name) {
    GlobalMultiMap gmMap = null;

    if (globalInstance == null) {
      globalInstance = new GlobalMultiMap(null);
    }

    if (name == null) {
      gmMap = globalInstance;
    }
    else {
      gmMap = (GlobalMultiMap) globalInstance.cache.get(name);

      if (gmMap == null) {
        gmMap = new GlobalMultiMap(name);
      }
    }

    return gmMap;
  }

  public synchronized String getName() {
    return name;
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

  public synchronized int size() {
    return cache.size();
  }

  public synchronized List<Object> geyKeyList() {
    ArrayList<Object> keyLst;
    Set<Object> keySet;
    int ksSz;
    Iterator<Object> iter;
    Object key;

    keySet = cache.keySet();
    ksSz = keySet.size();
    iter = keySet.iterator();

    keyLst = new ArrayList<Object>(ksSz);

    while (iter.hasNext()) {
      key = iter.next();
      keyLst.add(key);
    }

    key = null;
    iter = null;
    ksSz = 0;
    keySet = null;

    return keyLst;
  }

}
