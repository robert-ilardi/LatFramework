/**
 * Created Feb 26, 2024
 */
package com.ilardi.systems.util;

/**
 * @author robert.ilardi
 *
 */

public class ObjectPoolObjectEnvelope<T> {

  private String envelopeId;

  private T obj;

  private boolean reserved;

  private long creationTs;
  private long lastReserveTs;
  private long lastReleaseTs;

  public ObjectPoolObjectEnvelope(String envelopeId, T obj) {
    this.envelopeId = envelopeId;
    this.obj = obj;

    reserved = false;

    creationTs = System.currentTimeMillis();

    lastReserveTs = 0;
    lastReleaseTs = 0;
  }

  public boolean isReserved() {
    return reserved;
  }

  public void setReserved(boolean reserved) {
    this.reserved = reserved;
  }

  public long getLastReserveTs() {
    return lastReserveTs;
  }

  public void updateLastReserveTs() {
    this.lastReserveTs = System.currentTimeMillis();
  }

  public long getLastReleaseTs() {
    return lastReleaseTs;
  }

  public void updateLastReleaseTs() {
    this.lastReleaseTs = System.currentTimeMillis();
  }

  public T getObj() {
    return obj;
  }

  public long getCreationTs() {
    return creationTs;
  }

  public String getEnvelopeId() {
    return envelopeId;
  }

}
