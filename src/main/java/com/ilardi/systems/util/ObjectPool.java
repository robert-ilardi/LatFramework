/**
 * Created Feb 25, 2024
 */
package com.ilardi.systems.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author robert.ilardi
 *
 */

public class ObjectPool<T> {

  private static final Logger logger = LogManager.getLogger(ObjectPool.class);

  protected final Object opLock;

  protected PoolObjectAllocDealloc<T> allocDealloc;

  protected ArrayList<ObjectPoolObjectEnvelope<T>> pool;

  protected int minObjectCnt;
  protected int maxObjectCnt;

  protected volatile int inUseCnt;

  protected int envelopeCnt;

  public ObjectPool(int minObjectCnt, int maxObjectCnt, PoolObjectAllocDealloc<T> allocDealloc) throws IlardiSystemsException {
    opLock = new Object();

    envelopeCnt = 0;

    inUseCnt = 0;

    if (minObjectCnt < 0) {
      minObjectCnt = 0;
    }

    if (maxObjectCnt <= 0) {
      maxObjectCnt = 1;
    }

    if (minObjectCnt > maxObjectCnt) {
      minObjectCnt = maxObjectCnt;
    }

    this.allocDealloc = allocDealloc;

    this.minObjectCnt = minObjectCnt;
    this.maxObjectCnt = maxObjectCnt;

    pool = new ArrayList<ObjectPoolObjectEnvelope<T>>();

    preallocateMinimum();
  }

  private void preallocateMinimum() throws IlardiSystemsException {
    synchronized (opLock) {
      try {
        logger.info("Preallocating Minimum Pool Objects: " + minObjectCnt);

        for (int i = 0; i < minObjectCnt; i++) {
          allocate();
        }
      } //End try block
      catch (Exception e) {
        throw new IlardiSystemsException("An error occurred while attempting to Preallocate Minimum Objects in the Object Pool! System Message: " + e.getMessage(), e);
      }
    } //End synchronized block
  }

  private void allocate() throws IlardiSystemsException {
    synchronized (opLock) {
      try {
        logger.info("Allocating new Pool Object " + (pool.size() + 1) + " of " + getMaxObjectCnt());

        T obj = allocDealloc.allocate();

        ObjectPoolObjectEnvelope<T> envelope = envelop(obj);

        pool.add(0, envelope);

        obj = null;
      } //End try block
      catch (Exception e) {
        throw new IlardiSystemsException("An error occurred while attempting to Allocate New Object for Object Pool! System Message: " + e.getMessage(), e);
      }
    } //End synchronized block
  }

  private ObjectPoolObjectEnvelope<T> envelop(T obj) throws IlardiSystemsException {
    synchronized (opLock) {
      ObjectPoolObjectEnvelope<T> envelope;
      String envelopeId;

      logger.info("Creating new Pool Object Envelope for Object: " + obj);

      envelopeId = generateUniqueEnvelopeId();

      envelope = new ObjectPoolObjectEnvelope<T>(envelopeId, obj);

      return envelope;
    } //End synchronized block
  }

  private String generateUniqueEnvelopeId() throws IlardiSystemsException {
    synchronized (opLock) {
      StringBuilder sb;
      String envelopeId;

      logger.info("Generating Pool Object Envelope ID");

      sb = new StringBuilder();

      sb.append("ObjectEnvelope-");
      sb.append(envelopeCnt);
      sb.append("-");
      sb.append(System.currentTimeMillis());

      envelopeCnt++;

      envelopeId = sb.toString();
      sb = null;

      logger.info(">> Pool Object Envelope ID: " + envelopeId);

      return envelopeId;
    } //End synchronized block
  }

  public T reserve() throws IlardiSystemsException {
    synchronized (opLock) {
      try {
        ObjectPoolObjectEnvelope<T> envelope;
        T obj = null;

        logger.info("Attempting to Reserve Pool Object");

        while (inUseCnt >= maxObjectCnt) {
          logger.info(">> Waiting on Pool Object Availablity...");
          opLock.wait();
        }

        //Allocate More if Possible
        if (pool.size() < maxObjectCnt && inUseCnt < maxObjectCnt) {
          logger.info(">> Object Pool Exhusted but Maximum Instances Not Allocated");
          allocate();
        }

        if (!pool.isEmpty()) {
          envelope = reserveFirstFreeEnvelope();

          if (envelope != null) {
            obj = envelope.getObj();
            logger.info(">> Reserved Pool Object: " + envelope);
          }
          else {
            logger.warn(">> Pool Object Reservation Failed (Could NOT Find Free Object). Returning NULL.");
          }
        }
        else {
          logger.warn(">> Pool Object Reservation Failed (Pool Exhusted). Returning NULL.");
        }

        return obj;
      } //End try block
      catch (Exception e) {
        throw new IlardiSystemsException("An error occurred while attempting to Reserve Object from the Object Pool! System Message: " + e.getMessage(), e);
      }
      finally {
        opLock.notifyAll();
      }
    } //End synchronized block
  }

  private ObjectPoolObjectEnvelope<T> reserveFirstFreeEnvelope() {
    synchronized (opLock) {
      ObjectPoolObjectEnvelope<T> envelope = null;
      Iterator<ObjectPoolObjectEnvelope<T>> iter;

      logger.info("Searching for First Free Pool Object Envelope");

      iter = pool.iterator();

      while (iter.hasNext()) {
        envelope = iter.next();

        if (!envelope.isReserved()) {
          iter.remove();

          envelope.setReserved(true);
          envelope.updateLastReserveTs();

          inUseCnt++;

          break;
        }
        else {
          envelope = null;
        }
      }

      if (envelope != null) {
        pool.add(envelope); //Add to End
      }

      return envelope;
    } //End synchronized block
  }

  public void release(T obj) throws IlardiSystemsException {
    synchronized (opLock) {
      ObjectPoolObjectEnvelope<T> envelope = null;
      Iterator<ObjectPoolObjectEnvelope<T>> iter;
      T other;

      logger.info("Releasing Pool Object: " + obj);

      iter = pool.iterator();

      while (iter.hasNext()) {
        envelope = iter.next();
        other = envelope.getObj();

        if (other.equals(obj)) {
          iter.remove();

          envelope.setReserved(false);
          envelope.updateLastReleaseTs();

          inUseCnt--;

          break;
        }
        else {
          envelope = null;
        }
      } //End while iter loop

      if (envelope != null) {
        pool.add(0, envelope); //Add to Front
      }
    } //End synchronized block
  }

  public void deallocatePool(boolean gracefully) throws IlardiSystemsException {
    synchronized (opLock) {
      ObjectPoolObjectEnvelope<T> envelope = null;
      Iterator<ObjectPoolObjectEnvelope<T>> iter;
      T obj;

      try {
        logger.info("Deallocating Pool. Gracefully? " + (gracefully ? "YES" : "NO"));

        if (gracefully) {
          //Wait for all objects to be returned to pool
          while (inUseCnt > 0) {
            opLock.wait();
          }
        }

        iter = pool.iterator();

        while (iter.hasNext()) {
          envelope = iter.next();

          obj = envelope.getObj();

          allocDealloc.deallocate(obj);

          obj = null;
          envelope = null;

          iter.remove();
        }
      } //End try block
      catch (Exception e) {
        throw new IlardiSystemsException("An error occurred while attempting to Deallocate Entire Object Pool! System Message: " + e.getMessage(), e);
      }
      finally {
        opLock.notifyAll();
      }
    } //End synchronized block
  }

  public boolean verifyAllFree() {
    synchronized (opLock) {
      ObjectPoolObjectEnvelope<T> envelope;
      boolean completelyFree = true;

      for (int i = 0; i < pool.size(); i++) {
        envelope = pool.get(i);

        if (envelope.isReserved()) {
          completelyFree = false;
          break;
        }
      }

      return completelyFree;
    } //End synchronized block
  }

  public int countAvailable() {
    synchronized (opLock) {
      ObjectPoolObjectEnvelope<T> envelope;
      int cnt = 0;

      for (int i = 0; i < pool.size(); i++) {
        envelope = pool.get(i);

        if (!envelope.isReserved()) {
          cnt++;
        }
      }

      return cnt;
    } //End synchronized block
  }

  public int countReserved() {
    synchronized (opLock) {
      ObjectPoolObjectEnvelope<T> envelope;
      int cnt = 0;

      for (int i = 0; i < pool.size(); i++) {
        envelope = pool.get(i);

        if (envelope.isReserved()) {
          cnt++;
        }
      }

      return cnt;
    } //End synchronized block
  }

  public int getPoolCapacity() {
    synchronized (opLock) {
      int cnt;

      cnt = pool.size();

      return cnt;
    } //End synchronized block
  }

  public int getMinObjectCnt() {
    return minObjectCnt;
  }

  public int getMaxObjectCnt() {
    return maxObjectCnt;
  }

  public int getInUseCnt() {
    synchronized (opLock) {
      return inUseCnt;
    } //End synchronized block
  }

}
