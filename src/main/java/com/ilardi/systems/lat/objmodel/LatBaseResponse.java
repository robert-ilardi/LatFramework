/**
 * Created Feb 21, 2024
 */
package com.ilardi.systems.lat.objmodel;

/**
 * @author rober
 *
 */

public abstract class LatBaseResponse extends LatBaseObject {

  protected LatBasicReturnValue latBasicRetVal;

  public LatBaseResponse() {
    super();
  }

  public LatBasicReturnValue getLatBasicRetVal() {
    return latBasicRetVal;
  }

  public void setLatBasicRetVal(LatBasicReturnValue latBasicRetVal) {
    this.latBasicRetVal = latBasicRetVal;
  }

}
