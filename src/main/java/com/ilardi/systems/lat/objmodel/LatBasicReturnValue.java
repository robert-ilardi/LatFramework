/**
 * Created Feb 24, 2024
 */
package com.ilardi.systems.lat.objmodel;

/**
 * @author rober
 *
 */

public class LatBasicReturnValue extends LatBaseObject {

  protected LatBasicStatus status;

  protected int retCode;

  protected String retMesg;

  public LatBasicReturnValue() {
    super();
  }

  public LatBasicStatus getStatus() {
    return status;
  }

  public void setStatus(LatBasicStatus status) {
    this.status = status;
  }

  public int getRetCode() {
    return retCode;
  }

  public void setRetCode(int retCode) {
    this.retCode = retCode;
  }

  public String getRetMesg() {
    return retMesg;
  }

  public void setRetMesg(String retMesg) {
    this.retMesg = retMesg;
  }

}
