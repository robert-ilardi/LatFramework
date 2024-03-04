/**
 * Created Feb 25, 2024
 */
package com.ilardi.systems.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author robert.ilardi
 *
 */

public class MultiObjectPool<Tkey, Tobj> {

  private static final Logger logger = LogManager.getLogger(MultiObjectPool.class);

  protected HashMap<Tkey, ObjectPool<Tobj>> poolMap;

  protected final Object mopLock;

  public MultiObjectPool() {
    mopLock = new Object();

    poolMap = new HashMap<Tkey, ObjectPool<Tobj>>();
  }

  public Tobj reserve(Tkey key) throws IlardiSystemsException {
    synchronized (mopLock) {
      ObjectPool<Tobj> pool;
      Tobj obj = null;

      pool = poolMap.get(key);

      if (pool != null) {
        obj = pool.reserve();
      }
      else {
        logger.warn("Pool NOT Found for Key: " + key);
      }

      return obj;
    } //End synchronized block
  }

  public void release(Tkey key, Tobj obj) throws IlardiSystemsException {
    synchronized (mopLock) {
      ObjectPool<Tobj> pool;

      pool = poolMap.get(key);

      if (pool != null) {
        pool.release(obj);
      }
      else {
        logger.warn("Pool NOT Found for Key: " + key);
      }
    } //End synchronized block
  }

  public void destroy(boolean gracefully) throws IlardiSystemsException {
    synchronized (mopLock) {
      try {
        Tkey key;
        Set<Tkey> keySet;
        Iterator<Tkey> iter;
        ObjectPool<Tobj> pool;

        logger.info("Destroying Multi Object Pool. Gracefully? " + (gracefully ? "YES" : "NO"));

        keySet = poolMap.keySet();
        iter = keySet.iterator();

        while (iter.hasNext()) {
          key = iter.next();
          pool = poolMap.get(key);

          pool.deallocatePool(gracefully);
          pool = null;
        }

        poolMap.clear();
        poolMap = null;
      } //End try block
      catch (Exception e) {
        throw new IlardiSystemsException("An error occurred while attemping to Destroy Multi Object Pool! System Message: " + e.getMessage(), e);
      }
    } //End synchronized block   
  }

}
